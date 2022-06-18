package de.dnb.afs.wikibase.confluence;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class ConfluenceDokuParser {

	private static final Log logger = LogFactory.getLog(ConfluenceDokuParser.class);

	private ConfluenceDokuParserHandler handler;

	public final static String EXAMPLE_KEY = "Beispiel";

	public ConfluenceDokuParser(ConfluenceDokuParserHandler handler) {
		this.handler = handler;
	}

	public String getLabel(Document document) {
		Element titleElement = document.getElementById("title-text");
		if (titleElement != null) {
			String label = titleElement.text().replaceFirst("AP .* \\| .* - .* - ", "");
			logger.debug(label);
			return label;
		}
		return null;
	}

	public Elements getPanels(Document document) throws IOException {
		Elements panels = document.getElementsByAttributeValue("data-macro-name", "panel");

		for (Element panel : panels) {
			for (Element e : panel.getAllElements()) {
				cleanElement(e);
			}
		}
		return panels;
	}

	public Element cleanElement(Element e) {
		// remove empty tags (for example: <em></em> <div></div>)
		if (!e.hasText() && e.isBlock()) {
			e.remove();
			// remove redundant nested elements (for example: <em><em>Text</em></em>
		} else if (e.attr("data-macro-name").equals("exapand")) {
			e.remove();
		}
		// remove sections that are not needed
		else if (e.children().size() > 0 && e.child(0).tagName().equals(e.tagName()) && e.attributes().size() == 0) {
			e.unwrap();
		}
		return e;
	}

	public void parse(String pageId) throws IOException {

		final String pathname = "src" + File.separator + "test" + File.separator + "confluence" + File.separator
				+ pageId + ".html";
		File file = new File(pathname);

		logger.debug(file.getName());

		Document document = Jsoup.parse(file, "UTF-8", "");
		Cleaner cleaner = new Cleaner(
				Whitelist.basic().addAttributes("div", "data-macro-name").addAttributes("h1", "id"));
		document = cleaner.clean(document);
		document.outputSettings().escapeMode(EscapeMode.xhtml);
		document.outputSettings().charset("UTF-8");

		String label = getLabel(document);
		String labelDe = null;
		String labelEn = null;
		if (label != null) {
			String[] labels = label.split("/");
			labelDe = labels[0];
			if (labels.length > 1) {
				labelEn = labels[1];
			}
			this.handler.onLabel(labelDe, labelEn);
		}

		Elements panels = getPanels(document);
		for (Element panel : panels) {
			this.handler.onPanel(panel);
		}
	}

}