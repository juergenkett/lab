package de.dnb.afs.wikibase.restApi.datamodel;

import java.util.List;
import java.util.Map;

public class Item {

	public final String type = "item";

	String id;

	List<Label> labels;

	List<Description> descriptions;

	List<Alias> aliases;

	Map<String, Statement> statements;

	Sitelinks sitelinks;

}
