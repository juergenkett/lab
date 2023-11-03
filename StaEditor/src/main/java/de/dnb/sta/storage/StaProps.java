package de.dnb.sta.storage;

import java.util.HashSet;
import java.util.Set;

import de.dnb.sta.storage.wikibase.WbProps;

public class StaProps  {

	
	public String confluenceUser; 
	public String confluencePass;
	public String confluenceSpace;
	
	public  WbProps wbTestProps;
	public  WbProps wbProps;
	
	
//	private Set<String> textProperties = new HashSet<String>();

	
//	public boolean isTextProperty(String propKey) {
//		return textProperties.contains(propKey);
//	}
//	
//	public void addTextProperty(String propkey) {
//		this.textProperties.add(propkey);
//	}
}
