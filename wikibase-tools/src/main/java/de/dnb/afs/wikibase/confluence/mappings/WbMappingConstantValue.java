package de.dnb.afs.wikibase.confluence.mappings;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

import de.dnb.afs.wikibase.confluence.WbMapping;

public class WbMappingConstantValue extends WbMapping {

	private static final Log logger = LogFactory.getLog(WbMappingConstantValue.class);

	private PropertyIdValue wbId;

	private PropertyIdValue wbValueId;

	public WbMappingConstantValue(String labelPattern, PropertyIdValue wbId, PropertyIdValue wbValueId) {
		super(labelPattern);
		this.wbId = wbId;
		this.wbValueId = wbValueId;
	}

	@Override
	public void doMap(Map<String, MonolingualTextValue> entityLabels, String label, Elements panelElements, EntityIdValue wbEntityId, List<Statement> statements) {
		/*
		 * panel content auf value-Bereich zuschneiden
		 */
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}
		logger.debug("füge neues statement hinzu für panel '" + label + "'");
		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, wbId).withValue(wbValueId).build());

	}

}
