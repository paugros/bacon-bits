package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellSelector;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

public final class UserGroupSelector extends EntityCellSelector<UserGroup, UserGroupArg, UserGroupColumn> {
	private UserGroupTable userGroupTable;

	public UserGroupSelector(ArgMap<UserGroupArg> args) {
		this();
		userGroupTable = new UserGroupTable(args);
		userGroupTable.setTitle("User Groups");
		setEntityCellTable(userGroupTable);
		userGroupTable.setDisplayColumns(UserGroupColumn.GROUP, UserGroupColumn.DESCRIPTION);
	}

	private UserGroupSelector() {
		setModal(false);
		setText("Select a User Group");
	}

	@Override
	public UserGroupTable getCellTable() {
		return userGroupTable;
	}

}
