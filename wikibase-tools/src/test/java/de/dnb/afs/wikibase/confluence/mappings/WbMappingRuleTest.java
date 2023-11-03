package de.dnb.afs.wikibase.confluence.mappings;

import junit.framework.TestCase;

public class WbMappingRuleTest extends TestCase  {
	
	public void testGetShortLabel() {
		String shortLabel = WbMapping4Rules.getShortLabel("Basisregeln Verschiedene Formen desselben Namens");
		assertEquals(shortLabel, "Verschiedene Formen desselben Namens");
	
		shortLabel = WbMapping4Rules.getShortLabel("Basisregel Verschiedene Regeln desselben Namens");
		assertEquals(shortLabel, "Verschiedene Regeln desselben Namens");
		
		shortLabel = WbMapping4Rules.getShortLabel("Spezialregel Verschiedene Regeln desselben Namens");
		assertEquals(shortLabel, "Verschiedene Regeln desselben Namens");
	}
	
//	public void testIsHierarchicalRule() {
//		assertTrue(WbMappingRule.isHierachicalRule("Basisregeln Verschiedene Formen desselben Namens"));
//		assertFalse(WbMappingRule.isHierachicalRule("Verschiedene Regeln desselben Namens"));
//		assertTrue(WbMappingRule.isHierachicalRule("Spezialregel Verschiedene Regeln desselben Namens"));
//		assertFalse(WbMappingRule.isHierachicalRule("Spezialregeln"));
//	}
}
