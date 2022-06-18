package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public class WbConfluenceDokuParserHandler implements ConfluenceDokuParserHandler {

	private static final Log logger = LogFactory.getLog(WbConfluenceDokuParserHandler.class);

	private ApiConnection dokuApi = new BasicApiConnection(Utils.GND_DOKU_WIKIBASE_URL);

	private List<WbMapping> wbMappings = new ArrayList<WbMapping>();

	private Map<String, EntityDocument> wbEntityCache = new HashMap<String, EntityDocument>();

	private ItemDocumentBuilder rootItemDocumentBuilder;

	WikibaseDataFetcher wbdf = new WikibaseDataFetcher(dokuApi, Utils.GND_DOKU_SITE_IRI);

	private WbMapping lookupMapping(String label, String value) {
		if (label != null) {
			for (WbMapping wbMapping : wbMappings) {
				if (label.matches(wbMapping.getLabel())) {
					if (wbMapping.getValue() != null) {
						if (value.matches(wbMapping.getValue())) {
							return wbMapping;
						}
					} else {
						return wbMapping;
					}
				}
			}
		}
		return null;
	}

	public WbConfluenceDokuParserHandler() throws MediaWikiApiErrorException, IOException {
		wbMappings.add(
				new WbMapping("Entität.*", "P124").withLookupPattern("STA-Klasse - " + WbMapping.VALUE_PLACEHOLDER));
		wbMappings.add(new WbMapping("Status.*", "P485").withValue("Standardelement")
				.withWbValue(new ItemIdValueImpl("Q2038", Utils.GND_DOKU_SITE_IRI)));
		wbMappings.add(new WbMapping("Erfassungsmethode.*", "P126").withLookupPattern(WbMapping.VALUE_PLACEHOLDER));
		wbMappings.add(new WbMapping("Kodierung.*", "P4"));
		wbMappings.add(new WbMapping("Definition.*", "P1"));
		wbMappings.add(new WbMapping("Beziehungen zu anderen Elementen.*", "P401"));
		wbMappings.add(new WbMapping("Informationsquellen.*", "P402"));
		wbMappings.add(new WbMapping("Basisregeln.+", "P388").withNewItem(true));
		wbMappings.add(new WbMapping("Basisregeln", "P388"));
		wbMappings.add(new WbMapping("Spezialregeln.*", "P386"));
		wbMappings.add(new WbMapping("Spezifische Regeln.*", "P410").withNewItem(true));

		rootItemDocumentBuilder = ItemDocumentBuilder.forItemId(ItemIdValue.NULL);

		loadWbPropertiesIntoCache();

	}

	public void loadWbPropertiesIntoCache() throws MediaWikiApiErrorException, IOException {

		List<String> pids = wbMappings.stream().map(pid -> pid.getWbId()).toList();
		this.wbEntityCache.putAll(wbdf.getEntityDocuments(pids));
	}

	public EntityDocument lookupEntity(String query) throws MediaWikiApiErrorException, IOException {
		// look if entity is inside cache
		EntityDocument ret = wbEntityCache.get(query);
		if (ret != null)
			return ret;
		List<WbSearchEntitiesResult> results = wbdf.searchEntities(query, "de");
		logger.debug("Suchergebnis nach '" + query + "': " + results.size());
		for (WbSearchEntitiesResult result : results) {
			if (result.getMatch().getText().equalsIgnoreCase(query)) {
				logger.debug("Passende Entität gefunden: " + result.getEntityId());
				EntityDocument entity = wbdf.getEntityDocument(result.getEntityId());
				wbEntityCache.put(query, entity);
				return entity;
			}
		}
		return null;
	}

	@Override
	public void onPanel(Element panel) {
//		logger.debug(panel);
		if (panel.childNodeSize() < 1) {
			logger.debug("Überspringe panel, da es kein label hat");
			return;
		}

		String label = panel.child(0).text();

		if (panel.childNodeSize() < 2 || panel.child(1).childNodeSize() < 1) {
			logger.debug("Überspringe panel '" + label + "', da es keinen Inhaltsbereich hat");
			return;
		}

		String value = panel.child(1).child(0).text();

		WbMapping wbMapping = lookupMapping(label, value);
		if (wbMapping == null) {
			logger.debug("Überspringe panel, da kein passendes mapping registriert ist: " + label);
			return;
		}

		if (wbMapping.isNewItem()) {
			addPanelAsItem(label, panel, wbMapping);
		} else {
			logger.debug("füge neues statement hinzu für panel '" + label + "'");
			addPanelAsStatement(rootItemDocumentBuilder, panel, wbMapping);
		}

	}

	public void addPanelAsItem(String label, Element panel, WbMapping wbMapping) {
		// create new item
		ItemDocumentBuilder itemDocumentBuilder = ItemDocumentBuilder.forItemId(ItemIdValue.NULL).withLabel(label,
				"de");

		if (panel.childNodeSize() < 2 || panel.child(1).childNodeSize() < 2) {
			logger.warn(
					"Konnte kein item aus dem panel '" + label + "' generieren, da die nötigen Paragraphen fehlen.");
			logger.debug(panel);
			return;
		}
		logger.debug("füge neues item hinzu für panel '" + label + "'");
		StringValue firstParagraph = new StringValueImpl(panel.child(1).child(1).html());
		addPanelAsStatement(itemDocumentBuilder, panel, wbMapping);
		ItemDocument newItem = itemDocumentBuilder.build();
		rootItemDocumentBuilder.withStatement(StatementBuilder
				.forSubjectAndProperty(ItemIdValue.NULL, wbMapping.getWbPropertyId()).withValue(firstParagraph)
				.withQualifierValue(Utils.WB_EMBEDDED, newItem.getEntityId()).build());

	}

	public void addPanelAsStatement(ItemDocumentBuilder itemDocumentBuilder, Element panel, WbMapping wbMapping) {
		if (wbMapping.getWbValue() != null) {
			logger.debug("füge neues statement hinzu für panel '" + wbMapping.getLabel() + "' mit konstantem Wert: "
					+ wbMapping.getWbValue());
			rootItemDocumentBuilder
					.withStatement(StatementBuilder.forSubjectAndProperty(ItemIdValue.NULL, wbMapping.getWbPropertyId())
							.withValue(wbMapping.getWbValue()).build());
		} else if (wbMapping.getLookupPattern() != null) {
			addPanelWithLookupValue(itemDocumentBuilder, panel, wbMapping);
		} else {
			if (panel.childNodeSize() < 2) {
				logger.warn("panel enthaelt keinen content");
				return;
			}
			Element panelContent = panel.child(1);
			Elements panelElements = panelContent.children();
			for (Element panelElement : panelElements) {
				Utils.addPanelElement(itemDocumentBuilder, panelElement, wbMapping.getWbPropertyId());
			}
		}
	}

	public ItemDocument getItemDocument() {
		return rootItemDocumentBuilder.build();
	}

	@Override
	public void onLabel(String labelDe, String labelEn) {
		rootItemDocumentBuilder.withLabel(labelDe, "de");
		rootItemDocumentBuilder.withLabel(labelEn, "en");
	}

	public void addPanelWithLookupValue(ItemDocumentBuilder itemDocumentBuilder, Element panel, WbMapping wbMapping) {
		if (panel.childNodeSize() < 2) {
			logger.warn("Panel enthaelt keinen content");
			return;
		}
		Element panelContent = panel.child(1);
		String value = panelContent.child(0).text();
		String query = wbMapping.getLookupPattern().replace(WbMapping.VALUE_PLACEHOLDER, value);
		logger.debug("looking up entity with query '" + query + "'");
		try {
			EntityDocument entity = lookupEntity(query);
			if (entity != null) {

				rootItemDocumentBuilder.withStatement(
						StatementBuilder.forSubjectAndProperty(ItemIdValue.NULL, wbMapping.getWbPropertyId())
								.withValue(entity.getEntityId()).build());
			}
		} catch (MediaWikiApiErrorException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}
	}

}
