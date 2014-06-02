package ac.biu.nlp.nlp.ace_uima.utils;

import java.util.HashMap;


public class FinalHashMap<K,V> extends HashMap<K,V> {
	private static final long serialVersionUID = 6935659885154455622L;

	@Override
	public V put(K key, V value) {
		if (this.containsKey(key)) {
			throw new IllegalArgumentException("Key already exists: " + key);
		}
		return super.put(key, value);
	}
	
//	@Override
//	public void putAll(Map<? extends K, ? extends V> m) {
//		Set<K> intersected = new HashSet<K>(m.keySet());
//		intersected.retainAll(this.keySet());
//		if (!intersected.isEmpty()) {
//			throw new IllegalArgumentException("Some keys already exist: " + m.keySet());
//		}
//		super.putAll(m);
//	}
}
