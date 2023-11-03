package de.dnb.sta.storage;

public class StaStorageSynchronizer {

	private StaContentStorage source;

	private StaContentStorage target;

	public StaStorageSynchronizer(StaContentStorage source, StaContentStorage target) {
		this.source = source;
		this.target = target;
	}

	public void sync(String id) {
		this.target.save(this.source.load(id));
	}

}
