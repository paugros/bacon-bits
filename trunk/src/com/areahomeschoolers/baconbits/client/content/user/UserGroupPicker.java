package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.content.user.UserGroupCellTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellPicker;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

public class UserGroupPicker extends EntityCellPicker<UserGroup, UserArg, UserGroupColumn> {
	private UserGroupSelector selector;

	public UserGroupPicker(ArgMap<UserArg> args) {
		selector = new UserGroupSelector(args);
		setEntitySelector(selector);
	}

	@Override
	public UserGroupSelector getSelector() {
		return selector;
	}
}
