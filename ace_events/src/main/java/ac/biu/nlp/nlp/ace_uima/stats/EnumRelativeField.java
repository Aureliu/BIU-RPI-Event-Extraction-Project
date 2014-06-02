package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.utilities.math.MathUtils;

public class EnumRelativeField extends StatsField {

	public EnumRelativeField(FieldName name) {
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
		Double sum = (double) MathUtils.sum(elements.values());
		for (String value : values.get(name)) {
			Double relativeFrequency;
			if (elements.keySet().contains(value)) {
				relativeFrequency = elements.get(value) / sum;
			}
			else {
				relativeFrequency = 0.0;
			}
			
			result.add(relativeFrequency.toString());
		}
		return result;
	}

	private Map<String,Integer> elements = new HashMap<String,Integer>();
	private static Map<FieldName,List<String>> values = new HashMap<FieldName,List<String>>();
}
