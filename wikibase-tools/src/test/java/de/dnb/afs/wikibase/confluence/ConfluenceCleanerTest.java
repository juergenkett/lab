package de.dnb.afs.wikibase.confluence;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;

import de.dnb.afs.wikibase.confluence.factories.ConfluenceWbConfigFactory;
import de.dnb.sta.StaProps;
import de.dnb.sta.apps.PropsHelper;
import junit.framework.TestCase;

public class ConfluenceCleanerTest extends TestCase {

	private static final Log logger = LogFactory.getLog(ConfluenceToWbMapperTest.class);
	
	public void testClean() throws FileNotFoundException, IOException {
		final StaProps wbProps = PropsHelper.getProps();
//		final String confluenceId = "217541956";
//		final String confluenceId = "206373188";
		final String confluenceId = "289341673";
		// https://wiki.dnb.de/rest/api/content/289341673?expand=body.storage
		ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();
		Document document = new ConfluencePageLoader(config.confluenceUrl, wbProps.confluenceUser, wbProps.confluencePass)
		.loadDocument(confluenceId);
		logger.info(document.title());
		
		document = new ConfluenceCleaner().clean(document);
		logger.info(document);
	}
	
	
	public void testGetLabel() {
		String in = "AP 2.1 | Abspielgeschwindigkeit/playing speed";
		String out = ConfluenceCleaner.getLabel(in);
		assertEquals("Abspielgeschwindigkeit/playing speed", out);
		
		in = "AP 2.1 | PFKG - P - Bevorzugter Name einer Person/preferred name of person";
		out = ConfluenceCleaner.getLabel(in);
		assertEquals("Bevorzugter Name einer Person/preferred name of person", out);

		in = "AP 2.1 | PFKG - K - Zählung einer Konferenz/number of conference";
		out = ConfluenceCleaner.getLabel(in);
		assertEquals("Zählung einer Konferenz/number of conference", out);
	}
	
	public void testGetImgName() {
		String imgPath = ConfluenceCleaner.getImgPath("https://wiki.dnb.de/download/attachments/238302713/image2022-7-11_9-32-22.png?version=1&amp;modificationDate=1657524743000&amp;api=v2");
		assertEquals("/image2022-7-11_9-32-22.png", imgPath);
	}
}
