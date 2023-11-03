package de.dnb.sta.storage.datamodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class StaStatementList extends ArrayList<StaStatement> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public synchronized void removeStatements(String propertyKey) {
		removeAll(findStatements(propertyKey).toList());
	}

	public Stream<StaStatement> findStatements(String propertyKey) {
		return this.stream().filter(staValue -> staValue.getProperty().getKey().equals(propertyKey));
	}

	public StaStatement findFirstStatement(String propertyKey) {
		Optional<StaStatement> staValue = this.stream().filter(v -> v.getProperty().getKey().equals(propertyKey))
				.findFirst();
		return staValue.isPresent() ? staValue.get() : null;
	}

	public String findFirstValue(String propertyKey) {
		Optional<StaStatement> staValue = this.stream().filter(v -> v.getProperty().getKey().equals(propertyKey))
				.findFirst();
		return staValue.isPresent() ? staValue.get().getValue() : null;
	}

	public Map<StaProperty, List<StaStatement>> getStatementsMap() {
		LinkedHashMap<StaProperty, List<StaStatement>> ret = new LinkedHashMap<StaProperty, List<StaStatement>>();
		for (StaStatement value : this) {
			List<StaStatement> list = ret.get(value.getProperty());
			if (list == null) {
				list = new ArrayList<StaStatement>();
				ret.put(value.getProperty(), list);
			}
			list.add(value);
		}
		return ret;
	}

	public boolean hasStatement(String propertyKey) {
		return this.stream().filter(staValue -> staValue.getProperty().getKey().equals(propertyKey)).findAny()
				.isPresent();
	}

	public boolean hasStatement(String propertyKey, String value) {
		return this.stream().filter(
				staValue -> staValue.getProperty().getKey().equals(propertyKey) && staValue.getValue().equals(value))
				.findAny().isPresent();
	}
}
