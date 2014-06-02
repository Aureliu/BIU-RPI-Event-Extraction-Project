package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import ac.biu.nlp.nlp.ace_uima.utils.Counter;
import eu.excitementproject.eop.common.utilities.StringUtil;

public class ListCountsField extends StatsField {

	public ListCountsField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		String elemStr = (String) element;
		elements.add(elemStr);
	}

	@Override
	public List<String> getSubtitles() {
		return Arrays.asList(new String[] {""});
	}

	@Override
	public List<String> getValues() {
		// First alternative: hello(3)|table(5)|chair(2) 
//		List<String> toResult = new ArrayList<String>(elements.size());
//		for (Entry<String, Integer> entry : elements.entrySet()) {
//			toResult.add(String.format("%s(%s)", entry.getKey(), entry.getValue()));
//		}
//		String result = StringUtil.join(toResult, "|");
		
		// Second alternative: 1 = { ball | umbrella | pan }   2 = { dog | cat }
		List<String> toResult = new ArrayList<String>(elements.sizeByCount());
		for (Entry<Integer,Set<String>> entry : elements.entrySetByCount()) {
			List<String> vals = new ArrayList<String>(entry.getValue());
			Collections.sort(vals);
			String strings = StringUtil.join(vals, " | ");
			toResult.add(String.format("%s = { %s }", entry.getKey(), strings));
		}
		Collections.reverse(toResult); //It's nicer to have the higher frequencies first
		String result = StringUtil.join(toResult, "   ");
		
		return Arrays.asList(new String[] {result});
	}

	private Counter<String> elements = new Counter<String>();
}
