package de.dnb.sta.storage.confluence;

import java.io.IOException;
import java.lang.System.Logger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.dnb.sta.confluence.rest.datamodel.ConfluenceContent;
import de.dnb.sta.storage.StaContentStorage;
import de.dnb.sta.storage.StaPropertyStorage;
import de.dnb.sta.storage.StaProps;
import de.dnb.sta.storage.TagCleanerModifier;
import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaContentMapper;

public class StaStorageConfluence implements StaContentStorage {

//	private static final Logger LOGGER = System.getLogger(StaContentStorage.class.getName());

	private HttpClient client = HttpClient.newHttpClient();

	private Gson gson;

	private String authHeader;

	public final static String CONFLUENCE_KEY = "confluence";
	
	public StaContentMapper<ConfluenceContent, ConfluenceContent> mapper;

	private String confluenceUrl = "https://wiki.dnb.de";
	
	private String editorBaseUrl;

	public StaStorageConfluence(StaProps props, StaPropertyStorage staPropertyStorage, StaContentStorage staContentStorage) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		this.gson = builder.create();
		this.authHeader = getBasicAuthenticationHeader(props.confluenceUser, props.confluencePass);
		this.mapper = new StaContentConfluenceMapper(props.confluenceSpace, staPropertyStorage, staContentStorage);
	}

	public StaContentMapper<ConfluenceContent, ConfluenceContent> getMapper() {
		return mapper;
	}

	public void setMapper(StaContentMapper<ConfluenceContent, ConfluenceContent> mapper) {
		this.mapper = mapper;
	}

	@Override
	public StaContent load(String id) {
		try {
			ConfluenceContent confluenceContent = readPage(id);
			return modify4load(mapper.map(confluenceContent));
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String save(StaContent description) {
		try {
			return this.createOrUpdateStaContent(modify4save(description));
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public StaContent modify4load(StaContent staContent) {
		return new TagCleanerModifier().modify(staContent);
	}
	
	public StaContent modify4save(StaContent staContent) {
		new ConvertToStorageFormatModifier().modify(staContent);
		return staContent;
	}

	public long getVersion(String pageId) throws IOException, InterruptedException {
		return readPage(pageId).getVersion().getNumber();
	}

	public ConfluenceContent readPage(String pageId) throws IOException, InterruptedException {
		HttpRequest request = getReadRequest(pageId);
		HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
		return gson.fromJson(response.body(), ConfluenceContent.class);
	}

	public String createOrUpdateStaContent(StaContent staContent) throws IOException, InterruptedException {
		HttpRequest request;
		if (staContent.getId(CONFLUENCE_KEY) != null) {
			request = getUpdateRequest(staContent);
		} else {
			request = getCreateRequest(staContent);
		}
		client.send(request, BodyHandlers.ofString());
		return staContent.getId(getKey());
	}

	private static final String getBasicAuthenticationHeader(String username, String password) {
		String valueToEncode = username + ":" + password;
		return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
	}

	private HttpRequest getReadRequest(String pageId) {
		String uriString = this.confluenceUrl + "/rest/api/content/" + pageId + "?expand=version,body.storage";
		return HttpRequest.newBuilder().uri(URI.create(uriString)).header("Authorization", authHeader)
				.header("X-Atlassian-Token", "no-check").header("Content-type", "application/json").GET().build();
	}

	private HttpRequest getUpdateRequest(StaContent staContent) throws IOException, InterruptedException {
		String confluenceId = staContent.getId(CONFLUENCE_KEY);

		if (confluenceId == null)
			throw new IllegalArgumentException(
					"Beim Anlegen einer neuen Seite muss die confluence page-id der zu aktualisierenden Seite angegeben werden!");

		ConfluenceContent confluenceContent = mapper.map(staContent);

		long versionNumber = confluenceContent.getVersion().getNumber();
		if (versionNumber == 0) {
			// hole die aktuelle Versionsnummer
			versionNumber = getVersion(confluenceId);
		}
		// erhöhe die aktuelle Versionsnummer um 1
		confluenceContent.getVersion().setNumber(versionNumber + 1);

		BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(confluenceContent));
		String uriString = "https://wiki.dnb.de/rest/api/content" + "/" + confluenceId;

		return HttpRequest.newBuilder().uri(URI.create(uriString)).header("Authorization", authHeader)
				.header("X-Atlassian-Token", "no-check").header("Content-type", "application/json")
				.header("id", confluenceId).PUT(body).build();
	}

	private HttpRequest getCreateRequest(StaContent staContent) {
		if (staContent.getId(CONFLUENCE_KEY) != null)
			throw new IllegalArgumentException(
					"Beim Anlegen einer neuen Seite darf keine confluence page-id angegeben werden!");
		ConfluenceContent confluenceContent = mapper.map(staContent);
		String uriString = "https://wiki.dnb.de/rest/api/content";
		BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(confluenceContent));
		return HttpRequest.newBuilder().uri(URI.create(uriString)).header("Authorization", authHeader)
				.header("X-Atlassian-Token", "no-check").header("Content-type", "application/json").POST(body).build();
	}

	public String getPageUrl(String confluenceId) {
		return this.confluenceUrl + "/pages/viewpage.action?pageId=" + confluenceId;
	}

	public String getConfluenceUrl() {
		return confluenceUrl;
	}

	public void setConfluenceUrl(String confluenceUrl) {
		this.confluenceUrl = confluenceUrl;
	}

	public String getEditorBaseUrl() {
		return editorBaseUrl;
	}

	public void setEditorBaseUrl(String editorBaseUrl) {
		this.editorBaseUrl = editorBaseUrl;
	}

	@Override
	public String getKey() {
		return CONFLUENCE_KEY;
	}
}
