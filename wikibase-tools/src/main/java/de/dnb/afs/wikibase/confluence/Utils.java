package de.dnb.afs.wikibase.confluence;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.implementation.ItemIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.implementation.StringValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;

public class Utils {

	public final static String WB_EXAMPLE = "P11";

	public final static String GND_DOKU_WIKIBASE_URL = "http://doku.wikibase.wiki/w/api.php";
	public final static String GND_DOKU_SITE_IRI = "doku.wikibase.wiki/";

	public final static ItemIdValue WB_H4 = new ItemIdValueImpl("Q1343", GND_DOKU_SITE_IRI);
	public final static ItemIdValue WB_H5 = new ItemIdValueImpl("Q1346", GND_DOKU_SITE_IRI);
	public final static ItemIdValue WB_OL_LI = new ItemIdValueImpl("Q1345", GND_DOKU_SITE_IRI);
	public final static ItemIdValue WB_UL_LI = new ItemIdValueImpl("Q1344", GND_DOKU_SITE_IRI);
	public final static PropertyIdValue WB_LAYOUT_TYP = new PropertyIdValueImpl("P389", GND_DOKU_SITE_IRI);
	public final static PropertyIdValue WB_EMBEDDED = new PropertyIdValueImpl("P396", GND_DOKU_SITE_IRI);



	public static void addPanelParagraph(ItemDocumentBuilder itemDocumentBuilder, Element panelParagraph,
			PropertyIdValue propertyId) {
		StringValue value = new StringValueImpl(panelParagraph.html());
		if (value != null) {
			itemDocumentBuilder.withStatement(
					StatementBuilder.forSubjectAndProperty(ItemIdValue.NULL, propertyId).withValue(value).build());
		}
	}

	public static void addPanelH4(ItemDocumentBuilder itemDocumentBuilder, Element panelParagraph,
			PropertyIdValue propertyId) {
		StringValue value = new StringValueImpl(panelParagraph.text());
		itemDocumentBuilder.withStatement(StatementBuilder.forSubjectAndProperty(ItemIdValue.NULL, propertyId)
				.withValue(value).withQualifierValue(Utils.WB_LAYOUT_TYP, Utils.WB_H4).build());
	}

	public static void addPanelH5(ItemDocumentBuilder itemDocumentBuilder, Element panelParagraph,
			PropertyIdValue propertyId) {
		StringValue value = new StringValueImpl(panelParagraph.text());
		itemDocumentBuilder.withStatement(StatementBuilder.forSubjectAndProperty(ItemIdValue.NULL, propertyId)
				.withValue(value).withQualifierValue(Utils.WB_LAYOUT_TYP, Utils.WB_H5).build());
	}

	public static void addPanelUlLi(ItemDocumentBuilder itemDocumentBuilder, Element panelParagraph,
			PropertyIdValue propertyId) {
		StringValue value = new StringValueImpl(panelParagraph.html());

		itemDocumentBuilder.withStatement(StatementBuilder.forSubjectAndProperty(ItemIdValue.NULL, propertyId)
				.withValue(value).withQualifierValue(Utils.WB_LAYOUT_TYP, Utils.WB_UL_LI).build());
	}

	public static void addPanelOlLi(ItemDocumentBuilder itemDocumentBuilder, Element panelParagraph,
			PropertyIdValue propertyId) {
		StringValue value = new StringValueImpl(panelParagraph.html());

		itemDocumentBuilder.withStatement(StatementBuilder.forSubjectAndProperty(ItemIdValue.NULL, propertyId)
				.withValue(value).withQualifierValue(Utils.WB_LAYOUT_TYP, Utils.WB_OL_LI).build());
	}

	public static void onPanelExample(Elements exampleParagraphs) {
		// TODO Auto-generated method stub

	}

	public static void addPanelElement(ItemDocumentBuilder itemDocumentBuilder, Element panelElement,
			PropertyIdValue propertyId) {
		if (panelElement.text().trim().length() == 0)
			return;
		String tagName = panelElement.tagName();
		if (tagName.equals("p")) {
			addPanelParagraph(itemDocumentBuilder, panelElement, propertyId);
		} else if (tagName.equals("h4")) {
			addPanelH4(itemDocumentBuilder, panelElement, propertyId);
		} else if (tagName.equals("h5")) {
			addPanelH5(itemDocumentBuilder, panelElement, propertyId);
		} else if (tagName.equals("ul")) {
			for (Element li : panelElement.children()) {
				addPanelUlLi(itemDocumentBuilder, li, propertyId);
			}
		} else if (tagName.equals("ol")) {
			for (Element li : panelElement.children()) {
				addPanelOlLi(itemDocumentBuilder, li, propertyId);
			}
		}
	}

}
