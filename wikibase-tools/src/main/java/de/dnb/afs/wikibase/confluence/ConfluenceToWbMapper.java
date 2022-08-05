package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.STAIdGenerator;
import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityProperties;

/**
 * @author kett
 *
 */
public class ConfluenceToWbMapper {

	private static final Log logger = LogFactory.getLog(ConfluenceToWbMapper.class);

	private List<WbMapping> wbMappings;

	private WbEntityEditor wbEditor;

	private ConfluenceWbConfig config;

	private STAIdGenerator staIdGenerator;

	public ConfluenceToWbMapper(WbEntityEditor wbEditor, STAIdGenerator staIdGenerator, List<WbMapping> wbMappings,
			ConfluenceWbConfig config) throws MediaWikiApiErrorException, IOException {
		this.wbMappings = wbMappings;
		this.wbEditor = wbEditor;
		this.config = config;
		this.staIdGenerator = staIdGenerator;

	}

	private void addInitialStatements(WbEntityProperties entityProperties) {
		entityProperties.getStatements()
				.add(StatementBuilder.forSubjectAndProperty(entityProperties.getEntityId(), config.pSchema)
						.withValue(config.iRdaDocumentation).build());
		entityProperties.getStatements()
				.add(StatementBuilder.forSubjectAndProperty(entityProperties.getEntityId(), config.pElementOf)
						.withValue(config.iRdaProperty).build());
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

	private TermedStatementDocument getEntity(String wbEntityId) throws MediaWikiApiErrorException, IOException {
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
		addLabels(document, entity);
		addAliases(document, entity);
		addInitialStatements(entity);
		addStaCode(oldVersion, entity);

		Elements panels = document.getElementsByAttributeValue("data-macro-name", "panel");
		for (Element panel : panels) {
			addPanelStatements(panel, entity);
		}
		logger.debug("Item wird aktualisiert ...");
		wbEditor.updateEntity(entity, true);
		return null;

	}

	private void addStaCode(TermedStatementDocument oldVersion, WbEntityProperties entity)
			throws MediaWikiApiErrorException, IOException {
		StringValue staCode = oldVersion.findStatementStringValue(config.pStaCode);
		if (staCode == null) {
			entity.setStaCode(staIdGenerator.generateId(entity));
		} else {
			entity.setStaCode(staCode.getString());
		}
		entity.getStatements().add(StatementBuilder.forSubjectAndProperty(entity.getEntityId(), config.pStaCode)
				.withValue(Datamodel.makeStringValue(entity.getStaCode())).build());
	}

	private void addAliases(Document document, WbEntityProperties entity) {
		String confluencePageId = document.getElementById("confluence-page-id").attr("content");
		if (confluencePageId != null) {
			entity.getAliases().add(Datamodel.makeMonolingualTextValue("confluence:" + confluencePageId, "de"));
		

		}
//		Utils.addNormalizedLabel(entity);
	}

	public void addPanelStatements(Element panel, WbEntityProperties entity)
			throws MediaWikiApiErrorException, IOException {
		String panelAsText = panel.text();

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

		/*
		 * Mapping suchen
		 */
		WbMapping wbMapping = lookupMapping(panelAsText);
		if (wbMapping == null) {
			logger.debug("Überspringe panel, da kein passendes mapping registriert ist: " + label);
			return;
		}

		logger.debug("Starte Mapping für panel : " + label + " mit " + wbMapping);
		wbMapping.doMap(label, panelElements, entity);
	}

	public void addLabels(Document document, WbEntityProperties entity) {
		String label = document.title();
		String labelDe = null;
		String labelEn = null;
		if (label != null) {
			String[] labels = label.split("/");
			labelDe = labels[0];
			if (labels.length > 1) {
				labelEn = labels[1];
			}
			entity.getLabels().put("de", Datamodel.makeMonolingualTextValue(labelDe, "de"));
			entity.getLabels().put("en", Datamodel.makeMonolingualTextValue(labelEn, "en"));
		}
	}
}
