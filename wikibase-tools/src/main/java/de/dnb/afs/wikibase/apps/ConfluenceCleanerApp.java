package de.dnb.afs.wikibase.apps;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.confluence.ConfluenceDokuCleaner;
import de.dnb.afs.wikibase.confluence.ConfluencePageLoader;
import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceWbConfigFactory;

public class ConfluenceCleanerApp {

	private static final Log logger = LogFactory.getLog(ConfluenceCleanerApp.class);
	
	public static void main(String[] args) throws MediaWikiApiErrorException, IOException {
		final WbProps wbProps = PropsHelper.getProps();
		ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();
		Document document = new ConfluenceDokuCleaner().clean(
				new ConfluencePageLoader(config.confluenceUrl, wbProps.confluenceUser, wbProps.confluencePass)
						.loadDocument("217541956"));
		
		logger.debug(document);
	}
	
}
