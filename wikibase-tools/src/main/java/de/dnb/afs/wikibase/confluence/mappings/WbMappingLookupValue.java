package de.dnb.afs.wikibase.confluence.mappings;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.select.Elements;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityLoader;
import de.dnb.afs.wikibase.confluence.WbMapping;

public class WbMappingLookupValue extends WbMapping {

	private static final Log logger = LogFactory.getLog(WbMappingLookupValue.class);

	public static final String VALUE_PLACEHOLDER = "$value";

	private PropertyIdValue wbId;

	private String lookupPattern;

	private WbEntityLoader wbEntityLoader;

	public WbMappingLookupValue(String labelPattern, PropertyIdValue wbId, String lookupPattern,
			WbEntityLoader wbEntityLoader) {
		super(labelPattern);
		this.wbId = wbId;
		this.lookupPattern = lookupPattern;
		this.wbEntityLoader = wbEntityLoader;
	}

	@Override
	public void doMap(Map<String, MonolingualTextValue> entityLabels, String label, Elements panelElements, EntityIdValue wbEntityId, List<Statement> statements) {
		/*
		 * panel content auf value-Bereich zuschneiden
		 */
		if (panelElements == null || panelElements.size() < 1) {
			logger.debug("Überspringe panel, da das Panel leer ist.");
			return;
		}

		String value = panelElements.first().text();
		String query = lookupPattern.replace(VALUE_PLACEHOLDER, value);
		logger.debug("Suche Entität mit Query: '" + query + "'");
		try {
			EntityDocument entity = wbEntityLoader.lookupEntity(query);
			if (entity != null) {
				Statement statement = StatementBuilder.forSubjectAndProperty(wbEntityId, wbId)
						.withValue(entity.getEntityId()).build();
				logger.debug("Füge statement für label " + label + " hinzu: " + statement);
				statements.add(statement);
			}
		} catch (MediaWikiApiErrorException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		}

	}
}