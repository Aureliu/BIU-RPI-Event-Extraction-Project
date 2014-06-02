package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.excitementproject.eop.common.utilities.math.MathUtils;

public class CountIntField extends StatsField {

	public CountIntField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		elements.add((Integer) element);
	}

	@Override
	public List<String> getSubtitles() {
		return Arrays.asList(new String[] {"count", "min", "max", "avg", "sum", /*, "std"*/});
	}

	@Override
	public List<String> getValues() {
		if (elements.isEmpty()) {
			return Arrays.asList(new String[] {"-", "-", "-", "-", "-", /*, "-"*/});
		}
		else {
			Integer count = elements.size();
			Integer min = Collections.min(elements);
			Integer max = Collections.max(elements);
			Double avg = MathUtils.averageInt(elements);
			Integer sum = MathUtils.sum(elements);
			/*Double std = MathUtils.stdInt(elements);*/
			return Arrays.asList(new String[] {count.toString(), min.toString(), max.toString(), avg.toString(), sum.toString()/*, std.toString()*/});
		}
	}
	
	private List<Integer> elements = new ArrayList<Integer>(); 

}
