package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;

public class CountUniqueField extends StatsField {

	public CountUniqueField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		elements.add((String) element);
	}

	@Override
	public List<String> getSubtitles() {
		return Arrays.asList(new String[] {""});
	}

	@Override
	public List<String> getValues() {
		int len = elements.size();
		return Arrays.asList(new String[] {Integer.toString(len)});
	}

	private Set<String> elements = new HashSet<String>();
}
