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
public class WbMapper4Elements extends WbMapper {

	public WbMapper4Elements(WbEntityEditor wbEditor, List<WbMapping> wbMappings,
			ConfluenceWbConfig config) throws MediaWikiApiErrorException, IOException {
		super(wbEditor, wbMappings, config);
	}

	protected void addInitialStatements(WbEntityProperties props) {
		props.addStatement(StatementBuilder.forSubjectAndProperty(props.getEntityId(), config.pSchema)
						.withValue(config.iRdaDocumentation).build());
		props.addStatement(StatementBuilder.forSubjectAndProperty(props.getEntityId(), config.pElementOf)
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
