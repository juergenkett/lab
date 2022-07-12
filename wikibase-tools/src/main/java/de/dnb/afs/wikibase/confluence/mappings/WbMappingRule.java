package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
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
	public void doMap(Map<String, MonolingualTextValue> entityLabels, String label, Elements panelElements,
			EntityIdValue wbEntityId, List<Statement> statements) {
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}
		/*
		 * Label der Regel ermitteln
		 */
		String entityLabelDe = entityLabels.containsKey("de") ? entityLabels.get("de").getText() + " - " : "";
		entityLabelDe = "STA-Regel: " + entityLabelDe + label;

		/*
		 * In manchen Panels ist mehr als eine Regel enthalten. Die jeweiligen Regeln
		 * werden dann von einer Überschrift h4 eingeleitet
		 */
		Rule rule = new Rule();
		rule.label = entityLabelDe;
		rule.shortLabel = getShortLabel(label);
		for (Element e : panelElements) {
			if (e.tagName().equals("h4")) {
				mapRule(rule, wbEntityId, statements);
				rule = new Rule();
				rule.label = entityLabelDe + " " + e.text();
				rule.shortLabel = e.text();

			} else {
				rule.elements.add(e);
			}
		}
		mapRule(rule, wbEntityId, statements);

	}

	private void mapRule(Rule rule, EntityIdValue wbEntityId, List<Statement> statements) {
		if (rule.elements.size() == 0) {
			return;
		}
		try {
			rule.id = ItemIdValue.NULL;
			ItemDocument oldVersion = (ItemDocument) this.wbEntityEditor.lookupEntity(rule.label);
			if (oldVersion != null) {
				logger.debug("Alte Version des Regel-Item mit Id " + oldVersion.getEntityId().getId()
						+ " mit identischem Label gefunden. Ersetze diese ...");
				rule.id = oldVersion.getEntityId();
			}
			/*
			 * Füge Standard-Statements hinzu
			 */
			ItemDocumentBuilder ruleItemBuilder = ItemDocumentBuilder.forItemId(rule.id).withLabel(rule.label, "de")
					.withStatement(StatementBuilder.forSubjectAndProperty(rule.id, config.schema)
							.withValue(config.staDocumentation).build())
					.withStatement(StatementBuilder.forSubjectAndProperty(rule.id, config.elementOf)
							.withValue(new ItemIdValueImpl("Q3095", config.wbIri)).build())
					.withStatement(StatementBuilder
							.forSubjectAndProperty(rule.id, new PropertyIdValueImpl("P397", config.wbIri))
							.withValue(wbEntityId).build());
			/*
			 * Füge Defintionen hinzu
			 */
			addRuleDefinitions(rule, ruleItemBuilder);

			/*
			 * Erzeuge neues Rule-Item
			 */
			TermedStatementDocument ruleItem = ruleItemBuilder.build();
			if (rule.id.isPlaceholder()) {
				logger.debug("erzeuge neues Regel-Item ... ");
				ruleItem = wbEntityEditor.createEntity(ruleItem);
			} else {
				logger.debug("aktualisiere Regel-Item " + oldVersion.getEntityId().getId());
				ruleItem = wbEntityEditor.updateEntity(ruleItem);
			}
			/*
			 * Ergänze in der Property eine Referenz auf das Rule-Item
			 */
			statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, wbPropertyId)
					.withValue(new StringValueImpl(rule.shortLabel))
					.withQualifierValue(config.wbEmbedded, ruleItem.getEntityId())
					.withQualifierValue(config.wbLayoutTyp, config.wbH4).build());

		} catch (IOException e) {
			logger.error(e);
		} catch (MediaWikiApiErrorException e) {
			logger.error(e);
		}

	}

	public ItemDocumentBuilder addRuleDefinitions(Rule rule, ItemDocumentBuilder ruleItemBuilder) {
		logger.debug("Füge " + rule.elements.size() + " Definitonen hinzu ...");
		for (Element e : rule.elements) {
			String content = e.html();
			if (content.length() > config.maxEntryLength) {
				logger.warn("Paragraph ist zu lang (" + content.length() + " Zeichen) und wird gekürzt.");
				content = content.substring(0, config.maxEntryLength);
			}
			StatementBuilder contentStatementBuilder = StatementBuilder
					.forSubjectAndProperty(rule.id, new PropertyIdValueImpl("P7", config.wbIri))
					.withValue(new StringValueImpl(content));
			if (e.attr("class").equals("example")) {
				contentStatementBuilder.withQualifierValue(config.wbLayoutTyp, config.example);
			}
			ruleItemBuilder.withStatement(contentStatementBuilder.build());
		}
		return ruleItemBuilder;
	}

}

class Rule {

	ItemIdValue id;

	String label;

	String shortLabel;

	Elements elements = new Elements();

}
