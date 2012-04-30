package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

public interface ValueGetter<C, T extends EntityDto<T>> {
	C get(T item);
}
