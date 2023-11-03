package de.dnb.afs.wikibase;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.wikibaseapi.OAuthApiConnection;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;
import de.dnb.afs.wikibase.confluence.factories.ConfluenceWbConfigFactory;
import de.dnb.sta.StaProps;
import de.dnb.sta.apps.PropsHelper;
import junit.framework.TestCase;

public class WbEntityEditorImplTest extends TestCase {

	public void test() throws FileNotFoundException, IOException, MediaWikiApiErrorException {

		ConfluenceWbConfig config = ConfluenceWbConfigFactory.newStandardConfig();

		final StaProps wbProps = PropsHelper.getProps();

		OAuthApiConnection prodDokuApi = new OAuthApiConnection("https://sta.dnb.de/w/api.php", wbProps.consumerKey,
				wbProps.consumerSecret, wbProps.accessToken, wbProps.accessSecret);

		WikibaseDataFetcher wbdf = new WikibaseDataFetcher(prodDokuApi, config.wbIri);
		WikibaseDataEditor wbde = new WikibaseDataEditor(prodDokuApi, config.wbIri);
		wbde.setEditAsBot(true);
		wbde.setAverageTimePerEdit(100);

//		WbEntityEditorImpl wbEditor = new WbEntityEditorImpl(wbde, wbdf);

		ItemDocument oldVersion = (ItemDocument) wbdf.getEntityDocument("P592");
		
		assertTrue(oldVersion.hasStatement("P410"));

	}

}
