package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.Utils4Elements;

public class WbMapping4Description extends AbstractWbMapping {

	private static final Log logger = LogFactory.getLog(WbMapping4Description.class);

	public WbMapping4Description(String labelPattern, PropertyIdValue pId, ConfluenceWbConfig config,  WbEntityLoader entityLoader) {
		super(labelPattern, pId, config, entityLoader);
	}

	@Override
	public void doMap(String panelLabelDe, Elements panelElements, WbEntityProperties entity)
			throws MediaWikiApiErrorException, IOException {
		/*
		 * panel content auf value-Bereich zuschneiden
		 */
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}

		logger.debug("füge neues statement hinzu für panel '" + panelLabelDe + "'");
//		StringValue h4 = Datamodel.makeStringValue(panelLabelDe);
//		entity.getStatements().add(StatementBuilder.forSubjectAndProperty(entity.getEntityId(), this.wbId)
//				.withValue(h4).withQualifierValue(config.pLayoutTyp, config.iH4).build());
		for (Element panelElement : panelElements) {
			Utils4Elements.addPanelElement(entity, panelLabelDe, entity.getEntityId(), panelElement, this.wbId,
					entityLoader, config);
		}
	}

}
