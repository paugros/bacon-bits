package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreference;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;

public class ItemVisibilityWidget extends Composite {
	private PaddedPanel pp = new PaddedPanel();
	private GroupListBox glb = new GroupListBox();
	private DefaultListBox alb = new DefaultListBox();
	private PrivacyPreference privacyPreference;

	public ItemVisibilityWidget() {
		alb.addItem("Public", VisibilityLevel.PUBLIC.getId());
		alb.addItem("All site members", VisibilityLevel.SITE_MEMBERS.getId());
		alb.addItem("All my groups", VisibilityLevel.MY_GROUPS.getId());
		alb.addItem("Members of...", VisibilityLevel.GROUP_MEMBERS.getId());
		alb.addItem("Private", VisibilityLevel.PRIVATE.getId());

		glb.setPopulateCommand(new Command() {
			@Override
			public void execute() {
				if (glb.getItemCount() == 0) {
					removeItem(VisibilityLevel.GROUP_MEMBERS);
				}
			}
		});

		alb.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				toggleGroupListBox();
				if (privacyPreference != null) {
					privacyPreference.setVisibilityLevelId(alb.getIntValue());
				}
			}
		});

		glb.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (privacyPreference != null) {
					privacyPreference.setGroupId(glb.getIntValue());
				}
			}
		});

		pp.add(alb);
		pp.add(glb);
		toggleGroupListBox();

		initWidget(pp);
	}

	public ItemVisibilityWidget(PrivacyPreference privacyPreference) {
		this();

		setPrivacyPreference(privacyPreference);
	}

	public void addChangeHandler(ChangeHandler handler) {
		alb.addChangeHandler(handler);
		glb.addChangeHandler(handler);
	}

	public Integer getGroupId() {
		if (!glb.isVisible()) {
			return null;
		}
		return glb.getIntValue();
	}

	public PrivacyPreference getPrivacyPreference() {
		return privacyPreference;
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

	public void setEnabled(boolean enabled) {
		alb.setEnabled(enabled);
		glb.setEnabled(enabled);
	}

	public void setGroupId(Integer groupId) {
		glb.setValue(groupId);
	}

	public void setPrivacyPreference(PrivacyPreference privacyPreference) {
		this.privacyPreference = privacyPreference;

		setVisibilityLevelId(privacyPreference.getVisibilityLevelId());
		setGroupId(privacyPreference.getGroupId());
	}

	public void setVisibilityLevelId(Integer visibilityLevelId) {
		alb.setValue(visibilityLevelId);
		toggleGroupListBox();
	}

	public void showOnlyCurrentOrganization() {
		glb.showOnlyCurrentOrganization();
	}

	private void toggleGroupListBox() {
		int levelId = alb.getIntValue();

		glb.setVisible(levelId == VisibilityLevel.GROUP_MEMBERS.getId());
		if (glb.isVisible() && privacyPreference != null) {
			privacyPreference.setGroupId(glb.getIntValue());
		}
	}
}
