package ac.biu.nlp.nlp.ace_uima.utils;


public class IgnoreNullFinalHashMap<K,V> extends FinalHashMap<K,V> {
	private static final long serialVersionUID = -8248902927888797607L;

	@Override
	public V put(K key, V value) {
		if (key == null) {
			return null;
		}
		return super.put(key, value);
	}
}
