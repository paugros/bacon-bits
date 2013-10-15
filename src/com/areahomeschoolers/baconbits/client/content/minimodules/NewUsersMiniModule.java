package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.User;

public class NewUsersMiniModule extends UserMiniModule {
	private String title = "NEW MEMBERS";

	public NewUsersMiniModule() {
		ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
		args.put(UserArg.NEW_NUMBER, 5);
		populate(title, args);
	}

	public NewUsersMiniModule(ArrayList<User> users) {
		populate(title, users);
	}

}
