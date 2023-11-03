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

public class ReadFromConfluenceApp {

	private static final Logger LOGGER = System.getLogger(ReadFromConfluenceApp.class.getName());

	public static void main(String[] args)
			throws SecurityException, URISyntaxException, IOException, InterruptedException {
		final StaProps wbProps = PropsHelper.getProps();

		StaStorageConfluence confluenceEditor = new StaStorageConfluence(wbProps, new MockStaStorage(), new MockStaStorage());
		StaContent desc = confluenceEditor.load("297245251");

		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		LOGGER.log(Level.INFO, gson.toJson(desc));
	}

	public static void update() {

	}

}

