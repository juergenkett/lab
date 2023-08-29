package de.dnb.afs.wikibase.apps;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.confluence.ConfluenceCleaner;
import de.dnb.afs.wikibase.confluence.ConfluencePageLoader;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceWbConfigFactory;

public class Cleaner4ElementsApp {

	private static final Log logger = LogFactory.getLog(Cleaner4ElementsApp.class);
	
	public static void main(String[] args) throws MediaWikiApiErrorException, IOException {
		final WbProps wbProps = PropsHelper.getProps();
//		final String confluenceId = "217541956";
//		final String confluenceId = "206373188";
		final String confluenceId = "198097418";

		
		ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();
		Document document = new ConfluencePageLoader(config.confluenceUrl, wbProps.confluenceUser, wbProps.confluencePass)
		.loadDocument(confluenceId);
		
		document = new ConfluenceCleaner().cleanElementDesc(document);
		logger.info(document);
	}
	
}
