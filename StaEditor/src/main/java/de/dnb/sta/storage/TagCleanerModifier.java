package de.dnb.sta.storage;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.safety.Safelist;

import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaContentModifier;
import de.dnb.sta.storage.datamodel.StaStatement;

public class TagCleanerModifier implements StaContentModifier {

	private static final OutputSettings OUTPUT_SETTINGS = new OutputSettings().charset("UTF-8").prettyPrint(false)
			.escapeMode(EscapeMode.xhtml);
	private static final Safelist SAFELIST_TEXT = Safelist.basicWithImages().addTags("example", "collapsible")
			.addAttributes("table", "summary", "width")
			.addAttributes("td", "abbr", "axis", "colspan", "rowspan", "width")
			.addAttributes("th", "abbr", "axis", "colspan", "rowspan", "scope", "width").removeTags("span");
	private static final Safelist SAFELIST_STRING = SAFELIST_TEXT.removeTags("p");


	@Override
	public StaContent modify(StaContent staContent) {
		for (StaStatement staValue : staContent.getStatements()) {
			switch (staValue.getProperty().getValueType()) {
			case ID:
				staValue.setValue(Jsoup.clean(staValue.getValue(), "", Safelist.none(), OUTPUT_SETTINGS).trim());
				break;
			case TEXT:
				staValue.setValue(Jsoup.clean(staValue.getValue(), "", SAFELIST_TEXT, OUTPUT_SETTINGS).trim());
				break;
			case STRING:
				staValue.setValue(Jsoup.clean(staValue.getValue(), "", SAFELIST_STRING, OUTPUT_SETTINGS).trim());
				break;
			}
		}
		return staContent;
	}
}
