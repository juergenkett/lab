package de.dnb.sta.app;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dnb.sta.storage.PropsHelper;
import de.dnb.sta.storage.StaProps;
import de.dnb.sta.storage.confluence.StaStorageConfluence;
import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.wikibase.StaStorageWikibase;

public class SyncFromConfluenceApp {

	private static final Logger LOGGER = System.getLogger(SyncFromConfluenceApp.class.getName());

	public static void main(String[] args)
			throws SecurityException, URISyntaxException, IOException, InterruptedException {
		final StaProps staProps = PropsHelper.getProps();
		StaStorageWikibase wbStorage = new StaStorageWikibase(staProps.wbProps);
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		
		StaStorageConfluence confluenceEditor = new StaStorageConfluence(staProps, wbStorage, wbStorage);
//		staContent.setId(StaStorageConfluence.CONFLUENCE_KEY , "297245251");
		StaContent confluenceContent = confluenceEditor.load("297245251");
		LOGGER.log(Level.INFO, gson.toJson(confluenceContent));

	}
}
