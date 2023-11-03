package de.dnb.sta.storage.datamodel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class StaContent {

	private String sourceKey;

	private Map<String, String> ids = new LinkedHashMap<String, String>();

	private Map<String, Long> versions = new LinkedHashMap<String, Long>();

	private Map<String, String> types = new LinkedHashMap<String, String>();

	private String title;

//	private StaStatementGroups statementGroups = new StaStatementGroups();

	private StaStatementList statements = new StaStatementList();

	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

//	public Collection<StaStatementGroup> getStatementGroups() {
//		return statementGroups.getAll();
//	}

//	public void addStatementGroup(StaStatementGroup statementGroup) {
//		this.statementGroups.add(statementGroup);
//	}

//	public StaStatementGroup getStatementGroup(String propertyKey) {
//		return this.statementGroups.get(propertyKey);
//	}

//	public Collection<StaValue> getStatementGroupValues(String propertyKey) {
//		StaStatementGroup group = this.statementGroups.get(propertyKey);
//		if (group == null)
//			return Collections.emptyList();
//		return group.getValues();
//	}

	public StaStatement getFirstStatement(String propertyKey) {
		try {
			return this.statements.stream().filter(staValue -> staValue.getProperty().getKey().equals(propertyKey))
					.findFirst().get();
		} catch (NoSuchElementException e) {
			return null;
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getIds() {
		return ids;
	}

	public String getId(String key) {
		return ids.get(key);
	}

	public void setIds(Map<String, String> ids) {
		this.ids = ids;
	}

	public void setId(String key, String id) {
		this.ids.put(key, id);
	}

	public String getType(String key) {
		return this.types.get(key);
	}

	public String setType(String key, String type) {
		return this.types.put(key, type);
	}

	public Map<String, Long> getVersions() {
		return versions;
	}

	public long getVersion(String key) {
		if (versions.get(key) == null)
			return 0;
		return versions.get(key);
	}

	public void setVersions(Map<String, Long> versions) {
		this.versions = versions;
	}

	public void setVersion(String key, long version) {
		this.versions.put(key, version);
	}

	public StaStatementList getStatements() {
		return statements;
	}

	public void setStatements(StaStatementList statements) {
		this.statements = statements;
	}

//	public boolean hasStatement(String propertyKey, String value) {
//		StaStatementGroup statementGroup = this.getStatementGroup(propertyKey);
//		if (statementGroup != null) {
//			return statementGroup.hasValue(value);
//		}
//		return false;
//	}

}
