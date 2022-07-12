package de.dnb.afs.wikibase.confluence.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
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
		wbMappings.add(new WbMappingLookupValue("Erfassungsmethode.*", newPropertyId("P126"),
				WbMappingLookupValue.VALUE_PLACEHOLDER, wbEditor));
		wbMappings.add(new WbMappingTextValue("Status.*", newPropertyId("P485"), config));
		wbMappings.add(new WbMappingTextValue("Kodierung.*", newPropertyId("P4"), config));
		wbMappings.add(new WbMappingTextValue("Definition.*", newPropertyId("P1"), config));
		wbMappings.add(new WbMappingLookupValue("Beziehungen zu anderen Elementen Über.*", newPropertyId("P401"),
				WbMappingLookupValue.VALUE_PLACEHOLDER, wbEditor));
		wbMappings.add(new WbMappingLookupValue("Beziehungen zu anderen Elementen Unter.*", newPropertyId("P113"),
				WbMappingLookupValue.VALUE_PLACEHOLDER, wbEditor));
		wbMappings.add(new WbMappingTextValue("Informationsquellen.*", newPropertyId("P402"), config));
		wbMappings.add(new WbMappingRule("Basisregeln.*", newPropertyId("P388"), config, wbEditor));
//		wbMappings.add(new WbMappingRule("Spezialregeln.*", newPropertyId("P386"), config, wbEditor));
//		wbMappings.add(new WbMappingRule("Spezifische Regeln.*", newPropertyId("P410"), config, wbEditor));

		ItemDocument itemDocument = ItemDocumentBuilder.forItemId(ItemIdValue.NULL).build();

		List<Statement> initialStatments = Arrays.asList(
				StatementBuilder.forSubjectAndProperty(itemDocument.getEntityId(), config.schema)
						.withValue(config.rdaDocumentation).build(),
				StatementBuilder.forSubjectAndProperty(itemDocument.getEntityId(), config.elementOf)
						.withValue(config.rdaProperty).build());

		ConfluenceToWbMapper ret = new ConfluenceToWbMapper(wbEditor, wbMappings, initialStatments);
		return ret;
	}

	private static PropertyIdValue newPropertyId(String wbId) {
		return new PropertyIdValueImpl(wbId, config.wbIri);
	}

}
