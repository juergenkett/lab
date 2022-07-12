package de.dnb.afs.wikibase;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;


public class WbEntityEditorMock implements WbEntityEditor {
	


	@Override
	public TermedStatementDocument createEntity(TermedStatementDocument item) throws IOException, MediaWikiApiErrorException {
		return item;
	}

	@Override
	public void updateEntity(TermedStatementDocument item,  Map<String, MonolingualTextValue> labels, List<Statement> statements)
			throws IOException, MediaWikiApiErrorException {

	}

	@Override
	public TermedStatementDocument updateEntity(TermedStatementDocument existingEntity)
			throws MediaWikiApiErrorException, IOException {

		return null;
	}

	@Override
	public TermedStatementDocument updateEntity(TermedStatementDocument oldVersion, TermedStatementDocument newVersion)
			throws MediaWikiApiErrorException, IOException {

		return null;
	}

	@Override
	public EntityDocument lookupEntity(String query) throws MediaWikiApiErrorException, IOException {

		return null;
	}

	@Override
	public EntityDocument getEntity(String wbEntityId) throws MediaWikiApiErrorException, IOException {

		return null;
	}
}
