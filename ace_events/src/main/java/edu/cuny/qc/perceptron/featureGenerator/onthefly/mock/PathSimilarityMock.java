package edu.cuny.qc.perceptron.featureGenerator.onthefly.mock;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PathSimilarityMock {

	public Double apply(String path1, String path2) {
		Double result = map.get(new SimpleEntry<String, String>(path1, path2));
		if (result == null) {
			return DEFAULT_RETURN_VALUE;
		}
		return result;
	}
	
	private static final Double DEFAULT_RETURN_VALUE = -0.1;//0.05;
	private static Map<Entry<String, String>, Double> map = new HashMap<Entry<String, String>, Double>();
	static {
		map.put(new SimpleEntry<String, String>("-tmod<-#VBD#-nsubj->#", "cool-path"), 0.7);
	}
}
