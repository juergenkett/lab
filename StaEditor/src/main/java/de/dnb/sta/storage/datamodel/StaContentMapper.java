package de.dnb.sta.storage.datamodel;

public interface StaContentMapper <A,B> {
	
	public A map(StaContent staContent);
	
	public StaContent map(B targetContent);

}
