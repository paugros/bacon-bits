package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserMiniModule extends Composite {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private VerticalPanel vp = new VerticalPanel();

	public UserMiniModule() {
		initWidget(vp);
	}

	public UserMiniModule(String title, ArrayList<User> users) {
		this();
		populate(title, users);
	}

	protected void populate(final String title, ArgMap<UserArg> args) {
		userService.list(args, new Callback<ArrayList<User>>() {
			@Override
			protected void doOnSuccess(ArrayList<User> result) {
				if (!result.isEmpty()) {
					populate(title, result);
				} else {
					setVisible(false);
					removeFromParent();
				}
			}
		});
	}

	protected void populate(String title, ArrayList<User> users) {
		if (Common.isNullOrEmpty(users)) {
			removeFromParent();
			return;
		}

		vp.addStyleName("module");
		vp.setSpacing(8);

		Label label = new Label(title);
		label.addStyleName("moduleTitle");
		vp.add(label);

		for (User u : users) {
			VerticalPanel mhp = new VerticalPanel();

			Hyperlink link = new Hyperlink(u.getFirstName() + " " + u.getLastName(), PageUrl.user(u.getId()));
			link.addStyleName("mediumText");
			mhp.add(link);

			// HTML date = new HTML(Formatter.formatDateTime(e.getStartDate()));
			// date.setWordWrap(false);
			// date.addStyleName("italic");
			// mhp.add(date);

			vp.add(mhp);
		}

	}

}
