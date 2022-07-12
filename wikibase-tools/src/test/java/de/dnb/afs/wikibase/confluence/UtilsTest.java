package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityLoaderImpl;
import junit.framework.TestCase;

public class UtilsTest extends TestCase {
	
	private static final Log logger = LogFactory.getLog(UtilsTest.class);

	
	public void testAddWbIdsToRefs() throws MediaWikiApiErrorException, IOException {
		ApiConnection prodDokuApi = new BasicApiConnection("https://doku.wikibase.wiki/w/api.php");

		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(prodDokuApi, ConfluenceWbConfig.GND_DOKU_SITE_IRI);
		WbEntityLoader entityLoader = new WbEntityLoaderImpl(wbdf);
				
		Elements elements = new Elements();
		Element e1 = new Element("a").attr("class", "ref").text("UNKNOWNENTITY_12233453562346456");
		Element e2 = new Element("a").attr("class", "ref").text("Bevorzugter Name einer Person");
		Element e3 = new Element("a").attr("class", "ref");
		elements.addAll(Arrays.asList(e1, e2, e3));
		
		Utils.addWbIdsToRefs(elements, entityLoader);

		logger.debug(elements);
		
		assertFalse(e1.hasAttr("href"));
		assertTrue(e2.hasAttr("href"));
		assertFalse(e3.hasAttr("href"));
	}

}
