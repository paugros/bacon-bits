package com.areahomeschoolers.baconbits.client.content.user;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.GroupMembershipControl;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public final class UserGroupTable extends EntityCellTable<UserGroup, UserGroupArg, UserGroupColumn> {
	public enum UserGroupColumn implements EntityCellTableColumn<UserGroupColumn> {
		GROUP("Group"), ORGANIZATION("Organization"), DESCRIPTION("Description"), START_DATE("Start"), END_DATE("End"), ADMINISTRATOR("Administrator");

		private String title;

		UserGroupColumn(String title) {
			this.title = title;
		}

		@Override
		public String getTitle() {
			return title;
		}
	}

	private User user;
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public UserGroupTable(ArgMap<UserGroupArg> args) {
		this();
		setArgMap(args);
	}

	private UserGroupTable() {
		setRowStyles(new RowStyles<UserGroup>() {
			@Override
			public String getStyleNames(UserGroup row, int rowIndex) {
				if (row.getOrganization()) {
					return "bold";
				}
				return "";
			}
		});

		setDefaultSortColumn(UserGroupColumn.GROUP, SortDirection.SORT_ASC);
		setDisplayColumns(UserGroupColumn.GROUP, UserGroupColumn.DESCRIPTION);

		disablePaging();
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl();
		filterBox.addItem("Active");
		filterBox.addItem("Inactive");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				switch (filterBox.getSelectedIndex()) {
				case 0:
					for (UserGroup group : getFullList()) {
						setItemVisible(group, group.isActive(), false, false, false);
					}
					refreshForCurrentState();
					break;
				case 1:
					for (UserGroup group : getFullList()) {
						setItemVisible(group, !group.isActive(), false, false, false);
					}
					refreshForCurrentState();
					break;
				case 2:
					showAllItems();
					break;
				}
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	protected void fetchData() {
		userService.listGroups(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (UserGroupColumn col : getDisplayColumns()) {
			switch (col) {
			case ORGANIZATION:
				addTextColumn(col, new ValueGetter<String, UserGroup>() {
					@Override
					public String get(UserGroup item) {
						return item.getOrganizationName();
					}
				});
				break;
			case GROUP:
				addCompositeWidgetColumn(col, new WidgetCellCreator<UserGroup>() {
					@Override
					protected Widget createWidget(final UserGroup item) {
						return new Hyperlink(item.getGroupName(), PageUrl.userGroup(item.getId()));
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
				if (user != null) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<UserGroup>() {
						@Override
						protected Widget createWidget(final UserGroup item) {
							return new GroupMembershipControl(user, item).createAdminWidget(new Command() {
								@Override
								public void execute() {
									refresh();
								}
							});
						}
					}, new ValueGetter<Boolean, UserGroup>() {
						@Override
						public Boolean get(UserGroup item) {
							return item.getAdministrator();
						}
					});
				} else {
					addTextColumn(col, new ValueGetter<String, UserGroup>() {
						@Override
						public String get(UserGroup item) {
							return Common.yesNo(item.getAdministrator());
						}
					});
				}
				break;
			case END_DATE:
				addDateColumn(col, new ValueGetter<Date, UserGroup>() {
					@Override
					public Date get(UserGroup item) {
						return item.getEndDate();
					}
				});
				break;
			case START_DATE:
				addDateColumn(col, new ValueGetter<Date, UserGroup>() {
					@Override
					public Date get(UserGroup item) {
						return item.getStartDate();
					}
				});
				break;
			default:
				new AssertionError();
				break;
			}
		}

		if (user != null && Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			addCompositeWidgetColumn("Delete", new WidgetCellCreator<UserGroup>() {
				@Override
				protected Widget createWidget(final UserGroup group) {
					return new GroupMembershipControl(user, group).createMemberWidget(new Command() {
						@Override
						public void execute() {
							removeItem(group);
						}
					});
				}
			});
		}
	}

}
