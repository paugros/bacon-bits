package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.content.user.UserGroupCellTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellSelector;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

public final class UserGroupSelector extends EntityCellSelector<UserGroup, UserGroupArg, UserGroupColumn> {
	private UserGroupCellTable userGroupTable;

	public UserGroupSelector(ArgMap<UserGroupArg> args) {
		this();
		userGroupTable = new UserGroupCellTable(args);
		userGroupTable.setTitle("User Groups");
		setEntityCellTable(userGroupTable);
		userGroupTable.setDisplayColumns(UserGroupColumn.NAME, UserGroupColumn.DESCRIPTION);
	}

	private UserGroupSelector() {
		setModal(false);
		setText("Select a User Group");
	}

	@Override
	public UserGroupCellTable getCellTable() {
		return userGroupTable;
	}

}
