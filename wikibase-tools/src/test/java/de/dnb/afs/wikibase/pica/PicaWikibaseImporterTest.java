package de.dnb.afs.wikibase.pica;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.StatementGroup;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.xml.sax.SAXException;

import de.ddb.pica.parser.DefaultPicaParserHandler;
import de.ddb.pica.parser.PicaParser;
import de.ddb.pica.parser.PicaParserException;
import de.ddb.pica.parser.PicaParserFactory;
import de.ddb.pica.record.PicaField;
import de.ddb.pica.record.PicaRecord;
import de.ddb.pica.record.PicaSubfield;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class PicaWikibaseImporterTest extends TestCase {

	/**
	 * Pica+ <code>end-of-record</code>
	 */
	public static final byte TOKEN_END_OF_RECORD = 0x1D;

	/**
	 * Pica+ <code>end-of-record</code>
	 */
	public static final byte TOKEN_LINEFEED = 0x0A;

	private static final Log logger = LogFactory.getLog(PicaWikibaseImporterTest.class);

	private final static String LOCALHOST_WIKIBASE_URL = "http://localhost:8080/w/api.php";

	private final static String LOCALHOST_WIKIBASE_IRI = "gnd.network";

	private final static String GND_DOKU_WIKIBASE_URL = "http://doku.wikibase.wiki/w/api.php";

	private final static String GND_DOKU_SITE_IRI = "doku.wikibase.wiki/";

	private final static String QID_GND_SCHEMA = "Q1";

	private final static String PID_SUBELEMENT = "P115";

	private final static MyValueVisitor VALUE_VISITOR = new MyValueVisitor();

	private final static String PID_KODIERUNG = "P4";

	private final static String PID_TYP = "P3";

	private ApiConnection localApi = new BasicApiConnection(LOCALHOST_WIKIBASE_URL);

	private ApiConnection dokuApi = new BasicApiConnection(GND_DOKU_WIKIBASE_URL);

	private Map<String, String> picaCodeToWbId = new HashMap<String, String>();

	private Map<String, EntityDocument> wbProperties;

	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public PicaWikibaseImporterTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(PicaWikibaseImporterTest.class);
	}

	/**
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws PicaParserException
	 * @throws MediaWikiApiErrorException
	 */
	public void test() throws PicaParserException, IOException, ParserConfigurationException, SAXException,
			MediaWikiApiErrorException {
		logger.info("test start");

//		PicaRecord record = getPicaRecord();
//		logger.info("New Record - Level 0: " + record);
//		picaFieldToEntityId.put("002@", "P1");

		picaCodeToWbId.put("003U", "P295");
		picaCodeToWbId.put("003U$a", "P66");
		picaCodeToWbId.put("004B", "P63");
		picaCodeToWbId.put("004B$a", "P64");
		picaCodeToWbId.put("028A", "P58");
		picaCodeToWbId.put("028A$P", "P41");
		picaCodeToWbId.put("028A$a", "P21");
		picaCodeToWbId.put("028A$d", "P29");
		picaCodeToWbId.put("041R", "P72");
		picaCodeToWbId.put("041R$9", "P19");
		picaCodeToWbId.put("041R$4", "P169");

		
		loadWbProperties();

//		PicaRecord picaRecord = new PicaRecord();
//		PicaField picaField = picaRecord.addNewField("028A");
//		picaField.addNewSubfield("a", "Test.Nachname");
//		picaField.addNewSubfield("d", "Test.Vorname");
		
		PicaRecord picaRecord = getPicaRecord("102111847");

		ItemDocument itemDocument = createItemDocument(picaRecord);
		logger.info(itemDocument);

//		WikibaseDataEditor wbde = new WikibaseDataEditor(localApi, LOCALHOST_WIKIBASE_IRI);

//		ItemDocument newItemDocument = wbde.createItemDocument(itemDocument,
//				"Wikidata Toolkit example test item creation", null);

	}

	public void loadWbProperties() throws MediaWikiApiErrorException, IOException {
		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(dokuApi, GND_DOKU_SITE_IRI);
		List<String> pids = new ArrayList<String>(picaCodeToWbId.values());
		wbProperties = wbdf.getEntityDocuments(pids);
		logger.info(wbProperties.keySet());
	}

	public PropertyDocument getPropertyFromPicaName(String picaName) {
		PropertyDocument ret = null;
		String pid = picaCodeToWbId.get(picaName);
		if (pid != null) {
			ret = (PropertyDocument) wbProperties.get(pid);
		}
		return ret;
	}

	public ItemDocument createItemDocument(PicaRecord record) {
		ItemDocumentBuilder documentBuilder = ItemDocumentBuilder.forItemId(ItemIdValue.NULL).withLabel("Test.label",
				"de");
		for (PicaField field : record.getFields()) {
			PropertyDocument property = getPropertyFromPicaName(field.getName());
			if (property != null) {
				StatementBuilder statementBuilder = StatementBuilder.forSubjectAndProperty(ItemIdValue.NULL,
						property.getEntityId());
				for (PicaSubfield subfield : field.getSubfields()) {
					PropertyDocument qualifierProperty = getPropertyFromPicaName(
							field.getName() + "$" + subfield.getCode());
					if (qualifierProperty != null) {
						statementBuilder.withQualifierValue(qualifierProperty.getEntityId(),
								Datamodel.makeStringValue(subfield.getContent()));
					}
				}
				documentBuilder.withStatement(statementBuilder.build());
			}
		}
		return documentBuilder.build();
	}

	public void loadSchema() throws MediaWikiApiErrorException, IOException {
		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(dokuApi, GND_DOKU_SITE_IRI);
		Map<String, EntityDocument> entityMap = new HashMap<String, EntityDocument>();
		addEntityDocuments(wbdf, Collections.singletonList(QID_GND_SCHEMA), PID_SUBELEMENT, entityMap);
		logger.info("Map contains " + entityMap.size() + " entities.");
	}

	public static void addEntityDocuments(WikibaseDataFetcher wbdf, List<String> entityIds, String propertyId,
			Map<String, EntityDocument> entityMap) throws MediaWikiApiErrorException, IOException {
		entityIds.removeAll(entityMap.keySet());
		if (entityIds.size() > 0) {
			Map<String, EntityDocument> newEntities = wbdf.getEntityDocuments(entityIds);
			entityMap.putAll(newEntities);
			logger.info("Adding " + newEntities.size() + " new Entities: \n" + newEntities.keySet());
			for (EntityDocument entity : newEntities.values()) {

				addEntityDocuments(wbdf, getObjectIds(entity, propertyId), propertyId, entityMap);
			}
			entityMap.putAll(wbdf.getEntityDocuments(entityIds));
		}
	}

	/*
	 * return a list of all object ids that relate to the entity with respect to the
	 * given property id
	 */
	public static List<String> getObjectIds(EntityDocument entity, String propertyId) {
		List<String> ret = new ArrayList<String>();
		Iterator<Statement> iter = null;
		if (entity instanceof ItemDocument) {
			iter = ((ItemDocument) entity).getAllStatements();
		} else {
			iter = ((PropertyDocument) entity).getAllStatements();
		}
		while (iter.hasNext()) {
			Statement s = iter.next();

			if (s.getMainSnak().getPropertyId().getId().equals(propertyId)) {
				String entityId = ((EntityIdValue) s.getValue()).getId();
				ret.add(entityId);

			}
		}
		return ret;
	}

	public static Value findValue(EntityDocument entity, String propertyId, String qualifierId, String qualifierValue) {
		Value ret = null;
		StatementGroup statementGroup = ((ItemDocument) entity).findStatementGroup(PID_KODIERUNG);
		if (statementGroup != null) {
			for (Statement statement : statementGroup.getStatements()) {
//				for (Snak snak : statement.getQualifiers()) {
//					if (snak.getPropertyId().equals(qualifierId)) {
//
//					}
//				}
			}
		}
		return ret;

	}

	public static String getPicaFieldname(EntityDocument entity) {
		String ret = null;
		Iterator<Statement> iter = null;
		if (entity instanceof ItemDocument) {
			Statement statement = ((ItemDocument) entity).findStatement(PID_KODIERUNG);

		} else {
			iter = ((PropertyDocument) entity).getAllStatements();
		}
		while (iter.hasNext()) {
			Statement s = iter.next();

//			if (s.getMainSnak().getPropertyId().getId().equals(PID_KODIERUNG)) {
//				String code = s.getValue();
//			}
		}
		return ret;
	}

	public static PicaRecord getPicaRecord(String idn) throws PicaParserException, IOException {
		PicaRecord newRecord = null;

		final List<PicaRecord> records = new ArrayList<PicaRecord>();
		final DefaultPicaParserHandler handler = new DefaultPicaParserHandler(records);
		final String pathname = "src" + File.separator + "test" + File.separator + "data" + File.separator + idn 
				+ ".pp";
		final File file = new File(pathname);

		final InputStream in = new FileInputStream(file);
		PicaParser parser = PicaParserFactory.getInstance(handler);
		parser.parse(in, handler);
		in.close();
		newRecord = records.get(0);

		return newRecord;
	}
}

class MyValueVisitor implements ValueVisitor<List<String>> {

	@Override
	public List<String> visit(EntityIdValue value) {

		return null;
	}

	@Override
	public List<String> visit(GlobeCoordinatesValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> visit(MonolingualTextValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> visit(QuantityValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> visit(StringValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> visit(TimeValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> visit(UnsupportedValue value) {
		// TODO Auto-generated method stub
		return null;
	}

}
