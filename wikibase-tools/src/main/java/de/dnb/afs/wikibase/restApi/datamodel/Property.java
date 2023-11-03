package de.dnb.afs.wikibase.restApi.datamodel;

import com.google.gson.annotations.SerializedName;

class Property {
	public String id;

	@SerializedName("data-type")
	public String dataType;
}