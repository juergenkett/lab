package de.dnb.afs.wikibase.confluence.mappings;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

import de.dnb.afs.wikibase.WbEntityProperties;

public class Rule {
	
	public String label;

	public String elementLabel;

	PropertyIdValue wbSourcePropertyId;

	ItemIdValue wbEntityId;

	Rule topRule;

	String standardLabel;

	String shortLabel;

	Elements elements = new Elements();

	List<Rule> subrules = new ArrayList<Rule>();

	public Rule(String label, WbEntityProperties entityProps) {
		this.label = label;
		this.elementLabel = entityProps.getLabels().get("de").getText();
		this.shortLabel = WbMapping4RulesWithSubrules.getShortLabel(label); // Das Short-Label wird in Referenzen verwendet, um die
															// Überschriften weniger redundant zu gestalten
		this.standardLabel = "STA-Regel: " + elementLabel + " - " + label;
		this.wbSourcePropertyId = (PropertyIdValue) entityProps.getEntityId();
	}
	
	public Rule (String label, Rule surroundingRule) {
		this.label = label;
		this.elementLabel = surroundingRule.elementLabel;
		this.standardLabel = surroundingRule.standardLabel + " " + label;
		this.shortLabel =  WbMapping4RulesWithSubrules.getShortLabel(label);
		this.wbSourcePropertyId = (PropertyIdValue) surroundingRule.wbSourcePropertyId;
	}


	@Override
	public String toString() {
		return "{rule.wbEntityId: " + wbEntityId + ", rule.shortLabel:" + shortLabel + ", rule.elements.size:"
				+ elements.size() + ", rule.subrules:" + subrules + "}";
	}
}
