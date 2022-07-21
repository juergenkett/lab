package de.dnb.afs.wikibase;

import java.io.IOException;

import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public interface WbEntityEditor extends WbEntityLoader {

	public TermedStatementDocument createEntity(TermedStatementDocument entity)
			throws IOException, MediaWikiApiErrorException;
	
	public void createItem(WbEntityProperties entity)
			throws IOException, MediaWikiApiErrorException;

	/**
	 * @param entityId
	 * @param existingEntity
	 * @return
	 * @throws MediaWikiApiErrorException
	 * @throws IOException
	 */
	public TermedStatementDocument updateEntity(TermedStatementDocument existingEntity)
			throws MediaWikiApiErrorException, IOException;

	public void updateEntity(WbEntityProperties entity, boolean clear) throws IOException, MediaWikiApiErrorException;
}
