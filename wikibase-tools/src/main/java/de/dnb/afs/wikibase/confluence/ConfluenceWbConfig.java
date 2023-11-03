package de.dnb.afs.wikibase.confluence;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

/**
 * @author kett
 *
 */
public class ConfluenceWbConfig {

	public final static String GND_DOKU_SITE_IRI = "doku.wikibase.wiki/";
	public final static String GND_DOKU_WIKIBASE_URL = "https://doku.wikibase.wiki/w/api.php";
	public final static String LOCALHOST_WIKIBASE_URL = "http://localhost:8080/w/api.php";
	public final static String CONFLUENCE_URL = "https://wiki.dnb.de";

	public int maxEntryLength = 5000;

	public String wbIri = GND_DOKU_SITE_IRI;

	public String sourceWbUrl = GND_DOKU_WIKIBASE_URL;

	public String targetWbUrl = GND_DOKU_WIKIBASE_URL;

	public String confluenceUrl = CONFLUENCE_URL;
	
	public PropertyIdValue pEmbeddedItem = newPropertyId("P396");

	public PropertyIdValue pEmbeddedInProperty = newPropertyId("P397");

	public PropertyIdValue pEmbeddedInItem = newPropertyId("P398");

	public PropertyIdValue pSchema = newPropertyId("P110");

	public PropertyIdValue pElementOf = newPropertyId("P2");
	
	public PropertyIdValue pDefinition = newPropertyId("P1");

	public PropertyIdValue pDescription = newPropertyId("P7");
	
	public PropertyIdValue pDescription_appended = newPropertyId("P642"); 

	public PropertyIdValue pBasicRules = newPropertyId("P388");

	public PropertyIdValue pEntityType = newPropertyId("P124");

	public PropertyIdValue pLayoutTyp = newPropertyId("P389");

	public PropertyIdValue pStaNotation = newPropertyId("P643");
	
	public PropertyIdValue pRecordingMethodItem = newPropertyId("P126");
	
	public PropertyIdValue pWEMI_Layer = newPropertyId("P639");
	
	public PropertyIdValue pGeneral = newPropertyId("P659");
	
	public PropertyIdValue pRelationsActors = newPropertyId("P657");

	public PropertyIdValue pRelationsOtherResources = newPropertyId("P658");
	
	public ItemIdValue iRdaDocumentation = newItemId("Q263");

	public ItemIdValue iStaDocumentation = newItemId("Q3113");

	public ItemIdValue iRdaProperty = newItemId("Q264");
	
	public ItemIdValue iRdaRessourceType = newItemId("Q308");

	public ItemIdValue iExample = newItemId("Q3399");

	public ItemIdValue iOlLi = newItemId("Q1345");

	public ItemIdValue iUlLi = newItemId("Q1344");

	public ItemIdValue iHeader_Level2 = newItemId("Q1346");

	public ItemIdValue iHeader_Level1 = newItemId("Q1343");
	
	public ItemIdValue iRule = newItemId("Q3095");

	public ItemIdValue iSTAClassWork = newItemId("Q312");

	public ItemIdValue iSTAClassPerson = newItemId("Q1930");

	public ItemIdValue iSTAClassCorperateBody = newItemId("Q1933");

	public ItemIdValue iSTAClassFamiliy = newItemId("Q1934");

	public ItemIdValue iUnstructeredDescription = newItemId("Q301");

	public ItemIdValue iIdentifier = newItemId("Q1968");

	public ItemIdValue iStructuredDescription = newItemId("Q2039");
	
	public ItemIdValue iManifestation = newItemId("Q1942");

	public ItemIdValue iExpression = newItemId("Q1941");

	public ItemIdValue iWork= newItemId("Q312");
	
	public PropertyIdValue pInformationsources = newPropertyId("P402");
		
	public ItemIdValue iHeader_Level3 = newItemId("Q1347");
	
	
	private static PropertyIdValue newPropertyId(String id) {
		return Datamodel.makePropertyIdValue(id, GND_DOKU_SITE_IRI);
	}

	private static ItemIdValue newItemId(String id) {
		return Datamodel.makeItemIdValue(id, GND_DOKU_SITE_IRI);
	}

}
