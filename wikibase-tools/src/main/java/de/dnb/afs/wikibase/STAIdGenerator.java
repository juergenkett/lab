package de.dnb.afs.wikibase;

import java.io.IOException;

import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public interface STAIdGenerator {

	
	public String generateId(WbEntityProperties props) throws MediaWikiApiErrorException, IOException;
	
	
}
