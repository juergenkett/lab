package de.dnb.afs.wikibase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wikidata.wdtk.datamodel.helpers.TermedDocumentUpdateBuilder;
import org.wikidata.wdtk.datamodel.implementation.StatementUpdateImpl;
import org.wikidata.wdtk.datamodel.implementation.TermUpdateImpl;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public class WbEntityEditorImpl extends WbEntityLoaderImpl implements WbEntityEditor {

	private static final Log logger = LogFactory.getLog(WbEntityEditorImpl.class);

	private WikibaseDataEditor wbde;

	public WbEntityEditorImpl(WikibaseDataEditor wbde, WikibaseDataFetcher wbdf)
			throws MediaWikiApiErrorException, IOException {
		super(wbdf);
		this.wbde = wbde;
	}

	public TermedStatementDocument createEntity(TermedStatementDocument entity) throws IOException, MediaWikiApiErrorException {
		return (TermedStatementDocument) wbde.createEntityDocument(entity, null, null);
	}

	public TermedStatementDocument updateEntity(TermedStatementDocument oldVersion, TermedStatementDocument newVersion)
			throws MediaWikiApiErrorException, IOException {
		List<Statement> statements = new ArrayList<Statement>();
		newVersion.getAllStatements().forEachRemaining(statements::add);
		updateEntity(oldVersion, newVersion.getLabels(), statements);
		return (TermedStatementDocument) getEntity(oldVersion.getEntityId().getId());
	}

	@Override
	public TermedStatementDocument updateEntity(TermedStatementDocument existingEntity)
			throws MediaWikiApiErrorException, IOException {
		List<Statement> statements = new ArrayList<Statement>();
		existingEntity.getAllStatements().forEachRemaining(statements::add);
		updateEntity(existingEntity, existingEntity.getLabels(), statements);
		return (TermedStatementDocument) getEntity(existingEntity.getEntityId().getId());
	}

	@Override
	public void updateEntity(TermedStatementDocument oldVersion, Map<String, MonolingualTextValue> newLabels,
			List<Statement> newStatements) throws IOException, MediaWikiApiErrorException {
		Validate.isTrue(!oldVersion.getEntityId().isPlaceholder() ,
				"die zu aktualisierende Version hat eine Placeholder-Id");
	 
		TermedDocumentUpdateBuilder entityUpdateBuilder = TermedDocumentUpdateBuilder
				.forBaseRevisionId(oldVersion.getEntityId(), oldVersion.getRevisionId());
		if (newStatements != null && !newStatements.isEmpty()) {
			logger.debug("Füge statements zu Update hinzu");
			StatementUpdate statementsUpdate = new StatementUpdateImpl(newStatements,
					Collections.<Statement>emptyList(), Collections.<String>emptyList());
			entityUpdateBuilder = entityUpdateBuilder.updateStatements(statementsUpdate);
		}
		if (newLabels != null && !newLabels.isEmpty()) {
			logger.debug("Füge labels zu Update hinzu: " + newLabels.values());
			TermUpdate termUpdate = new TermUpdateImpl(newLabels.values(), Collections.emptyList());
			entityUpdateBuilder = entityUpdateBuilder.updateLabels(termUpdate);
		}
		executeEdit(entityUpdateBuilder.build());
	}

	private void executeEdit(StatementDocumentUpdate documentUpdate) throws IOException, MediaWikiApiErrorException {
		try {
			logger.debug(documentUpdate);
			wbde.editEntityDocument(documentUpdate, true, "updating", null);
		} catch (com.fasterxml.jackson.databind.exc.ValueInstantiationException e) {
			logger.debug(
					"In der Antwort fehlt der Parameter datatype. Das führt in der WDTK-Bibliothek leider zu einem Fehler, der hier aufgefangen wird. ");
		}
	}

}
