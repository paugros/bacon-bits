package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

public class AccessLevelListBox extends DefaultListBox {

	public AccessLevelListBox(Integer groupId) {
		addItem(AccessLevel.PUBLIC);
		addItem(AccessLevel.SITE_MEMBERS);
		addItem(AccessLevel.GROUP_MEMBERS);
		if (Application.getCurrentUser().administratorOf(groupId)) {
			addItem(AccessLevel.GROUP_ADMINISTRATORS);
		}
		if (Application.getCurrentUser().getSystemAdministrator()) {
			addItem(AccessLevel.SYSTEM_ADMINISTRATORS);
		}
	}

	private void addItem(AccessLevel level) {
		addItem(level.getDisplayName(), level.getId());
	}
}
