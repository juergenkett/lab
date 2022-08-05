package de.dnb.afs.wikibase.confluence.mappings;

import junit.framework.TestCase;

public class WbMappingRuleTest extends TestCase  {
	
	public void testGetShortLabel() {
		String shortLabel = WbMappingRule.getShortLabel("Basisregeln Verschiedene Formen desselben Namens");
		assertEquals(shortLabel, "Verschiedene Formen desselben Namens");
	
		shortLabel = WbMappingRule.getShortLabel("Basisregel Verschiedene Regeln desselben Namens");
		assertEquals(shortLabel, "Verschiedene Regeln desselben Namens");
		
		shortLabel = WbMappingRule.getShortLabel("Spezialregel Verschiedene Regeln desselben Namens");
		assertEquals(shortLabel, "Verschiedene Regeln desselben Namens");
	}
	
	public void testIsHierarchicalRule() {
		assertTrue(WbMappingRule.isHierachicalRule("Basisregeln Verschiedene Formen desselben Namens"));
		assertFalse(WbMappingRule.isHierachicalRule("Verschiedene Regeln desselben Namens"));
		assertTrue(WbMappingRule.isHierachicalRule("Spezialregel Verschiedene Regeln desselben Namens"));
		assertFalse(WbMappingRule.isHierachicalRule("Spezialregeln"));
	
		
	}
	


}
