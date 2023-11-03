package de.dnb.sta.storage.wikibase;

import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaContentModifier;
import de.dnb.sta.storage.datamodel.StaDatamodel.Action;
import de.dnb.sta.storage.datamodel.StaDatamodel.Type;
import de.dnb.sta.storage.datamodel.StaProperty;
import de.dnb.sta.storage.datamodel.StaStatement;

public class ChainTextValuesModifier implements StaContentModifier {

	private Element exampleElement = new Element("ac:structured-macro").attr("ac:name", "info")
			.attr("ac:schema-version", "1").appendElement("ac:rich-text-body");
	// "<ac:structured-macro ac:name=\"info\"
	// ac:schema-version=\"1\"><ac:rich-text-body>$value</ac:rich-text-body></ac:structured-macro>")
	private Element collapsibleElement = new Element("p");

	@Override
	public StaContent modify(StaContent staContent) {
		staContent.getStatements().getStatementsMap().entrySet().stream()
				.filter(e -> e.getKey().getValueType().equals(Type.TEXT)).forEach(s -> modify(staContent, s));

//		for (Map.Entry<StaProperty, List<StaStatement>> entry : staContent.getStatements().getStatementsMap().entrySet()) {
//			if (entry.getKey().getValueType().equals(Type.TEXT)) {
//			}
//		}

		return staContent;
	}

	public void modify(StaContent staContent, Map.Entry<StaProperty, List<StaStatement>> entry) {
		StaStatement textStatement = new StaStatement(entry.getKey(), chainTextValues(entry.getValue()), null,
				Action.UPDATE);
		staContent.getStatements().removeStatements(entry.getKey().getKey());
		staContent.getStatements().add(textStatement);
	}

	public String chainTextValues(List<StaStatement> textValues) {
		final Document document = new Document("");
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
		document.outputSettings().prettyPrint(false);

		Element ret = document.body();

		Element parent = null;

		for (StaStatement staValue : textValues) {
			String qualifierValue = staValue.getQualifierValue(StaStorageWikibase.LAYOUT_KEY);
			if (qualifierValue == null) {
				qualifierValue = "";
			}
			switch (qualifierValue) {
			case StaStorageWikibase.H1_KEY:
				ret.appendElement("h1").html(staValue.getValue());
				break;
			case StaStorageWikibase.H2_KEY:
				ret.appendElement("h2").html(staValue.getValue());
				break;
			case StaStorageWikibase.H3_KEY:
				ret.appendElement("h2").html(staValue.getValue());
				break;
			case StaStorageWikibase.EM_KEY:
				ret.appendElement("em").html(staValue.getValue());
				break;
			case StaStorageWikibase.STRONG_KEY:
				ret.appendElement("strong").html(staValue.getValue());
				break;
			case StaStorageWikibase.EXAMPLE_KEY:
				ret.appendElement("example").html(staValue.getValue());
				break;
			case StaStorageWikibase.COLLAPSIBLE_KEY:
				ret.appendElement("collapsible").html(staValue.getValue());
				break;
			case StaStorageWikibase.UL_KEY:
				if (parent == null || !parent.tagName().equals("ul")) {
					parent = ret.appendElement("ul");
				}
				parent.appendElement("li").html(staValue.getValue());
				break;
			case StaStorageWikibase.OL_KEY:
				if (parent == null || !parent.tagName().equals("ol")) {
					parent = ret.appendElement("ol");
				}
				parent.appendElement("li").html(staValue.getValue());
				break;
			default:
				ret.appendElement("p").html(staValue.getValue());
			}
		}
		return ret.html();
	}

}
