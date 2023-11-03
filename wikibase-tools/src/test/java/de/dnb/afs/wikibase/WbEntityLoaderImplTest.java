package de.dnb.afs.wikibase;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.wikibaseapi.OAuthApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceWbConfigFactory;
import de.dnb.sta.StaProps;
import de.dnb.sta.apps.PropsHelper;
import junit.framework.TestCase;

public class WbEntityLoaderImplTest extends TestCase {

	public void test() throws FileNotFoundException, IOException, MediaWikiApiErrorException {

		ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();

		final StaProps wbProps = PropsHelper.getProps();

		OAuthApiConnection prodDokuApi = new OAuthApiConnection("https://sta.dnb.de/w/api.php", wbProps.consumerKey,
				wbProps.consumerSecret, wbProps.accessToken, wbProps.accessSecret);

		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(prodDokuApi, config.wbIri);
		WikibaseDataEditor wbde = new WikibaseDataEditor(prodDokuApi, config.wbIri);
		wbde.setEditAsBot(true);
		wbde.setAverageTimePerEdit(100);

		WbEntityLoaderImpl wbLoader = new WbEntityLoaderImpl(wbdf);

		PropertyDocument pdoc = wbLoader.lookupProperty("STA-Regel: Zählung einer fortlaufenden Ressource | RDA-Eigenschaft - Fortlaufende Ressource - einfache Titelaufnahme für die monografische Reihe"); 
		assertNotNull(pdoc);

	}

}
