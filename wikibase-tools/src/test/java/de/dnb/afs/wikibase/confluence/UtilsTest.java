package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.BytesRef;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.tartarus.snowball.ext.German2Stemmer;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityLoaderImpl;
import junit.framework.TestCase;

public class UtilsTest extends TestCase {

	private static final Log logger = LogFactory.getLog(UtilsTest.class);

//	public void testAddWbIdsToRefs() throws MediaWikiApiErrorException, IOException {
//		ApiConnection prodDokuApi = new BasicApiConnection("https://doku.wikibase.wiki/w/api.php");
//
//		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(prodDokuApi, ConfluenceWbConfig.GND_DOKU_SITE_IRI);
//		WbEntityLoader entityLoader = new WbEntityLoaderImpl(wbdf);
//
//		Element e1 = new Element("a").attr("class", "ref").text("UNKNOWNENTITY_12233453562346456");
//		Element e2 = new Element("a").attr("class", "ref").text("Bevorzugter Name einer Person");
//		Element e3 = new Element("a").attr("class", "ref");
//		Element e4 = new Element("a").attr("class", "localRef").text("Bevorzugter Name einer Person");
//
//		Utils.updateRefs("", e1, entityLoader);
//		Utils.updateRefs("", e2, entityLoader);
//		Utils.updateRefs("", e3, entityLoader);
//		Utils.updateRefs("", e4, entityLoader);
//		logger.debug(e4);
//
//		assertFalse(e1.hasAttr("href"));
//		assertTrue(e2.hasAttr("href"));
//		assertFalse(e3.hasAttr("href"));
//		assertTrue(e4.hasAttr("href"));
//	}

	public void testRemoveLeadingAndTrailingBr() {
		Document document = Jsoup.parse(
				"<html><head></head><body><p><br/><br/>test</p><p>test</p><p>test<br/></p><p>test<br/>test</p></body></html>");
		Element p0 = document.body().child(0);
		Element p1 = document.body().child(1);
		Element p2 = document.body().child(2);
		Element p3 = document.body().child(3);
		Utils4Elements.removeLeadingAndTrailingBr(p0);
		Utils4Elements.removeLeadingAndTrailingBr(p1);
		Utils4Elements.removeLeadingAndTrailingBr(p2);
		Utils4Elements.removeLeadingAndTrailingBr(p3);
		assertEquals(p0.html(), "test");
		assertEquals(p1.html(), "test");
		assertEquals(p2.html(), "test");
		assertTrue(p3.getElementsByTag("br").size() == 1);

	}

	public void testStem() throws IOException {

		String input = "Bevorzugter Name einer Person";

		GermanAnalyzer analyzer = new GermanAnalyzer();
		BytesRef ref = analyzer.normalize(null, input);

//		GermanLightStemmer stemmer = new GermanLightStemmer();
		German2Stemmer stemmer = new German2Stemmer();
		stemmer.setCurrent(String.valueOf(input));
		stemmer.stem();
//		stemmer.stem(input, input.length-1);
//		logger.debug(new String(ref.bytes, StandardCharsets.UTF_8));
		String result = "";
		TokenStream stream = analyzer.tokenStream(null, new StringReader(input));
		stream.reset();
		while (stream.incrementToken()) {
			if (result.length()>0) {
				result = result + "_";
			}
			result = result + stream.getAttribute(CharTermAttribute.class).toString();
		}
		
		logger.debug(result);
	}

}
