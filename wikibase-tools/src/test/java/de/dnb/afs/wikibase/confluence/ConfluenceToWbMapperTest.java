package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.wikidata.wdtk.datamodel.helpers.PropertyDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.PropertyUpdateBuilder;
import org.wikidata.wdtk.datamodel.helpers.TermUpdateBuilder;
import org.wikidata.wdtk.datamodel.implementation.MonolingualTextValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.BasicApiConnection;
import org.wikidata.wdtk.wikibaseapi.LoginFailedException;
import org.wikidata.wdtk.wikibaseapi.OAuthApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.xml.sax.SAXException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityEditorImpl;
import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.WbEntityLoaderImpl;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceToWbMapperFactory;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceWbConfigFactory;
import junit.framework.TestCase;

public class ConfluenceToWbMapperTest extends TestCase {

	private static final Log logger = LogFactory.getLog(ConfluenceToWbMapperTest.class);

	private ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();

	private ApiConnection localApi = new BasicApiConnection(ConfluenceWbConfigFactory.LOCALHOST_WIKIBASE_URL);

	public void test() throws IOException, MediaWikiApiErrorException, LoginFailedException {

//		EntityDocument entity = PropertyDocumentBuilder
//				.forPropertyIdAndDatatype(PropertyIdValue.NULL, DatatypeIdValue.DT_PROPERTY)
//				.withLabel("Dies ist eine weitere generierte Testproperty", "de").build();
//		EntityDocument newEntity = wbde.createEntityDocument(entity, "ein weiterer Edit zum Testen", null);
//		LOGGER.debug(newEntity.getEntityId());

//		PropertyDocument entity = (PropertyDocument) wbLoader.getEntity(wbEntityId);
//
//		if (entity == null) {
//			LOGGER.warn("entity mit Id " + wbEntityId + " nicht gefunden. Überspringe Mapping.");
//			return;
//		}
//		LOGGER.debug("entity mit Id " + wbEntityId + " gefunden. Revision Id: " + entity.getRevisionId() + ", "
//				+ entity.getLabels());
////		PropertyUpdateBuilder propertyUpdateBuilder = PropertyUpdateBuilder.forBaseRevision((PropertyDocument) entity);
////		propertyUpdateBuilder.updateDescriptions(TermUpdateBuilder
////				.forTerms(Collections.singleton(
////						new MonolingualTextValueImpl("de", "Diese Beschreibung wurde automatisch generiert.")))
////				.build());
////		wbde.editEntityDocument(propertyUpdateBuilder.build(), true, "update", null);

//		entity = entity.withDescription(
//				new MonolingualTextValueImpl("Diese Beschreibung wurde automatisch generiert Nummer1.", "de"));
////		
//		entity = wbde.editPropertyDocument(entity, false, "update by ConfluenceImporter", Collections.emptyList());

		// wbEditor.updateEntity(entity, null, null);

//		WbEntityEditor wbEditor = new WbEntityEditorMock();



//		ItemDocument itemDocument = mapper.getItemDocument();
////		LOGGER.info(itemDocument);
//
//		final String pathname = "src" + File.separator + "test" + File.separator + "json" + File.separator + pageId
//				+ ".json";
//		File file = new File(pathname);
//		file.createNewFile();
//		FileOutputStream buffer = new FileOutputStream(file);
//		JsonSerializer serializer = new JsonSerializer(buffer);
//		serializer.processItemDocument(itemDocument);

	}

}
