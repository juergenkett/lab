package de.dnb.afs.wikibase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.TermedDocumentUpdateBuilder;
import org.wikidata.wdtk.datamodel.implementation.StatementUpdateImpl;
import org.wikidata.wdtk.datamodel.implementation.TermUpdateImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StatementUpdate;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TermUpdate;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import com.fasterxml.jackson.annotation.JacksonInject.Value;

/**
 * @author kett
 *
 */
public class WbEntityEditorImpl extends WbEntityLoaderImpl implements WbEntityEditor {

	private static final Log logger = LogFactory.getLog(WbEntityEditorImpl.class);

	private WikibaseDataEditor wbde;

	private boolean isAddOnlyMode = false;

	private boolean isReadIOnly = false;

	public WbEntityEditorImpl(WikibaseDataEditor wbde, WikibaseDataFetcher wbdf)
			throws MediaWikiApiErrorException, IOException {
		super(wbdf);
		this.wbde = wbde;
	}

	public boolean isAddOnlyMode() {
		return isAddOnlyMode;
	}

	public void setAddOnlyMode(boolean isAddOnlyMode) {
		this.isAddOnlyMode = isAddOnlyMode;
	}

	public boolean isReadIOnly() {
		return isReadIOnly;
	}

	public void setReadIOnly(boolean isReadIOnly) {
		this.isReadIOnly = isReadIOnly;
	}

	public TermedStatementDocument updateEntity(TermedStatementDocument oldVersion, TermedStatementDocument newVersion)
			throws MediaWikiApiErrorException, IOException {
		List<Statement> statements = new ArrayList<Statement>();
		newVersion.getAllStatements().forEachRemaining(statements::add);
		updateEntity(oldVersion, newVersion.getLabels(), newVersion.getAliases(), statements);
		return (TermedStatementDocument) getEntity(oldVersion.getEntityId().getId());
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
		updateEntity(entityUpdateBuilder.build());
	}

	public void updateEntity(StatementDocumentUpdate documentUpdate) throws IOException, MediaWikiApiErrorException {
		if (isReadIOnly) {
			logger.debug("READ ONLY MODE: Daten werden nicht geschrieben.");
			return;
		}
		try {
			logger.debug("Schreibe in WB ... ");
			wbde.editEntityDocument(documentUpdate, false, "updating", null);
		} catch (com.fasterxml.jackson.databind.exc.ValueInstantiationException e) {
			logger.debug(
					"In der Antwort fehlt der Parameter datatype. Das führt in der WDTK-Bibliothek leider zu einem Fehler, der hier aufgefangen wird. ");
		}
	}

	@Override
	public void updateEntity(WbEntityProperties entity) throws IOException, MediaWikiApiErrorException {
		TermedStatementDocument oldVersion = (TermedStatementDocument) this.wbdf
				.getEntityDocument(entity.getEntityId().getId());

		updateEntity(entity, oldVersion);
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
	public void createOrUpdateItem(WbEntityProperties props) throws IOException, MediaWikiApiErrorException {
		/*
		 * prüfe, ob das Item bereits existiert
		 */
		ItemDocument oldVersion = null;
		if (props.getEntityId().isPlaceholder()) {
			oldVersion = (ItemDocument) this.lookupEntity(props.getLabel_DE());
		} else {
			oldVersion = (ItemDocument) this.wbdf.getEntityDocument(props.getEntityId().getId());
		}

		if (oldVersion != null) {
			logger.debug("Alte Version des Item mit Id " + oldVersion.getEntityId().getId() + " gefunden. ");
			props.setRevisionId(oldVersion.getRevisionId());
			props.setEntityId(oldVersion.getEntityId());
			updateEntity(props, oldVersion);
		} else {
			createEntity(props);
		}
	}

	public void createEntity(WbEntityProperties props) throws IOException, MediaWikiApiErrorException {

		logger.debug("erzeuge neues Item ... ");
		ItemDocument document = Datamodel.makeItemDocument(ItemIdValue.NULL,
				new ArrayList<MonolingualTextValue>(props.getLabels().values()),
				new ArrayList<MonolingualTextValue>(props.getDescriptions().values()), props.getAliases(),
				makeStatementGroups(props.getStatements()), Collections.emptyMap(), 0);
		if (isReadIOnly) {
			logger.debug("READ ONLY MODE: Daten werden nicht geschrieben.");
			return;
		}
		TermedStatementDocument result = (TermedStatementDocument) wbde.createEntityDocument(document, null, null);
		props.setRevisionId(result.getRevisionId());
		props.setEntityId(result.getEntityId());
	}

	private void updateEntity(WbEntityProperties entity, TermedStatementDocument oldVersion)
			throws IOException, MediaWikiApiErrorException {
		TermedDocumentUpdateBuilder entityUpdateBuilder = TermedDocumentUpdateBuilder
				.forBaseRevisionId(entity.getEntityId(), entity.getRevisionId());

		if (entity.getStatements() != null && !entity.getStatements().isEmpty()) {
			logger.debug("Füge statements zu Update hinzu");
			Collection<Statement> update = new ArrayList<Statement>();
			Collection<Statement> add = new ArrayList<Statement>();
			Collection<String> delete = new HashSet<String>();

			for (Statement statement : entity.getStatements()) {
				String propertyId =  statement.getMainSnak().getPropertyId().getId();
				if (statement.getValue() instanceof StringValue) {
					String stringValue = ((StringValue)statement.getValue()).getString();
					if (stringValue.isEmpty()) {
						logger.warn("Überspringe Statement, das der String-Inhalt leer ist " + propertyId + "=" + statement.getValue());
						continue;
					}
				}
				if (isAddOnlyMode) {
					if (statement.getStatementId() == null || statement.getStatementId().isEmpty()) {
						if (!isExactStatementExists(statement, oldVersion)) {
							logger.debug(
									"Füge statement zu Add hinzu " + propertyId);
							add.add(statement);
						}
					}
				} else {

					List<String> existingStatementIds = getExistingStatementIds(statement.getMainSnak().getPropertyId().getId(),
							oldVersion);
//					/*
//					 * Falls eine Property nur einmal mit einem Statement belegt ist, überschreibe
//					 * dieses oder (falls es aktuell ist), lass alles wie es ist.
//					 */
//					if (existingStatementIds.size() == 1) {
//						if (!isExactStatementExists(s, oldVersion)) {
//							if (existingStatementIds.get(0).equals(s.getStatementId())) {
//								update.add(s);
//							} else {
//								delete.addAll(existingStatementIds);
//								add.add(s);
//							}
//						}
//						/*
//						 * Existiert zur Property bislang keine Statement oder gibt es mehrere
//						 * Statements dazu, dann lösche diese und füge das Statement als neues Statement
//						 * an
//						 */
//					} else {
					delete.addAll(existingStatementIds);
					logger.debug(
							"Füge statement zu Add hinzu " + propertyId + "=" + statement.getValue() + " attributes ");

					add.add(statement);
//					}
				}
			}
			logger.info(
					"Update ... #add: " + add.size() + ", #update: " + update.size() + ", #delete: " + delete.size());
			if (add.size() > 0 || update.size() > 0) {

				StatementUpdate statementsUpdate = new StatementUpdateImpl(add, update, delete);
				entityUpdateBuilder = entityUpdateBuilder.updateStatements(statementsUpdate);
				updateEntity(entityUpdateBuilder.build());
			} else {
				logger.debug("Element ist aktuell. Kein Update ...");
			}

		}

//		if (entity.getLabels() != null && !entity.getLabels().isEmpty()) {
//			logger.debug("Füge labels zu Update hinzu: " + entity.getLabels().values());
//			TermUpdate labelUpdate = new TermUpdateImpl(entity.getLabels().values(), Collections.emptyList());
//			entityUpdateBuilder = entityUpdateBuilder.updateLabels(labelUpdate);
//		}
//		if (entity.getAliases() != null && !entity.getAliases().isEmpty()) {
//			logger.debug("Füge Aliases zu Update hinzu: " + entity.getAliases());
//			entityUpdateBuilder.updateAliases("de",
//					new AliasUpdateImpl(entity.getAliases(), Collections.emptyList(), Collections.emptyList()));
//		}

	}

	private List<String> getExistingStatementIds(String propertyId, TermedStatementDocument oldVersion) {
		ArrayList<String> ret = new ArrayList<String>();
		for (StatementGroup group : oldVersion.getStatementGroups()) {
			if (propertyId.equals(group.getProperty().getId())) {
				logger.debug("Statement zu Property " + propertyId + " gefunden.");
				for (Statement oldStatement : group.getStatements()) {
					ret.add(oldStatement.getStatementId());
				}
			}
		}
		return ret;
	}

	public boolean isExactStatementExists(Statement statement, TermedStatementDocument oldVersion) {
		String propertyId = statement.getMainSnak().getPropertyId().getId();
		logger.debug("Prüfe, ob statement zur Property " + propertyId + " bereits inhaltsgleich existiert ...");
		for (StatementGroup group : oldVersion.getStatementGroups()) {
			if (propertyId.equals(group.getProperty().getId())) {
				String value = statement.getValue().toString();
				logger.debug("Statement zu Property " + propertyId + " gefunden. Vergleiche Inhalt ... " + value);
				for (Statement oldStatement : group.getStatements()) {
					if (value.equals(oldStatement.getValue().toString())) {
						logger.debug("Übereinstimmender Inhalt gefunden");
						return true;
					}
				}
				logger.debug("Kein übereinstimmender Inhalt gefunden");
			}
		}

		return false;
	}

	@Override
	public void setIsAddOnlyMode(boolean flag) {
		this.isAddOnlyMode = flag;
	}

}
