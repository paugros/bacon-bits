package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserCellTable.UserColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.EmailDisplay;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public final class UserCellTable extends EntityCellTable<User, UserArg, UserColumn> {
	public enum UserColumn implements EntityCellTableColumn<UserColumn> {
		NAME("Name"), EMAIL("Email"), HOME_PHONE("Home phone"), MOBILE_PHONE("Mobile phone"), GROUP("Group(s)"), STATUS("Status");

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

	public void addStatusFilterBox() {
		final com.areahomeschoolers.baconbits.client.widgets.DefaultListBox filterBox = getTitleBar().addFilterListControl();
		filterBox.addItem("Active");
		filterBox.addItem("Inactive");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent e) {
				switch (filterBox.getSelectedIndex()) {
				case 0:
					for (User item : getFullList()) {
						setItemVisible(item, item.isActive(), false, false, false);
					}
					break;
				case 1:
					for (User item : getFullList()) {
						setItemVisible(item, !item.isActive(), false, false, false);
					}
					break;
				case 2:
					showAllItems();
					break;
				}

				refreshForCurrentState();
			}
		});

		filterBox.setSelectedIndex(0);

		addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				filterBox.fireEvent(new ChangeEvent() {
				});
			}
		});
	}

	@Override
	protected void fetchData() {
		userService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (UserColumn col : getDisplayColumns()) {
			switch (col) {
			case GROUP:
				addWidgetColumn(col, new WidgetCellCreator<User>() {
					@Override
					protected Widget createWidget(User item) {
						return new HTML(Formatter.formatNoteText(item.getGroupsText()));
					}
				});
				break;
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
