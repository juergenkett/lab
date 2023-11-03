package de.dnb.sta.storage;

import de.dnb.sta.storage.datamodel.StaContent;

public interface StaContentStorage {

	public StaContent load(String id);
	
	public String save(StaContent description);
	
	public String getKey();
	
}
