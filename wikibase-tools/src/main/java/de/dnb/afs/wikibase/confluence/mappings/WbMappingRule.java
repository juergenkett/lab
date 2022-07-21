package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
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

	private PropertyIdValue wbPropertyId;

	private ConfluenceWbConfig config;

	private WbEntityEditor wbEntityEditor;

	public WbMappingRule(String labelPattern, PropertyIdValue wbId, ConfluenceWbConfig config,
			WbEntityEditor wbEntityEditor) {
		super(labelPattern);
		this.wbPropertyId = wbId;
		this.config = config;
		this.wbEntityEditor = wbEntityEditor;
	}

	public static String getShortLabel(String label) {
		return label.replaceFirst("\\w+regel[n]?? ", "");
	}

	@Override
	public void doMap(String panelLabelDe, Elements panelElements, WbEntityProperties entityProps) {
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}
		/*
		 * Label der Regel ermitteln
		 */

		Map<String, MonolingualTextValue> entityLabels = entityProps.getLabels();
		String entityLabelDe = entityLabels.containsKey("de") ? entityLabels.get("de").getText() + " - " : "";
		entityLabelDe = "STA-Regel: " + entityLabelDe + panelLabelDe;

		/*
		 * In manchen Panels ist mehr als eine Regel enthalten. Die jeweiligen Regeln
		 * werden dann von einer Überschrift h4 eingeleitet
		 */
		Rule rule = new Rule();
		Rule subrule = null;
		rule.label = entityLabelDe;
		rule.shortLabel = getShortLabel(panelLabelDe);
		rule.wbSourcePropertyId = (PropertyIdValue) entityProps.getEntityId();
		
		for (Element e : panelElements) {
			if (e.tagName().equals("h4")) {
				subrule = new Rule();
				subrule.label = entityLabelDe + " " + e.text();
				subrule.shortLabel = e.text();
				subrule.wbSourcePropertyId = rule.wbSourcePropertyId;
				subrule.wbTopRuleId = rule.wbEntityId;
				rule.subrules.add(subrule);
			} else if (subrule != null) {
				subrule.elements.add(e);
			} else {
				rule.elements.add(e);
			}
		}
		logger.debug("Mappe rule: " + rule);
		mapRule(rule, entityProps);
	}

	private void mapRule(Rule rule, WbEntityProperties wbEntityProps) {
		if (rule.elements.isEmpty() && rule.subrules.isEmpty()) {
			return;
		}
		try {
			WbEntityProperties ruleProps = createRuleItem(rule);
			if (ruleProps == null) {
				return;
			}
			/*
			 * Ergänze Unterregeln als Referenz
			 */
			if (!rule.subrules.isEmpty()) {
				for (Rule subrule : rule.subrules) {
					logger.debug("ergänze Unterregel ");
					subrule.wbTopRuleId =rule.wbEntityId;
					WbEntityProperties subruleProps = createRuleItem(subrule);
					logger.debug("{subruleProps.entityId:" + subruleProps.getEntityId()
							+ ", subruleProps.statements.size:" + subruleProps.getStatements().size() + "}");
					WbEntityProperties refProps =  new WbEntityProperties();
					refProps.setEntityId(rule.wbEntityId);
					refProps.getStatements().add(newRefStatement(rule.wbEntityId, subrule));
					wbEntityEditor.updateEntity(refProps, false);
				}
			}

			/*
			 * Ergänze in der Quell-Entität eine Referenz auf das Rule-Item
			 */
			logger.debug("Ergänze in der Quell-Entität " + wbEntityProps.getEntityId()
					+ " eine Referenz auf das Rule-Item" + rule.wbEntityId);
			wbEntityProps.getStatements().add(newRefStatement(rule.wbSourcePropertyId, rule));
		} catch (MediaWikiApiErrorException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}
	
	public Statement newRefStatement(EntityIdValue sourceId, Rule targetRule) {
		Validate.ensureNotNull(sourceId);
		Statement refStatement = StatementBuilder.forSubjectAndProperty(sourceId, wbPropertyId)
				.withValue(new StringValueImpl(targetRule.shortLabel))
				.withQualifierValue(config.wbEmbedded, targetRule.wbEntityId)
				.withQualifierValue(config.wbLayoutTyp, config.wbH4).build();
		return refStatement;
	}

	public WbEntityProperties createRuleItem(Rule rule) throws MediaWikiApiErrorException, IOException {
		Validate.ensureNotNull(rule.wbSourcePropertyId);
		Validate.isFalse(rule.wbSourcePropertyId.isPlaceholder(),
				"Die Entity-Id, auf die die Regel referenzieren soll, darf kein Platzhalter sein.");

		rule.wbEntityId = ItemIdValue.NULL;
		ItemDocument oldVersion = null;

		oldVersion = (ItemDocument) this.wbEntityEditor.lookupEntity(rule.label);

		if (oldVersion != null) {
			logger.debug("Alte Version des Regel-Item mit Id " + oldVersion.getEntityId().getId()
					+ " mit identischem Label gefunden. Ersetze diese ...");
			rule.wbEntityId = oldVersion.getEntityId();
		}

		/*
		 * Füge die konstanten Statements hinzu
		 */
		WbEntityProperties ruleProps = new WbEntityProperties();
		ruleProps.setEntityId(rule.wbEntityId);
		ruleProps.getLabels().put("de", new MonolingualTextValueImpl(rule.label, "de"));
		ruleProps.getStatements().add(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.schema)
				.withValue(config.staDocumentation).build());
		ruleProps.getStatements().add(StatementBuilder.forSubjectAndProperty(rule.wbEntityId, config.elementOf)
				.withValue(new ItemIdValueImpl("Q3095", config.wbIri)).build());
		/* 
		 * Rückverweis auf das Quell-Item
		 */
		ruleProps.getStatements()
				.add(StatementBuilder
						.forSubjectAndProperty(rule.wbEntityId, new PropertyIdValueImpl("P397", config.wbIri))
						.withValue(rule.wbSourcePropertyId).build());
		/*
		 * Rückverweis auf die "Top"-Regel
		 */
		if (rule.wbTopRuleId != null) {
			ruleProps.getStatements()
			.add(StatementBuilder
					.forSubjectAndProperty(rule.wbEntityId, new PropertyIdValueImpl("P398", config.wbIri))
					.withValue(rule.wbTopRuleId).build());
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

	public void addRuleDefinitions(Rule rule, WbEntityProperties props) {
		logger.debug("Füge " + rule.elements.size() + " Definitionen hinzu ...");
		for (Element e : rule.elements) {
			Utils.removeLeadingAndTrailingBr(e);
			String content = e.html();
			if (content.length() > config.maxEntryLength) {
				logger.warn("Paragraph ist zu lang (" + content.length() + " Zeichen) und wird gekürzt.");
				content = content.substring(0, config.maxEntryLength);
			}
			StatementBuilder contentStatementBuilder = StatementBuilder
					.forSubjectAndProperty(rule.wbEntityId, new PropertyIdValueImpl("P7", config.wbIri))
					.withValue(new StringValueImpl(content));
			if (e.attr("class").equals("example")) {
				contentStatementBuilder.withQualifierValue(config.wbLayoutTyp, config.example);
			}
			props.getStatements().add(contentStatementBuilder.build());
		}

	}

}

class Rule {

	PropertyIdValue wbSourcePropertyId;
	
	ItemIdValue wbTopRuleId;

	ItemIdValue wbEntityId;

	String label;

	String shortLabel;

	Elements elements = new Elements();

	List<Rule> subrules = new ArrayList<Rule>();

	@Override
	public String toString() {
		return "{rule.wbEntityId: " + wbEntityId + ", rule.shortLabel:" + shortLabel + ", rule.elements.size:"
				+ elements.size() + ", rule.subrules:" + subrules + "}";
	}

}
