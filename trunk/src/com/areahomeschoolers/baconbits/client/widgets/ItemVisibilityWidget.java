package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;

public class ItemVisibilityWidget extends Composite {
	private PaddedPanel pp = new PaddedPanel();
	private GroupListBox glb = new GroupListBox();
	private DefaultListBox alb = new DefaultListBox();

	public ItemVisibilityWidget() {
		alb.addItem("Public", AccessLevel.PUBLIC.getId());
		alb.addItem("All site members", AccessLevel.SITE_MEMBERS.getId());
		alb.addItem("Members of...", AccessLevel.GROUP_MEMBERS.getId());
		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			alb.addItem("Administrators of...", AccessLevel.GROUP_ADMINISTRATORS.getId());
		}

		if (Application.getCurrentUser().getSystemAdministrator()) {
			alb.addItem("System administrators", AccessLevel.SYSTEM_ADMINISTRATORS.getId());
		}

		alb.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				toggleGroupListBox();
			}
		});

		pp.add(alb);
		pp.add(glb);
		toggleGroupListBox();

		initWidget(pp);
	}

	public Integer getAccessLevelId() {
		return alb.getIntValue();
	}

	public Integer getGroupId() {
		if (!glb.isVisible()) {
			return null;
		}
		return glb.getIntValue();
	}

	public void setAccessLevelId(Integer accessLevelId) {
		alb.setValue(accessLevelId);
		toggleGroupListBox();
	}

	public void setGroupId(Integer groupId) {
		glb.setValue(groupId);
	}

	private void toggleGroupListBox() {
		int levelId = alb.getIntValue();

		glb.setVisible(levelId == AccessLevel.GROUP_ADMINISTRATORS.getId() || levelId == AccessLevel.GROUP_MEMBERS.getId());
	}
}
