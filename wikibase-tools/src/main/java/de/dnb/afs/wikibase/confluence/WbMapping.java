package de.dnb.afs.wikibase.confluence;

import java.util.List;
import java.util.Map;

import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public abstract class WbMapping {

	private String labelPattern;

	public String getLabelPattern() {
		return labelPattern;
	}

	public WbMapping(String labelPattern) {
		this.labelPattern = labelPattern;
	}

	public abstract void doMap(Map<String, MonolingualTextValue> entityLabels, String panelLabelDe, Elements paragraphs, EntityIdValue wbEntityId,
			List<Statement> wbStatements);
}
