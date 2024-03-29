package de.dnb.afs.wikibase.confluence.mappings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.WbMapping;

public class WbMapping4ConstantValue extends WbMapping {

	private static final Log logger = LogFactory.getLog(WbMapping4ConstantValue.class);

	private PropertyIdValue wbValueId;

	public WbMapping4ConstantValue(String labelPattern, PropertyIdValue wbId, PropertyIdValue wbValueId) {
		super(labelPattern, wbId);
		this.wbValueId = wbValueId;
	}

	@Override
	public void doMap(String panelLabelDe, Elements panelElements,
			WbEntityProperties entity) { /*
													 * panel content auf value-Bereich zuschneiden
													 */
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}
		logger.debug("füge neues statement hinzu für panel '" + panelLabelDe + "'");
		entity.getStatements()
				.add(StatementBuilder.forSubjectAndProperty(entity.getEntityId(), wbId).withValue(wbValueId).build());
	}

}
