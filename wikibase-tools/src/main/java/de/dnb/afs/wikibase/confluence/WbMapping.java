package de.dnb.afs.wikibase.confluence;

import java.io.IOException;

import org.jsoup.select.Elements;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityProperties;

public abstract class WbMapping {

	private String labelPattern;

	public String getLabelPattern() {
		return labelPattern;
	}

	public WbMapping(String labelPattern) {
		this.labelPattern = labelPattern;
	}

	public abstract void doMap(String panelLabelDe, Elements paragraphs, WbEntityProperties entity)  throws MediaWikiApiErrorException, IOException;
}
