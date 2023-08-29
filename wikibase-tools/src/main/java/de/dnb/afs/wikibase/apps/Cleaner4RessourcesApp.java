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

public class Cleaner4RessourcesApp {

	private static final Log logger = LogFactory.getLog(Cleaner4RessourcesApp.class);
	
	public static void main(String[] args) throws MediaWikiApiErrorException, IOException {
		final WbProps wbProps = PropsHelper.getProps();
//		final String confluenceId = "217541956";
//		final String confluenceId = "206373188";
//		final String confluenceId = "259632451";
		final String confluenceId = "235318029";
		ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();
		Document document = new ConfluencePageLoader(config.confluenceUrl, wbProps.confluenceUser, wbProps.confluencePass)
		.loadDocument(confluenceId);
		logger.info(document);

		document = new ConfluenceCleaner().cleanRessource(document);
		logger.info(document);
	}
	
}
