package de.dnb.afs.wikibase;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public interface  WbEntityEditor extends WbEntityLoader {

	

	public TermedStatementDocument createEntity(TermedStatementDocument entity) throws IOException, MediaWikiApiErrorException;

	/**
	 * <p>Löscht die Eigenschaften einer Entität und ersetzt diese durch die gegebenen Eigenschaften</p>
	 * @param oldVersion
	 * @param newLabels
	 * @param newStatements
	 * @throws IOException
	 * @throws MediaWikiApiErrorException
	 */
	public void updateEntity(TermedStatementDocument oldVersion, Map<String, MonolingualTextValue> newLabels, List<Statement> newStatements)
			throws IOException, MediaWikiApiErrorException;
	
	
	/**
	 * @param entityId
	 * @param existingEntity
	 * @return
	 * @throws MediaWikiApiErrorException
	 * @throws IOException
	 */
	public TermedStatementDocument updateEntity(TermedStatementDocument existingEntity)
			throws MediaWikiApiErrorException, IOException ;
	
	/**
	 * 
	 * @param oldVersion
	 * @param newVersion
	 * @return
	 * @throws MediaWikiApiErrorException
	 * @throws IOException
	 */
	public TermedStatementDocument updateEntity(TermedStatementDocument oldVersion, TermedStatementDocument newVersion)
			throws MediaWikiApiErrorException, IOException; 

}
