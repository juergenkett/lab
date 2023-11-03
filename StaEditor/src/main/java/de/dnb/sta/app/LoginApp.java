package de.dnb.sta.app;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.github.scribejava.apis.MediaWikiApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;

import de.dnb.sta.storage.PropsHelper;
import de.dnb.sta.storage.StaProps;

public class LoginApp {

	final static String WB_PROD = "https://sta.dnb.de";
	final static String WB_TEST = "https://testwikibase0.dnb.de";
	
	public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
		
		final MediaWikiApi mediaWikiApi = new MediaWikiApi(WB_PROD+ "/w/index.php",
				WB_PROD+ "/wiki/");
		
		final StaProps staProps = PropsHelper.getProps();

		final OAuth10aService service = new ServiceBuilder(staProps.wbProps.consumerKey)
				.apiSecret(staProps.wbProps.consumerSecret).build(mediaWikiApi);

		final Scanner in = new Scanner(System.in);

		System.out.println("=== Twitter's OAuth Workflow ===");
		System.out.println();

		// Obtain the Request Token
		System.out.println("Fetching the Request Token...");
		final OAuth1RequestToken requestToken = service.getRequestToken();		
		System.out.println("Got the Request Token!");
		System.out.println();

		System.out.println("Now go and authorize ScribeJava here:");
		System.out.println(service.getAuthorizationUrl(requestToken));
		System.out.println("And paste the verifier here");
		System.out.print(">>");
		final String oauthVerifier = in.nextLine();
		System.out.println();

		// Trade the Request Token and Verifier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
		System.out.println("Got the Access Token!");
		System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
		System.out.println();
		in.close();

	}

}
