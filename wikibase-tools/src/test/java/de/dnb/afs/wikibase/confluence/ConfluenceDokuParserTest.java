package de.dnb.afs.wikibase.confluence;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wikidata.wdtk.datamodel.helpers.JsonSerializer;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.xml.sax.SAXException;

import de.dnb.afs.wikibase.confluence.ConfluenceDokuParser;
import de.dnb.afs.wikibase.confluence.WbConfluenceDokuParserHandler;
import junit.framework.TestCase;

public class ConfluenceDokuParserTest extends TestCase {

	private static final Log LOGGER = LogFactory.getLog(ConfluenceDokuParserTest.class);

	public void test() throws ParserConfigurationException, SAXException, IOException, MediaWikiApiErrorException {
		LOGGER.info("start test");

		WbConfluenceDokuParserHandler handler = new WbConfluenceDokuParserHandler();
		ConfluenceDokuParser parser = new ConfluenceDokuParser(handler);
		String pageId = "217541956";
		parser.parse(pageId);
		ItemDocument itemDocument = handler.getItemDocument();
//		LOGGER.info(itemDocument);
		final String pathname = "src" + File.separator + "test" + File.separator + "json" + File.separator + pageId
				+ ".json";
		File file = new File(pathname);
		file.createNewFile();
		FileOutputStream buffer = new FileOutputStream(file);

		JsonSerializer serializer = new JsonSerializer(buffer);

		serializer.processItemDocument(itemDocument);

	}

}
