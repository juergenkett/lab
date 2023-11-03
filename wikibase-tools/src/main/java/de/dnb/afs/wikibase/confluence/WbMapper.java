package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityProperties;

/**
 * @author kett
 *
 */
public abstract class WbMapper {

	private static final Log logger = LogFactory.getLog(WbMapper.class);

	protected List<WbMapping> wbMappings;

	protected WbEntityEditor wbEditor;

	protected ConfluenceWbConfig config;

	public WbMapper(WbEntityEditor wbEditor, List<WbMapping> wbMappings,
			ConfluenceWbConfig config) throws MediaWikiApiErrorException, IOException {
		this.wbMappings = wbMappings;
		this.wbEditor = wbEditor;
		this.config = config;
	}

	protected abstract void addInitialStatements(WbEntityProperties entityProperties);

	public WbMapping lookupMapping(String panelText) {
		if (panelText != null) {
			for (WbMapping wbMapping : wbMappings) {
				if (panelText.matches(wbMapping.getLabelPattern())) {
					return wbMapping;
				}
			}
		}
		return null;
	}

	public TermedStatementDocument getEntity(String wbEntityId) throws MediaWikiApiErrorException, IOException {
		return (TermedStatementDocument) wbEditor.getEntity(wbEntityId);
	}

	public EntityDocument map(Document document, String wbEntityId) throws MediaWikiApiErrorException, IOException {

		TermedStatementDocument oldVersion = getEntity(wbEntityId);

		if (oldVersion == null) {
			logger.warn("entity mit Id " + wbEntityId + " nicht gefunden. Überspringe Mapping.");
			return null;
		}
		logger.debug("entity mit Id " + wbEntityId + " gefunden. Revision Id: " + oldVersion.getRevisionId());

		WbEntityProperties entity = new WbEntityProperties();
		entity.setEntityId(oldVersion.getEntityId());
		entity.setRevisionId(oldVersion.getRevisionId());
		entity.setLabels(oldVersion.getLabels());
//		entity.setDescriptions(oldVersion.getDescriptions());
//		addAliases(document, entity);
//		addInitialStatements(entity);
//		Statement staCode = oldVersion.findStatement(config.pStaNotation);
//		if (staCode != null) {
//			entity.getStatements().add(staCode);
//		}
		Elements properties = document.getElementsByTag("property");
		for (Element property : properties) {
			addPanelStatements(property, entity);
		}
		logger.debug("Item wird aktualisiert ...");
		wbEditor.updateEntity(entity);
		return null;

	}


//	public void addStaCode(TermedStatementDocument oldVersion, WbEntityProperties entity)
//			throws MediaWikiApiErrorException, IOException {
//		StringValue staCode = oldVersion.findStatementStringValue(config.pStaCode);
//		if (staCode == null) {
//			entity.setStaCode(staIdGenerator.generateId(entity));
//		} else {
//			entity.setStaCode(staCode.getString());
//		}
//		entity.getStatements().add(StatementBuilder.forSubjectAndProperty(entity.getEntityId(), config.pStaCode)
//				.withValue(Datamodel.makeStringValue(entity.getStaCode())).build());
//	}

	public void addAliases(Document document, WbEntityProperties entity) {
		String confluencePageId = document.getElementById("confluence-page-id").attr("content");
		if (confluencePageId != null) {
			entity.getAliases().add(Datamodel.makeMonolingualTextValue("confluence:" + confluencePageId, "de"));

		}
//		Utils.addNormalizedLabel(entity);
	}

	public void addPanelStatements(Element panel, WbEntityProperties entity)
			throws MediaWikiApiErrorException, IOException {

		if (!panel.hasAttr("label")) {
			logger.debug("Überspringe panel, da es kein label hat");
			return;
		}

		/*
		 * label des panels einlesen
		 */
		String label = panel.attr("label");

		if (panel.children().size() < 1) {
			logger.debug("Überspringe panel '" + label + "', da es keinen Inhaltsbereich hat");
			return;
		}

		/*
		 * Content des Panels (2. Kind des Panels: <div class="panelContent"/>).
		 */
		Elements panelElements = panel.children();

		/*
		 * Mapping suchen
		 */
		WbMapping wbMapping = lookupMapping(label);
		if (wbMapping == null) {
			wbMapping = lookupMapping(panel.text());
		}
		if (wbMapping == null) {
			logger.debug("Überspringe panel, da kein passendes mapping registriert ist: " + label);
			return;
		}

		logger.debug("Starte Mapping für panel : " + label + " mit " + wbMapping);
		wbMapping.doMap(label, panelElements, entity);
	}
}
