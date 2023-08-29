package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityProperties;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;

public class WbMapping4SpecificRules extends WbMapping4Rules {

	private static final Log logger = LogFactory.getLog(WbMapping4SpecificRules.class);

	public WbMapping4SpecificRules(String labelPattern, PropertyIdValue mainPropertyId, ConfluenceWbConfig config,
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
		Rule rule = new Rule(getRuleLabel(panelLabelDe), entityProps);
		for (Element e : panelElements) {
			rule.elements.add(e);
		}
		logger.debug("Mappe rule: " + rule);
		mapRule(rule, entityProps);
	}
	
	public static String getRuleLabel(String panelLabel) {
		String ret = panelLabel.replaceFirst("(Spezialregel[n]? )|(Spezifische Regel[n]? )|(Basisregel[n]? )", "").replaceFirst("^(für) ", "");
		ret = ret.substring(0,1).toUpperCase() + ret.substring(1);
		return ret;
	}
}
