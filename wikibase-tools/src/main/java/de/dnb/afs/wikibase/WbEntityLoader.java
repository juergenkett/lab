package de.dnb.afs.wikibase;

import java.io.IOException;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public interface WbEntityLoader {

	
	public EntityDocument lookupEntity(String query) throws MediaWikiApiErrorException, IOException;
	
	public PropertyDocument lookupProperty(String query) throws MediaWikiApiErrorException, IOException;

	public String lookupPID(String query) throws MediaWikiApiErrorException, IOException;

	public ItemDocument lookupItem(String query) throws MediaWikiApiErrorException, IOException;
	
	public String lookupQID(String query) throws MediaWikiApiErrorException, IOException;
	
	public EntityDocument getEntity(String wbEntityId) throws MediaWikiApiErrorException, IOException;

}
