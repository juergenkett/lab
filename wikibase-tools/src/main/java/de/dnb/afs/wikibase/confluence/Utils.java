package de.dnb.afs.wikibase.confluence;

import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

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
				.withValue(value).withQualifierValue(config.getWbLayoutTyp(), config.getWbH4()).build());
	}

	public static void addPanelH5(List<Statement> statements,EntityIdValue wbEntityId, Element panelParagraph, PropertyIdValue propertyId,
			ConfluenceWbConfig config) {
		StringValue value = new StringValueImpl(panelParagraph.text());
		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId)
				.withValue(value).withQualifierValue(config.getWbLayoutTyp(), config.getWbH5()).build());
	}

	public static void addPanelUlLi(List<Statement> statements,EntityIdValue wbEntityId,  Element panelParagraph, PropertyIdValue propertyId,
			ConfluenceWbConfig config) {
		StringValue value = new StringValueImpl(panelParagraph.html());

		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId)
				.withValue(value).withQualifierValue(config.getWbLayoutTyp(), config.getWbUlLi()).build());
	}

	public static void addPanelOlLi(List<Statement> statements,EntityIdValue wbEntityId,  Element panelParagraph, PropertyIdValue propertyId,
			ConfluenceWbConfig config) {
		StringValue value = new StringValueImpl(panelParagraph.html());

		statements.add(StatementBuilder.forSubjectAndProperty(wbEntityId, propertyId)
				.withValue(value).withQualifierValue(config.getWbLayoutTyp(), config.getWbOlLi()).build());
	}

	public static void onPanelExample(Elements exampleParagraphs) {
		// TODO Auto-generated method stub

	}

	public static void addPanelElement(List<Statement> statements, EntityIdValue wbEntityId,  Element panelElement,
			PropertyIdValue propertyIdValue, ConfluenceWbConfig config) {
		if (panelElement.text().trim().length() == 0)
			return;

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

}
