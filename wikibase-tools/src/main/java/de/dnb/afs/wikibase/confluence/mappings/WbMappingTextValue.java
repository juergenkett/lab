package de.dnb.afs.wikibase.confluence.mappings;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

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
	public void doMap(Map<String, MonolingualTextValue> entityLabels, String label, Elements panelElements,  EntityIdValue wbEntityId,  List<Statement> statements) {
		/*
		 * panel content auf value-Bereich zuschneiden
		 */
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}
		logger.debug("füge neues statement hinzu für panel '" + label + "'");
		for (Element panelElement : panelElements) {
			Utils.addPanelElement(statements, wbEntityId, panelElement, wbId, config);
		}
	}

}
