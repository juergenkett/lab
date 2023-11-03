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
import de.dnb.afs.wikibase.confluence.Utils4Elements;
import de.dnb.afs.wikibase.confluence.WbMapping;

public class WbMapping4Rules extends WbMapping {

	private static final Log logger = LogFactory.getLog(WbMapping4Rules.class);

	protected ConfluenceWbConfig config;

	protected WbEntityEditor wbEntityEditor;

	public WbMapping4Rules(String labelPattern, PropertyIdValue mainPropertyId, ConfluenceWbConfig config,
			WbEntityEditor wbEntityEditor) {
		super(labelPattern, mainPropertyId);
		this.config = config;
		this.wbEntityEditor = wbEntityEditor;
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
		List<Rule> rules = collectRules(panelElements, entityProps);
		for (Rule rule : rules) {
			mapRule(rule, entityProps);
		}
	}

	public List<Rule> collectRules(Elements panelElements, WbEntityProperties entityProps) {
		List<Rule> rules = new ArrayList<Rule>();
		Rule rule = new Rule(entityProps.getLabel_DE(), entityProps);
		rules.add(rule);
		/*
		 * In manchen Panels ist mehr als eine Regel enthalten. Die jeweiligen Regeln
		 * werden dann von einer Überschrift h4 eingeleitet
		 */
		for (Element e : panelElements) {
			/*
			 * Wenn eine neue Regel durch ein h4 eingeleitet wird, schließe die bisherige
			 * Regel ab und starte eine neue Regel ...
			 */
			if (e.tagName().equals("h4")) {
				logger.debug("Mappe rule: " + rule);
				rules.add(rule);
				rule = new Rule(e.text(), entityProps);
			} else {
				rule.elements.add(e);
			}
		}
		rules.add(rule);
		return rules;
	}

	public WbEntityProperties createOrUpdateRuleItem(Rule rule) throws MediaWikiApiErrorException, IOException {
		/*
		 * Erzeuge oder aktualisiere Rule-Item
		 */

		WbEntityProperties ruleProps = createRuleProps(rule);

		wbEntityEditor.createOrUpdateItem(ruleProps);
		rule.wbEntityId = (ItemIdValue) ruleProps.getEntityId();
		return ruleProps;
	}

	public void mapRule(Rule rule, WbEntityProperties wbEntityProps) throws MediaWikiApiErrorException, IOException {
		if (rule.elements.isEmpty() && rule.subrules.isEmpty()) {
			return;
		}
		createOrUpdateRuleItem(rule);
		/*
		 * Ergänze in der Quell-Entität eine Referenz auf das Rule-Item
		 */
		logger.debug("Ergänze in der Quell-Entität " + wbEntityProps.getEntityId() + " eine Referenz auf das Rule-Item"
				+ rule.wbEntityId);
		Validate.ensureNotNull(rule.wbSourcePropertyId);
		Statement refStatement = StatementBuilder.forSubjectAndProperty(rule.wbSourcePropertyId, this.wbId)
				.withValue(Datamodel.makeStringValue(rule.shortLabel))
				.withQualifierValue(config.pEmbeddedItem, rule.wbEntityId)
				.withQualifierValue(config.pLayoutTyp, config.iHeader_Level1).build();
		wbEntityProps.addStatement(refStatement);
	}

	public WbEntityProperties createRuleProps(Rule rule) throws MediaWikiApiErrorException, IOException {

		Validate.ensureNotNull(rule.wbSourcePropertyId);
		Validate.isFalse(rule.wbSourcePropertyId.isPlaceholder(),
				"Die Entity-Id, auf die die Regel referenzieren soll, darf kein Platzhalter sein.");
		WbEntityProperties ruleProps = new WbEntityProperties();
		
		/*
		 * prüfe, ob das Item bereits existiert
		 */
		ItemDocument oldVersion = (ItemDocument) wbEntityEditor.lookupEntity(rule.standardLabel);
		if (oldVersion != null) {
			ruleProps.setRevisionId(oldVersion.getRevisionId());
			ruleProps.setEntityId(oldVersion.getEntityId());
			rule.wbEntityId = oldVersion.getEntityId();
		} else {
			ruleProps.setEntityId(ItemIdValue.NULL);
			rule.wbEntityId = ItemIdValue.NULL; 
		}

		/*
		 * Füge die konstanten Statements hinzu
		 */
		ruleProps.getLabels().put("de", Datamodel.makeMonolingualTextValue(rule.standardLabel, "de"));

//		ruleProps.getAliases().add(Datamodel.makeMonolingualTextValue(Utils.normalizeLabel(rule.shortLabel), "de"));
//		Utils.addNormalizedLabel(ruleProps);

		ruleProps.addStatement(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pSchema)
				.withValue(config.iStaDocumentation).build());
		ruleProps.addStatement(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pElementOf)
				.withValue(config.iRule).build());
		/*
		 * Rückverweis auf das Quell-Item
		 */
		ruleProps.addStatement(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pEmbeddedInProperty)
						.withValue(rule.wbSourcePropertyId).build());
		/*
		 * Rückverweis auf die "Top"-Regel
		 */
		if (rule.topRule != null) {
			ruleProps.addStatement(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.pEmbeddedInItem)
							.withValue(rule.topRule.wbEntityId).build());
		}

		/*
		 * Füge Defintionen hinzu
		 */
		if (!rule.elements.isEmpty()) {
			addRuleDescriptions(rule, ruleProps);
		}
		return ruleProps;
	}

	public void addRuleDescriptions(Rule rule, WbEntityProperties props)
			throws MediaWikiApiErrorException, IOException {
		logger.debug("Füge " + rule.elements.size() + " Definitionen hinzu ...");
		for (Element e : rule.elements) {
			String contextLabel = (rule.topRule != null) ? rule.topRule.label : null;
			Utils4Elements.addPanelElement(props, contextLabel, rule.wbEntityId, e, config.pDescription, wbEntityEditor, config);
		}
	}

}
