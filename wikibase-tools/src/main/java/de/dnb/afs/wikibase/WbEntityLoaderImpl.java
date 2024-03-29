package de.dnb.afs.wikibase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyDocument;
import org.wikidata.wdtk.wikibaseapi.WbGetEntitiesSearchData;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public class WbEntityLoaderImpl implements WbEntityLoader {

	protected WikibaseDataFetcher wbdf;

	private Map<String, String> pidCache = new HashMap<String, String>();

	private static final Log logger = LogFactory.getLog(WbEntityLoaderImpl.class);

	public WbEntityLoaderImpl(WikibaseDataFetcher wbdf) throws MediaWikiApiErrorException, IOException {
		this.wbdf = wbdf;
	}

	public EntityDocument lookupEntity(String query) throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		properties.search = query;
		properties.language = "de";
		properties.type = "property";
		EntityDocument ret = lookupEntity(properties);
		if (ret == null) {
			properties.type = "item";
			ret = lookupEntity(properties);
		}
		return ret;
	}

	public ItemDocument lookupItem(String query) throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		properties.search = query.trim();
		properties.language = "de";
		properties.type = "item";
		EntityDocument ret = lookupEntity(properties);
		return (ItemDocument) ret;
	}

	public PropertyDocument lookupProperty(String query) throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		String normalizedQuery = query.trim();
		properties.search = normalizedQuery; // URLEncoder.encode(query, "UTF-8");
		properties.language = "de";
		properties.type = "property";
		EntityDocument ret = lookupEntity(properties);
		if (ret != null) {
			pidCache.put(normalizedQuery, ret.getEntityId().getId());
		}
		return (PropertyDocument) ret;
	}

	public String lookupPID(String query) throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		String normalizedQuery =query.trim();
		String ret = pidCache.get(normalizedQuery);
		if (ret == null) {
			properties.search = normalizedQuery; // URLEncoder.encode(query, "UTF-8");
			properties.language = "de";
			properties.type = "property";
			EntityDocument doc = lookupEntity(properties);
			if (doc!=null) {
				ret=doc.getEntityId().getId();
			}
			pidCache.put(normalizedQuery, ret);
		}
		return ret;
	}

	public String lookupQID(String query) throws MediaWikiApiErrorException, IOException {
		WbGetEntitiesSearchData properties = new WbGetEntitiesSearchData();
		properties.search = query.trim(); // URLEncoder.encode(query, "UTF-8");
		properties.language = "de";
		properties.type = "item";
		EntityDocument entity = lookupEntity(properties);
		if (entity !=null) {
			return entity.getEntityId().getId();
		}
		return null;
	}

	public EntityDocument lookupEntity(WbGetEntitiesSearchData properties)
			throws MediaWikiApiErrorException, IOException {
		properties.limit = Integer.toUnsignedLong(1);
		List<WbSearchEntitiesResult> results = wbdf.searchEntities(properties);
		
		logger.debug("Suchergebnis nach '" + properties.search + "': " + results.size());
		for (WbSearchEntitiesResult result : results) {
//			if (result.getMatch().getText().equalsIgnoreCase(properties.search)) {
//				logger.info("Passende Entität gefunden für '" +  properties.search + "': " + result.getEntityId() + " '" + result.getMatch().getText() + "'");
			EntityDocument entity = wbdf.getEntityDocument(result.getEntityId());
			return entity;
//			}
		}
		return null;
	}

	public EntityDocument getEntity(String wbEntityId) throws MediaWikiApiErrorException, IOException {
		return wbdf.getEntityDocument(wbEntityId);
	}
}
