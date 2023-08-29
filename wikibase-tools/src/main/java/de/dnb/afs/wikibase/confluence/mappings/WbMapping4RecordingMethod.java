package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.Utils4Elements;

public class WbMapping4RecordingMethod extends AbstractWbMapping {

	public WbMapping4RecordingMethod(String labelPattern, PropertyIdValue wbId, ConfluenceWbConfig config,
			WbEntityLoader entityLoader) {
		super(labelPattern, wbId, config, entityLoader);
	}

	
	/**
	 * Erfassungsmethode (P656)
	 * Umsetzung: 
	 *     als Zeichenkette
	 *     Qualifier: 
	 *     wenn "Identifikator" Qualifier: Erfassungsmethode | Item → Identifikator
	 *     wenn "unstrukturierte Beschreibung: Qualifier: Erfassungsmethode | Item → unstrukturierte Beschreibung
	 *     wenn "strukturierte Beschreibung": Qualifier: Erfassungsmethode | Item → Strukturierte Beschreibung: normiertes Vokabular
	 *     		Qualifier: zulässige Werte (P8) ...
	 */
	@Override
	public void doMap(String panelLabelDe, Elements paragraphs, WbEntityProperties entity)
			throws MediaWikiApiErrorException, IOException {
		/*
		 * panel content auf value-Bereich zuschneiden
		 */
		if (paragraphs == null || paragraphs.size() < 1) {
			return;
		}
		for (Element paragraph : paragraphs) {
			String paragraphAsText = paragraph.text().trim().toLowerCase();
			ItemIdValue qualifier = null;
			if (paragraphAsText.startsWith("unstrukturierte beschreibung")) {
				qualifier = config.iUnstructeredDescription;
			} else if (paragraphAsText.startsWith("identifikator")) {
				qualifier = config.iIdentifier;
			} else if (paragraphAsText.startsWith("strukturierte beschreibung")) {
				qualifier = config.iStructuredDescription;
			}
			if (qualifier != null) {
				addKeyword(entity.getStatements(), entity.getEntityId(), paragraph, wbId, config, qualifier);
			} else {
				Utils4Elements.addPanelElement(entity, panelLabelDe, entity.getEntityId(), paragraph, wbId, entityLoader,
						config);
			}

		}

	}

	public static void addKeyword(List<Statement> statements, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config, ItemIdValue qualifierValue) {
		StringValue value = Utils4Elements.normalizeHtmlContent(panelParagraph.html());
		if (value != null) {
			statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
					.withQualifierValue(config.pRecordingMethodItem, qualifierValue).build());
		}
	}

}
