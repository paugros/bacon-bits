package com.areahomeschoolers.baconbits.shared;

import java.util.HashMap;

/**
 * Extension of {@link HashMap} that allows bidirectional member access by providing a {@link #reverseGet(Object) reverseGet} method.
 */
public class BidiMap<K, V> extends HashMap<K, V> implements ReversibleMap<K, V> {
	private static final long serialVersionUID = -2059208230442880557L;
	private HashMap<V, K> reverseMap = new HashMap<V, K>();

	public BidiMap() {
		super();
	}

	// override for efficiency.
	@Override
	public boolean containsValue(Object value) {
		return reverseMap.containsKey(value);
	}

	@Override
	public V put(K key, V value) {
		super.put(key, value);
		// we simply maintain two maps
		reverseMap.put(value, key);
		return value;
	}

	@Override
	public K reverseGet(V value) {
		return reverseMap.get(value);
	}
}