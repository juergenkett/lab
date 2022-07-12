package de.dnb.afs.wikibase.apps;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.wikidata.wdtk.wikibaseapi.OAuthApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityEditorImpl;
import de.dnb.afs.wikibase.confluence.ConfluenceDokuCleaner;
import de.dnb.afs.wikibase.confluence.ConfluencePageLoader;
import de.dnb.afs.wikibase.confluence.ConfluenceToWbMapper;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceToWbMapperFactory;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceWbConfigFactory;

public class ConfluenceWbImporterApp {

	private static final Log logger = LogFactory.getLog(ConfluenceWbImporterApp.class);

	public static void main(String[] args) throws MediaWikiApiErrorException, IOException {
		logger.info("start test");
		ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();

		final WbProps wbProps = PropsHelper.getProps();

		OAuthApiConnection prodDokuApi = new OAuthApiConnection("https://doku.wikibase.wiki/w/api.php",
				wbProps.consumerKey, wbProps.consumerSecret, wbProps.accessToken, wbProps.accessSecret);

		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(prodDokuApi, config.wbIri);
		WikibaseDataEditor wbde = new WikibaseDataEditor(prodDokuApi, config.wbIri);
		wbde.setEditAsBot(true);
		wbde.setAverageTimePerEdit(500);

		WbEntityEditor wbEditor = new WbEntityEditorImpl(wbde, wbdf);
		String wbEntityId = "P632";
		String pageId = "217541956";

		ConfluenceToWbMapper mapper = ConfluenceToWbMapperFactory.newConfluenceToWbMapper(wbEditor);

		ConfluencePageLoader loader = new ConfluencePageLoader(config.confluenceUrl, wbProps.confluenceUser,
				wbProps.confluencePass);
		ConfluenceDokuCleaner cleaner = new ConfluenceDokuCleaner();
		Document cleanDoc = cleaner.clean(loader.loadDocument(pageId));
		cleanDoc.title("Test für Confluence Seite " + pageId + "/" + "Test for Confluence page " + pageId);
		mapper.map(cleanDoc, wbEntityId);

	}

}
