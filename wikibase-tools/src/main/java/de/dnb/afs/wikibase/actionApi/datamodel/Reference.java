package de.dnb.afs.wikibase.actionApi.datamodel;

import java.util.List;

import com.google.gson.annotations.SerializedName;

class Reference {
	String hash;
	List<Snak> snaks;
	
	@SerializedName("snaks-order")
	List<String> snaksOrder;
}
