package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserStatusIndicator;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.user.client.ui.Composite;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.google.gwt.user.client.ui.Image;
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
			PaddedPanel mhp = new PaddedPanel();

			if (Application.getUserActivity().get(u.getId()) != null) {
				UserStatusIndicator usi = new UserStatusIndicator(u.getId());
				usi.setShowWeeksAndMonths(true);
				usi.setTextVisible(false);
				mhp.add(usi);
			} else {
				Image spacer = new Image(MainImageBundle.INSTANCE.pixel());
				spacer.setSize("15px", "9px");
				mhp.add(spacer);
			}

			DefaultHyperlink link = new DefaultHyperlink(u.getFirstName() + " " + u.getLastName(), PageUrl.user(u.getId()));
			link.addStyleName("mediumText");
			mhp.add(link);

			vp.add(mhp);
		}

	}

}
