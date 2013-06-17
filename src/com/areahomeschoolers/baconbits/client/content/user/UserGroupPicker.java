package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellPicker;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

public class UserGroupPicker extends EntityCellPicker<UserGroup, UserGroupArg, UserGroupColumn> {
	private UserGroupSelector selector;

	public UserGroupPicker(ArgMap<UserGroupArg> args) {
		selector = new UserGroupSelector(args);
		setEntitySelector(selector);
	}

	@Override
	public UserGroupSelector getSelector() {
		return selector;
	}
}
