package edu.cuny.qc.util;

import java.util.LinkedHashMap;

public class FinalKeysMap<K, V> extends LinkedHashMap<K, V> {
	private static final long serialVersionUID = 9053646523055114466L;

	public FinalKeysMap() {
		super();
	}
	
	public FinalKeysMap(int initialCapacity) {
		super(initialCapacity);
	}
	
	@Override
    public V put(K key, V value) {
		if (this.containsKey(key)) {
			throw new IllegalArgumentException("Key " + key + " already exists in map");
		}
		return super.put(key, value);
	}
}
