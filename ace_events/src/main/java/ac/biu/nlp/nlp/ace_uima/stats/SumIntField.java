package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.excitementproject.eop.common.utilities.math.MathUtils;

/**
 * Identical to CountIntField, but only with the "sum" subfield.
 * @author Ofer Bronstein
 *
 */
public class SumIntField extends StatsField {

	public SumIntField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		elements.add((Integer) element);
	}

	@Override
	public List<String> getSubtitles() {
		return Arrays.asList(new String[] {"sum"});
	}

	@Override
	public List<String> getValues() {
		if (elements.isEmpty()) {
			return Arrays.asList(new String[] {"-"});
		}
		else {
			Integer sum = MathUtils.sum(elements);
			return Arrays.asList(new String[] {sum.toString()});
		}
	}
	
	private List<Integer> elements = new ArrayList<Integer>(); 

}
