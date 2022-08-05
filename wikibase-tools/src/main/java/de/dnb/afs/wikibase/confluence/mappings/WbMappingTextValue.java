package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.Utils;
import de.dnb.afs.wikibase.confluence.WbMapping;

public class WbMappingTextValue extends WbMapping {

	private static final Log logger = LogFactory.getLog(WbMappingTextValue.class);

	private PropertyIdValue wbId;

	private ConfluenceWbConfig config;

	private WbEntityLoader entityLoader;

	public WbMappingTextValue(String labelPattern, PropertyIdValue wbId, ConfluenceWbConfig config,
			WbEntityLoader entityLoader) {
		super(labelPattern);
		this.wbId = wbId;
		this.config = config;
		this.entityLoader = entityLoader;
	}

	@Override
	public void doMap(String panelLabelDe, Elements panelElements, WbEntityProperties entity) throws MediaWikiApiErrorException, IOException {
		/*
		 * panel content auf value-Bereich zuschneiden
		 */
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}
		logger.debug("füge neues statement hinzu für panel '" + panelLabelDe + "'");
		for (Element panelElement : panelElements) {
			Utils.addPanelElement(entity, panelLabelDe , entity.getEntityId(), panelElement, wbId, entityLoader, config);
		}
	}

}
