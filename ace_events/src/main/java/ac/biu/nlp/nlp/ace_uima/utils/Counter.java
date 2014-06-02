package ac.biu.nlp.nlp.ace_uima.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class Counter<T> {
	
	public Counter() {
		map = new HashMap<T,Integer>();
		mapByCountCache = new TreeMap<Integer,Set<T>>(); // Sorted by count
		cacheUpdated = false;
	}
	
	public void add(T element) {
		if (map.containsKey(element)) {
			map.put(element, map.get(element)+1);
		}
		else {
			map.put(element, 1);
		}
		cacheUpdated = false;
	}
	
	public Set<Entry<Integer,Set<T>>> entrySetByCount() {
		if (!cacheUpdated) {
			updateMapByCountCache();
		}
		return mapByCountCache.entrySet();
	}
	
	public Set<T> getByCount(Integer count) {
		if (!cacheUpdated) {
			updateMapByCountCache();
		}
		return mapByCountCache.get(count);
	}
	
	public int sizeByCount() {
		if (!cacheUpdated) {
			updateMapByCountCache();
		}
		return mapByCountCache.size();
		
	}
	
	private void updateMapByCountCache() {
		mapByCountCache.clear();
		for (Entry<T,Integer> entry : map.entrySet()) {
			if (mapByCountCache.containsKey(entry.getValue())) {
				mapByCountCache.get(entry.getValue()).add(entry.getKey());
			}
			else {
				Set<T> set = new HashSet<T>();
				mapByCountCache.put(entry.getValue(), set);
				set.add(entry.getKey());
			}
		}
		cacheUpdated = true;
	}

	Map<T,Integer> map;
	Map<Integer,Set<T>> mapByCountCache;
	boolean cacheUpdated;
}
