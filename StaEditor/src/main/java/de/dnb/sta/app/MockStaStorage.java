package de.dnb.sta.app;

import de.dnb.sta.storage.StaContentStorage;
import de.dnb.sta.storage.StaPropertyStorage;
import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaDatamodel.Type;
import de.dnb.sta.storage.datamodel.StaProperty;

public class MockStaStorage implements StaPropertyStorage, StaContentStorage {

	@Override
	public StaProperty getProperty(String propid) {
		return new StaProperty(propid, "Label von " + propid, Type.STRING);
	}

	@Override
	public StaContent load(String id) {
		StaContent ret = new StaContent();
		return ret;
	}

	@Override
	public String save(StaContent description) {
		return "ID";
	}

	@Override
	public String getKey() {
		return "MockStaStorage";
	}

}
