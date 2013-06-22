package com.areahomeschoolers.baconbits.client.content.user;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.Label;
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
				if (user == null && Application.isSystemAdministrator()) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<UserGroup>() {
						@Override
						protected Widget createWidget(final UserGroup item) {
							return new ClickLabel(item.getGroupName(), new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									UserGroupEditDialog dialog = new UserGroupEditDialog(UserGroupTable.this);
									dialog.setText("Edit Group");
									dialog.center(item);
								}
							});
						}
					});
				} else {
					addTextColumn(col, new ValueGetter<String, UserGroup>() {
						@Override
						public String get(UserGroup item) {
							return item.getGroupName();
						}
					});
				}
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
							if (!Application.administratorOf(item)) {
								return new Label(Common.yesNo(item.getAdministrator()));
							}
							return new ClickLabel(Common.yesNo(item.getAdministrator()), new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									String action = item.getAdministrator() ? "Revoke" : "Grant";
									String confirm = action + " administrator access for " + user.getFullName() + " in the " + item.getGroupName() + " group?";
									ConfirmDialog.confirm(confirm, new ConfirmHandler() {
										@Override
										public void onConfirm() {
											item.setAdministrator(!item.getAdministrator());
											refresh();
											userService.updateUserGroupRelation(user, item, true, new Callback<Void>(false) {
												@Override
												protected void doOnSuccess(Void item) {
												}
											});
										}
									});
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
					if (!Application.administratorOf(group)) {
						return new Label("");
					}

					return new ClickLabel("X", new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							String confirm = "Remove " + user.getFullName() + " from the " + group.getGroupName() + " group?";
							ConfirmDialog.confirm(confirm, new ConfirmHandler() {
								@Override
								public void onConfirm() {
									removeItem(group);
									userService.updateUserGroupRelation(user, group, false, new Callback<Void>(false) {
										@Override
										protected void doOnSuccess(Void item) {
										}
									});
								}
							});
						}
					});
				}
			});
		}
	}

}
