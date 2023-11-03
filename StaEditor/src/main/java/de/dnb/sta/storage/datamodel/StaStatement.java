package de.dnb.sta.storage.datamodel;

import java.util.Optional;

import de.dnb.sta.storage.datamodel.StaDatamodel.Action;
import de.dnb.sta.storage.datamodel.StaDatamodel.Type;

public class StaStatement {

	private StaProperty property;

	private String value;

	private String id;

	private Action action = null;

//	private StaStatementGroups qualifiers;

	private StaStatementList qualifiers;

	private StaContent embeddedContent;

//	private StaEmbeddedTextValue embeddedTextValue;

//	public StaValue() {
//
//	}

	public StaStatement(StaProperty property, String value, String id, Action action) {
		this.value = value;
		this.id = id;
		this.action = action;
		this.property = property;
	}

//	public StaValue(String value) {
//		this.value = value;
//	}
//	
//	public StaValue(String value, Type type) {
//		this.value = value;
//		this.type = type;
//	}

	public String getValue() {
		return value;
	}

	public StaStatement setValue(String value) {
		this.value = value;
		return this;
	}

	public Action getAction() {
		return action;
	}

	public StaStatement setAction(Action action) {
		this.action = action;
		return this;
	}

	public String getId() {
		return id;
	}

	public StaStatement setId(String id) {
		this.id = id;
		return this;
	}

	public Type getType() {
		return property.getValueType();
	}

//
//	public StaStatementGroup getQualifier(String key) {
//		if (this.qualifiers == null)
//			return null;
//		return this.qualifiers.get(key);
//	}

	public String getQualifierValue(String key) {
		if (qualifiers == null)
			return null;
		Optional<StaStatement> staValue = this.qualifiers.stream().filter(v -> v.getProperty().getKey().equals(key))
				.findFirst();
		return staValue.isPresent() ? staValue.get().getValue() : null;

//		String ret = null;
//		if (this.qualifiers == null)
//			return null;
//		StaStatementGroup statementGroup = this.qualifiers.get(key);
//		if (statementGroup != null) {
//			List<StaValue> qualifierValues = statementGroup.getValues();
//			if (qualifierValues != null && !qualifierValues.isEmpty()) {
//				return qualifierValues.get(0).getValue();
//			}
//		}
//		return ret;
	}

	public void removeQualifier(String key) {
		if (this.qualifiers != null)
			this.qualifiers.removeStatements(key);
	}

	public StaStatement addQualifier(StaStatement value) {
		if (this.qualifiers == null) {
			this.qualifiers = new StaStatementList();
		}
		this.qualifiers.add(value);
		return this;
	}

//	public StaValue addQualifier(StaStatementGroup statementGroup) {
//		if (this.qualifiers == null) {
//			this.qualifiers = new StaStatementGroups();
//		}
//		this.qualifiers.add(statementGroup);
//		return this;
//	}

	public StaContent getEmbeddedContent() {
		return embeddedContent;
	}

	public StaStatement setEmbeddedContent(StaContent embeddedContent) {
		this.embeddedContent = embeddedContent;
		return this;
	}

	public StaProperty getProperty() {
		return property;
	}

	public StaStatement setProperty(StaProperty property) {
		this.property = property;
		return this;
	}

	public StaStatementList getQualifiers() {
		return qualifiers;
	}

	public void setQualifiers(StaStatementList qualifiers) {
		this.qualifiers = qualifiers;
	}

//	public StaEmbeddedTextValue getEmbeddedTextValue() {
//		return embeddedTextValue;
//	}
//
//	public void setEmbeddedTextValue(StaEmbeddedTextValue embeddedTextValue) {
//		this.embeddedTextValue = embeddedTextValue;
//	}

}
