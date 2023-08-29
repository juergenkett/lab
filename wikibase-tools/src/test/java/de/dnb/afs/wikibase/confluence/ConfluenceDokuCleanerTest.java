package de.dnb.afs.wikibase.confluence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class ConfluenceDokuCleanerTest extends TestCase {
	
	private static final Log logger = LogFactory.getLog(ConfluenceDokuCleanerTest.class);
	
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
