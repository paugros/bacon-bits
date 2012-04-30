package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

public class DefaultValueGetter<T extends EntityDto<T>> implements ValueGetter<Integer, T> {

	private Map<T, Integer> sortIndexes = new HashMap<T, Integer>();
	private int order = 0;

	@Override
	public Integer get(T entity) {
		Integer sortIndex = sortIndexes.get(entity);

		if (sortIndex == null) {
			sortIndex = order++;
			sortIndexes.put(entity, sortIndex);
		}

		return sortIndex;
	}

}
