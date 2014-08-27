package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import eu.excitementproject.eop.common.utilities.math.MathUtils;

/**
 * Holds a const value. Stores only one value, which gets overriden every time.
 * @author Ofer Bronstein
 *
 */
public class ConstField extends StatsField {

	public ConstField(FieldName name) {
		super(name);
	}

	@Override
	public void addElement(Object element) {
		this.element = element;
	}

	@Override
	public List<String> getSubtitles() {
		return Arrays.asList(new String[] {SUBTITLE});
	}

	@Override
	public List<String> getValues() {
		return Arrays.asList(new String[] {element.toString()});
	}
	
	private Object element = "-"; 
	private static final String SUBTITLE = "";
}
