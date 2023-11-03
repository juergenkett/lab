package de.dnb.afs.wikibase.actionApi.datamodel;

import java.util.List;
import java.util.Map;


/**
    id
        The canonical ID of the entity.
    type
        The entity type identifier. “item” for data items, and “property” for properties.
    datatype
        The datatype to be used with the Property (Properties only)
    labels
        Contains the labels in different languages, see Labels, Descriptions and Aliases.
    descriptions
        Contains the descriptions in different languages, see Labels, Descriptions and Aliases.
    aliases
        Contains aliases in different languages, see Labels, Descriptions and Aliases.
    claims
        Contains any number of statements, groups by property. Note: WikibaseMediaInfo uses the "statements" key instead. 
    sitelinks
        Contains sitelinks to pages on different sites describing the item, see [Sitelinks] (Items only).
 */

public class Entity {
	
	public String id;
	public String type;
	public String datatype;
	public Map<String, Label> labels;
	public Map<String, Label> descriptions;
	public Map<String, List<Label>> aliases;
	public Map<String, List<Claim>> claims;
	public Map<String, Sitelink> sitelinks;
	public String lastrevid;
	public String modified;
	public String title;
	public String pageId;
	public String ns;
}