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

	public PropertyIdValue pDefintion = newPropertyId("P7");
	
	public PropertyIdValue pBasicRules = newPropertyId("P388");
	
	public PropertyIdValue pEntityType = newPropertyId("P124");
	
	public PropertyIdValue pLayoutTyp = newPropertyId("P389");
	
	public PropertyIdValue pStaCode = newPropertyId("P641");
	
	public ItemIdValue iRdaDocumentation = newItemId("Q263");

	public ItemIdValue iStaDocumentation = newItemId("Q3113");

	public ItemIdValue iRdaProperty = newItemId("Q264");
	
	public ItemIdValue iExample = newItemId("Q3399");
	
	public ItemIdValue iOlLi = newItemId("Q1345");

	public ItemIdValue iUlLi = newItemId("Q1344");

	public ItemIdValue iH5 = newItemId("Q1346");

	public ItemIdValue iH4 = newItemId("Q1343");
	
	public ItemIdValue iRule = newItemId("Q3095");
	
	public ItemIdValue iSTAClassWork = newItemId("Q312");
	
	public ItemIdValue iSTAClassPerson = newItemId("Q1930");
	
	public ItemIdValue iSTAClassCorperateBody = newItemId("Q1933");
	
	public ItemIdValue iSTAClassFamiliy = newItemId("Q1934");
	
	
	private static PropertyIdValue newPropertyId(String id) {
		return Datamodel.makePropertyIdValue(id, GND_DOKU_SITE_IRI);
	}

	private static ItemIdValue newItemId(String id) {
		return  Datamodel.makeItemIdValue(id, GND_DOKU_SITE_IRI);
	}

}
