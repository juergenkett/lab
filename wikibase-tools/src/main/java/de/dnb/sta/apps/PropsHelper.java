package de.dnb.afs.wikibase.apps;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropsHelper {
	static String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
	static String appConfigPath = rootPath + "wb.properties";

	public static WbProps getProps() throws FileNotFoundException, IOException {
		Properties appProps = new Properties();
		appProps.load(new FileInputStream(appConfigPath));
		WbProps ret = new WbProps();

		ret.consumerKey = appProps.getProperty("wb.consumerKey");
		ret.consumerSecret = appProps.getProperty("wb.consumerSecret");
		ret.accessToken = appProps.getProperty("wb.accessToken");
		ret.accessSecret = appProps.getProperty("wb.accessSecret");
		ret.confluenceUser = appProps.getProperty("confluence.user");
		ret.confluencePass = appProps.getProperty("confluence.pass");
		return ret;

	}

}
