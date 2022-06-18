package de.dnb.afs.wikibase.confluence;

import org.jsoup.nodes.Element;

public interface ConfluenceDokuParserHandler {

	public void onPanel(Element Panel);

	public void onLabel(String labelDe, String labelEn);
}
