package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

public class GroupListBox extends DefaultListBox {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private static List<UserGroup> groups;
	private boolean onlyOrgs = false;

	public GroupListBox() {
		populate();
	}

	public void showOnlyOrganizations() {
		onlyOrgs = true;
		populate();
	}

	private void doPopulate() {
		clear();

		for (UserGroup g : groups) {
			if (onlyOrgs && !g.getOrganization()) {
				continue;
			}
			String text = g.getGroupName();
			if (!g.getOrganization()) {
				text = " - " + text;
			}
			addItem(text, g.getId());
		}
	}

	private void populate() {
		if (groups == null) {
			ArgMap<UserGroupArg> args = new ArgMap<UserGroupArg>(Status.ACTIVE);
			args.put(UserGroupArg.USER_ID, Application.getCurrentUser().getId());

			userService.listGroups(args, new Callback<ArrayList<UserGroup>>(false) {
				@Override
				protected void doOnSuccess(ArrayList<UserGroup> result) {
					groups = result;
					doPopulate();
				}
			});
		} else {
			doPopulate();
		}
	}
}
