package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class StatsRow {

	public StatsRow(LinkedHashMap<FieldName,StatsFieldType> fieldSpecs, boolean supportDynamicFields, StatsFieldType defaultFieldType) throws StatsException {
		this.fields = new LinkedHashMap<FieldName,StatsField>(fieldSpecs.size());

		for (Entry<FieldName,StatsFieldType> entry: fieldSpecs.entrySet()) {
			StatsField field = instantiateField(entry.getKey(), entry.getValue());
			fields.put(entry.getKey(), field);
		}
		
		this.supportDynamicFields = supportDynamicFields;
		this.defaultFieldType = defaultFieldType;
	}
	
	public StatsRow(LinkedHashMap<FieldName,StatsFieldType> fieldSpecs) throws StatsException {
		this(fieldSpecs, false, null);
	}

	public LinkedHashMap<FieldName,StatsField> getFields() {
		return fields;
	}

	public StatsField getField(FieldName name, boolean isFieldDynamic) throws StatsException {
		StatsField result = fields.get(name);
		if (result==null && supportDynamicFields && isFieldDynamic) {
			result = instantiateField(name, defaultFieldType);
			fields.put(name, result);
		}
		return result;
	}
	
	private StatsField instantiateField(FieldName name, StatsFieldType fieldType) throws StatsException {
		StatsField field = null;
		
		switch (fieldType) {
		case ENUM_REL:    field = new EnumRelativeField(name); break;
		case ENUM_SUM:    field = new EnumSumField(name); break;
		case COUNT_INT:   field = new CountIntField(name); break;
		case SUM_INT:     field = new SumIntField(name); break;
		case COUNT_DOUBLE:field = new CountDoubleField(name); break;
		case LIST_COUNTS: field = new ListCountsField(name); break;
		default: throw new StatsException("Invalid field type: " + fieldType);
		}
		
		return field;
	}
	
	private LinkedHashMap<FieldName,StatsField> fields;
	private boolean supportDynamicFields = false;
	private StatsFieldType defaultFieldType = null;
}
