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

import de.dnb.afs.wikibase.confluence.mappings.WbMappingRule;

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
			if (isRemovePanel(panel)) {
				logger.debug("# remove panel " + panel.text());
				panel.remove();
				continue;
			}
			cleanPanel(panel);
			newDocument.body().appendChild(panel);
		}
		return newDocument;
	}

	public boolean isRemovePanel(Element panel) {
		return panel.text().replaceAll("&nbsp;", " ").trim().startsWith("Kommentare") || isEmptyPanel(panel);
	}

	public static boolean isEmptyPanel(Element panel) {
		if (panel.children().size() < 2)
			return true;
		if (panel.child(1).hasAttr("data-macro-name"))
			return true;
		String content = panel.child(1).text().replaceAll("nbsp;", "").trim().toLowerCase();

		if (content.isBlank())
			return true;
		if (content.startsWith("nicht zutreffend")) {
			return true;
		}
		if (content.startsWith("kommentare"))
			return true;

		return false;
	}

	public Elements cleanHead(Element head) {
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

	public static String getLabel(Document document) {
		Element titleElement = document.getElementById("title-text");
		if (titleElement != null) {
			return getLabel(titleElement.text());
		}
		return null;
	}

	public static String getLabel(String label) {
		return label.replaceFirst("AP .* \\| ", "").replaceFirst(".* - .* - ", "");
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
			} else if (e.tag().normalName().equals("span")) {
				if (isRef(e)) {
					if (isContainingWords(e)) {
//						logger.debug("Füge Referenz hinzu: '" + e + "'");
						e.tagName("a");
						e.attr("class", "ref");
						e.removeAttr("style");
					} else {
//						logger.debug("Entferne Referenz: '" + e + "'");
						e.unwrap();

					}
				} else if (isLocalRef(e)) {
					if (isContainingWords(e)) {
//						logger.debug("Füge lokale Referenz hinzu: '" + e + "'");
						String anchorId = getLocalRef(e.text());
						e.tagName("a");
						e.attr("class", "localRef");
						e.removeAttr("style");
						e.attr("href", anchorId);

					} else {
//						logger.debug("Entferne lokale Referenz: '" + e + "'");
						e.unwrap();

					}
				} else {
//					logger.debug("Entferne span: '" + e + "'");
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

//			/*
//			 * bette Listen-Elemente in <p> Tags ein
//			 */
//			if ((e.normalName().equals("ul") || e.normalName().equals("ol")) && !e.parent().normalName().equals("p")) {
//				Element p = new Element("p");
//				e.replaceWith(p);
//				p.appendChild(e);
//			}

		}
	}

	public static String getLocalRef(String text) {
		return "#" + WbMappingRule.getShortLabel(StringUtils.removeEnd(text.trim(), "."));
	}

	public boolean isContainingWords(Element e) {
		String text = e.text().trim();
		return text.length() > 3 && text.matches("\\w.*");
	}

	public boolean isRef(Element e) {
		boolean isMarkedAsRef = StringUtils.deleteWhitespace(e.attr("style")).equals("color:rgb(255,0,255);");
		boolean isNotContaingOvewritingMark = e.getElementsByAttribute("style").size() < 2;

		return isMarkedAsRef && isNotContaingOvewritingMark;
	}

	public boolean isLocalRef(Element e) {
		boolean isMarkedAsLocalRef = StringUtils.deleteWhitespace(e.attr("style")).equals("color:rgb(255,0,0);");
		boolean isNotContaingOverwritingMark = e.getElementsByAttribute("style").size() < 2;
		return isMarkedAsLocalRef && isNotContaingOverwritingMark;

	}
}
