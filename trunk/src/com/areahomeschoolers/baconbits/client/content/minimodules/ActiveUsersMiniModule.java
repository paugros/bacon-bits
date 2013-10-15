package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.User;

public class ActiveUsersMiniModule extends UserMiniModule {
	private String title = "ACTIVE MEMBERS";

	public ActiveUsersMiniModule() {
		ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
		args.put(UserArg.ACTIVE_NUMBER, 5);
		populate(title, args);
	}

	public ActiveUsersMiniModule(ArrayList<User> users) {
		populate(title, users);
	}

}
