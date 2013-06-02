package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;

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

	public GroupListBox(final Integer selectedItem) {
		ArgMap<UserGroupArg> args = new ArgMap<UserGroupArg>(Status.ACTIVE);
		args.put(UserGroupArg.USER_ID, Application.getCurrentUser().getId());

		userService.listGroups(args, new Callback<ArrayList<UserGroup>>() {
			@Override
			protected void doOnSuccess(ArrayList<UserGroup> result) {
				addItem("None", 0);
				for (UserGroup g : result) {
					addItem(g.getGroupName(), g.getId());
				}

				setValue(selectedItem);
			}
		});
	}
}
