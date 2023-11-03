package de.dnb.sta.storage.wikibase;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.PropertyUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermedDocumentUpdateBuilder;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocument;
import org.wikidata.wdtk.datamodel.interfaces.TermedStatementDocumentUpdate;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.wikibaseapi.OAuthApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.sta.storage.StaContentStorage;
import de.dnb.sta.storage.StaPropertyStorage;
import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaContentMapper;
import de.dnb.sta.storage.datamodel.StaDatamodel.Action;
import de.dnb.sta.storage.datamodel.StaDatamodel.Type;
import de.dnb.sta.storage.datamodel.StaProperty;
import de.dnb.sta.storage.datamodel.StaStatement;

public class StaStorageWikibase implements StaContentStorage,
		StaContentMapper<TermedStatementDocumentUpdate, TermedStatementDocument>, StaPropertyStorage {

	private WikibaseDataEditor wbEditor;
	private WikibaseDataFetcher wbReader;
	private final static String WIKIBASE_KEY = "wikibase";
	public final static String STA_IRI = "sta.dnb.de";
	public static final String EMBEDS_KEY = "P396";
	public static final String IS_EMBEDED_KEY = "P397";
	public static final String LAYOUT_KEY = "P389";
	public static final String DESCRIPTION_KEY = "P7";

	private static final Logger LOGGER = System.getLogger(StaStorageWikibase.class.getName());
	public static final String H1_KEY = "Q1343";
	public static final String H2_KEY = "Q1346";
	public static final String H3_KEY = "Q1347";
	public static final String OL_KEY = "Q1345";
	public static final String UL_KEY = "Q1344";
	public static final String EXAMPLE_KEY = "Q3399";
	public static final String COLLAPSIBLE_KEY = "Q3107";
	public static final String EM_KEY = "Q3127";
	public static final String STRONG_KEY = "Q3128";

//	private Map<String, WikibaseMapper> mappers;

	private Map<String, StaProperty> propertyCache = new HashMap<String, StaProperty>();

	private StaValueVisitor valueVisitor = new StaValueVisitor();

	private String wbBaseUrl;

	public StaStorageWikibase(WbProps wbProps) {
		OAuthApiConnection apiConnection = new OAuthApiConnection(wbProps.wbUrl + "/w/api.php", wbProps.consumerKey,
				wbProps.consumerSecret, wbProps.accessToken, wbProps.accessSecret);

		this.wbEditor = new WikibaseDataEditor(apiConnection, StaStorageWikibase.STA_IRI);
		this.wbReader = new WikibaseDataFetcher(apiConnection, StaStorageWikibase.STA_IRI);
		this.wbEditor.setEditAsBot(true);
		this.wbEditor.setAverageTimePerEdit(100);
		this.wbBaseUrl = wbProps.wbUrl;
//		this.mappers = new HashMap<String, WikibaseMapper>();
	}

	@Override
	public String getKey() {
		return WIKIBASE_KEY;
	}

	@Override
	public StaContent load(String id) {
		try {
			EntityDocument wbEntity = wbReader.getEntityDocument(id);
			return modify4Load(map((TermedStatementDocument) wbEntity));
		} catch (MediaWikiApiErrorException | IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String save(StaContent staContent) {
		try {
			String source = staContent.getSourceKey();
			String sourceInfo = "Quelle: " + source + ", " + source + "-id: " + staContent.getId(source) + ", " + source
					+ "-version: " + staContent.getVersion(source);
			if (staContent.getId(getKey()) == null) {
				// neue Entität anlegen
				LOGGER.log(Level.INFO, "Lege neue Entität an. " + sourceInfo);
				create(staContent, sourceInfo);
			}
			// bestehende Entität aktualisieren
			LOGGER.log(Level.INFO,
					"Aktualisiere Eigenschaften der Entität " + staContent.getId(getKey()) + ". " + sourceInfo);
			TermedStatementDocumentUpdate edit = map(modify4Save(staContent));
			LOGGER.log(Level.INFO, edit.getStatements().getReplaced());

			this.wbEditor.editEntityDocument(edit, false, sourceInfo, null);
			return staContent.getId(getKey());
		} catch (IOException | MediaWikiApiErrorException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <p>
	 * Legt eine neue Wikibase-Entität (Item oder Property) für ein
	 * StaContent-Objekt an. Neuangelegte Wikibase-Properties haben immer den
	 * Wikibase-Wertetyp "STRING" (dt. "Zeichenkette")
	 * </p>
	 * 
	 * @param staContent
	 * @param sourceInfo - natürlichsprachliche Information zur Herkunft des
	 *                   StaContent. Diese wird Wikibase für das ChangeLog
	 *                   übergeben.
	 * @return Wikibase ID der Entität
	 * @throws IOException
	 * @throws MediaWikiApiErrorException
	 */
	public void create(StaContent staContent, String sourceInfo) throws IOException, MediaWikiApiErrorException {
		TermedStatementDocument docIn = null;
		String wbType = staContent.getType(getKey());
		if (wbType == null)
			throw new IllegalArgumentException(
					"Um eine neue Entiät anzulegen, muss der Wikibase-Entitätstyp ('Item' oder 'Property') gesetzt sein");
		switch (staContent.getType(getKey())) {
		case EntityIdValue.ET_ITEM: {
			docIn = Datamodel.makeItemDocument(ItemIdValue.NULL);
			break;
		}
		case EntityIdValue.ET_PROPERTY: {
			docIn = Datamodel.makePropertyDocument(PropertyIdValue.NULL,
					Datamodel.makeDatatypeIdValueFromJsonString(DatatypeIdValue.JSON_DT_STRING));
			break;
		}
		default:
			throw new IllegalArgumentException(
					"Nicht unterstützer Wikibase-Entitätstyp: " + staContent.getType(getKey()));
		}

		// label hinzufügen
		docIn.withLabel(Datamodel.makeMonolingualTextValue(staContent.getTitle(), "de"));
//		// statements hinzufügen
//		for (StaStatementGroup staStatementsGroup : staContent.getStatementGroups()) {
//			PropertyIdValue propId = Datamodel.makePropertyIdValue(staStatementsGroup.getProperty().getKey(), STA_IRI);
//			for (StaValue staValue : staStatementsGroup.getValues()) {
//				docIn.withStatement(mapStatement(docIn.getEntityId(), propId, staValue));
//			}
//		}
		EntityDocument docOut = this.wbEditor.createEntityDocument(docIn, "Leere Entität für Daten aus " + sourceInfo,
				null);
		staContent.setId(getKey(), docOut.getEntityId().getId());
		staContent.setVersion(getKey(), docOut.getRevisionId());
	}

	public StaContent modify4Load(StaContent staContent) {
	     new ChainTextValuesModifier().modify(staContent);
		return staContent;
	}

	public StaContent modify4Save(StaContent staContent) {
		SplitTextValuesModifier modifier = new SplitTextValuesModifier();
		modifier.setWikibaseStorage(this);
		modifier.modify(staContent);
		return staContent;
	}

	@Override
	public TermedStatementDocumentUpdate map(StaContent staContent) {

		EntityIdValue entityId = null;
		TermedDocumentUpdateBuilder updateBuilder = null;
		switch (staContent.getType(getKey())) {
		case EntityIdValue.ET_ITEM:
			entityId = Datamodel.makeItemIdValue(staContent.getId(getKey()), STA_IRI);
			updateBuilder = ItemUpdateBuilder.forBaseRevisionId(entityId, staContent.getVersion(getKey()));
			break;
		case EntityIdValue.ET_PROPERTY:
			entityId = Datamodel.makePropertyIdValue(staContent.getId(getKey()), STA_IRI);
			updateBuilder = PropertyUpdateBuilder.forBaseRevisionId(entityId, staContent.getVersion(getKey()));
			break;
		default:
			throw new IllegalArgumentException(
					"Nicht unterstützer Wikibase-Entitätstyp: " + staContent.getType(getKey()));
		}
		List<Statement> update = new ArrayList<Statement>();
		List<String> remove = new ArrayList<String>();
		List<Statement> add = new ArrayList<Statement>();
		for (StaStatement staStatement : staContent.getStatements()) {
			if (staStatement.getAction() != null) {
				switch (staStatement.getAction()) {
				case UPDATE:
					if (staStatement.getId() == null) {
						add.add(map(entityId, staStatement));
					} else {
						update.add(map(entityId, staStatement));
					}
					break;
				case REMOVE:
					remove.add(staStatement.getId());
				}
			}
		}
		if (update.size() + remove.size() + add.size() > 0) {
			LOGGER.log(Level.INFO,
					"aktualisiere: " + update.size() + ", füge hinzu: " + add.size() + ", lösche:" + remove.size());
		} else {
			LOGGER.log(Level.INFO, "keine Änderungen");
		}
		updateBuilder.updateStatements(Datamodel.makeStatementUpdate(add, update, remove));
		return updateBuilder.build();
	}

	private Statement map(EntityIdValue entityId, StaStatement staStatement) {
		PropertyIdValue propId = Datamodel.makePropertyIdValue(staStatement.getProperty().getKey(), STA_IRI);
		/*
		 * eingebettete Entitäten speichern oder aktualisieren
		 */
		if (staStatement.getEmbeddedContent() != null) {
			/*
			 * Bei Bedarf Rückverlinkung ergänzen
			 */
			if (!staStatement.getEmbeddedContent().getStatements().hasStatement(IS_EMBEDED_KEY, entityId.getId())) {
				staStatement.getEmbeddedContent().getStatements()
						.add(new StaStatement(getProperty(IS_EMBEDED_KEY), entityId.getId(), null, Action.UPDATE));
			}
			String embeddedContentId = save(staStatement.getEmbeddedContent());
			staStatement.setValue(embeddedContentId);
		}
//		if (staValue.getEmbeddedTextValue() != null) {
//			String embeddedContentId = save(mapEmbeddedTextToStaContent(entityId.getId(), staValue));
//			staValue.setValue(embeddedContentId);
//		}
		StatementBuilder statementBuilder = StatementBuilder.forSubjectAndProperty(entityId, propId)
				.withValue(mapValue(staStatement)).withId(staStatement.getId());
		if (staStatement.getQualifiers() != null) {
			for (StaStatement qualifierStatement : staStatement.getQualifiers()) {
				PropertyIdValue qualifierPropId = Datamodel
						.makePropertyIdValue(qualifierStatement.getProperty().getKey(), STA_IRI);
				statementBuilder.withQualifierValue(qualifierPropId, mapValue(qualifierStatement));
			}
		}
		return statementBuilder.build();
	}

	private Value mapValue(StaStatement staStatement) {
		Value wbValue;
		switch (staStatement.getType()) {
		case ID:
			char firstLetter = staStatement.getValue().charAt(0);
			switch (firstLetter) {
			case 'Q':
				wbValue = Datamodel.makeItemIdValue(staStatement.getValue(), STA_IRI);
				break;
			case 'P':
				wbValue = Datamodel.makePropertyIdValue(staStatement.getValue(), STA_IRI);
				break;
			default:
				throw new IllegalArgumentException("Nicht unterstütztes ID-Format: " + staStatement.getValue());
			}
			break;
		default:
			wbValue = Datamodel.makeStringValue(staStatement.getValue());
			break;
		}
		return wbValue;
	}

	@Override
	public StaContent map(TermedStatementDocument wbDocument) {
		StaContent ret = new StaContent();

		ret.setId(getKey(), wbDocument.getEntityId().getId());
//		List<StatementGroup> statementGroups = wbDocument.getStatementGroups();
		ret.setVersion(getKey(), wbDocument.getRevisionId());
		ret.setTitle(wbDocument.findLabel("de"));
		ret.setType(getKey(), wbDocument.getEntityId().getEntityType());

		Iterator<Statement> iter = wbDocument.getAllStatements();
		while (iter.hasNext()) {
			ret.getStatements().add(map(iter.next()));
		}

//		for (StatementGroup statementGroup : wbDocument.getAllStatements()) {
//			StaStatementGroup staStatementGroup = map(statementGroup);
//			ret.addStatementGroup(staStatementGroup);
//		}
		return ret;
	}

//	public StaStatementGroup map(StatementGroup statementGroup) {
//		String propertyId = statementGroup.getProperty().getId();
////		WikibaseMapper mapper = mappers.get(propertyId);
////		if (mapper != null) {
////			return mapper.map(statementGroup);
////		}
////		/*
////		 * Default Mapping
////		 */
//		StaStatementGroup staStatementGroup = new StaStatementGroup();
//		StaProperty staProperty = getProperty(propertyId);
//		staStatementGroup.setProperty(staProperty);
//		staStatementGroup.setValues(map(staProperty, statementGroup.getStatements()));
//		return staStatementGroup;
//
//	}

//	public List<StaValue> map(StaProperty staProperty, List<Statement> statements) {
//		List<StaValue> ret = new ArrayList<StaValue>();
//		for (Statement statement : statements) {
//			ret.add(map(statement));
//		}
//		return ret;
//	}

	public StaStatement map(Statement statement) {
		String value = statement.getValue().accept(valueVisitor);
		StaProperty staProp = getProperty(statement.getMainSnak().getPropertyId().getId());
		StaStatement staValue = new StaStatement(staProp, value, statement.getStatementId(), null);

		List<SnakGroup> qualifiers = statement.getQualifiers();
		for (SnakGroup snakGroup : qualifiers) {
			mapWbSnakGroup(staValue, snakGroup);
		}
		if (staValue.getQualifiers() != null) {
			String embedId = staValue.getQualifiers().findFirstValue(EMBEDS_KEY);
			if (embedId != null) {
				staValue.setEmbeddedContent(load(embedId));
			}
		}
		return staValue;
	}

	public void mapWbSnakGroup(StaStatement staValue, SnakGroup snakGroup) {
		String qualifierId = snakGroup.getProperty().getId();
		for (Snak snak : snakGroup.getSnaks()) {
			String value = snak.accept(valueVisitor);
			staValue.addQualifier(new StaStatement(getProperty(qualifierId), value, null, null));
		}
	}

	/**
	 * TODO: Nutze SPARQL, um alle STA-Properties abzufragen und initial zu cachen
	 */
	@Override
	public StaProperty getProperty(String propId) {
		StaProperty ret = this.propertyCache.get(propId);
		if (ret == null) {

			try {
				PropertyDocument propertyDocument = (PropertyDocument) this.wbReader.getEntityDocument(propId);
				if (propertyDocument != null) {
					String label = propertyDocument.findLabel("de");
					int index = label.indexOf('|');
					if (index > -1) {
						label = label.substring(0, index).strip();
					}
					Type type = Type.STRING;
					String datatype = propertyDocument.getDatatype().getJsonString();
					if (datatype.equals(DatatypeIdValue.JSON_DT_ITEM)
							|| datatype.equals(DatatypeIdValue.JSON_DT_PROPERTY)) {
						type = Type.ID;
					} else if (propId.equals(DESCRIPTION_KEY)) {
						type = Type.TEXT;
					}
					ret = new StaProperty(propId, label, type);
					this.propertyCache.put(propId, ret);
				}
			} catch (MediaWikiApiErrorException | IOException e) {
				LOGGER.log(Level.ERROR, "Konnte Property nicht aus Wikibase laden: " + propId, e);
				return new StaProperty(propId, propId, Type.STRING);
			}
		}
		return ret;
	}

	public String getEntityUrl(String entityId) {
		String entityPath;
		if (entityId.startsWith("P")) {
			entityPath = "Property:" + entityId;
		} else {
			entityPath = "Item:" + entityId;
		}
		return wbBaseUrl + "/wiki/" + entityPath;
	}

//	public String getEntityLabel(String wbId) {
//	String ret = this.entityLabelsCache.get(wbId);
//	if (ret == null) {
//		try {
//			TermedStatementDocument entityDocument = (TermedStatementDocument) this.wbReader
//					.getEntityDocument(wbId);
//			if (entityDocument != null) {
//				ret = entityDocument.findLabel("de");
//				int index = ret.indexOf('|');
//				if (index > -1) {
//					ret = ret.substring(0, index).strip();
//				}
//
//				this.entityLabelsCache.put(wbId, ret);
//			}
//		} catch (MediaWikiApiErrorException | IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
//	return ret;
//}

//	private StaContent mapEmbeddedTextToStaContent(String contentId, StaValue staValue) {
//		StaContent ret = null;
//		if (contentId != null) {
//			ret = load(staValue.getEmbeddedTextValue().getContentId());
//		}
//		if (ret == null) {
//			/*
//			 * neuen StaContent anlegen
//			 */
//			ret = new StaContent();
//			ret.setId(getKey(), staValue.getEmbeddedTextValue().getContentId());
//			ret.setTitle(staValue.getValue());
//			ret.setType(getKey(), EntityIdValue.ET_ITEM);
//			/*
//			 * TODO: Schema und andere Standard-Props ergänzen
//			 */
//		}
//		ret.addStatementGroup(new StaStatementGroup(getProperty(DESCRIPTION_KEY),
//				new StaValue(staValue.getEmbeddedTextValue().getValue(), null, Type.TEXT, Action.UPDATE)));
//
////		/*
////		 * bisherige StatementIds einsammeln
////		 */
////		Iterator<String> idIterator = ret.getStatementGroupValues(DESCRIPTION_KEY).stream().map(value -> value.getId())
////				.filter(id -> id != null).iterator();
////		StaStatementGroup description = new StaStatementGroup();
////		description.setProperty(getProperty(DESCRIPTION_KEY));
////		List<StaValue> newValues = new ArrayList<StaValue>();
////		addSplittedTextValues(newValues,
////				Collections.singletonList(
////						new StaValue(staValue.getEmbeddedTextValue().getValue(), null, Type.TEXT, Action.UPDATE)),
////				idIterator);
////		ret.addStatementGroup(description);
//		/*
//		 * Rückverlinkung ergänzen
//		 */
//		if (!ret.hasStatement(IS_EMBEDED_KEY, contentId)) {
//			staValue.getEmbeddedContent().addStatement(getProperty(IS_EMBEDED_KEY), new StaValue(contentId, Type.ID));
//		}
//		return ret;
//	}

//	public void addSplittedTextValues(List<StaValue> newValues, List<StaValue> oldValues, Iterator<String> idIterator) {
//		for (StaValue oldValue : oldValues) {
//			Element jsoupValue = Jsoup.parse(oldValue.getValue()).body();
//			/*
//			 * clean up content
//			 */
//			for (Element element : jsoupValue.getAllElements()) {
//				// remove empty tags (for example: <em></em> <div></div>)
//				if (!element.hasText() && element.isBlock() && element.hasParent()) {
//					element.remove();
//				}
//			}
//
//			/*
//			 * split
//			 */
//			for (Element paragraph : jsoupValue.children()) {
//				String id = null;
//				if (idIterator.hasNext()) {
//					id = idIterator.next();
//				}
//
//				switch (paragraph.tagName()) {
//				case "h1":
//					newValues.add(new StaValue(paragraph.html(), id, Type.TEXT, oldValue.getAction())
//							.addQualifier(getProperty(LAYOUT_KEY), new StaValue(H1_KEY)));
//					break;
//				case "h2":
//					newValues.add(new StaValue(paragraph.html(), id, Type.TEXT, oldValue.getAction())
//							.addQualifier(getProperty(LAYOUT_KEY), new StaValue(H2_KEY)));
//					break;
//				case "h3":
//					newValues.add(new StaValue(paragraph.html(), id, Type.TEXT, oldValue.getAction())
//							.addQualifier(getProperty(LAYOUT_KEY), new StaValue(H3_KEY)));
//					break;
//				case "ol":
//					for (Element li : paragraph.children()) {
//						newValues.add(new StaValue(li.html(), id, Type.TEXT, oldValue.getAction())
//								.addQualifier(getProperty(LAYOUT_KEY), new StaValue(OL_KEY)));
//					}
//					break;
//				case "ul":
//					for (Element li : paragraph.children()) {
//						newValues.add(new StaValue(li.html(), id, Type.TEXT, oldValue.getAction())
//								.addQualifier(getProperty(LAYOUT_KEY), new StaValue(UL_KEY)));
//					}
//					break;
//				case "example":
//					newValues.add(new StaValue(paragraph.html(), id, Type.TEXT, oldValue.getAction())
//							.addQualifier(getProperty(LAYOUT_KEY), new StaValue(EXAMPLE_KEY)));
//					break;
//				case "collapsible":
//					newValues.add(new StaValue(paragraph.html(), id, Type.TEXT, oldValue.getAction())
//							.addQualifier(getProperty(LAYOUT_KEY), new StaValue(COLLAPSIBLE_KEY)));
//					break;
//				default:
//					newValues.add(new StaValue(paragraph.html(), id, Type.TEXT, oldValue.getAction()));
//				}
//			}
//		}
//
//		/*
//		 * entferne überschüssige Statements
//		 */
//		while (idIterator.hasNext()) {
//			newValues.add(new StaValue("", idIterator.next(), Type.TEXT, Action.REMOVE));
//		}
//	}

}
