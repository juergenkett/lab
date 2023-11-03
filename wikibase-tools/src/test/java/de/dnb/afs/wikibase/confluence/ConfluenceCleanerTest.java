package de.dnb.afs.wikibase.confluence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestCase;

public class ConfluenceDokuCleanerTest extends TestCase {
	
	private static final Log logger = LogFactory.getLog(ConfluenceDokuCleanerTest.class);
	
	public void testGetLabel() {
		

		
		String in = "AP 2.1 | Abspielgeschwindigkeit/playing speed";
		String out = ConfluenceDokuCleaner.getLabel(in);
		assertEquals("Abspielgeschwindigkeit/playing speed", out);
		
		in = "AP 2.1 | PFKG - P - Bevorzugter Name einer Person/preferred name of person";
		out = ConfluenceDokuCleaner.getLabel(in);
		assertEquals("Bevorzugter Name einer Person/preferred name of person", out);

		in = "AP 2.1 | PFKG - K - Zählung einer Konferenz/number of conference";
		out = ConfluenceDokuCleaner.getLabel(in);
		assertEquals("Zählung einer Konferenz/number of conference", out);
	}
	
	

}
