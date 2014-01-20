package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellPicker;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;

public class UserPicker extends EntityCellPicker<User, UserArg, UserColumn> {
	private UserSelector selector;

	public UserPicker(ArgMap<UserArg> args) {
		selector = new UserSelector(args);
		setEntitySelector(selector);
	}

	@Override
	public UserSelector getSelector() {
		return selector;
	}
}
