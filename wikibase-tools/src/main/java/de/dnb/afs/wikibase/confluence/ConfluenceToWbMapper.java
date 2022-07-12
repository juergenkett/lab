package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.implementation.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;

/**
 * @author kett
 *
 */
public class ConfluenceToWbMapper {

	private static final Log logger = LogFactory.getLog(ConfluenceToWbMapper.class);

	private List<WbMapping> wbMappings;

	private List<Statement> initialStatements;

	private WbEntityEditor wbEditor;

	public ConfluenceToWbMapper(WbEntityEditor wbEditor, List<WbMapping> wbMappings, List<Statement> initialStatements)
			throws MediaWikiApiErrorException, IOException {
		this.wbMappings = wbMappings;
		this.wbEditor = wbEditor;

		this.initialStatements = initialStatements;
	}

	private WbMapping lookupMapping(String panelText) {
		if (panelText != null) {
			for (WbMapping wbMapping : wbMappings) {
				if (panelText.matches(wbMapping.getLabelPattern())) {
					return wbMapping;
				}
			}
		}
		return null;
	}

//	private ItemDocument newItem() {
//		ItemDocumentBuilder itemBuilder = ItemDocumentBuilder.forItemId(ItemIdValue.NULL);
//		for (Statement statement : initialStatements) {
//			itemBuilder.withStatement(statement);
//		}
//		return itemBuilder.build();
//	}
//
//	private PropertyDocument newProperty() {
//		PropertyDocumentBuilder propertyBuilder = PropertyDocumentBuilder.forPropertyIdAndDatatype(PropertyIdValue.NULL,
//				DatatypeIdValue.DT_PROPERTY);
//		for (Statement statement : initialStatements) {
//			propertyBuilder.withStatement(statement);
//		}
//		return propertyBuilder.build();
//	}

	private TermedStatementDocument getEntity(String wbEntityId) throws MediaWikiApiErrorException, IOException {
		return (TermedStatementDocument) wbEditor.getEntity(wbEntityId);
	}

	public EntityDocument map(Document document, String wbEntityId) {
		try {
			TermedStatementDocument entity = getEntity(wbEntityId);

			if (entity == null) {
				logger.warn("entity mit Id " + wbEntityId + " nicht gefunden. Überspringe Mapping.");
				return null;
			}
			logger.debug("entity mit Id " + wbEntityId + " gefunden. Revision Id: " + entity.getRevisionId());
			Map<String, MonolingualTextValue> labels = getLabels(document);
			List<Statement> statements = new ArrayList<Statement>();
			Elements panels = document.getElementsByAttributeValue("data-macro-name", "panel");
			for (Element panel : panels) {
				mapPanel(labels, panel, entity.getEntityId(), statements);
			}
			logger.debug("Item wird aktualisiert ...");
			wbEditor.updateEntity(entity, labels, statements);
			return entity;
		} catch (IOException | MediaWikiApiErrorException e) {
			logger.error(e);
			return null;
		}
	}

	public void mapPanel(Map<String, MonolingualTextValue> entityLabels, Element panel, EntityIdValue wbEntityId,
			List<Statement> statements) {
		String panelAsText = panel.text();
//		logger.debug("PANEL-TEXT: " + panelAsText);

		if (panel.children().size() < 1) {
			logger.debug("Überspringe panel, da es kein label hat");
			return;
		}

		/*
		 * label des panels einlesen (1. Kindelement des Panels: <div
		 * class="panelHeader"/>)
		 */
		String label = panel.child(0).text();

		if (panel.children().size() < 2) {
			logger.debug("Überspringe panel '" + label + "', da es keinen Inhaltsbereich hat");
			return;
		}

		/*
		 * Content des Panels (2. Kind des Panels: <div class="panelContent"/>).
		 */
		Element contentPanel = panel.child(1);
		Elements panelElements = contentPanel.children();
//		logger.debug("PANEL-CONTENT: " + panelElements);

		/*
		 * Mapping suchen
		 */
		WbMapping wbMapping = lookupMapping(panelAsText);
		if (wbMapping == null) {
			logger.debug("Überspringe panel, da kein passendes mapping registriert ist: " + label);
			return;
		}

		logger.debug("Starte Mapping für panel : " + label + " mit " + wbMapping);
		wbMapping.doMap(entityLabels, label, panelElements, wbEntityId, statements);
	}

	public Map<String, MonolingualTextValue> getLabels(Document document) {
		Map<String, MonolingualTextValue> ret = new HashMap<String, MonolingualTextValue>();
		String label = document.title();
		String labelDe = null;
		String labelEn = null;
		if (label != null) {
			String[] labels = label.split("/");
			labelDe = labels[0];
			if (labels.length > 1) {
				labelEn = labels[1];
			}
			ret.put("de", new MonolingualTextValueImpl(labelDe, "de"));
			ret.put("en", new MonolingualTextValueImpl(labelEn, "en"));
		}
		return ret;
	}
}
