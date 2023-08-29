package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.Utils4Elements;

public class WbMapping4ResourceWEMI extends AbstractWbMapping {

	private static final Log logger = LogFactory.getLog(WbMapping4ResourceWEMI.class);

	private ItemIdValue wemiLevel;

	public WbMapping4ResourceWEMI(String labelPattern, PropertyIdValue wbId, ItemIdValue wemiLevel,
			ConfluenceWbConfig config, WbEntityLoader entityLoader) {
		super(labelPattern, wbId, config, entityLoader);
		this.wemiLevel = wemiLevel;
	}

	@Override
	public void doMap(String panelLabelDe, Elements panelElements, WbEntityProperties newEntity) {
		/*
		 * panel content auf value-Bereich zuschneiden
		 */
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}

		try {
			for (Element element : panelElements) {
				if (element.hasAttr("label")) {
					String label = element.attr("label");
					EntityDocument elementEntity = this.entityLoader.lookupProperty(label);
					if (elementEntity == null) {
						logger.warn("Überspringe Referenz auf RDA-Element " + element.attr("label")
								+ ", da dieses in WB nicht gefunden wurde");
						continue;
					}
					StatementBuilder statement = StatementBuilder
							.forSubjectAndProperty(newEntity.getEntityId(), this.wbId)
							.withValue(elementEntity.getEntityId());
					statement.withQualifierValue(config.pWEMI_Layer, wemiLevel);
					boolean isAfterEmbed = false;
					for (Element subelement : element.children()) {
						if (subelement.tagName().equals("embed")) {
							isAfterEmbed = true;
							ItemIdValue id = getItemIdValue(subelement.text());
							if (id != null && !id.getId().isEmpty()) {
								statement.withQualifierValue(this.config.pEmbeddedItem, id);
								logger.debug("RDA-Regel '" + id + "' wird als Referenz eingefügt.");
							} else {
								logger.warn("RDA-Regel '" + subelement.text()
										+ "' konnte nicht gefunden werden. Sie wird nur als Text eingefügt.");
								statement.withQualifierValue(this.config.pDescription,
										Datamodel.makeStringValue(subelement.text()));
							}
						} else {
							logger.debug("Füge Inhalt als HTML ein ...");
							StringValue value = Utils4Elements.normalizeHtmlContent(subelement.html());
							if (value == null) {
								continue;
							}
							if (isAfterEmbed) {
								statement.withQualifierValue(this.config.pDescription_appended, value);
							} else {
								statement.withQualifierValue(this.config.pDescription, value);
							}
						}
					}
					newEntity.getStatements().add(statement.build());
				}

			}
		} catch (MediaWikiApiErrorException | IOException e) {
			logger.warn(e);
		}

//		String value = panelElements.first().text();
//		String query = lookupPattern.replace(VALUE_PLACEHOLDER, value);
//		logger.debug("Suche Entität mit Query: '" + query + "'");
//		try {
//			EntityDocument refEntity = wbEntityLoader.lookupItem(query);
//			if (refEntity != null) {
//				Statement statement = StatementBuilder.forSubjectAndProperty(newEntity.getEntityId(), wbId)
//						.withValue(refEntity.getEntityId()).build();
//				logger.debug("Füge statement für label " + panelLabelDe + " hinzu: " + statement);
//				newEntity.getStatements().add(statement);
//			}
//		} catch (MediaWikiApiErrorException e) {
//			logger.warn(e);
//		} catch (IOException e) {
//			logger.warn(e);
//		}
	}

	public ItemIdValue getItemIdValue(String text) throws MediaWikiApiErrorException, IOException {
		String qid;
		if (text.matches("Q[0-9]+")) {
			qid = text.trim();
		} else {
			String query = text;
			logger.debug("looking up " + query);
			qid = this.entityLoader.lookupQID(query);
		}
		if (qid != null) {
			return Datamodel.makeItemIdValue(qid, config.wbIri);
		}
		return null;
	}
}
