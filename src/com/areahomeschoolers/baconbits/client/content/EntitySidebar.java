package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.shared.dto.SidebarEntity;

public interface EntitySidebar<T extends SidebarEntity> {
	public T getEntity();

	public void setEntity(T ent);
}
