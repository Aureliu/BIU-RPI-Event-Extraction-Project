package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

public class ListValuesField extends ListField {

	public ListValuesField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		Entry<String, String> entry = (Entry<String, String>) element;
		elements.put(entry.getKey(), entry.getValue());
//		String key = entry.getKey();
//		String val = entry.getValue();
//		if (elements.containsKey(key)) {
//			elements.get(key).add(val);
//		}
//		else {
//			Counter<String> counter = new Counter<String>();
//			counter.add(val);
//			elements.put(key, counter);
//		}
	}

	@Override
	public int getListSize() {
		return elements.keySet().size();
	}

	@Override
	public <O extends Object, C extends Collection<String>, I extends Iterable<Entry<O, C>>> I getList() {
		Set<Entry<O,C>> out = new HashSet<Entry<O,C>>(elements.keySet().size());
		for (Entry<String, Collection<String>> en : elements.asMap().entrySet()) {
			Multiset<String> valueTerms = HashMultiset.create(en.getValue());
			List<String> uniqueTermsWithCounts = new ArrayList<String>(valueTerms.elementSet().size());
			for (String uniqueTerm : valueTerms.elementSet()) {
				uniqueTermsWithCounts.add(String.format("%s*%s", uniqueTerm, valueTerms.count(uniqueTerm)));
			}
			out.add(new AbstractMap.SimpleEntry<O,C>((O) en.getKey(), (C) uniqueTermsWithCounts));
		}
		
		return (I) out;
	}

	@Override
	public void finalizeResults(List<String> toResult) {
		Collections.sort(toResult);
	}
	
	private Multimap<String, String> elements = ArrayListMultimap.create();
	//private Map<String, Counter<String>> elements = new TreeMap<String, Counter<String>>(); //sorted by key
}
