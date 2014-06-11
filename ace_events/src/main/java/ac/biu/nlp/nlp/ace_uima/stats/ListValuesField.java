package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import ac.biu.nlp.nlp.ace_uima.utils.Counter;

public class ListValuesField extends ListField {

	public ListValuesField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		Entry<String, String> entry = (Entry<String, String>) element;
		String key = entry.getKey();
		String val = entry.getValue();
		if (elements.containsKey(key)) {
			elements.get(key).add(val);
		}
		else {
			Counter<String> counter = new Counter<String>();
			counter.add(val);
			elements.put(key, counter);
		}
	}

	@Override
	public int getListSize() {
		return elements.size();
	}

	@Override
	public <O extends Object, C extends Collection<String>, I extends Iterable<Entry<O, C>>> I getList() {
		Set<Entry<O,C>> out = new HashSet<Entry<O,C>>(elements.size());
		for (Entry<String,Counter<String>> entry : elements.entrySet()) {
			Counter<String> counter = entry.getValue();
			List<String> vals = new ArrayList<String>(counter.sizeByCount());
			for (Entry<Integer, Set<String>> entry2 : counter.entrySetByCount()) {
				for (String s : entry2.getValue()) {
					vals.add(String.format("%s*%s", s, entry2.getKey()));
				}
			}
			out.add(new AbstractMap.SimpleEntry<O,C>((O) entry.getKey(), (C) vals));
		}
		return (I) out;
	}

	@Override
	public void finalizeResults(List<String> toResult) {
		// do nothing
	}
	
	//private Multimap<String, String> elements = ArrayListMultimap.create();
	private Map<String, Counter<String>> elements = new TreeMap<String, Counter<String>>(); //sorted by key
}
