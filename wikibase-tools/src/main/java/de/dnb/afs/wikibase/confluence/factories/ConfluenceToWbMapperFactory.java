package de.dnb.afs.wikibase.confluence.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.confluence.ConfluenceToWbMapper;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.WbMapping;
import de.dnb.afs.wikibase.confluence.mappings.WbMappingLookupValue;
import de.dnb.afs.wikibase.confluence.mappings.WbMappingRule;
import de.dnb.afs.wikibase.confluence.mappings.WbMappingTextValue;

public class ConfluenceToWbMapperFactory {

	private static ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();

	public static ConfluenceToWbMapper newConfluenceToWbMapper(WbEntityEditor wbEditor)
			throws MediaWikiApiErrorException, IOException {

		List<WbMapping> wbMappings = new ArrayList<WbMapping>();
		wbMappings.add(new WbMappingLookupValue("Entität.*", newPropertyId("P124"),
				"STA-Klasse - " + WbMappingLookupValue.VALUE_PLACEHOLDER, wbEditor));
//		wbMappings.add(new WbMappingTextValue("Status.*", newPropertyId("P485"), config));
		wbMappings.add(new WbMappingLookupValue("Erfassungsmethode.*", newPropertyId("P126"),
				WbMappingLookupValue.VALUE_PLACEHOLDER, wbEditor));
		wbMappings.add(new WbMappingTextValue("Link zum Toolkit.*", newPropertyId("P638"), config));
		wbMappings.add(new WbMappingTextValue("Format.*", newPropertyId("P4"), config));
		wbMappings.add(new WbMappingTextValue("Definition.*", newPropertyId("P1"), config));
		wbMappings.add(new WbMappingTextValue("Geltungsbereich/Erklärung.*", newPropertyId("P631"), config));
		wbMappings.add(new WbMappingLookupValue("Beziehungen zu anderen Elementen Über.*", newPropertyId("P401"),
				WbMappingLookupValue.VALUE_PLACEHOLDER, wbEditor));
		wbMappings.add(new WbMappingLookupValue("Beziehungen zu anderen Elementen Unter.*", newPropertyId("P113"),
				WbMappingLookupValue.VALUE_PLACEHOLDER, wbEditor));
		wbMappings.add(new WbMappingTextValue("Informationsquellen.*", newPropertyId("P402"), config));
		wbMappings.add(new WbMappingRule("Basisregeln.*", newPropertyId("P388"), config, wbEditor));
//		wbMappings.add(new WbMappingRule("Spezialregeln.*", newPropertyId("P386"), config, wbEditor));
//		wbMappings.add(new WbMappingRule("Spezifische Regeln.*", newPropertyId("P410"), config, wbEditor));
		wbMappings.add(new WbMappingTextValue("Geltungsbereich/Erklärung.*", newPropertyId("P631"), config));



		ConfluenceToWbMapper ret = new ConfluenceToWbMapper(wbEditor, wbMappings, config);
		return ret;
	}

	private static PropertyIdValue newPropertyId(String wbId) {
		return new PropertyIdValueImpl(wbId, config.wbIri);
	}

}
