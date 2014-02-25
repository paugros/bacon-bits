package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellSelector;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;

public final class UserSelector extends EntityCellSelector<User, UserArg, UserColumn> {
	private UserTable userTable;

	public UserSelector(ArgMap<UserArg> args) {
		this();
		userTable = new UserTable(args);
		userTable.setTitle("User Groups");
		userTable.getTitleBar().addSearchControl();
		setEntityCellTable(userTable);

		userTable.removeColumn(UserColumn.STATUS);
		userTable.removeColumn(UserColumn.COMMON_INTERESTS);
		userTable.removeColumn(UserColumn.AGE);
		userTable.removeColumn(UserColumn.GROUP);
		userTable.removeColumn(UserColumn.ACTIVITY);
	}

	private UserSelector() {
		setModal(false);
		setMinSelect(1);
		setText("Select Members");
	}

	@Override
	public UserTable getCellTable() {
		return userTable;
	}

}
