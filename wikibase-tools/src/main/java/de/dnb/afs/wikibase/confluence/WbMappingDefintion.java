package de.dnb.afs.wikibase.confluence;

import org.wikidata.wdtk.datamodel.implementation.PropertyIdValueImpl;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;

public class WbMapping {

	public static final String VALUE_PLACEHOLDER = "$value";

	private boolean isNewItem = false;

	private String label;

	private String value;

	private String wbId;

	private String lookupPattern;

	private Value wbValue;

	public WbMapping(String label, String wbId) {
		this.label = label;
		this.wbId = wbId;
		this.value = null;
	}

	public boolean isNewItem() {
		return isNewItem;
	}

	public WbMapping withNewItem(boolean isNewItem) {
		this.isNewItem = isNewItem;
		return this;
	}

	public String getLabel() {
		return label;
	}

	public WbMapping withLabel(String label) {
		this.label = label;
		return this;
	}

	public String getWbId() {
		return wbId;
	}

	public PropertyIdValue getWbPropertyId() {
		return new PropertyIdValueImpl(wbId, Utils.GND_DOKU_SITE_IRI);
	}

	public WbMapping withWbId(String wbId) {
		this.wbId = wbId;
		return this;
	}

	public String getLookupPattern() {
		return lookupPattern;
	}

	public WbMapping withLookupPattern(String lookupPattern) {
		this.lookupPattern = lookupPattern;
		return this;
	}

	public String getValue() {
		return value;
	}

	public WbMapping withValue(String value) {
		this.value = value;
		return this;
	}

	public WbMapping withWbValue(Value wbValue) {
		this.wbValue = wbValue;
		return this;
	}

	public Value getWbValue() {
		return this.wbValue;
	}

}
