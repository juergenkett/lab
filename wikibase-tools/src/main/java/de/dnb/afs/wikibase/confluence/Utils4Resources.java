package de.dnb.afs.wikibase.confluence;

import java.io.IOException;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityProperties;

public class Utils4Resources {

	public static void addPanelParagraph(WbEntityProperties props, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = Utils4Elements.normalizeHtmlContent(panelParagraph.html());
		if (value != null) {
			props.addStatement(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value).build());
		}
	}

	public static void addExample(WbEntityProperties props, EntityIdValue wbEntityId, Element element,
			PropertyIdValue propertyId, ConfluenceWbConfig config, WbEntityLoader entityLoader)
			throws MediaWikiApiErrorException, IOException {
		if (element.attr("class").equals("detailed-example")) {
			for (Element table : element.getElementsByTag("table")) {
				table.attr("class", "example-table");
			}
			for (Element tr : element.getElementsByTag("tr")) {
				Element td = tr.firstElementChild();
				String rdaElementName = td.text().trim();
				String pid = entityLoader.lookupPID(rdaElementName);
				if (pid != null) {
					Element href = new Element("a").attr("href", pid).attr("title", rdaElementName);
					href.appendChildren(td.childNodes());
					td.appendChild(href);
				}
			}
		}
		StringValue value = Utils4Elements.normalizeHtmlContent(element.html());
		if (value != null) {
			props.addStatement(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
					.withQualifierValue(config.pLayoutTyp, config.iExample).build());
		}
	}

	public static void addPanelH3(WbEntityProperties props, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = Datamodel.makeStringValue(panelParagraph.text());
		props.addStatement(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iHeader_Level1).build());
	}

	public static void addPanelH4(WbEntityProperties props, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = Datamodel.makeStringValue(panelParagraph.text());
		props.addStatement(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iHeader_Level2).build());
	}

	public static void addPanelH5(WbEntityProperties props, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = Datamodel.makeStringValue(panelParagraph.text());
		props.addStatement(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iHeader_Level3).build());
	}

	public static void addPanelUlLi(WbEntityProperties props, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = Utils4Elements.normalizeHtmlContent(panelParagraph.html());

		props.addStatement(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iUlLi).build());
	}

	public static void addPanelOlLi(WbEntityProperties props, EntityIdValue wbEntityId, Element panelParagraph,
			PropertyIdValue propertyId, ConfluenceWbConfig config) {
		StringValue value = Utils4Elements.normalizeHtmlContent(panelParagraph.html());

		props.addStatement(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId).withValue(value)
				.withQualifierValue(config.pLayoutTyp, config.iOlLi).build());
	}

	public static void addPanelElement(WbEntityProperties props, String contextLabel, EntityIdValue wbEntityId,
			Element panelElement, PropertyIdValue propertyIdValue, WbEntityLoader entityLoader,
			ConfluenceWbConfig config) throws MediaWikiApiErrorException, IOException {
		Utils4Resources.removeLeadingAndTrailingBr(panelElement);
		if (panelElement.text().trim().length() == 0)
			return;

//		if (entityLoader != null) {
//			updateRefs(contextLabel, panelElement, entityLoader);
//		}

		String tagName = panelElement.tagName();
		if (tagName.equals("p")) {
			addPanelParagraph(props, wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("h3")) {
			addPanelH3(props, wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("h4")) {
			addPanelH4(props, wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("h5")) {
			addPanelH5(props, wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("ul")) {
			for (Element li : panelElement.children()) {
				addPanelUlLi(props, wbEntityId, li, propertyIdValue, config);
			}
		} else if (tagName.equals("ol")) {
			for (Element li : panelElement.children()) {
				addPanelOlLi(props, wbEntityId, li, propertyIdValue, config);
			}
		} else if (tagName.equals("example")) {
			addExample(props, wbEntityId, panelElement, propertyIdValue, config, entityLoader);
		} else if (tagName.equals("subproperty")) {
			StringValue value = Datamodel.makeStringValue(panelElement.attr("label"));
			props.addStatement(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyIdValue).withValue(value)
					.withQualifierValue(config.pLayoutTyp, config.iHeader_Level1).build());
			for (Element child : panelElement.children()) {
				addPanelElement(props, contextLabel, wbEntityId, child, propertyIdValue, entityLoader, config);
			}
		}

	}

//	public static void updateRefs(String contextLabel, Element e, WbEntityLoader wbLoader)
//			throws MediaWikiApiErrorException, IOException {
//		for (Element ref : e.getElementsByClass("ref")) {
////			String query = normalizeLabel(ref.text());
//			String query = ref.text().trim();
//			if (query != null && !query.isEmpty()) {
//				EntityDocument refDoc = wbLoader.lookupEntity(query);
//				if (refDoc != null) {
//					logger.info("Konnte Referenz auflösen: " + query);
//					ref.attr("href", refDoc.getEntityId().getId());
//				} else {
//					logger.info("Konnte Referenz nicht auflösen: " + query);
//				}
//			}
//		}
//
////		for (Element ref : e.getElementsByClass("localRef")) {
////			String query;
////			if (contextLabel != null && !contextLabel.isBlank()) {
////				query = normalizeLabel(contextLabel + " " + ref.text());
////			} else {
////				query = normalizeLabel(ref.text());
////			}
////			if (query != null && !query.isEmpty()) {
////				EntityDocument refDoc = wbLoader.lookupEntity(query);
////				if (refDoc != null) {
////					logger.info("Konnte Referenz auflösen: " + query);
////					ref.attr("href", "#" + refDoc.getEntityId().getId());
////				} else {
////					logger.info("Konnte Referenz nicht auflösen: " + query);
////				}
////			}
////		}
//	}

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
