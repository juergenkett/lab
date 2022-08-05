package de.dnb.afs.wikibase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermedDocumentUpdateBuilder;
import org.wikidata.wdtk.datamodel.implementation.AliasUpdateImpl;
import org.wikidata.wdtk.datamodel.implementation.ItemDocumentImpl;
import org.wikidata.wdtk.datamodel.implementation.StatementUpdateImpl;
import org.wikidata.wdtk.datamodel.implementation.TermUpdateImpl;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
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

	public TermedStatementDocument createEntity(TermedStatementDocument entity)
			throws IOException, MediaWikiApiErrorException {
		return (TermedStatementDocument) wbde.createEntityDocument(entity, null, null);
	}

	public TermedStatementDocument updateEntity(TermedStatementDocument oldVersion, TermedStatementDocument newVersion)
			throws MediaWikiApiErrorException, IOException {
		List<Statement> statements = new ArrayList<Statement>();
		newVersion.getAllStatements().forEachRemaining(statements::add);
		updateEntity(oldVersion, newVersion.getLabels(), newVersion.getAliases(), statements);
		return (TermedStatementDocument) getEntity(oldVersion.getEntityId().getId());
	}

	public TermedStatementDocument updateEntity(TermedStatementDocument existingEntity)
			throws MediaWikiApiErrorException, IOException {
		List<Statement> statements = new ArrayList<Statement>();
		existingEntity.getAllStatements().forEachRemaining(statements::add);
		updateEntity(existingEntity, existingEntity.getLabels(), existingEntity.getAliases(), statements);
		return (TermedStatementDocument) getEntity(existingEntity.getEntityId().getId());
	}

	public void updateEntity(TermedStatementDocument oldVersion, Map<String, MonolingualTextValue> newLabels,
			Map<String, List<MonolingualTextValue>> newAliases, List<Statement> statements)
			throws IOException, MediaWikiApiErrorException {
		Validate.isTrue(!oldVersion.getEntityId().isPlaceholder(),
				"die zu aktualisierende Version hat eine Placeholder-Id");

		TermedDocumentUpdateBuilder entityUpdateBuilder = TermedDocumentUpdateBuilder
				.forBaseRevisionId(oldVersion.getEntityId(), oldVersion.getRevisionId());
		if (statements != null && !statements.isEmpty()) {
			logger.debug("Füge statements zu Update hinzu");

			StatementUpdate statementsUpdate = new StatementUpdateImpl(statements, Collections.<Statement>emptyList(),
					Collections.<String>emptyList());
			entityUpdateBuilder = entityUpdateBuilder.updateStatements(statementsUpdate);
		}
		if (newLabels != null && !newLabels.isEmpty()) {
			logger.debug("Füge labels zu Update hinzu: " + newLabels.values());
			TermUpdate labelUpdate = new TermUpdateImpl(newLabels.values(), Collections.emptyList());
			entityUpdateBuilder = entityUpdateBuilder.updateLabels(labelUpdate);
		}
		updateEntity(entityUpdateBuilder.build(), true);
	}

	public void updateEntity(StatementDocumentUpdate documentUpdate, boolean clear)
			throws IOException, MediaWikiApiErrorException {
		try {
			wbde.editEntityDocument(documentUpdate, clear, "updating", null);
		} catch (com.fasterxml.jackson.databind.exc.ValueInstantiationException e) {
			logger.debug(
					"In der Antwort fehlt der Parameter datatype. Das führt in der WDTK-Bibliothek leider zu einem Fehler, der hier aufgefangen wird. ");
		}
	}

	@Override
	public void updateEntity(WbEntityProperties entity, boolean clear) throws IOException, MediaWikiApiErrorException {
		TermedDocumentUpdateBuilder entityUpdateBuilder = TermedDocumentUpdateBuilder
				.forBaseRevisionId(entity.getEntityId(), entity.getRevisionId());
		if (entity.getStatements() != null && !entity.getStatements().isEmpty()) {
			logger.debug("Füge statements zu Update hinzu");

			StatementUpdate statementsUpdate = new StatementUpdateImpl(entity.getStatements(),
					Collections.<Statement>emptyList(), Collections.<String>emptyList());
			entityUpdateBuilder = entityUpdateBuilder.updateStatements(statementsUpdate);
		}
		if (entity.getLabels() != null && !entity.getLabels().isEmpty()) {
			logger.debug("Füge labels zu Update hinzu: " + entity.getLabels().values());
			TermUpdate labelUpdate = new TermUpdateImpl(entity.getLabels().values(), Collections.emptyList());
			entityUpdateBuilder = entityUpdateBuilder.updateLabels(labelUpdate);
		}
		if (entity.getAliases() != null && !entity.getAliases().isEmpty()) {
			logger.debug("Füge Aliases zu Update hinzu: " + entity.getAliases());
			entityUpdateBuilder.updateAliases("de",
					new AliasUpdateImpl(entity.getAliases(), Collections.emptyList(), Collections.emptyList()));
		}
		updateEntity(entityUpdateBuilder.build(), clear);

	}

	public static List<StatementGroup> makeStatementGroups(List<Statement> statements) {
		Map<String, List<Statement>> sortedStatements = new HashMap<String, List<Statement>>();
		for (Statement s : statements) {
			List<Statement> list = sortedStatements.get(s.getMainSnak().getPropertyId().getId());
			if (list == null) {
				list = new ArrayList<Statement>();
				sortedStatements.put(s.getMainSnak().getPropertyId().getId(), list);
			}
			list.add(s);
		}
		ArrayList<StatementGroup> ret = new ArrayList<StatementGroup>();
		for (List<Statement> statementList : sortedStatements.values()) {
			ret.add(Datamodel.makeStatementGroup(statementList));
		}
		
	
		
		return ret;
	}

	@Override
	public void createItem(WbEntityProperties props) throws IOException, MediaWikiApiErrorException {

		ItemDocument document = Datamodel.makeItemDocument(ItemIdValue.NULL,
				new ArrayList<MonolingualTextValue>(props.getLabels().values()),
				new ArrayList<MonolingualTextValue>(props.getDescriptions().values()), props.getAliases(),
				makeStatementGroups(props.getStatements()), Collections.emptyMap() , 0);

		
//		ItemDocumentBuilder builder = ItemDocumentBuilder.forItemId(ItemIdValue.NULL);
//		for (String language : props.getLabels().keySet()) {
//			builder.withLabel(props.getLabels().get(language));
//		}
//		for (MonolingualTextValue alias : props.getAliases()) {
//			builder.withLabel(alias);
//		}
//		for (MonolingualTextValue desc : props.getDescriptions().values()) {
//			logger.debug(desc.getLanguageCode() + " " + desc.getText());
//			builder.withDescription(desc);
//		}
//		for (Statement statement : props.getStatements()) {
//			builder.withStatement(statement);
//		}
//		ItemDocument document = builder.build();
		TermedStatementDocument result = (TermedStatementDocument) wbde.createEntityDocument(document, null, null);
		props.setRevisionId(result.getRevisionId());
		props.setEntityId(result.getEntityId());
	}

}
