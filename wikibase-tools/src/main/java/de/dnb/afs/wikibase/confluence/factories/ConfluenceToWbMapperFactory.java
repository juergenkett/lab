package de.dnb.afs.wikibase.confluence.factories;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.WbMapper4Elements;
import de.dnb.afs.wikibase.confluence.WbMapper4Resources;
import de.dnb.afs.wikibase.confluence.WbMapping;
import de.dnb.afs.wikibase.confluence.mappings.WbMapping4LookupValue;
import de.dnb.afs.wikibase.confluence.mappings.WbMapping4RecordingMethod;
import de.dnb.afs.wikibase.confluence.mappings.WbMapping4ResourceTextValue;
import de.dnb.afs.wikibase.confluence.mappings.WbMapping4ResourceWEMI;
import de.dnb.afs.wikibase.confluence.mappings.WbMapping4Rules;
import de.dnb.afs.wikibase.confluence.mappings.WbMapping4SpecificRules;
import de.dnb.afs.wikibase.confluence.mappings.WbMapping4TextValue;

public class ConfluenceToWbMapperFactory {

	private static ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();

	public static WbMapper4Elements newWbMapper4Elements(WbEntityEditor wbEditor)
			throws MediaWikiApiErrorException, IOException {

		List<WbMapping> wbMappings = new ArrayList<WbMapping>();
		wbMappings.add(new WbMapping4LookupValue("Entität", newPropertyId("P124"),
				"STA-Klasse - " + WbMapping4LookupValue.VALUE_PLACEHOLDER, wbEditor));
//		wbMappings.add(new WbMappingTextValue("Status.*", newPropertyId("P485"), config));
		wbMappings.add(new WbMapping4RecordingMethod("Erfassungsmethode", newPropertyId("P656"), config, wbEditor));
		wbMappings.add(new WbMapping4TextValue("Link zum Toolkit", newPropertyId("P638"), config, wbEditor));
		wbMappings.add(new WbMapping4TextValue("Format.*", newPropertyId("P4"), config, wbEditor));
	
		wbMappings.add(new WbMapping4TextValue("Definition", config.pDefinition, config, wbEditor));
		wbMappings.add(new WbMapping4TextValue("Geltungsbereich/Erklärung", newPropertyId("P631"), config, wbEditor));
		wbMappings.add(new WbMapping4TextValue("Beziehungen zu anderen Elementen.*", newPropertyId("P663"), config, wbEditor));
		
		wbMappings.add(new WbMapping4TextValue("Informationsquellen", newPropertyId("P402"), config, wbEditor));

		wbMappings.add(new WbMapping4Rules("Basisregel[n]?", config.pBasicRules, config, wbEditor));
		wbMappings.add(new WbMapping4SpecificRules("Basisregel[n]? \\w.*", config.pBasicRules, config, wbEditor));

		wbMappings.add(new WbMapping4Rules("Spezialregel[n]?", newPropertyId("P386"), config, wbEditor));
		wbMappings.add(
				new WbMapping4SpecificRules("Spezialregel[n]? \\w.*", newPropertyId("P386"), config, wbEditor));

		wbMappings.add(new WbMapping4Rules("Spezifische Regel[n]?", newPropertyId("P410"), config, wbEditor));
		wbMappings.add(new WbMapping4SpecificRules("Spezifische Regel[n]? \\w.*", newPropertyId("P410"), config,
				wbEditor));

		wbMappings.add(new WbMapping4TextValue("Geltungsbereich/Erklärung", newPropertyId("P631"), config, wbEditor));
		
		wbMappings.add(new WbMapping4TextValue("normiertes Vokabular", newPropertyId("P660"), config, wbEditor));
		
		wbMappings.add(new WbMapping4TextValue("Liste der Beziehungskennzeichnungen", newPropertyId("P661"), config, wbEditor));

		WbMapper4Elements ret = new WbMapper4Elements(wbEditor, wbMappings, config);
		

		return ret;
	}
	
	public static WbMapper4Elements newWbMapper4Elements_Add(WbEntityEditor wbEditor)
			throws MediaWikiApiErrorException, IOException {

		List<WbMapping> wbMappings = new ArrayList<WbMapping>();
	
		wbMappings.add(new WbMapping4SpecificRules("Spezifische Regel[n]? \\w.*", newPropertyId("P410"), config,
				wbEditor));
		wbEditor.setIsAddOnlyMode(true);
		WbMapper4Elements ret = new WbMapper4Elements(wbEditor, wbMappings, config);
		return ret;
	}

	public static WbMapper4Resources newWbMapper4Ressources(
			WbEntityEditor wbEditor) throws MediaWikiApiErrorException, IOException {
		List<WbMapping> wbMappings = new ArrayList<WbMapping>();
		WbMapper4Resources ret = new WbMapper4Resources(wbEditor, wbMappings, config);
		wbMappings.add(new WbMapping4ResourceTextValue("Allgemeines", config.pGeneral, config, wbEditor)); // 
		wbMappings.add(new WbMapping4ResourceTextValue("Definition", config.pDefinition, config, wbEditor));
		wbMappings.add(new WbMapping4ResourceTextValue("Beschreibung", config.pDescription, config, wbEditor));
		wbMappings.add(new WbMapping4ResourceTextValue("Informationsquellen", config.pInformationsources, config, wbEditor));
		wbMappings.add(new WbMapping4ResourceWEMI("Manifestation", newPropertyId("P637"), config.iManifestation, config, wbEditor));
		wbMappings.add(new WbMapping4ResourceWEMI("Expression", newPropertyId("P637"), config.iExpression, config, wbEditor));
		wbMappings.add(new WbMapping4ResourceWEMI("Werk", newPropertyId("P637"), config.iWork, config, wbEditor));
		wbMappings.add(new WbMapping4ResourceTextValue("Beschreibung \\(nachgestellt\\)", config.pDescription_appended, config, wbEditor)); 

		wbMappings.add(new WbMapping4ResourceTextValue("Beziehungen Akteure", config.pRelationsActors, config, wbEditor));
		wbMappings.add(new WbMapping4ResourceTextValue("Beziehungen zu .*", config.pRelationsOtherResources, config, wbEditor));
		return ret;
	}

	private static PropertyIdValue newPropertyId(String wbId) {
		return new PropertyIdValueImpl(wbId, config.wbIri);
	}

}
