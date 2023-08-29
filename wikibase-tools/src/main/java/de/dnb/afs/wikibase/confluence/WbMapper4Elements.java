package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.STAIdGenerator;
import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityProperties;

/**
 * @author kett
 *
 */
public class WbMapper4Elements extends WbMapper {

	public WbMapper4Elements(WbEntityEditor wbEditor, STAIdGenerator staIdGenerator, List<WbMapping> wbMappings,
			ConfluenceWbConfig config) throws MediaWikiApiErrorException, IOException {
		super(wbEditor, staIdGenerator, wbMappings, config);
	}

	private static final Log logger = LogFactory.getLog(WbMapper4Elements.class);

	protected void addInitialStatements(WbEntityProperties entityProperties) {
		entityProperties.getStatements()
				.add(StatementBuilder.forSubjectAndProperty(entityProperties.getEntityId(), config.pSchema)
						.withValue(config.iRdaDocumentation).build());
		entityProperties.getStatements()
				.add(StatementBuilder.forSubjectAndProperty(entityProperties.getEntityId(), config.pElementOf)
						.withValue(config.iRdaProperty).build());
	}

//	@Override
//	protected void addLabels(Document document, WbEntityProperties entity) {
//		String label = document.title();
//		String labelDe = null;
//		String labelEn = null;
//		if (label != null) {
//			String[] labels = label.split("/");
//			labelDe = labels[0];
//			if (labels.length > 1) {
//				labelEn = labels[1];
//			}
//			entity.getLabels().put("de", Datamodel.makeMonolingualTextValue(labelDe, "de"));
//			entity.getLabels().put("en", Datamodel.makeMonolingualTextValue(labelEn, "en"));
//		}
//	
//	}

}
