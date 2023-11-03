package de.dnb.afs.wikibase.confluence.mappings;

import junit.framework.TestCase;

public class WbMapping4SpecificRulesTest extends TestCase {

	public void testGetRuleLabel() {
		String test = WbMapping4SpecificRules.getRuleLabel("Spezialregeln für leckere Bananen");
		assertEquals("Leckere Bananen", test);
		
		test = WbMapping4SpecificRules.getRuleLabel("Basisregeln");
		assertEquals("Basisregeln", test);
		
		test = WbMapping4SpecificRules.getRuleLabel("Basisregeln Bananen");
		assertEquals("Bananen", test);
		
		test = WbMapping4SpecificRules.getRuleLabel("Basisregeln für Bananen");
		assertEquals("Bananen", test);
		
		test = WbMapping4SpecificRules.getRuleLabel("Basisregeln dafür");
		assertEquals("Dafür", test);
		
		test = WbMapping4SpecificRules.getRuleLabel("Spezifische Regel Bananen");
		assertEquals("Bananen", test);
		
		test = "Formatasdad";
		assertTrue (test.matches("Format.*"));
		
		test =  WbMapping4SpecificRules.getRuleLabel("Spezifische Regeln für integrierende Ressourcen");
		assertEquals("Integrierende Ressourcen", test);
		


	}
	
}
