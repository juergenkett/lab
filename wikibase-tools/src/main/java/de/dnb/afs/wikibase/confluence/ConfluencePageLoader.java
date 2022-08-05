package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ConfluencePageLoader {

	private static final String CONFLUENCE_LOGIN_PATH = "/dologin.action";

	private static final String PAGE_PATH = "/pages/viewpage.action?pageId=";
	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36";

	private Response loginResponse;
	private String username;
	private String password;
	private String confluenceBaseUrl;

	public ConfluencePageLoader(String confluenceBaseUrl, String username, String password) {
		this.username = username;
		this.password = password;
		this.confluenceBaseUrl = confluenceBaseUrl;
	}

	public Document loadDocument(String pageId) throws IOException {
		if (loginResponse == null) {
			loginResponse = doLoginPost();
		}

		Map<String, String> cookies = loginResponse.cookies();

		Document document = Jsoup.connect(confluenceBaseUrl + PAGE_PATH + pageId).cookies(cookies) 
				.method(Method.GET).userAgent(USER_AGENT).get();

		return document;
	}
	
	public Document loadDocument(URL url) throws IOException {
		if (loginResponse == null) {
			loginResponse = doLoginPost();
		}
		Map<String, String> cookies = loginResponse.cookies();
		Document document = Jsoup.connect(url.toExternalForm()).cookies(cookies) 
				.method(Method.GET).userAgent(USER_AGENT).get();
		return document;
	}

	public Response doLoginPost() throws IOException {
		return Jsoup.connect(confluenceBaseUrl + CONFLUENCE_LOGIN_PATH).method(Method.POST).userAgent("Mozilla").ignoreContentType(true)
				.data("os_username", username).data("os_password", password)
				.header("Content-Type", "application/x-www-form-urlencoded").execute();
	}
}
