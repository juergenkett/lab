package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.Utils;
import de.dnb.afs.wikibase.confluence.WbMapping;

public class WbMappingRule extends WbMapping {

	private static final Log logger = LogFactory.getLog(WbMappingRule.class);

	private ConfluenceWbConfig config;

	private WbEntityEditor wbEntityEditor;

	private PropertyIdValue mainPropertyId;

	private int ruleIndex = 0;

	public WbMappingRule(String labelPattern, PropertyIdValue mainPropertyId, ConfluenceWbConfig config,
			WbEntityEditor wbEntityEditor) {
		super(labelPattern);
		this.config = config;
		this.wbEntityEditor = wbEntityEditor;
		this.mainPropertyId = mainPropertyId;
	}

	public static String getShortLabel(String label) {
		return label.replaceFirst("\\w+regel[n]?? ", "");
	}

	@Override
	public void doMap(String panelLabelDe, Elements panelElements, WbEntityProperties entityProps)
			throws MediaWikiApiErrorException, IOException {
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}

		Rule rule = new Rule(panelLabelDe, entityProps);
		Rule subrule = null;
		/*
		 * In manchen Panels ist mehr als eine Regel enthalten. Die jeweiligen Regeln
		 * werden dann von einer Überschrift h4 eingeleitet
		 */
		for (Element e : panelElements) {
			if (e.tagName().equals("h4")) {
				if (isHierachicalRule(panelLabelDe)) {
					/*
					 * Sammle die Regel als Unterregel im übergeordneten Regel-Item
					 */
					subrule = new Rule(e.text(), rule);
					subrule.topRule = rule;
					rule.subrules.add(subrule);
				} else {
					/*
					 * Ergänze die Regel direkt als Referenz in der Elementbeschreibung
					 */
					logger.debug("Mappe rule: " + rule);
					mapRule(rule, entityProps);
					rule = new Rule(e.text(), entityProps);
				}
			} else if (subrule != null) {
				subrule.elements.add(e);
			} else {
				rule.elements.add(e);
			}
		}
		logger.debug("Mappe rule: " + rule);
		mapRule(rule, entityProps);
	}


	public static boolean isHierachicalRule(String panelLabelDe) {
		return panelLabelDe.matches("\\w+regel[n]? .*\\w");
	}

	private void mapRule(Rule rule, WbEntityProperties wbEntityProps) throws MediaWikiApiErrorException, IOException {
		if (rule.elements.isEmpty() && rule.subrules.isEmpty()) {
			return;
		}
		WbEntityProperties ruleProps = createRuleItem(rule);
		if (ruleProps == null) {
			return;
		}
		/*
		 * Ergänze Unterregeln als Referenz
		 */
		if (!rule.subrules.isEmpty()) {
			int index = 0;
			for (Rule subrule : rule.subrules) {
				index = index + 1;
				addSubruleToRule(subrule, rule, index);
			}
		}

		/*
		 * Ergänze in der Quell-Entität eine Referenz auf das Rule-Item
		 */
		logger.debug("Ergänze in der Quell-Entität " + wbEntityProps.getEntityId() + " eine Referenz auf das Rule-Item"
				+ rule.wbEntityId);
		Validate.ensureNotNull(rule.wbSourcePropertyId);
		Statement refStatement = StatementBuilder.forSubjectAndProperty(rule.wbSourcePropertyId, this.mainPropertyId)
				.withValue(Datamodel.makeStringValue(rule.shortLabel))
				.withQualifierValue(config.pEmbeddedItem, rule.wbEntityId)
				.withQualifierValue(config.pLayoutTyp, config.iH4).build();
		wbEntityProps.getStatements().add(refStatement);
	}

	public void addSubruleToRule(Rule subrule, Rule rule, int index) throws MediaWikiApiErrorException, IOException {
		logger.debug("ergänze Unterregel ");
		subrule.topRule = rule;
		WbEntityProperties subruleProps = createRuleItem(subrule);
		logger.debug("{subruleProps.entityId:" + subruleProps.getEntityId() + ", subruleProps.statements.size:"
				+ subruleProps.getStatements().size() + "}");
		WbEntityProperties refProps = new WbEntityProperties();
		refProps.setEntityId(rule.wbEntityId);

		Validate.ensureNotNull(rule.wbEntityId);
		Statement refStatement = StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pDefintion)
				.withValue(Datamodel.makeStringValue(subrule.shortLabel))
				.withQualifierValue(config.pEmbeddedItem, subrule.wbEntityId)
				.withQualifierValue(config.pLayoutTyp, config.iH4).build();
		refProps.getStatements().add(refStatement);
		wbEntityEditor.updateEntity(refProps, false);
	}

	public WbEntityProperties createRuleItem(Rule rule) throws MediaWikiApiErrorException, IOException {
		Validate.ensureNotNull(rule.wbSourcePropertyId);
		Validate.isFalse(rule.wbSourcePropertyId.isPlaceholder(),
				"Die Entity-Id, auf die die Regel referenzieren soll, darf kein Platzhalter sein.");
		ruleIndex = ruleIndex + 1;
		rule.wbEntityId = ItemIdValue.NULL;
		ItemDocument oldVersion = null;

		oldVersion = (ItemDocument) this.wbEntityEditor.lookupEntity(rule.standardLabel);

		if (oldVersion != null) {
			logger.debug("Alte Version des Regel-Item mit Id " + oldVersion.getEntityId().getId()
					+ " mit identischem Label gefunden. Ersetze diese ...");
			rule.wbEntityId = oldVersion.getEntityId();
		}

		/*
		 * Füge die konstanten Statements hinzu
		 */
		WbEntityProperties ruleProps = new WbEntityProperties();
		String staCode = rule.elementStaCode + "-" + ruleIndex;
		ruleProps.setStaCode(staCode);
		ruleProps.getStatements().add(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pStaCode)
				.withValue(Datamodel.makeStringValue(staCode)).build());
		ruleProps.setEntityId(rule.wbEntityId);
		ruleProps.getLabels().put("de", Datamodel.makeMonolingualTextValue(rule.standardLabel, "de"));

//		ruleProps.getAliases().add(Datamodel.makeMonolingualTextValue(Utils.normalizeLabel(rule.shortLabel), "de"));
//		Utils.addNormalizedLabel(ruleProps);

		ruleProps.getStatements().add(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pSchema)
				.withValue(config.iStaDocumentation).build());
		ruleProps.getStatements().add(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pElementOf)
				.withValue(config.iRule).build());
		/*
		 * Rückverweis auf das Quell-Item
		 */
		ruleProps.getStatements()
				.add(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pEmbeddedInProperty)
						.withValue(rule.wbSourcePropertyId).build());
		/*
		 * Rückverweis auf die "Top"-Regel
		 */
		if (rule.topRule != null) {
			ruleProps.getStatements()
					.add(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pEmbeddedInItem)
							.withValue(rule.topRule.wbEntityId).build());
		}

		/*
		 * Füge Defintionen hinzu
		 */
		if (!rule.elements.isEmpty()) {
			addRuleDefinitions(rule, ruleProps);
		}

		if (ruleProps.getEntityId().isPlaceholder()) {
			/*
			 * Erzeuge neues Rule-Item
			 */
			logger.debug("erzeuge neues Regel-Item ... ");
			wbEntityEditor.createItem(ruleProps);
			rule.wbEntityId = (ItemIdValue) ruleProps.getEntityId();
		} else {
			/*
			 * Aktualisiere bestehendes Rule-Item
			 */
			logger.debug("aktualisiere Regel-Item " + ruleProps.getEntityId().getId());

			wbEntityEditor.updateEntity(ruleProps, true);
		}

		return ruleProps;
	}

	public void addRuleDefinitions(Rule rule, WbEntityProperties props) throws MediaWikiApiErrorException, IOException {
		logger.debug("Füge " + rule.elements.size() + " Definitionen hinzu ...");
		for (Element e : rule.elements) {
			String contextLabel = (rule.topRule != null) ? rule.topRule.label : null;
			Utils.addPanelElement(props, contextLabel, rule.wbEntityId, e, config.pDefintion, wbEntityEditor, config);
		}

	}

}

class Rule {

	public String label;

	public String elementLabel;

	public String elementStaCode;

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
		this.shortLabel = WbMappingRule.getShortLabel(label); // Das Short-Label wird in Referenzen verwendet, um die
															// Überschriften weniger redundant zu gestalten
		this.standardLabel = "STA-Regel: " + elementLabel + " - " + label;
		this.wbSourcePropertyId = (PropertyIdValue) entityProps.getEntityId();
		this.elementStaCode = entityProps.getStaCode();
	}
	
	public Rule (String label, Rule surroundingRule) {
		this.label = label;
		this.elementLabel = surroundingRule.elementLabel;
		this.standardLabel = surroundingRule.standardLabel + " " + label;
		this.shortLabel =  WbMappingRule.getShortLabel(label);
		this.wbSourcePropertyId = (PropertyIdValue) surroundingRule.wbSourcePropertyId;
		this.elementStaCode = surroundingRule.elementStaCode;
	}


	@Override
	public String toString() {
		return "{rule.wbEntityId: " + wbEntityId + ", rule.shortLabel:" + shortLabel + ", rule.elements.size:"
				+ elements.size() + ", rule.subrules:" + subrules + "}";
	}

}
