package de.dnb.sta.storage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import de.dnb.sta.storage.wikibase.WbProps;

public class PropsHelper {
	static String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	static String appConfigPath = rootPath + "wb.properties";

	public static StaProps getProps() throws FileNotFoundException, IOException {
		Properties appProps = new Properties();
		appProps.load(new FileInputStream(appConfigPath));
		StaProps ret = new StaProps();
		ret.wbProps = new WbProps();
		ret.wbTestProps = new WbProps();

		
		ret.wbProps.consumerKey = appProps.getProperty("wb.consumerKey");
		ret.wbProps.consumerSecret = appProps.getProperty("wb.consumerSecret");
		ret.wbProps.accessToken = appProps.getProperty("wb.accessToken");
		ret.wbProps.accessSecret = appProps.getProperty("wb.accessSecret");
		ret.wbProps.wbUrl = appProps.getProperty("wb.url");
		
		ret.wbTestProps.consumerKey = appProps.getProperty("wb.test.consumerKey");
		ret.wbTestProps.consumerSecret = appProps.getProperty("wb.test.consumerSecret");
		ret.wbTestProps.accessToken = appProps.getProperty("wb.test.accessToken");
		ret.wbTestProps.accessSecret = appProps.getProperty("wb.test.accessSecret");
		ret.wbTestProps.wbUrl = appProps.getProperty("wb.test.url");
		
		ret.confluenceUser = appProps.getProperty("confluence.user");
		ret.confluencePass = appProps.getProperty("confluence.pass");
		ret.confluenceSpace = appProps.getProperty("confluence.space");

		
		
		
		return ret;
	}

}
