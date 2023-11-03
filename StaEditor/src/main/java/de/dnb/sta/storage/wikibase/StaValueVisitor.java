package de.dnb.sta.storage.wikibase;

import org.wikidata.wdtk.datamodel.interfaces.EntityIdValue;
import org.wikidata.wdtk.datamodel.interfaces.GlobeCoordinatesValue;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.NoValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.QuantityValue;
import org.wikidata.wdtk.datamodel.interfaces.SnakVisitor;
import org.wikidata.wdtk.datamodel.interfaces.SomeValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.StringValue;
import org.wikidata.wdtk.datamodel.interfaces.TimeValue;
import org.wikidata.wdtk.datamodel.interfaces.UnsupportedValue;
import org.wikidata.wdtk.datamodel.interfaces.ValueSnak;
import org.wikidata.wdtk.datamodel.interfaces.ValueVisitor;

public class StaValueVisitor implements ValueVisitor<String>, SnakVisitor<String> {

	@Override
	public String visit(EntityIdValue value) {
//		StaValue ret = new StaValue();
//		ret.setValue(value.getId());
//		ret.setType(Type.ID);
		return value.getId();
	}

	@Override
	public String visit(GlobeCoordinatesValue value) {
		throw new IllegalArgumentException("Nicht unterstützer Entitäten-Typ: " + value.getClass());
	}

	@Override
	public String visit(MonolingualTextValue value) {
		throw new IllegalArgumentException("Nicht unterstützer Entitäten-Typ: " + value.getClass());
	}

	@Override
	public String visit(QuantityValue value) {
		throw new IllegalArgumentException("Nicht unterstützer Entitäten-Typ: " + value.getClass());
	}

	@Override
	public String visit(StringValue value) {
//		StaValue ret = new StaValue();
//		ret.setValue();
////		ret.setType(Type.STRING);
		return value.getString();
	}

	@Override
	public String visit(TimeValue value) {
		throw new IllegalArgumentException("Nicht unterstützer Entitäten-Typ: " + value.getClass());
	}

	@Override
	public String visit(UnsupportedValue value) {
		throw new IllegalArgumentException("Nicht unterstützer Entitäten-Typ: " + value.getClass());
	}

	@Override
	public String visit(ValueSnak snak) {
		return snak.getValue().accept(this);
	}

	@Override
	public String visit(SomeValueSnak snak) {
		throw new IllegalArgumentException("Nicht unterstützer Entitäten-Typ: " + snak.getClass());
	}

	@Override
	public String visit(NoValueSnak snak) {
		return null;
	}
}
