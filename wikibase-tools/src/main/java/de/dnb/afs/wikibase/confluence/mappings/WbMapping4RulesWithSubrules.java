package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;

public class WbMapping4RulesWithSubrules extends WbMapping4Rules {

	private static final Log logger = LogFactory.getLog(WbMapping4RulesWithSubrules.class);

	public WbMapping4RulesWithSubrules(String labelPattern, PropertyIdValue mainPropertyId, ConfluenceWbConfig config,
			WbEntityEditor wbEntityEditor) {
		super(labelPattern, mainPropertyId, config, wbEntityEditor);
	}

	@Override
	public void doMap(String panelLabelDe, Elements panelElements, WbEntityProperties entityProps)
			throws MediaWikiApiErrorException, IOException {
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}
		Rule toprule = new Rule(panelLabelDe, entityProps);
		Rule subrule = null;
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
				/*
				 * Sammle die Regel als Unterregel im übergeordneten Regel-Item
				 */
				logger.debug("Starte neue subrule: " + e.text());
				subrule = new Rule(e.text(), toprule);
				subrule.topRule = toprule;
				toprule.subrules.add(subrule);
				/*
				 * isHierachicalRule ... ansonsten füge das Element der aktuellen Regel bzw.
				 * Unterregel an
				 */
			} else if (subrule == null) {
				toprule.elements.add(e);
			} else {
				subrule.elements.add(e);
			}
		}
		logger.debug("Mappe rule: " + toprule);
		mapRule(toprule, entityProps);
		/*
		 * Generiere Unterregeln und füge diese als Referenz ein
		 */
		if (!toprule.subrules.isEmpty()) {
			int index = 0;
			for (Rule sr : toprule.subrules) {
				index = index + 1;
				addSubruleToRule(sr, toprule, index);
			}
		}
	}

	public void addSubruleToRule(Rule subrule, Rule rule, int index) throws MediaWikiApiErrorException, IOException {
		logger.debug("ergänze Unterregel ");
		subrule.topRule = rule;
		/*
		 * 1. lege Subrule in WB an
		 */

		WbEntityProperties subruleProps = createOrUpdateRuleItem(subrule);
		logger.debug("{subruleProps.entityId:" + subruleProps.getEntityId() + ", subruleProps.statements.size:"
				+ subruleProps.getStatements().size() + "}");

		/*
		 * 2. Füge in der Elementbeschreibung ein Statement mit einer Referenz auf die
		 * Subrule ein
		 */
		WbEntityProperties refProps = new WbEntityProperties();
		refProps.setEntityId(rule.wbEntityId);
		Validate.ensureNotNull(rule.wbEntityId);
		Statement refStatement = StatementBuilder.forSubjectAndProperty(rule.wbEntityId, this.wbId)
				.withValue(Datamodel.makeStringValue(subrule.shortLabel))
				.withQualifierValue(config.pEmbeddedItem, subruleProps.getEntityId())
				.withQualifierValue(config.pLayoutTyp, config.iHeader_Level1).build();
		refProps.getStatements().add(refStatement);
		wbEntityEditor.updateEntity(refProps);
	}

}
