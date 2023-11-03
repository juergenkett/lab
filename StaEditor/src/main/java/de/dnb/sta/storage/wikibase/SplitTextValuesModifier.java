package de.dnb.sta.storage.wikibase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import de.dnb.sta.storage.datamodel.StaContent;
import de.dnb.sta.storage.datamodel.StaContentModifier;
import de.dnb.sta.storage.datamodel.StaDatamodel.Action;
import de.dnb.sta.storage.datamodel.StaDatamodel.Type;
import de.dnb.sta.storage.datamodel.StaProperty;
import de.dnb.sta.storage.datamodel.StaStatement;

public class SplitTextValuesModifier implements StaContentModifier {

	private final StaProperty LAYOUT_PROPERTY = new StaProperty(StaStorageWikibase.LAYOUT_KEY, "Layout", Type.ID);

	private StaStorageWikibase wikibaseStorage;

	public StaStorageWikibase getWikibaseStorage() {
		return wikibaseStorage;
	}

	public void setWikibaseStorage(StaStorageWikibase wikibaseStorage) {
		this.wikibaseStorage = wikibaseStorage;
	}

	@Override
	public StaContent modify(StaContent staContent) {
		StaContent oldStaContent = null;
		if (this.wikibaseStorage != null) {
			oldStaContent = this.wikibaseStorage.load(staContent.getId(wikibaseStorage.getKey()));
		}
		for (Entry<StaProperty, List<StaStatement>> entry : staContent.getStatements().getStatementsMap().entrySet()) {
			if (entry.getKey().getValueType().equals(Type.TEXT)) {
				/*
				 * bisherige StatementIds einsammeln
				 */
				Iterator<String> idIterator = null;

				if (oldStaContent != null) {
					idIterator = oldStaContent.getStatements().findStatements((entry.getKey().getKey()))
							.map(value -> value.getId()).filter(id -> id != null).iterator();
//					idIterator = oldStaContent
//							.getStatementGroupValues(staStatementGroup.getProperty().getKey()).stream()
//							.map(value -> value.getId()).filter(id -> id != null).iterator();
				}
				List<StaStatement> newValues = new ArrayList<StaStatement>();
				List<StaStatement> oldValues = entry.getValue();
				addSplittedTextValues(newValues, oldValues, idIterator);
				staContent.getStatements().removeAll(oldValues);
				staContent.getStatements().addAll(newValues);
//				staStatementGroup.setValues(newValues);
			}
		}

		return staContent;
	}

	public void addSplittedTextValues(List<StaStatement> newValues, List<StaStatement> oldValues, Iterator<String> idIterator) {
		for (StaStatement oldValue : oldValues) {
			Element jsoupValue = Jsoup.parse(oldValue.getValue()).body();
			/*
			 * clean up content
			 */
			for (Element element : jsoupValue.getAllElements()) {
				// remove empty tags (for example: <em></em> <div></div>)
				if (!element.hasText() && element.isBlock() && element.hasParent()) {
					element.remove();
				}
			}

			/*
			 * split
			 */
			for (Element paragraph : jsoupValue.children()) {
				String id = null;
				if (idIterator != null && idIterator.hasNext()) {
					id = idIterator.next();
				}

				switch (paragraph.tagName()) {
				case "h1":
					newValues.add(new StaStatement(oldValue.getProperty(), paragraph.html(), id, oldValue.getAction())
							.addQualifier(
									new StaStatement(LAYOUT_PROPERTY, StaStorageWikibase.H1_KEY, null, Action.UPDATE)));
					break;
				case "h2":
					newValues.add(new StaStatement(oldValue.getProperty(), paragraph.html(), id, oldValue.getAction())
							.addQualifier(
									new StaStatement(LAYOUT_PROPERTY, StaStorageWikibase.H2_KEY, null, Action.UPDATE)));
					break;
				case "h3":
					newValues.add(new StaStatement(oldValue.getProperty(), paragraph.html(), id, oldValue.getAction())
							.addQualifier(
									new StaStatement(LAYOUT_PROPERTY, StaStorageWikibase.H3_KEY, null, Action.UPDATE)));
					break;
				case "ol":
					for (Element li : paragraph.children()) {
						newValues.add(
								new StaStatement(oldValue.getProperty(), li.html(), id, oldValue.getAction()).addQualifier(
										new StaStatement(LAYOUT_PROPERTY, StaStorageWikibase.OL_KEY, null, Action.UPDATE)));
					}
					break;
				case "ul":
					for (Element li : paragraph.children()) {
						newValues.add(
								new StaStatement(oldValue.getProperty(), li.html(), id, oldValue.getAction()).addQualifier(
										new StaStatement(LAYOUT_PROPERTY, StaStorageWikibase.UL_KEY, null, Action.UPDATE)));
					}
					break;
				case "example":
					newValues.add(new StaStatement(oldValue.getProperty(), paragraph.html(), id, oldValue.getAction())
							.addQualifier(new StaStatement(LAYOUT_PROPERTY, StaStorageWikibase.EXAMPLE_KEY, null,
									Action.UPDATE)));
					break;
				case "collapsible":
					newValues.add(new StaStatement(oldValue.getProperty(), paragraph.html(), id, oldValue.getAction())
							.addQualifier(new StaStatement(LAYOUT_PROPERTY, StaStorageWikibase.COLLAPSIBLE_KEY, null,
									Action.UPDATE)));
					break;
				default:
					newValues.add(new StaStatement(oldValue.getProperty(), paragraph.html(), id, oldValue.getAction()));
				}
			}

			/*
			 * entferne überschüssige Statements
			 */
			while (idIterator != null && idIterator.hasNext()) {
				newValues.add(new StaStatement(oldValue.getProperty(), "", idIterator.next(), Action.REMOVE));
			}
		}
	}

}