package de.dnb.sta.storage.datamodel;

import de.dnb.sta.storage.datamodel.StaDatamodel.Type;

public class StaProperty {
	
	private final String key;
	
	private final String label;
	
	private final Type valueType;
	
	public StaProperty(String key, String label, Type valueType) {
		this.key=key;
		this.label = label;
		this.valueType = valueType;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public Type getValueType() {
		return valueType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StaProperty) {
			return ((StaProperty) obj).getKey().equals(key);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}
}
