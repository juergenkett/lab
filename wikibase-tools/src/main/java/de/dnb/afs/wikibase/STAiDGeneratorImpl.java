package de.dnb.afs.wikibase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.confluence.ConfluenceWbConfig;

public class STAiDGeneratorImpl implements STAIdGenerator {

	private ConfluenceWbConfig config;

	private WbEntityLoader wbEntityLoader;

	private Map<String, String> codesMap = new HashMap<String, String>();

	public STAiDGeneratorImpl(ConfluenceWbConfig config, WbEntityLoader wbEntityLoader) {
		this.config = config;
		this.wbEntityLoader = wbEntityLoader;
	}
	
	@Override
	public String generateId(WbEntityProperties props) throws MediaWikiApiErrorException, IOException {
		return "rda-" + props.getEntityId().getId().substring(1);
	}
	
//	@Override
//	public String getId(TermedStatementDocument document) throws MediaWikiApiErrorException, IOException {
//		Statement statement = document.findStatement("");
//		if (statement == null) {
//			return generateId(document);
//		} else {
//			return ((StringValue) statement.getValue()).getString();
//		}
//	}

//	public String generateId(TermedStatementDocument document) throws MediaWikiApiErrorException, IOException {
//		Validate.isFalse(document.getEntityId().isPlaceholder());
//		String entityTypeQid = findItemIdValue(document, config.pEntityType);
//		String code = codesMap.get(entityTypeQid);
//		if (code == null) {
//			TermedStatementDocument entityTypeDoc = (TermedStatementDocument) wbEntityLoader.lookupEntity(entityTypeQid);
//			Validate.notNull(entityTypeDoc, "Entitätentyp " + entityTypeQid + " konne in Wikibase nicht gefunden werden.");
//			String label = entityTypeDoc.findLabel("de");
//			Validate.isTrue(label.startsWith("STA-Klasse - "));
//			code = String.valueOf(label.charAt(13));
//			codesMap.put(entityTypeQid, entityTypeDoc.findLabel(code));
//		}
//		return "R-" + code + "-" + document.getEntityId().getId().substring(1);
//	}

//	private String findItemIdValue(TermedStatementDocument document, PropertyIdValue propId) {
//		ItemIdValue elementOfValue = document.findStatementItemIdValue(config.pElementOf);
//		Validate.notNull(elementOfValue,
//				"Zur Generierung einer Kurzbezeichnung muss die Eigenschaft " + propId.getId() + " gesetzt sein.");
//		return elementOfValue.getId();
//	}

}
