package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.ArrayList;
import java.util.List;

public abstract class StatsField {
	
	public StatsField(FieldName name) {
		this.name = name;
	}
	
	public FieldName getName() {
		return name;
	}
	
	/**
	 * returns all values, where each values is truncated by
	 * <tt>charsLimit</tt> characters.
	 * @param charsLimit
	 * @return
	 */
	public List<String> getValues(int charsLimit) {
		List<String> values = getValues();
		List<String> result = new ArrayList<String>(values.size());
		for (String val : values) {
			String toAdd = val.replace(StatsDocument.SEPARATOR, StatsDocument.SEPARATOR_SUBSTITUTE);
			if (toAdd.length() > charsLimit) {
				toAdd = toAdd.substring(0, charsLimit);
			}
			result.add(toAdd);
		}
		return result;
	}
	
	public String toString() {
		return name.toString();
	}
	
	// Abstract methods
	
	// A more proper implementation would use a generic <T> for the param's
	// type, but then every call to this method has to know the specific field
	// type (the class inheriting from this one) in compile time. We don't
	// want to do that right now.
	public abstract void addElement(Object element);
	
	public abstract List<String> getSubtitles();
	
	public abstract List<String> getValues();
	
	protected FieldName name;
}
