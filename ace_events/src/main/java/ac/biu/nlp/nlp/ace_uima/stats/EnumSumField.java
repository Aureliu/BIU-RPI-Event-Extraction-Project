package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.utilities.math.MathUtils;

public class EnumSumField extends StatsField {

	public EnumSumField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		String elemStr = (String) element;
		if (elements.containsKey(elemStr)) {
			elements.put(elemStr, elements.get(elemStr)+1);
		}
		else {
			elements.put(elemStr, 1);
		}
		if (!values.containsKey(name)) {
			values.put(name, new ArrayList<String>());
		}
		if (!values.get(name).contains(elemStr)) {
			values.get(name).add(elemStr);
		}
	}

	@Override
	public List<String> getSubtitles() {
		return values.get(name);
	}

	@Override
	public List<String> getValues() {
		List<String> result = new ArrayList<String>(elements.size());
		for (String value : values.get(name)) {
			Integer number = 0;
			if (elements.keySet().contains(value)) {
				number = elements.get(value);
			}			
			result.add(number.toString());
		}
		return result;
	}

	private Map<String,Integer> elements = new LinkedHashMap<String,Integer>();
	private static Map<FieldName,List<String>> values = new LinkedHashMap<FieldName,List<String>>();
}
