package ac.biu.nlp.nlp.ie.onthefly.features.mock;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LexicalSimilarityMock {

	public Double apply(String word1, String word2) {
		Double result = map.get(new SimpleEntry<String, String>(word1, word2));
		if (result == null) {
			return DEFAULT_RETURN_VALUE;
		}
		return result;
	}
	
	private static final Double DEFAULT_RETURN_VALUE = -0.05;//0.1;
	private static Map<Entry<String, String>, Double> map = new HashMap<Entry<String, String>, Double>();
	static {
		map.put(new SimpleEntry<String, String>("forced", "remove"), 0.8);
		map.put(new SimpleEntry<String, String>("bombarded", "bomb"), 0.9);
		map.put(new SimpleEntry<String, String>("people", "people"), 1.0);
		map.put(new SimpleEntry<String, String>("homes", "house"), 0.95);
		map.put(new SimpleEntry<String, String>("consume", "house"), 0.95);
	}
	
	/**
	 * for testing
	 */
	public static void main(String[] args) {
		LexicalSimilarityMock mock = new LexicalSimilarityMock();
		Double result1 = mock.apply("forced", "remove");
		Double result2 = mock.apply("forced", "removeX");
		System.out.printf("forced + removed: %f\n", result1);
		System.out.printf("forced + removedX: %f\n", result2);
	}
}
