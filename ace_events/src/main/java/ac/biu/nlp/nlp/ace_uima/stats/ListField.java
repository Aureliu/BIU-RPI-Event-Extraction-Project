package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import eu.excitementproject.eop.common.utilities.StringUtil;

public abstract class ListField extends StatsField {

	public ListField(FieldName name) {
		super(name);
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
		List<String> toResult = new ArrayList<String>(getListSize());
		for (Entry<? extends Object, ? extends Collection<String>> entry : getList()) {
			List<String> vals = new ArrayList<String>(entry.getValue());
			Collections.sort(vals, getComparator());
			String strings = StringUtil.join(vals, " | ");
			toResult.add(String.format("%s = { %s }", entry.getKey(), strings));
		}
		finalizeResults(toResult);
		String result = StringUtil.join(toResult, "   ");
		
		return Arrays.asList(new String[] {result});
	}
	
	protected Comparator<String> getComparator() {
		return null;
	}

	public abstract int getListSize();
	public abstract <O extends Object, C extends Collection<String>, I extends Iterable<Entry<O, C>>> I getList();
	public abstract void finalizeResults(List<String> toResult);
}
