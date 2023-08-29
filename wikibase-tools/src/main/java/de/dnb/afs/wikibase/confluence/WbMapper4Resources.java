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
public class WbMapper4Resources extends WbMapper {

	private static final Log logger = LogFactory.getLog(WbMapper4Resources.class);

	
	public WbMapper4Resources(WbEntityEditor wbEditor, STAIdGenerator staIdGenerator, List<WbMapping> wbMappings,
			ConfluenceWbConfig config) throws MediaWikiApiErrorException, IOException {
		super(wbEditor, staIdGenerator, wbMappings, config);
	}

	public void addInitialStatements(WbEntityProperties entityProperties) {
		entityProperties.getStatements()
				.add(StatementBuilder.forSubjectAndProperty(entityProperties.getEntityId(), config.pSchema)
						.withValue(config.iRdaDocumentation).build());
		entityProperties.getStatements()
				.add(StatementBuilder.forSubjectAndProperty(entityProperties.getEntityId(), config.pElementOf)
						.withValue(config.iRdaRessourceType).build());
	}

//	@Override
//	protected void addLabels(Document document, WbEntityProperties entity) {
//		String labelDe = document.title().replaceFirst(":", " -");
//		entity.getLabels().put("de", Datamodel.makeMonolingualTextValue(labelDe, "de"));
//	}
}
