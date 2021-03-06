package com.areahomeschoolers.baconbits.client.content.user;

import java.util.Date;
import java.util.HashMap;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.GroupMembershipControl;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTableColumn;
import com.areahomeschoolers.baconbits.client.widgets.cellview.ValueGetter;
import com.areahomeschoolers.baconbits.client.widgets.cellview.WidgetCellCreator;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public final class UserTable extends EntityCellTable<User, UserArg, UserColumn> {
	public enum UserColumn implements EntityCellTableColumn<UserColumn> {
		PICTURE(""), ACTIVITY("Last active"), NAME("Name"), EMAIL("Email"), SEX("Sex"), LOCATION("Location"), PHONE("Phone"), ADDED("Added"), STATUS("Status"), AGE(
				"Age"), INTERESTS("Common interests"), ADMINISTRATOR("Administrator"), APPROVAL("Approval"), DELETE("Delete"), NEWS("News");

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
	private HashMap<Integer, UserStatusIndicator> userIndicators = new HashMap<Integer, UserStatusIndicator>();
	private UserGroup userGroup;

	public UserTable(ArgMap<UserArg> args) {
		this();
		setArgMap(args);
	}

	private UserTable() {
		setDefaultSortColumn(UserColumn.NAME, SortDirection.SORT_ASC);
		setDisplayColumns(UserColumn.values());
	}

	public void addStatusFilterBox() {
		final DefaultListBox filterBox = getTitleBar().addFilterListControl(false);
		filterBox.addItem("Active");
		filterBox.addItem("Inactive");
		filterBox.addItem("All");
		filterBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent e) {
				getArgMap().remove(EventArg.SHOW_INACTIVE);

				switch (filterBox.getSelectedIndex()) {
				case 0:
					getArgMap().setStatus(Status.ACTIVE);
					break;
				case 1:
					getArgMap().setStatus(Status.INACTIVE);
					break;
				case 2:
					getArgMap().setStatus(Status.ALL);
					break;
				}

				populate();
			}
		});

		filterBox.setSelectedIndex(0);
	}

	public UserGroup getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Override
	protected void fetchData() {
		userService.list(getArgMap(), getCallback());
	}

	@Override
	protected void setColumns() {
		for (UserColumn col : getDisplayColumns()) {
			switch (col) {
			case LOCATION:
				addTextColumn(col, new ValueGetter<String, User>() {
					@Override
					public String get(User item) {
						String text = "";
						if (!Common.isNullOrBlank(item.getCity())) {
							text += item.getCity();
							if (!Common.isNullOrBlank(item.getState())) {
								text += ", ";
							}
						}
						if (!Common.isNullOrBlank(item.getState())) {
							text += item.getState();
						}
						return text;
					}
				});
				break;
			case NEWS:
				addTextColumn(col, new ValueGetter<String, User>() {
					@Override
					public String get(User item) {
						return Common.yesNo(item.getReceiveNews());
					}
				});
				break;
			case INTERESTS:
				addNumberColumn(col, new ValueGetter<Number, User>() {
					@Override
					public Number get(User item) {
						return item.getCommonInterestCount();
					}
				});
				break;
			case ADDED:
				addDateColumn(col, new ValueGetter<Date, User>() {
					@Override
					public Date get(User item) {
						return item.getAddedDate();
					}
				});
				break;
			case PICTURE:
				addWidgetColumn(col, new WidgetCellCreator<User>() {
					@Override
					protected Widget createWidget(User item) {
						Image i = new Image(MainImageBundle.INSTANCE.blankProfileSmall());
						if (item.getSmallImageId() != null) {
							i = new Image(ClientUtils.createDocumentUrl(item.getSmallImageId(), item.getImageExtension()));
						}
						i.getElement().getStyle().setBorderColor("#c7c7c7");
						i.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
						i.getElement().getStyle().setBorderWidth(1, Unit.PX);
						DefaultHyperlink l = new DefaultHyperlink(i.toString(), true, PageUrl.user(item.getId()));
						return l;
					}
				});
				break;
			case ACTIVITY:
				addCompositeWidgetColumn(col, new WidgetCellCreator<User>() {
					@Override
					protected Widget createWidget(User user) {
						UserStatusIndicator indicator = new UserStatusIndicator(user.getId());
						if (Application.isSystemAdministrator()) {
							indicator.setShowWeeksAndMonths(true);
						}
						userIndicators.put(user.getId(), indicator);
						return indicator;
					}
				}, new ValueGetter<Date, User>() {
					@Override
					public Date get(User user) {
						return Application.getUserActivity().get(user.getId());
					}
				});
				break;
			case AGE:
				addTextColumn(col, new ValueGetter<String, User>() {
					@Override
					public String get(User item) {
						String age = "";
						if (item.isChild()) {
							age += item.getAge();
						} else {
							age += "Adult";
						}
						age += " - " + Formatter.formatDate(item.getBirthDate(), "MMM");

						return age;
					}
				}, new ValueGetter<Integer, User>() {
					@Override
					public Integer get(User item) {
						return item.getAge();
					}
				});
				break;
			case NAME:
				addWidgetColumn(col, new WidgetCellCreator<User>() {
					@Override
					protected Widget createWidget(User item) {
						return new DefaultHyperlink(item.getFullName(), PageUrl.user(item.getId()));
					}
				});
				break;
			case SEX:
				addTextColumn(col, new ValueGetter<String, User>() {
					@Override
					public String get(User item) {
						return item.getSexyText();
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
						return WidgetFactory.createEmailLink(item.getEmail());
					}
				});
				break;
			case PHONE:
				addWidgetColumn(col, new WidgetCellCreator<User>() {
					@Override
					protected Widget createWidget(User item) {
						String text = "";
						if (!Common.isNullOrBlank(item.getHomePhone())) {
							text += "<font face=courier>h:</font> " + item.getHomePhone() + "<br>";
						}
						if (!Common.isNullOrBlank(item.getMobilePhone())) {
							text += "<font face=courier>m:</font> " + item.getMobilePhone() + "<br>";
						}
						return new HTML(text);
					}
				});
				break;
			case APPROVAL:
				if (userGroup != null) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<User>() {
						@Override
						protected Widget createWidget(final User user) {
							userGroup.setGroupApproved(user.getGroupApproved());
							userGroup.setUserApproved(user.getUserApproved());
							return new GroupMembershipControl(user, userGroup).createApprovalWidget(new Command() {
								@Override
								public void execute() {
									refresh();
								}
							});
						}
					});
				}
				break;
			case ADMINISTRATOR:
				if (userGroup != null) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<User>() {
						@Override
						protected Widget createWidget(final User user) {
							return new GroupMembershipControl(user, userGroup).createAdminWidget(new Command() {
								@Override
								public void execute() {
									refresh();
								}
							});
						}
					}, new ValueGetter<Boolean, User>() {
						@Override
						public Boolean get(User item) {
							return item.administratorOf(userGroup.getId());
						}
					});
				}
				break;
			case DELETE:
				if (userGroup != null && Application.administratorOf(userGroup)) {
					addCompositeWidgetColumn(col, new WidgetCellCreator<User>() {
						@Override
						protected Widget createWidget(final User user) {
							return new GroupMembershipControl(user, userGroup).createMemberWidget(new Command() {
								@Override
								public void execute() {
									removeItem(user);
								}
							});
						}
					});
				}
				break;
			default:
				new AssertionError();
				break;
			}
		}
	}
}
