package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultimap;

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
		return elements.elementSet().size();
	}

	@Override
	public <O extends Object, C extends Collection<String>, I extends Collection<Entry<O, C>>> I getList() {
		//Set<Entry<Integer, Set<String>>> set = elements.entrySetByCount();
		Multimap<Integer, String> countToTerms = TreeMultimap.create(); //keep multimap sorted by key, which is count
		for(String term : elements.elementSet()) {
			countToTerms.put(elements.count(term), term);
		}
		Set<Entry<O,C>> out = new LinkedHashSet<Entry<O,C>>(countToTerms.keySet().size()); //LinkedHashSet to maintain ordering
		for (Entry<Integer,Collection<String>> entry : countToTerms.asMap().entrySet()) {
			out.add(new AbstractMap.SimpleEntry<O,C>((O) entry.getKey(), (C) entry.getValue()));
		}
		return (I) out;
	}

	@Override
	public void finalizeResults(List<String> toResult) {
		Collections.reverse(toResult); //It's nicer to have the higher frequencies first
	}
	
	private Multiset<String> elements = HashMultiset.create();
	//private Counter<String> elements = new Counter<String>();
}
