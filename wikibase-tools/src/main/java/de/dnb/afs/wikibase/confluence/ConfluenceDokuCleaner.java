package de.dnb.afs.wikibase.confluence;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

public class ConfluenceDokuCleaner {

	private static final Log logger = LogFactory.getLog(ConfluenceDokuCleaner.class);

	public Document clean(Document document) {
		Element head = document.head();
		Cleaner cleaner = new Cleaner(Safelist.basic().addAttributes("div", "data-macro-name").addAttributes("h1", "id")
				.addTags("head", "h2", "h3", "h4").addAttributes("span", "style"));
		document = cleaner.clean(document);

		Document newDocument = Document.createShell(document.baseUri());
		newDocument.outputSettings().escapeMode(EscapeMode.extended);
		newDocument.outputSettings().charset(CharEncoding.UTF_8);
		newDocument.title(getLabel(document));
		newDocument.head().appendChildren(cleanHead(head));
		for (Element panel : document.body().getElementsByAttributeValue("data-macro-name", "panel")) {
			cleanPanel(panel);
			newDocument.body().appendChild(panel);
		}
		logger.debug(newDocument);
		return newDocument;
	}

	public Elements cleanHead(Element head) {
		logger.debug(head);
		Elements ret = new Elements();
		for (Node child : head.childNodes()) {
			if (child instanceof Element) {
				Element e = (Element) child;
				if (e.attr("name").equals("ajs-page-id")) {
					e.attr("id", "confluence-page-id");
					ret.add(e);
				} 
			}
		}
		return ret;
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
				if (isRef(e)) {
					e.tagName("a");
					e.attr("class", "ref");
					e.removeAttr("style");
				} else if (isLocalRef(e)) {
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

	public boolean isRef(Node e) {
		return StringUtils.deleteWhitespace(e.attr("style")).equals("color:rgb(255,0,255);");
	}

	public boolean isLocalRef(Node e) {
		return StringUtils.deleteWhitespace(e.attr("style")).equals("color:rgb(255,0,0);");
	}
}
