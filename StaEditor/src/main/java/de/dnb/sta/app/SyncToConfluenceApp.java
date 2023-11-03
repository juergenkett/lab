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

public class SyncToConfluenceApp {

	private static final Logger LOGGER = System.getLogger(SyncToConfluenceApp.class.getName());

	public static void main(String[] args)
			throws SecurityException, URISyntaxException, IOException, InterruptedException {
		final StaProps wbProps = PropsHelper.getProps();

		StaStorageWikibase wbStorage = new StaStorageWikibase(wbProps.wbProps);
		StaContent staContent = wbStorage.load("P531");
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		LOGGER.log(Level.INFO, gson.toJson(staContent));
		
		StaStorageConfluence confluenceEditor = new StaStorageConfluence(wbProps, wbStorage, wbStorage);
		staContent.setId(StaStorageConfluence.CONFLUENCE_KEY , "297245251");
		confluenceEditor.save(staContent);
	}
}
