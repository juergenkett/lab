package de.dnb.sta.storage.confluence;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.dnb.sta.confluence.rest.datamodel.ConfluenceContent;
import de.dnb.sta.confluence.rest.datamodel.Version;
import de.dnb.sta.storage.StaContentStorage;
import de.dnb.sta.storage.StaPropertyStorage;
import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaContentMapper;
import de.dnb.sta.storage.datamodel.StaDatamodel.Action;
import de.dnb.sta.storage.datamodel.StaProperty;
import de.dnb.sta.storage.datamodel.StaStatement;

public class StaContentConfluenceMapper implements StaContentMapper<ConfluenceContent, ConfluenceContent> {

	private static final String DESCRIPTION = "P7";
	private static final String EMBEDS = "P396";

	private String targetSpace;

//	private StaContentModifier modifier;

	private StaPropertyStorage staPropertyStorage;

	private StaContentStorage staContentStorage;

	private static final Logger LOGGER = System.getLogger(StaContentConfluenceMapper.class.getName());

	public StaContentConfluenceMapper(String targetSpace, StaPropertyStorage staPropertyStorage,
			StaContentStorage staContentStorage) {
		this.targetSpace = targetSpace;
		this.staPropertyStorage = staPropertyStorage;
		this.staContentStorage = staContentStorage;
//		this.modifier = new StaContentModifier();
//		modifier.addPattern(LAYOUT_KEY, H1, "<h1>$value</h1>");
//		modifier.addPattern(LAYOUT_KEY, H2, "<h2>$value</h2>");
//		modifier.addPattern(LAYOUT_KEY, EXAMPLE,
//				"<ac:structured-macro ac:name=\"info\" ac:schema-version=\"1\"><ac:rich-text-body>$value</ac:rich-text-body></ac:structured-macro>");

	}

	@Override
	public ConfluenceContent map(StaContent staContent) {
//		modifier.modify(staContent);
		ConfluenceContent confluenceContent = new ConfluenceContent();
		confluenceContent.setBodyValue(mapStaContent(staContent).html());
		confluenceContent.setSpaceKey(targetSpace);
		confluenceContent.setTitle(staContent.getTitle());
		Version version = new Version();
		version.setNumber(staContent.getVersion(StaStorageConfluence.CONFLUENCE_KEY));
		confluenceContent.setVersion(version);
		return confluenceContent;
	}

	public Element mapStaContent(StaContent staContent) {
		final Document document = new Document("");
		document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
		document.outputSettings().escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml);
		document.outputSettings().prettyPrint(false);

		Element headerTable = document.body().appendElement("table");
		Element headerTableRow1 = headerTable.appendElement("tr");
		headerTableRow1.appendElement("th").text("Quelle");
		headerTableRow1.appendElement("th").text("Id");
		headerTableRow1.appendElement("th").text("Version");
		headerTableRow1.appendElement("th").text("Typ");
		Element headerTableRow2 = headerTable.appendElement("tr");
		headerTableRow2.appendElement("th").text("wikibase");
		headerTableRow2.appendElement("td").attr("class", "wikibase-id")
				.text(staContent.getId(staContentStorage.getKey()));
		headerTableRow2.appendElement("td").attr("class", "wikibase-version")
				.text(String.valueOf(staContent.getVersion(staContentStorage.getKey())));
		headerTableRow2.appendElement("td").attr("class", "wikibase-type")
				.text(String.valueOf(staContent.getType(staContentStorage.getKey())));

		Element statementsTable = document.body().appendElement("table");
		Integer counter = 0;
		for (Entry<StaProperty, List<StaStatement>> entry : staContent.getStatements().getStatementsMap().entrySet()) {
			// Property
			statementsTable.appendElement("tr").appendElement("th").attr("colspan", "3")
					.attr("class", "highlight-#00a3bf").attr("title", entry.getKey().getKey())
					.text(entry.getKey().getLabel());
			for (StaStatement value : entry.getValue()) {
				statementsTable.appendChild(mapStatement(value, entry.getKey(), null, counter++));
			}
		}
		return document.body();
	}

	public Element mapStatement(StaStatement value, StaProperty prop, Element propertyCol, int counter) {
		Element statementRow = new Element("tr").attr("class", "statement");

		// Property
		if (propertyCol != null) {
			statementRow.appendChild(propertyCol);
		}

		// Value
		Element valueCol = statementRow.appendElement("td").attr("class", "value");
		valueCol.appendChild(statementBox(prop.getKey(), value));

		// embedded statements
		if (value.getEmbeddedContent() != null) {
			valueCol.appendChild(
					mapEmbeddedStatementGroup(value.getEmbeddedContent().getStatements().getStatementsMap(), counter));
		}

		// Qualifiers
		Element qualifierCol = statementRow.appendElement("td").attr("class", "qualifiers");
		if (value.getQualifiers() != null && value.getQualifiers().size() > 0) {
			Element qualifierTable = qualifierCol.appendElement("table");
			for (StaStatement qualifier : value.getQualifiers()) {
//				for (StaValue staValue : qualifier.g .getValues()) {
				Element qualifierRow = qualifierTable.appendElement("tr").attr("class", "qualifier");
				// Qualifier property
				qualifierRow.appendElement("td").attr("title", qualifier.getProperty().getKey())
						.attr("class", "qualifier-label").text(qualifier.getProperty().getLabel());
				// Qualifier value
				qualifierRow.appendElement("td").attr("class", "qualifier-value").appendChild(mapValue(qualifier));
//				}
			}
		}

		// Actions
		statementRow.appendElement("td").attr("class", "action").appendChild(actionTable(counter));
		return statementRow;
	}

	public Element mapEmbeddedStatementGroup(Map<StaProperty, List<StaStatement>> statementsMap, Integer counter) {
		Element ret = new Element("table");
		for (Entry<StaProperty, List<StaStatement>> entry : statementsMap.entrySet()) {
			if (entry.getKey().getKey().equals(DESCRIPTION)) {
				for (StaStatement value : entry.getValue()) {
					Element statementRow = new Element("tr").attr("class", "embedded-statement");
					// Value
					Element valueCol = statementRow.appendElement("td").attr("class", "value");
					valueCol.appendChild(statementBox(entry.getKey().getKey(), value));
					ret.appendChild(statementRow);
				}
			}
		}
		return ret;
	}

	public Element mapValue(StaStatement staValue) {
		Element p = new Element("p");
		switch (staValue.getType()) {
		case ID:
			StaContent referencedContent = this.staContentStorage.load(staValue.getValue());
			String title = "";
			if (referencedContent != null) {
				title = normalizeTitle(referencedContent.getTitle());
			}
			p.appendElement("a").attr("href", "https://sta.dnb.de/entity/" + staValue.getValue()).attr("title", title)
					.text(staValue.getValue());
			break;
		default:
			p.append(staValue.getValue());
		}
		return p;

	}

	public static String normalizeTitle(String title) {
		String ret = title;
		int index = ret.indexOf('|');
		if (index > -1) {
			ret = ret.substring(0, index);
		}
		return ret.strip();
	}

//	public StaValue mapHtmlValue(String htmlValue) {
//		StaValue ret = new StaValue(htmlValue);
//
//		return ret;
//	}

	public static Element actionTable(int row) {
		Element table = new Element("table");
		table.appendElement("tr").appendChild(actionCol(row, "update", "⟳"))
				.appendChild(actionCol(row + 2, "remove", "x"));
		return table;
	}

	private static Element actionCol(int id, String className, String text) {
		Element actionCol = new Element("td").attr("title", className);
		Element taskElement = actionCol.appendElement("ac:task-list").appendElement("ac:task");
		taskElement.appendElement("ac:task-id").text(String.valueOf(id));
		taskElement.appendElement("ac:task-status").attr("class", className).text("incomplete");
		taskElement.appendElement("ac:task-body").text(text);
		return actionCol;
	}

	private Element statementBox(String propertyKey, StaStatement staValue) {
		Element ret = new Element("ac:structured-macro").attr("ac:name", "panel").attr("ac:schema-version", "1");
		String wbId = staValue.getId();
		if (wbId != null) {
			ret.appendElement("ac:parameter").attr("ac:name", "wikibase-entity-id").text(wbId);
		}
		ret.appendElement("ac:parameter").attr("ac:name", "borderStyle").text("none");
		ret.appendElement("ac:parameter").attr("ac:name", "showIcon").text("false");
		ret.appendElement("ac:parameter").attr("ac:name", "property-key").text(propertyKey);
		if (staValue.getId() != null) {
			ret.appendElement("ac:parameter").attr("ac:name", "statement-id").text(staValue.getId());
		}
		Element body = new Element("ac:rich-text-body");
		body.appendChild(mapValue(staValue));
		ret.appendChild(body);
		return ret;
	}

	@Override
	public StaContent map(ConfluenceContent confluenceContent) {
		if (confluenceContent.getId() == null) {
			return null;
		}
		StaContent staContent = new StaContent();
		staContent.setTitle(confluenceContent.getTitle());
		staContent.setId(StaStorageConfluence.CONFLUENCE_KEY, confluenceContent.getId());
		staContent.setVersion(StaStorageConfluence.CONFLUENCE_KEY, confluenceContent.getVersion().getNumber());
		staContent.setSourceKey(StaStorageConfluence.CONFLUENCE_KEY);
		final Document confluenceBody = Jsoup.parse(confluenceContent.getBody().storage.value);
		Elements wikibaseId = confluenceBody.getElementsByClass("wikibase-id");
		if (!wikibaseId.isEmpty()) {
			staContent.setId(staContentStorage.getKey(), wikibaseId.text());
		}
		Elements wikibaseVersion = confluenceBody.getElementsByClass("wikibase-version");
		if (!wikibaseVersion.isEmpty()) {
			staContent.setVersion(staContentStorage.getKey(), Long.parseLong(wikibaseVersion.text()));
		}
		Elements wikibaseType = confluenceBody.getElementsByClass("wikibase-type");
		staContent.setType(staContentStorage.getKey(), wikibaseType.text());
		Elements confluenceStatements = confluenceBody.getElementsByClass("statement");
		for (Element confluenceStatement : confluenceStatements) {
			mapConfluenceStatement(staContent, confluenceStatement);
		}

//		for (StaStatementGroup statementGroup : staContent.getStatementGroups()) {
//			normalizeHtmlValues(statementGroup);
//		}
		return staContent;
	}

	private void mapConfluenceStatement(StaContent staContent, Element confluenceStatement) {
		Element actionsElement = confluenceStatement.select("> .action").first();
		Action action = null;
//		if (actionsElement != null) {
			boolean isRemove = "complete".equals(actionsElement.select("[title=remove]").select("ac|task-status").text());
			boolean isUpdate = "complete".equals(actionsElement.select("[title=update]").select("ac|task-status").text());
			if (isRemove) {
				action = Action.REMOVE;
			} else if (isUpdate) {
				action = Action.UPDATE;
			}
//		}

		/*
		 * nur wenn eine Aktion definiert ist, füge das Statement hinzu
		 */
		if (action == null)
			return;
		Element valueElement = confluenceStatement.select("> .value").first();

		Element valueBox = valueElement.select("ac|structured-macro").first();
		String propertyKey = valueBox.select("> [ac:name=property-key]").first().text();

		String value = valueBox.select("> ac|rich-text-body").html();

//		String statementId = confluenceValue.getElementsByAttributeValue("ac:name", "statement-id").first().text();
//		String propertyKey = confluenceValue.getElementsByAttributeValue("ac:name", "property-key").first().text();
//		String propertyLabel = confluenceStatement.getElementsByAttributeValue("ac:name", "property-label").text();
//		String valueType = confluenceStatement.getElementsByAttributeValue("ac:name", "value-type").text();
//		String value = confluenceValue.getElementsByTag("ac:rich-text-body").first().html();
		StaProperty staProperty = this.staPropertyStorage.getProperty(propertyKey);
		StaStatement staValue = new StaStatement(staProperty, value, null, action);
		Element statementIdElement = valueBox.select("> [ac:name=statement-id]").first();
		if (statementIdElement != null) {
			staValue.setId(statementIdElement.text());
		}

		Elements qualifiersElements = confluenceStatement.select("> .qualifiers");

		if (qualifiersElements != null) {
			for (Element qualifierStatement : qualifiersElements.select(".qualifier")) {
				String qualifierKey = qualifierStatement.getElementsByClass("qualifier-label").attr("title");
				String qualifierValue = qualifierStatement.getElementsByClass("qualifier-value").text();
				StaProperty qualifierProp = this.staPropertyStorage.getProperty(qualifierKey);
				staValue.addQualifier(new StaStatement(qualifierProp, qualifierValue, null, action));
			}
		}
		staContent.getStatements().add(staValue);

		/*
		 * verarbeite eingebettete statements
		 */
		String embeddedEntityId = staValue.getQualifierValue(EMBEDS);

		for (Element embeddedConfluenceStatement : valueElement.getElementsByClass("embedded-statement")) {
			StaContent embeddedStaContent = staValue.getEmbeddedContent();
			if (embeddedStaContent == null) {
				embeddedStaContent = new StaContent();
				embeddedStaContent.setId(staContentStorage.getKey(), embeddedEntityId);
				staValue.setEmbeddedContent(embeddedStaContent);
				LOGGER.log(Level.DEBUG, "Found Embedded Content ");
			}
			mapConfluenceStatement(embeddedStaContent, embeddedConfluenceStatement);
		}
	}

//	private void addEmbeddedStatement(StaValue lastStaValue, StaValue staValue, StaProperty staProperty,
//			Element confluenceStatement) {
//		if (lastStaValue == null) {
//			return;
//		}
//		StaContent embeddedStaContent = lastStaValue.getEmbeddedContent();
//		if (embeddedStaContent == null) {
//			embeddedStaContent = new StaContent();
//			lastStaValue.setEmbeddedContent(embeddedStaContent);
//			String embeddedId = null;
//			Elements embeddedIdElements = confluenceStatement.getElementsByAttributeValue("ac:name",
//					"wikibase-entity-id");
//			if (embeddedIdElements.size() > 0) {
//				embeddedId = embeddedIdElements.first().text();
//			}
//			if (embeddedId != null) {
//				embeddedStaContent.setId(StaStorageWikibase.WIKIBASE_KEY, embeddedId);
//			}
//		}
//		embeddedStaContent.addStatement(staProperty, staValue);
//	}

//	public void normalizeStringValues(StaStatementGroup statementGroup) {
//		List<StaValue> staValues = statementGroup.getValues();
//		List<StaValue> newStaValues = new ArrayList<StaValue>();
//		/*
//		 * collect statement ids
//		 */
//		List<String> statementIds = new ArrayList<String>(staValues.size());
//		for (StaValue oldValue : staValues) {
//			statementIds.add(oldValue.getId());
//		}
//		Iterator<String> idIterator = statementIds.iterator();
//
//		for (StaValue oldValue : staValues) {
//			Element jsoupValue = Jsoup.parse(oldValue.getValue()).body();
//			/*
//			 * clean up content
//			 */
//			for (Element element : jsoupValue.getAllElements()) {
//				// remove empty tags (for example: <em></em> <div></div>)
//				if (!element.hasText() && element.isBlock() && element.hasParent()) {
//					element.remove();
//				}
//			}
//
//			/*
//			 * split
//			 */
//			for (Element paragraph : jsoupValue.children()) {
//				StaValue newValue = new StaValue();
//				newValue.setType(statementGroup.getProperty().getValueType());
//				newValue.setAction(oldValue.getAction());
//				if (idIterator.hasNext()) {
//					newValue.setId(idIterator.next());
//				}
//				if (paragraph.tagName().equals("h1")) {
//					newValue.setValue(paragraph.html());
//					newValue.addQualifier(this.staPropertyStorage.getProperty(LAYOUT_KEY), new StaValue(H1));
//				} else if (paragraph.tagName().equals("h2")) {
//					newValue.setValue(paragraph.html());
//					newValue.addQualifier(this.staPropertyStorage.getProperty(LAYOUT_KEY), new StaValue(H2));
//				} else if (paragraph.attr("ac:name").equals("info")) {
//					newValue.setValue(paragraph.firstElementChild().html());
//					newValue.addQualifier(this.staPropertyStorage.getProperty(LAYOUT_KEY), new StaValue(EXAMPLE));
//				} else {
//					newValue.setValue(paragraph.html());
//				}
//				newStaValues.add(newValue);
//			}
//		}
//		statementGroup.setValues(newStaValues);
//	}

//	private static Action mapConfluenceAction(Element confluenceStatement) {
//		boolean isRemove = "complete".equals(confluenceStatement.getElementsByClass("remove").text());
//		if (isRemove)
//			return Action.REMOVE;
//		boolean isUpdate = "complete".equals(confluenceStatement.getElementsByClass("update").text());
//		if (isUpdate)
//			return Action.UPDATE;
//		return null;
//	}
}
