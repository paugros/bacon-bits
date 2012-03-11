package com.areahomeschoolers.baconbits.client.common;

import java.util.HashMap;
import java.util.Map;

public interface ReversibleMap<K, V> extends Map<K, V> {
	/**
	 * Gets the {@link HashMap} key that is paired with a particular value.
	 * 
	 * @param value
	 *            The value whose corresponding key will be returned.
	 */
	public K reverseGet(V value);
}
