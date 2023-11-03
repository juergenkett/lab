package de.dnb.afs.wikibase.confluence.mappings;

import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.WbMapping;

public abstract class AbstractWbMapping extends WbMapping {

	protected ConfluenceWbConfig config;

	protected WbEntityLoader entityLoader;

	public AbstractWbMapping(String labelPattern, PropertyIdValue wbId, ConfluenceWbConfig config,
			WbEntityLoader entityLoader) {
		super(labelPattern, wbId);
		this.config = config;
		this.entityLoader = entityLoader;
	}

}
