package de.dnb.afs.wikibase.apps;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.wikidata.wdtk.wikibaseapi.OAuthApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityEditorImpl;
import de.dnb.afs.wikibase.confluence.ConfluenceCleaner;
import de.dnb.afs.wikibase.confluence.ConfluencePageLoader;
import de.dnb.afs.wikibase.confluence.WbMapper4Resources;
import de.dnb.afs.wikibase.confluence.WbMapper4Elements;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceToWbMapperFactory;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceWbConfigFactory;

public class Importer4ResourcesApp {

	private static final Log logger = LogFactory.getLog(Importer4ResourcesApp.class);

	public static void main(String[] args) throws MediaWikiApiErrorException, IOException {
		logger.info("start test");
		ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();

		final WbProps wbProps = PropsHelper.getProps();

		OAuthApiConnection prodDokuApi = new OAuthApiConnection("https://sta.dnb.de/w/api.php",
				wbProps.consumerKey, wbProps.consumerSecret, wbProps.accessToken, wbProps.accessSecret);
		
//		OAuthApiConnection prodDokuApi = new OAuthApiConnection("https://doku.wikibase.wiki/w/api.php",
//		wbProps.consumerKey, wbProps.consumerSecret, wbProps.accessToken, wbProps.accessSecret);

		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(prodDokuApi, config.wbIri);
		WikibaseDataEditor wbde = new WikibaseDataEditor(prodDokuApi, config.wbIri);
		wbde.setEditAsBot(true);
		wbde.setAverageTimePerEdit(100);

		WbEntityEditorImpl wbEditor = new WbEntityEditorImpl(wbde, wbdf);
//		wbEditor.setReadIOnly(true);
//		wbEditor.setAddOnlyMode(true); 
		WbMapper4Resources mapper = ConfluenceToWbMapperFactory.newWbMapper4Ressources(wbEditor);
		ConfluencePageLoader loader = new ConfluencePageLoader(config.confluenceUrl, wbProps.confluenceUser,
				wbProps.confluencePass);
		ConfluenceCleaner cleaner = new ConfluenceCleaner();

//		bulkImport(mapper, loader, cleaner);

//		String wbEntityId = "P444";
//		String pageId = "198096549";
//		String wbEntityId = "P439";
//		String pageId = "206396771";
//		String wbEntityId = "Q8514";
//		String pageId = "235317993";
//		String wbEntityId = "Q8520";
//		String pageId = "235318026";
		String wbEntityId = "Q8505";
		String pageId = "235318052";
//
//
//		 
		singleImport(wbEntityId, pageId, mapper, loader, cleaner);

	}

	public static void bulkImport(WbMapper4Resources mapper, ConfluencePageLoader loader,
			ConfluenceCleaner cleaner) {
		try (BufferedReader br = new BufferedReader(
				new FileReader("src/main/resources/resources.txt", Charset.forName("UTF-8")));
				PrintWriter problemsWriter = new PrintWriter(new FileWriter("out/problems.txt"))) {
			String wbEntityId = null;
			String confluenceUrl = null;
			String confluenceId = null;
			String label = null;
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] split = line.split("\\t");
					wbEntityId = split[0];
					label = split[1];
					confluenceId = split[2];
					confluenceUrl = "https://wiki.dnb.de/pages/viewpage.action?pageId=" + confluenceId;
					Document cleanDoc = cleaner.cleanRessource(loader.loadDocument(new URL(confluenceUrl)));
					mapper.map(cleanDoc, wbEntityId);
					logger.info("Ressource hinzugefügt: " + wbEntityId + " " + confluenceId);
				} catch (Exception e) {
					logger.error("Ressource konnte nicht hinzugefügt werden: " + wbEntityId + " " + confluenceUrl, e);
					problemsWriter.println(wbEntityId + "//t" + label + "//t" + confluenceUrl + "//t" + e);
					problemsWriter.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void singleImport(String wbId, String confluenceId, WbMapper4Resources mapper,
			ConfluencePageLoader loader, ConfluenceCleaner cleaner) {
		Document cleanDoc;
		try {
			cleanDoc = cleaner.cleanRessource(loader.loadDocument(confluenceId));
			mapper.map(cleanDoc, wbId);
		} catch (IOException | MediaWikiApiErrorException e) {
			logger.error(e, e);
		}
	}

}
