package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserCellTable.UserColumn;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.EmailDisplay;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public final class UserCellTable extends EntityCellTable<User, UserArg, UserColumn> {
	public enum UserColumn implements EntityCellTableColumn<UserColumn> {
		NAME("Name"), EMAIL("Email"), HOME_PHONE("Home phone"), MOBILE_PHONE("Mobile phone"), STATUS("Status");

		private String title;

		UserColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public UserCellTable(ArgMap<UserArg> args) {
		this();
		setArgMap(args);
	}

	private UserCellTable() {
		setDefaultSortColumn(UserColumn.NAME, SortDirection.SORT_ASC);
		setDisplayColumns(UserColumn.values());
	}

	@Override
	protected void fetchData() {
		userService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (UserColumn col : getDisplayColumns()) {
			switch (col) {
			case NAME:
				addWidgetColumn(col, new WidgetCellCreator<User>() {
					@Override
					protected Widget createWidget(User item) {
						return new Hyperlink(item.getFullName(), PageUrl.user(item.getId()));
					}
				});
				break;
			case STATUS:
				addTextColumn(col, new ValueGetter<String, User>() {
					@Override
					public String get(User item) {
						return item.isActive() ? "Active" : "Inactive";
					}
				});
				break;
			case EMAIL:
				addCompositeWidgetColumn(col, new WidgetCellCreator<User>() {
					@Override
					protected Widget createWidget(User item) {
						return new EmailDisplay(item.getEmail());
					}
				});
				break;
			case HOME_PHONE:
				addTextColumn(col, new ValueGetter<String, User>() {
					@Override
					public String get(User item) {
						return item.getHomePhone();
					}
				});
				break;
			case MOBILE_PHONE:
				addTextColumn(col, new ValueGetter<String, User>() {
					@Override
					public String get(User item) {
						return item.getMobilePhone();
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
