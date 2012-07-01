package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

public class GroupListBox extends RequiredListBox {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public GroupListBox(final Integer selectedItem) {
		userService.listGroups(new ArgMap<UserArg>(Status.ACTIVE), new Callback<ArrayList<UserGroup>>() {
			@Override
			protected void doOnSuccess(ArrayList<UserGroup> result) {
				addItem("All groups", 0);
				for (UserGroup g : result) {
					addItem(g.getGroupName(), g.getId());
				}

				setValue(selectedItem);
			}
		});
	}
}
