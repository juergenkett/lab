package de.dnb.afs.wikibase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;

public class WbEntityProperties {

	private Map<String, MonolingualTextValue> descriptions = new HashMap<String, MonolingualTextValue>() ;

	private List<MonolingualTextValue> aliases = new ArrayList<MonolingualTextValue>();

	private Map<String, MonolingualTextValue> labels = new HashMap<String, MonolingualTextValue>();
	
	private List<Statement> statements = new ArrayList<Statement>();

	private EntityIdValue entityId;
	
	private String staCode;
	
	long revisionId;
	
	public String getStaCode() {
		return staCode;
	}

	public void setStaCode(String staCode) {
		this.staCode = staCode;
	}

	public Map<String, MonolingualTextValue> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(Map<String, MonolingualTextValue> descriptions) {
		this.descriptions = descriptions;
	}

	public List<MonolingualTextValue> getAliases() {
		return aliases;
	}

	public void setAliases(List<MonolingualTextValue> aliases) {
		this.aliases = aliases;
	}

	public Map<String, MonolingualTextValue> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, MonolingualTextValue> labels) {
		this.labels = labels;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	public EntityIdValue getEntityId() {
		return entityId;
	}

	public void setEntityId(EntityIdValue entityId) {
		this.entityId = entityId;
	}

	public long getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(long revisionId) {
		this.revisionId = revisionId;
	}
}
