package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;

public class UserStatusLink extends Composite {
	private HorizontalPanel panel = new HorizontalPanel();
	private UserStatusIndicator statusIndicator = new UserStatusIndicator();
	private DefaultHyperlink userLink = new DefaultHyperlink();

	public UserStatusLink() {
		initWidget(panel);
		statusIndicator.setTextVisible(false);
	}

	public UserStatusLink(String displayName, int userId) {
		this();
		userLink.setText(displayName);
		setUserId(userId);
	}

	public UserStatusLink(User user) {
		this(user.getFullName(), user.getId());
	}

	public void setText(String text) {
		userLink.setText(text);
	}

	public void setUserId(int userId) {
		userLink.setTargetHistoryToken(PageUrl.user(userId));
		if (userId > 0) {
			statusIndicator.setUserId(userId);
			panel.insert(statusIndicator, 0);
		} else {
			statusIndicator.removeFromParent();
		}
		panel.add(userLink);
	}

}
