package de.dnb.afs.wikibase.confluence;

import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;

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

	public Value wbOlLi = newItemId("Q1345");

	public Value wbUlLi = newItemId("Q1344");

	public PropertyIdValue wbLayoutTyp = newPropertyId("P389");

	public Value wbH5 = newItemId("Q1346");

	public Value wbH4 = newItemId("Q1343");

	public PropertyIdValue wbEmbedded = newPropertyId("P396");

	public PropertyIdValue schema = newPropertyId("P110");

	public PropertyIdValue elementOf = newPropertyId("P2");

	public PropertyIdValue entityType = newPropertyId("P124");

	public Value rdaDocumentation = newItemId("Q263");

	public Value staDocumentation = newItemId("Q3113");

	public Value rdaProperty = newItemId("Q264");
	
	public Value example = newItemId("Q3399");


	private static PropertyIdValue newPropertyId(String id) {
		return new PropertyIdValueImpl(id, GND_DOKU_SITE_IRI);
	}

	private static ItemIdValue newItemId(String id) {
		return new ItemIdValueImpl(id, GND_DOKU_SITE_IRI);
	}

}
