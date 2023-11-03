package de.dnb.sta.app;

import java.io.IOException;
import java.net.URISyntaxException;

import de.dnb.sta.storage.PropsHelper;
import de.dnb.sta.storage.StaProps;
import de.dnb.sta.storage.confluence.StaStorageConfluence;
import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaDatamodel.Type;
import de.dnb.sta.storage.datamodel.StaProperty;
import de.dnb.sta.storage.datamodel.StaStatement;

public class WriteToConfluenceApp {

	public static void main(String[] args)
			throws SecurityException, URISyntaxException, IOException, InterruptedException {
		final StaProps wbProps = PropsHelper.getProps();

		StaStorageConfluence confluenceEditor = new StaStorageConfluence(wbProps, new MockStaStorage(),
				new MockStaStorage());

		StaContent staContent = new StaContent();
		staContent.setTitle("STA Element 001");
		staContent.setId("MOCK-STORAGE-ID", "Q1");
		staContent.setId(StaStorageConfluence.CONFLUENCE_KEY, "297244487");
		StaProperty staProp1 = new StaProperty("P2", "Definition", Type.STRING);
		StaProperty staProp2 = new StaProperty("P1", "Beschreibung", Type.STRING);

		StaStatement value1 = new StaStatement(staProp2, "Dies ist eine Elementbeschreibung", "Statement-ID-1", null);

		String valueString = "<strong>Dies</strong> ist eine Elementdefinition. <p> Warum auch immer. </p> "
				+ "<h1>Jetzt kommt auch noch ein <a href=\"nirgendwohin\">Link</a> in einer Überschrift</h1>"
				+ "<div id=\"divid\">Mal sehen, ob divs und ids geschrieben werden dürfen.</div>";
		StaStatement value2 = new StaStatement(staProp1, valueString, "Statement-ID-2", null);

		StaStatement value3 = new StaStatement(staProp1, "Der dritte Wert!", "Statement-ID-3", null);
		
		staContent.getStatements().add(value1);
		staContent.getStatements().add(value2);
		staContent.getStatements().add(value3);


		confluenceEditor.createOrUpdateStaContent(staContent);
	}
}
