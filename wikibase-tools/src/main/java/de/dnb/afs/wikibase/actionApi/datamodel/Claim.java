package de.dnb.afs.wikibase.actionApi.datamodel;

import java.util.List;
import java.util.Map;

class Claim {
	public String id;
	public Snak mainsnak;
	public String type = "statement";
	public String rank = "normal";
	public Map<String, List<Snak>> qualifiers;
	public List<Reference> references;
}
