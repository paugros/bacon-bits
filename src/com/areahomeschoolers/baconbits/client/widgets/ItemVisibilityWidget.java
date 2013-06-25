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
		alb.addItem("All my groups", VisibilityLevel.MY_GROUPS.getId());
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

	public Integer getGroupId() {
		if (!glb.isVisible()) {
			return null;
		}
		return glb.getIntValue();
	}

	public Integer getVisibilityLevelId() {
		return alb.getIntValue();
	}

	public void removeItem(VisibilityLevel level) {
		for (int i = 0; i < alb.getItemCount(); i++) {
			if (alb.getIntValue(i) == level.getId()) {
				alb.removeItem(i);
			}
		}
	}

	public void setGroupId(Integer groupId) {
		glb.setValue(groupId);
	}

	public void setVisibilityLevelId(Integer visibilityLevelId) {
		alb.setValue(visibilityLevelId);
		toggleGroupListBox();
	}

	private void toggleGroupListBox() {
		int levelId = alb.getIntValue();

		glb.setVisible(levelId == VisibilityLevel.GROUP_MEMBERS.getId());
	}
}
