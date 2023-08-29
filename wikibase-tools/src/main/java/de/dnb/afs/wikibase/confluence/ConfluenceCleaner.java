package de.dnb.afs.wikibase.confluence;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import de.dnb.afs.wikibase.confluence.mappings.WbMapping4Rules;

public class ConfluenceCleaner {

	private static final Log logger = LogFactory.getLog(ConfluenceCleaner.class);

	public Document cleanElementDesc(Document document) {
		Element head = document.head();
		Cleaner cleaner = new Cleaner(Safelist.basic().removeTags("span").addAttributes("ri:url", "ri:value")
				.addAttributes("div", "data-macro-name").addAttributes("h1", "id").addAttributes("p", "style")
				.addTags("head", "h2", "h3", "h4", "h5", "code").removeAttributes("all:", "rel").removeTags("pre"));
//				.addAttributes("span", "style"));
		document = cleaner.clean(document);

		Document newDocument = Document.createShell(document.baseUri());
		newDocument.outputSettings().escapeMode(EscapeMode.extended);
		newDocument.outputSettings().charset(CharEncoding.UTF_8);
		newDocument.title(getLabel(document));
		newDocument.head().appendChildren(cleanHead(head));
		for (Element panel : document.body().getElementsByAttributeValue("data-macro-name", "panel")) {
			Element property = new Element("property");
			property.attr("label", panel.child(0).text());
			if (panel.children().size() > 1) {
				property.appendChildren(panel.child(1).childNodes());
				cleanValue(property);
				if (!isSkipProperty(property)) {
					newDocument.body().appendChild(property);
				}
			}
		}
		return newDocument;
	}

	public Document cleanRessource(Document document) {
		Element head = document.head();
		Cleaner cleaner = new Cleaner(Safelist.basic().removeTags("span").addAttributes("ri:url", "ri:value")
				.addAttributes("div", "data-macro-name").addAttributes("h1", "id")
				.addTags("head", "h2", "h3", "h4", "h5", "table", "tr", "td", "code").removeTags("pre"));
//				.addAttributes("span", "style"));
		document = cleaner.clean(document);

		Document newDocument = Document.createShell(document.baseUri());
		newDocument.outputSettings().escapeMode(EscapeMode.extended);
		newDocument.outputSettings().charset(CharEncoding.UTF_8);
		newDocument.title(getLabel(document));
		newDocument.outputSettings().prettyPrint();
		newDocument.head().appendChildren(cleanHead(head));

		Element property = null;

		for (Element h2 : document.body().getElementsByTag("h2")) {
			property = new Element("property");
			property.attr("label", h2.text());
			Element subproperty = null;
			for (Element sibling : h2.nextElementSiblings()) {
				if (sibling.normalName().equals("h2")) {
					break;
				}
				if (sibling.normalName().equals("h3")) {
					subproperty = new Element("subproperty");
					subproperty.attr("label", sibling.text());
					property.appendChild(subproperty);
				} else if (subproperty != null) {
					subproperty.appendChild(sibling);
				} else {
					property.appendChild(sibling);
				}
			}

			cleanValue(property);
			if (!isSkipProperty(property)) {
				newDocument.body().appendChild(property);
			}
		}
		return newDocument;
	}

	public void addSubproperties(Element property) {
		Element subproperty;
		for (Element h : property.getElementsByTag("h3")) {
			subproperty = new Element("subproperty");
			subproperty.attr("label", h.text());
			for (Element sibling : h.nextElementSiblings()) {
				if (sibling.normalName().equals("h3")) {
					break;
				}
				subproperty.appendChild(sibling);
			}
		}
	}

	public boolean isSkipProperty(Element property) {
		return property.attr("label").startsWith("Kommentare") || isEmptyProperty(property);
	}

	public static boolean isEmptyProperty(Element panel) {
		String content = panel.text().replaceAll("nbsp;", "").trim().toLowerCase();
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

	public static Map<String, String> getUrlQueryParameters(String href) {
		final Map<String, String> ret = new LinkedHashMap<String, String>();
		try {
			URL url = new URL(href);
			if (url.getQuery() != null) {
				final String[] pairs = url.getQuery().split("&");
				for (String pair : pairs) {
					final int idx = pair.indexOf("=");
					final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
					final String value = idx > 0 && pair.length() > idx + 1
							? URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
							: null;
					ret.put(key, value);
				}
			}
		} catch (MalformedURLException | UnsupportedEncodingException e) {
		}

		return ret;
	}

	public static String getUrlTitleParam(String href) {
		return getUrlQueryParameters(href).get("title");
	}

	public boolean isWbId(String value) {
		return value != null && value.matches("[PQ]\\d+");
	}

	public void cleanValue(Element panel) {
		for (Element element : panel.getAllElements()) {
			// remove empty tags (for example: <em></em> <div></div>)
			if (!element.hasText() && element.isBlock() && element.hasParent()) {
				element.remove();
				// remove sections that are not needed
			} else if (element.attr("data-macro-name").equals("expand")) {
				element.remove();
			} else if (element.normalName().equals("a")) {
				cleanA(element);
			} else if (element.normalName().equals("ri:url")) {
				cleanRiUrl(panel);
			} else if (element.normalName().equals("p")) {
				if (element.attr("style").startsWith("margin-left:")) {
					Element div = new Element("div");
					div.attr("style", element.attr("style"));
					List<Node> childNodes = element.childNodes();
					div.appendChildren(childNodes);
					element.appendChild(div);
				}
				element.removeAttr("style");
			}
//			else if (e.tag().normalName().equals("span")) {
//				cleanSpan(e);
//			} 
			else if (element.attr("data-macro-name").equals("info")) {
				element.tagName("example");
				element.removeAttr("data-macro-name");
				for (Element subelement : element.children()) {
					if (subelement.normalName().equals("div")) {
						subelement.unwrap();
					} else {
						String label = subelement.text();
						if (label.equals("Vollbeispiel")) {
							element.attr("class", "detailed-example");
							subelement.remove();
						} else {
							subelement.tagName("strong");
							element.attr("label", label);
						}
					}
				}
			} else if (element.attr("data-macro-name").equals("code")) {
				element.tagName("embed");
				element.clearAttributes();
				element.text(element.text());
			}
			// remove redundant nested elements (for example: <em><em>Text</em></em>
			else if (!element.isBlock() && element.hasParent() && element.parent().tagName().equals(element.tagName())
					&& element.attributes().size() == 0) {
				element.unwrap();
			} else if ((element.tagName().equals("em") || element.tagName().equals("strong"))
					&& (!element.hasText() || element.parent().tagName().equals(element.tagName()))) {
				element.unwrap();
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

	/**
	 * Korrigiere fehlerhafte interne Referenzen
	 * 
	 * @param e
	 */
	public void cleanA(Element e) {
		String href = e.attr("href");
		String wbRefEntityId = getUrlTitleParam(href);
		if (isWbId(wbRefEntityId)) {
			e.clearAttributes();
			e.attr("class", "ref");
			e.attr("href", wbRefEntityId);
		}
	}

	/**
	 * &height=563&width=915" alt="Dies ist ein Bild" style="border:5px"
	 * 
	 * @param e
	 */
	public void cleanRiUrl(Element e) {
		String href = e.attr("ri:value");
		String imgPath = getImgPath(href);
		if (imgPath != null) {
			e.tagName("img");
			e.clearAttributes();
			e.attr("src", imgPath);
			e.attr("height", "563");
			e.attr("width", "915");
			e.attr("alt", "Dies ist ein Bild");
			e.attr("style", "border:5px");
		}
	}

	public static String getImgPath(String href) {
		String ret = null;
		if (href == null)
			return null;
		try {
			URL url = new URL(href);
			ret = url.getPath().substring(url.getPath().lastIndexOf('/'));
		} catch (MalformedURLException e) {
		}
		return ret;
	}

	public void cleanSpan(Element e) {
		if (isRef(e)) {
			if (isContainingWords(e)) {
//				logger.debug("Füge Referenz hinzu: '" + e + "'");

				e.tagName("a");
				e.attr("class", "ref");
				e.removeAttr("style");
			} else {
//				logger.debug("Entferne Referenz: '" + e + "'");
				e.unwrap();

			}
		} else if (isLocalRef(e)) {
			if (isContainingWords(e)) {
//				logger.debug("Füge lokale Referenz hinzu: '" + e + "'");
				String anchorId = getLocalRef(e.text());
				e.tagName("a");
				e.attr("class", "localRef");
				e.removeAttr("style");
				e.attr("href", anchorId);

			} else {
//				logger.debug("Entferne lokale Referenz: '" + e + "'");
				e.unwrap();

			}
		} else {
//			logger.debug("Entferne span: '" + e + "'");
			e.unwrap();
		}
	}

	public static String getLocalRef(String text) {
		return "#" + WbMapping4Rules.getShortLabel(StringUtils.removeEnd(text.trim(), "."));
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
