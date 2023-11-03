package de.dnb.afs.wikibase;

import java.io.IOException;

import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public interface WbEntityEditor extends WbEntityLoader {

	public void createOrUpdateItem(WbEntityProperties entity)
			throws IOException, MediaWikiApiErrorException;

	public void updateEntity(WbEntityProperties entity) throws IOException, MediaWikiApiErrorException;
	
	public void setIsAddOnlyMode(boolean flag);
	
}
