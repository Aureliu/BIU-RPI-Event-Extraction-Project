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
		// Second alternative: 1 = { ball | umbrella | pan }   2 = { dog | cat }
		
		throw new RuntimeException("All the rest of the code in the method is great and works! But I have some weird maven compilation problem on te-srv2 with it (generics-related), so this hsould be solved.... GL :)");
		
//		List<String> toResult = new ArrayList<String>(getListSize());
//		Collection<Entry<? extends Object, ? extends Collection<String>>> list = getList();
//		int charsLeft = StatsDocument.MAX_CHARS;
//		int keysLeft = list.size();
//		for (Entry<? extends Object, ? extends Collection<String>> entry : list) {
//			List<String> vals = new ArrayList<String>(entry.getValue());
//			Collections.sort(vals, getComparator());
//			String strings = StringUtil.join(vals, " | ");
//			
//			int charsAllowed = charsLeft/keysLeft;
//			if (strings.length() > charsAllowed) {
//				strings = strings.substring(0, charsAllowed) + "...";
//			}
//			
//			String keyAndVal = String.format("___%s = { %s }", entry.getKey(), strings);
//			toResult.add(keyAndVal);
//			
//			charsLeft -= keyAndVal.length();
//			keysLeft -= 1;
//		}
//		finalizeResults(toResult);
//		String result = StringUtil.join(toResult, "   ");
//		
//		return Arrays.asList(new String[] {result});
	}
	
	protected Comparator<String> getComparator() {
		return null;
	}

	public abstract int getListSize();
	public abstract <O extends Object, C extends Collection<String>, I extends Collection<Entry<O, C>>> I getList();
	public abstract void finalizeResults(List<String> toResult);
}
