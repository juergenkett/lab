package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;

public class Utils {

	public static void addPanelParagraph(List<Statement> statements, EntityIdValue wbEntityId,  Element panelParagraph,
			PropertyIdValue propertyId) {

		StringValue value = new StringValueImpl(panelParagraph.html());
		if (value != null) {
			statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId)
					.withValue(value).build());
		}
	}

	public static void addPanelH4(List<Statement> statements,EntityIdValue wbEntityId, Element panelParagraph, PropertyIdValue propertyId,
			ConfluenceWbConfig config) {
		StringValue value = new StringValueImpl(panelParagraph.text());
		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId)
				.withValue(value).withQualifierValue(config.wbLayoutTyp, config.wbH4).build());
	}

	public static void addPanelH5(List<Statement> statements,EntityIdValue wbEntityId, Element panelParagraph, PropertyIdValue propertyId,
			ConfluenceWbConfig config) {
		StringValue value = new StringValueImpl(panelParagraph.text());
		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId)
				.withValue(value).withQualifierValue(config.wbLayoutTyp, config.wbH5).build());
	}

	public static void addPanelUlLi(List<Statement> statements,EntityIdValue wbEntityId,  Element panelParagraph, PropertyIdValue propertyId,
			ConfluenceWbConfig config) {
		StringValue value = new StringValueImpl(panelParagraph.html());

		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId)
				.withValue(value).withQualifierValue(config.wbLayoutTyp, config.wbUlLi).build());
	}

	public static void addPanelOlLi(List<Statement> statements,EntityIdValue wbEntityId,  Element panelParagraph, PropertyIdValue propertyId,
			ConfluenceWbConfig config) {
		StringValue value = new StringValueImpl(panelParagraph.html());

		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId)
				.withValue(value).withQualifierValue(config.wbLayoutTyp, config.wbOlLi).build());
	}

	public static void onPanelExample(Elements exampleParagraphs) {
		// TODO Auto-generated method stub

	}

	public static void addPanelElement(List<Statement> statements, EntityIdValue wbEntityId,  Element panelElement,
			PropertyIdValue propertyIdValue, ConfluenceWbConfig config) {
		if (panelElement.text().trim().length() == 0)
			return;
		Utils.removeLeadingAndTrailingBr(panelElement);
		String tagName = panelElement.tagName();
		if (tagName.equals("p")) {
			addPanelParagraph(statements, wbEntityId, panelElement, propertyIdValue);
		} else if (tagName.equals("h4")) {
			addPanelH4(statements, wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("h5")) {
			addPanelH5(statements, wbEntityId, panelElement, propertyIdValue, config);
		} else if (tagName.equals("ul")) {
			for (Element li : panelElement.children()) {
				addPanelUlLi(statements, wbEntityId, li, propertyIdValue, config);
			}
		} else if (tagName.equals("ol")) {
			for (Element li : panelElement.children()) {
				addPanelOlLi(statements, wbEntityId, li, propertyIdValue, config);
			}
		}
	}
	
	public static void addWbIdsToRefs(Elements elements, WbEntityLoader wbLoader) throws MediaWikiApiErrorException, IOException {
		for (Element e : elements) {
			for (Element ref : e.getElementsByClass("ref")) {
				String query = e.text();
				if (query!=null && !query.isEmpty()) {
					EntityDocument refDoc = wbLoader.lookupEntity(e.text());
					if (refDoc!=null) {
						ref.attr("href", refDoc.getEntityId().getId());
					}	
				}
			}
		}
	}
	
	public static void removeLeadingAndTrailingBr(Element e) {
		for (Node child : e.childNodes()) {
			if (child instanceof Element && ((Element)child).tagName().equals("br")) {
				child.remove();
			} else break;
		}
		for (int i = e.childNodes().size(); i-- > 0;) {
			Node child = e.childNode(i);
			if (child instanceof Element && ((Element)child).tagName().equals("br")) {
				child.remove();
			} else break;
		}
	}

}
