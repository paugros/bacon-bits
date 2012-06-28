package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupCellTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

public final class UserGroupCellTable extends EntityCellTable<UserGroup, UserArg, UserGroupColumn> {
	public enum UserGroupColumn implements EntityCellTableColumn<UserGroupColumn> {
		NAME("Name"), DESCRIPTION("Description"), ADMINISTRATOR("Administrator");

		private String title;

		UserGroupColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public UserGroupCellTable(ArgMap<UserArg> args) {
		this();
		setArgMap(args);
	}

	private UserGroupCellTable() {
		setDefaultSortColumn(UserGroupColumn.NAME, SortDirection.SORT_ASC);
		setDisplayColumns(UserGroupColumn.NAME, UserGroupColumn.DESCRIPTION);
	}

	@Override
	protected void fetchData() {
		userService.listGroups(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (UserGroupColumn col : getDisplayColumns()) {
			switch (col) {
			case NAME:
				addTextColumn(col, new ValueGetter<String, UserGroup>() {
					@Override
					public String get(UserGroup item) {
						return item.getGroupName();
					}
				});
				break;
			case DESCRIPTION:
				addTextColumn(col, new ValueGetter<String, UserGroup>() {
					@Override
					public String get(UserGroup item) {
						return item.getDescription();
					}
				});
				break;
			case ADMINISTRATOR:
				addTextColumn(col, new ValueGetter<String, UserGroup>() {
					@Override
					public String get(UserGroup item) {
						return Common.yesNo(item.getAdministrator());
					}
				});
				break;
			default:
				new AssertionError();
				break;
			}
		}
	}

}
