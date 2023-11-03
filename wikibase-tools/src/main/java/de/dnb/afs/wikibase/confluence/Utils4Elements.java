package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.de.GermanLightStemmer;
import org.apache.lucene.analysis.de.GermanStemmer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.BytesRef;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityProperties;

public class Utils {

	private static final Log logger = LogFactory.getLog(Utils.class);

	public static void addPanelParagraph(List<Statement> statements, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = normalizeHtmlContent(panelParagraph.html());
		if (value != null) {
			statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value).build());
		}
	}

	public static void addExample(List<Statement> statements, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = normalizeHtmlContent(panelParagraph.html());
		if (value != null) {
			statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
					.withQualifierValue(config.pLayoutTyp, config.iExample).build());
		}
	}

	public static void addPanelH4(List<Statement> statements, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = Datamodel.makeStringValue(panelParagraph.text());
		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iH4).build());
	}

	public static void addPanelH5(List<Statement> statements, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = Datamodel.makeStringValue(panelParagraph.text());
		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iH5).build());
	}

	public static void addPanelUlLi(List<Statement> statements, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = normalizeHtmlContent(panelParagraph.html());

		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iUlLi).build());
	}

	public static void addPanelOlLi(List<Statement> statements, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = normalizeHtmlContent(panelParagraph.html());

		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iOlLi).build());
	}

	public static void addPanelElement(WbEntityProperties props, String contextLabel, EntityIdValue wbEntityId, Element panelElement,
			PropertyIdValue propertyIdValue, WbEntityLoader entityLoader, ConfluenceWbConfig config)
			throws MediaWikiApiErrorException, IOException {
		Utils.removeLeadingAndTrailingBr(panelElement);
		if (panelElement.text().trim().length() == 0)
			return;

		if (entityLoader != null) {
			updateRefs(contextLabel, panelElement, entityLoader);
		}

		String tagName = panelElement.tagName();
		if (tagName.equals("p")) {
			addPanelParagraph(props.getStatements(), wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("h4")) {
			addPanelH4(props.getStatements(), wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("h5")) {
			addPanelH5(props.getStatements(), wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("ul")) {
			for (Element li : panelElement.children()) {
				addPanelUlLi(props.getStatements(), wbEntityId, li, propertyIdValue, config);
			}
		} else if (tagName.equals("ol")) {
			for (Element li : panelElement.children()) {
				addPanelOlLi(props.getStatements(), wbEntityId, li, propertyIdValue, config);
			}
		} else if (panelElement.attr("class").equals("example")) {
			addExample(props.getStatements(), wbEntityId, panelElement, propertyIdValue, config);
		}

	}

	public static StringValue normalizeHtmlContent(String html) {
		return Datamodel.makeStringValue(StringUtils.normalizeSpace(html));
	}

	public static void updateRefs(String contextLabel, Element e, WbEntityLoader wbLoader) throws MediaWikiApiErrorException, IOException {
		for (Element ref : e.getElementsByClass("ref")) {
//			String query = normalizeLabel(ref.text());
			String query = ref.text().trim();
			if (query != null && !query.isEmpty()) {
				EntityDocument refDoc = wbLoader.lookupEntity(query);
				if (refDoc != null) {
					logger.info("Konnte Referenz auflösen: " + query);
					ref.attr("href", refDoc.getEntityId().getId());
				} else {
					logger.info("Konnte Referenz nicht auflösen: " + query);
				}
			}
		}

//		for (Element ref : e.getElementsByClass("localRef")) {
//			String query;
//			if (contextLabel != null && !contextLabel.isBlank()) {
//				query = normalizeLabel(contextLabel + " " + ref.text());
//			} else {
//				query = normalizeLabel(ref.text());
//			}
//			if (query != null && !query.isEmpty()) {
//				EntityDocument refDoc = wbLoader.lookupEntity(query);
//				if (refDoc != null) {
//					logger.info("Konnte Referenz auflösen: " + query);
//					ref.attr("href", "#" + refDoc.getEntityId().getId());
//				} else {
//					logger.info("Konnte Referenz nicht auflösen: " + query);
//				}
//			}
//		}
	}

	public static void removeLeadingAndTrailingBr(Element e) {
		for (Node child : e.childNodes()) {
			if (child instanceof Element && ((Element) child).tagName().equals("br")) {
				child.remove();
			} else
				break;
		}
		for (int i = e.childNodes().size(); i-- > 0;) {
			Node child = e.childNode(i);
			if (child instanceof Element && ((Element) child).tagName().equals("br")) {
				child.remove();
			} else
				break;
		}
	}

//	public static String normalizeLabel(String label) {
//		label = label.replace("STA-Regel: ", "");
//		String result = "";
//		GermanAnalyzer analyzer = new GermanAnalyzer();
//		TokenStream stream = analyzer.tokenStream(null, new StringReader(label));
//		try {
//			stream.reset();
//			while (stream.incrementToken()) {
//				if (result.length() > 0) {
//					result = result + "_";
//				}
//				result = result + stream.getAttribute(CharTermAttribute.class).toString();
//			}
//		} catch (IOException e) {
//
//		} finally {
//			analyzer.close();
//		}
//		return result;
//	}

//	public static void addNormalizedLabel(WbEntityProperties props) {
//		props.getAliases()
//				.add(Datamodel.makeMonolingualTextValue(normalizeLabel(props.getLabels().get("de").getText()), "de"));
//	}

}
