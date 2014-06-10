package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import ac.biu.nlp.nlp.ace_uima.utils.Counter;

public class ListCountsField extends ListField {

	public ListCountsField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		String elemStr = (String) element;
		elements.add(elemStr);
	}

	@Override
	public int getListSize() {
		return elements.sizeByCount();
	}

	@Override
	public <O extends Object, C extends Collection<String>, I extends Iterable<Entry<O, C>>> I getList() {
		Set<Entry<Integer, Set<String>>> set = elements.entrySetByCount();
		Set<Entry<O,C>> out = new LinkedHashSet<Entry<O,C>>(set.size());
		for (Entry<Integer, Set<String>> entry : set) {
			out.add(new AbstractMap.SimpleEntry<O,C>((O) entry.getKey(), (C) entry.getValue()));
		}
		return (I) out;
	}

	@Override
	public void finalizeResults(List<String> toResult) {
		Collections.reverse(toResult); //It's nicer to have the higher frequencies first
	}
	
	private Counter<String> elements = new Counter<String>();
}
