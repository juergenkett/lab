package de.dnb.afs.wikibase.confluence.mappings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.Utils;
import de.dnb.afs.wikibase.confluence.WbMapping;

public class WbMappingTextValue extends WbMapping {

	private static final Log logger = LogFactory.getLog(WbMappingTextValue.class);

	private PropertyIdValue wbId;

	private ConfluenceWbConfig config;

	public WbMappingTextValue(String labelPattern, PropertyIdValue wbId, ConfluenceWbConfig config) {
		super(labelPattern);
		this.wbId = wbId;
		this.config = config;
	}

	@Override
	public void doMap(String panelLabelDe, Elements panelElements, WbEntityProperties entity) {
		/*
		 * panel content auf value-Bereich zuschneiden
		 */
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}
		logger.debug("füge neues statement hinzu für panel '" + panelLabelDe + "'");
		for (Element panelElement : panelElements) {
			Utils.addPanelElement(entity.getStatements(), entity.getEntityId(), panelElement, wbId, config);
		}
	}

}
