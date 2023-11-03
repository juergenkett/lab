package de.dnb.sta.storage.confluence;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaContentModifier;
import de.dnb.sta.storage.datamodel.StaDatamodel.Type;
import de.dnb.sta.storage.datamodel.StaStatement;

public class ConvertToStorageFormatModifier implements StaContentModifier {

	private static final Logger LOGGER = System.getLogger(StaContentModifier.class.getName());

	@Override
	public StaContent modify(StaContent staContent) {
		staContent.getStatements().stream().forEach(s -> modify(s));
		return staContent;
	}

	private void modify(StaStatement s) {
		if (s.getType().equals(Type.TEXT)) {
			modifyText(s);
		}
		if (s.getEmbeddedContent() != null) {
			modify(s.getEmbeddedContent());
		}
	}

	public void modifyText(StaStatement staStatement) {
		Element value = Jsoup.parse(staStatement.getValue()).body();
		Element newElement = null;
		for (Element e : value.children()) {
			switch (e.tagName()) {
			case "example":
				newElement = new Element("ac:structured-macro").attr("ac:name", "info").attr("ac:schema-version", "1");
				newElement.appendElement("ac:rich-text-body").html(e.html());
				e.replaceWith(newElement);
				break;
			case "collapsible":
				newElement = new Element("ac:structured-macro").attr("ac:name", "ui-expand").attr("ac:schema-version",
						"1");
				newElement.appendElement("ac:rich-text-body").html(e.html());
				e.replaceWith(newElement);
				break;
			}
		}
		staStatement.setValue(value.html());
	}
}
