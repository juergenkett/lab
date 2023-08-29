package de.dnb.afs.wikibase.confluence;

import java.io.IOException;

import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityProperties;

public abstract class WbMapping {

	protected String labelPattern;
	protected PropertyIdValue wbId;

	public String getLabelPattern() {
		return labelPattern;
	}

	public WbMapping(String labelPattern, PropertyIdValue wbId) {
		this.labelPattern = labelPattern;
		this.wbId = wbId;
	}

	public abstract void doMap(String panelLabelDe, Elements paragraphs, WbEntityProperties entity)  throws MediaWikiApiErrorException, IOException;
	
//	public void doMapIfNotExisting(String panelLabelDe, Elements paragraphs, WbEntityProperties entity)  throws MediaWikiApiErrorException, IOException {
//		
//		doMap(panelLabelDe, paragraphs, entity);
//	}
}
