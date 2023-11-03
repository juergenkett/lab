package de.dnb.afs.wikibase.confluence;

import java.io.IOException;
import java.util.List;

import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import de.dnb.afs.wikibase.WbEntityEditor;
import de.dnb.afs.wikibase.WbEntityProperties;

/**
 * @author kett
 *
 */
public class WbMapper4Resources extends WbMapper {
	
	public WbMapper4Resources(WbEntityEditor wbEditor, List<WbMapping> wbMappings,
			ConfluenceWbConfig config) throws MediaWikiApiErrorException, IOException {
		super(wbEditor, wbMappings, config);
	}

	public void addInitialStatements(WbEntityProperties props) {
		props.addStatement(StatementBuilder.forSubjectAndProperty(props.getEntityId(), config.pSchema)
						.withValue(config.iRdaDocumentation).build());
		props.addStatement(StatementBuilder.forSubjectAndProperty(props.getEntityId(), config.pElementOf)
						.withValue(config.iRdaRessourceType).build());
	}

//	@Override
//	protected void addLabels(Document document, WbEntityProperties entity) {
//		String labelDe = document.title().replaceFirst(":", " -");
//		entity.getLabels().put("de", Datamodel.makeMonolingualTextValue(labelDe, "de"));
//	}
}
