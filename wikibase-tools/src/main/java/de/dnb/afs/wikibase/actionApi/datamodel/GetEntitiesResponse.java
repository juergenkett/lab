package de.dnb.afs.wikibase.actionApi.datamodel;

import java.util.Map;

public class GetEntitiesResponse {
	
	/**
	 * { "entities": { "Q11": { "type": "item", "id": "Q11", "labels": { ... },
	 * "descriptions": { ... }, "aliases": { ... }, "claims": { "P31": [ { ... } ]
	 * }, "sitelinks": {} } } }
	 */
	
	public Map<String, Entity> entities;
}
