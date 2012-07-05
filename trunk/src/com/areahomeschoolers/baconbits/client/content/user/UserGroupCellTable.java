package com.areahomeschoolers.baconbits.client.content.user;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupCellTable.UserGroupColumn;
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
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Widget;

public final class UserGroupCellTable extends EntityCellTable<UserGroup, UserArg, UserGroupColumn> {
	public enum UserGroupColumn implements EntityCellTableColumn<UserGroupColumn> {
		NAME("Name"), DESCRIPTION("Description"), START_DATE("Start"), END_DATE("End"), ADMINISTRATOR("Administrator");

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
	private UserGroupEditDialog dialog = new UserGroupEditDialog(this);

	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public UserGroupCellTable(ArgMap<UserArg> args) {
		this();
		setArgMap(args);
	}

	private UserGroupCellTable() {
		setDefaultSortColumn(UserGroupColumn.NAME, SortDirection.SORT_ASC);
		setDisplayColumns(UserGroupColumn.NAME, UserGroupColumn.DESCRIPTION);

		disablePaging();
		dialog.setText("Edit Group");
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
			case NAME:
				if (user == null && Application.isAdministrator()) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<UserGroup>() {
						@Override
						protected Widget createWidget(final UserGroup item) {
							return new ClickLabel(item.getGroupName(), new MouseDownHandler() {
								@Override
								public void onMouseDown(MouseDownEvent event) {
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
							return new ClickLabel(Common.yesNo(item.getAdministrator()), new MouseDownHandler() {
								@Override
								public void onMouseDown(MouseDownEvent event) {
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

		if (user != null) {
			addCompositeWidgetColumn("Delete", new WidgetCellCreator<UserGroup>() {
				@Override
				protected Widget createWidget(final UserGroup group) {
					return new ClickLabel("X", new MouseDownHandler() {
						@Override
						public void onMouseDown(MouseDownEvent event) {
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
