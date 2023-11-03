package de.dnb.afs.sta;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wikidata.wdtk.wikibaseapi.OAuthApiConnection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dnb.sta.StaContentModifier;
import de.dnb.sta.StaProps;
import de.dnb.sta.apps.PropsHelper;
import de.dnb.sta.apps.SyncToConfluenceApp;
import de.dnb.sta.confluence.StaStorageConfluence;
import de.dnb.sta.datamodel.StaContent;
import de.dnb.sta.wikibase.StaStorageWikibase;
import junit.framework.TestCase;

public class StaContentModifierTest extends TestCase {
	
	private static final Log logger = LogFactory.getLog(StaContentModifierTest.class);

	
	public void test() throws FileNotFoundException, IOException {
		final StaProps wbProps = PropsHelper.getProps();
		String wbUri = "https://sta.dnb.de";
		logger.info("consumer-key:" + wbProps.consumerKey);
//		"https://sta.dnb.de/w/api.php"
//		"https://testwikibase0.dnb.de/w/api.php"
		OAuthApiConnection wbapi = new OAuthApiConnection(wbUri + "/w/api.php",
				wbProps.consumerKey, wbProps.consumerSecret, wbProps.accessToken, wbProps.accessSecret);
		
		StaContentModifier modifier = new StaContentModifier();
		StaStorageWikibase wbStorage = new StaStorageWikibase(wbapi);
		StaContent staContent = wbStorage.load("Q9503");
		
		modifier.addPattern("P389", "Q3399", "EXAMPLE: $value");
		
		modifier.modify(staContent);
		staContent.setId(StaStorageConfluence.CONFLUENCE_KEY , "297245251");
		
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		logger.info(gson.toJson(staContent));
	}

}
