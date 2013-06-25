package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;

public class ItemVisibilityWidget extends Composite {
	private PaddedPanel pp = new PaddedPanel();
	private GroupListBox glb = new GroupListBox();
	private DefaultListBox alb = new DefaultListBox();

	public ItemVisibilityWidget() {
		alb.addItem("Public", VisibilityLevel.PUBLIC.getId());
		alb.addItem("All site members", VisibilityLevel.SITE_MEMBERS.getId());
		alb.addItem("Members of...", VisibilityLevel.GROUP_MEMBERS.getId());
		alb.addItem("Private", VisibilityLevel.PRIVATE.getId());

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

	public Integer getVisibilityLevelId() {
		return alb.getIntValue();
	}

	public Integer getGroupId() {
		if (!glb.isVisible()) {
			return null;
		}
		return glb.getIntValue();
	}

	public void setVisibilityLevelId(Integer visibilityLevelId) {
		alb.setValue(visibilityLevelId);
		toggleGroupListBox();
	}

	public void setGroupId(Integer groupId) {
		glb.setValue(groupId);
	}

	private void toggleGroupListBox() {
		int levelId = alb.getIntValue();

		glb.setVisible(levelId == VisibilityLevel.GROUP_MEMBERS.getId());
	}
}
