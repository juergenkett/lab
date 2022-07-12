package de.dnb.afs.wikibase.confluence;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;

public class ConfluenceDokuCleaner {

	public Document clean(Document document) {
		Cleaner cleaner = new Cleaner(Safelist.basic().addAttributes("div", "data-macro-name").addAttributes("h1", "id")
				.addTags("title").addTags("h2", "h3", "h4").addAttributes("span", "style"));
		document = cleaner.clean(document);
		Document newDocument = Document.createShell(document.baseUri());
		newDocument.outputSettings().escapeMode(EscapeMode.xhtml);
		newDocument.outputSettings().charset(CharEncoding.UTF_8);
		newDocument.title(getLabel(document));
		for (Element panel : document.body().getElementsByAttributeValue("data-macro-name", "panel")) {
			cleanPanel(panel);
			newDocument.body().appendChild(panel);
		}
		return newDocument;
	}

	public String getLabel(Document document) {
		Element titleElement = document.getElementById("title-text");
		if (titleElement != null) {
			String label = titleElement.text().replaceFirst("AP .* \\| .* - .* - ", "");

			return label;
		}
		return null;
	}

	public void cleanPanel(Element panel) {
//		panel.tagName("panel");
//		panel.removeAttr("data-macro-name");
		for (Element e : panel.getAllElements()) {
			// remove empty tags (for example: <em></em> <div></div>)
			if (!e.hasText() && e.isBlock()) {
				e.remove();
				// remove sections that are not needed
			} else if (e.attr("data-macro-name").equals("expand")) {
				e.remove();
			} else if (e.tag().getName().equals("span")) {
				if (StringUtils.deleteWhitespace(e.attr("style")).equals("color:rgb(255,0,255);")) {
					e.tagName("a");
					e.attr("class", "ref");
					e.removeAttr("style");
				} else if (StringUtils.deleteWhitespace(e.attr("style")).equals("color:rgb(255,0,0);")) {
					e.tagName("a");
					e.attr("class", "localRef");
					e.removeAttr("style");
				} else {
					e.unwrap();
				}
			} else if (e.attr("data-macro-name").equals("info")) {

				e.child(0).attr("class", "example");
				e.unwrap();
			}
			// remove redundant nested elements (for example: <em><em>Text</em></em>
			else if (!e.isBlock() && e.parent().tagName().equals(e.tagName()) && e.attributes().size() == 0) {

				e.unwrap();
			} else if ((e.tagName().equals("em") || e.tagName().equals("strong"))
					&& (!e.hasText() || e.parent().tagName().equals(e.tagName()))) {
				e.unwrap();
			}
		}
	}
}
