package de.dnb.sta.confluence.rest.datamodel;

public class ConfluenceContent {

	private final String type = "page";

	private String title;

	private Space space;

	private Body body;
	
	private Version version;
	
	private String id;
	
	public void setSpaceKey(String spaceKey) {
		this.space = new Space();
		this.space.key = spaceKey;
	}
	
	public void setBodyValue(String value) {
		this.body = new Body();
		this.body.storage = new Storage();
		this.body.storage.value = value;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
