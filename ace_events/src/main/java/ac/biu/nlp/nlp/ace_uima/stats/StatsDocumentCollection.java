package ac.biu.nlp.nlp.ace_uima.stats;

import java.util.HashMap;
import java.util.Map;

/**
 * This class defines the specific stats documents.
 * A lot of hard-coded stuff here, in a perfect world most of this
 * class would be in configuration. So for modifying the structure
 * of the docs, you should probably change things here (and in the
 * calling classes that creates the values themselves and assign
 * them to the fields).
 *  
 * @author Ofer Bronstein
 *
 */
public abstract class StatsDocumentCollection {

	public abstract void updateDocs(Map<String,String> key, String fieldNameLvl1, String fieldNameLvl2, Object element, boolean isDynamic) throws StatsException;

	
	public void updateDocs(Map<String,String> key, String fieldNameLvl1, String fieldNameLvl2, Object element) throws StatsException {
		updateDocs(key, fieldNameLvl1, fieldNameLvl2, element, false);
	}
	
	protected <K,V> HashMap<K,V> without(Map<K,V> map, K[] keysToRemove) throws StatsException {
		HashMap<K,V> result = new HashMap<K,V>(map);
		for (K k : keysToRemove) {
			if (!map.containsKey(k)) {
				throw new StatsException("Key to be removed not present in map: " + k);
			}
			result.remove(k);
		}
		return result;
	}
}
