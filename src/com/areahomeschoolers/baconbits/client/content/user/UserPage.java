package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupCellTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPage implements Page {
	private final Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(FormField formField) {
			save(formField);
		}
	});
	private VerticalPanel page;
	private User user = new User();
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private UserFieldTable fieldTable;
	private TabPage tabPanel;

	public UserPage(VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		int userId = Url.getIntegerParameter("userId");
		if (!Application.isAuthenticated() && userId < 0) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		this.page = page;

		userService.getPageData(userId, new Callback<UserPageData>() {
			@Override
			protected void doOnSuccess(UserPageData result) {
				if (result == null) {
					new ErrorPage(PageError.PAGE_NOT_FOUND);
					return;
				}

				user = result.getUser();
				initializePage();
			}
		});
	}

	private void createFieldTable() {
		fieldTable = new UserFieldTable(form, user);
		fieldTable.setWidth("100%");
	}

	private void initializePage() {
		final String title = user.isSaved() ? user.getFullName() : "New User";
		createFieldTable();
		form.initialize();

		if (!user.isSaved()) {
			form.configureForAdd(fieldTable);
			page.add(WidgetFactory.newSection(title, fieldTable, ContentWidth.MAXWIDTH1100PX));
		} else {
			tabPanel = new TabPage();
			form.emancipate();

			tabPanel.add("Main", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					tabBody.add(WidgetFactory.newSection(title, fieldTable, ContentWidth.MAXWIDTH1100PX));

					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Events", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					ArgMap<EventArg> eventArgs = new ArgMap<EventArg>(EventArg.USER_ID, user.getId());
					EventParticipantCellTable eventsTable = new EventParticipantCellTable(eventArgs);
					eventsTable.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PRICE, ParticipantColumn.FIELDS,
							ParticipantColumn.STATUS);
					eventsTable.setTitle("Events");

					eventsTable.getTitleBar().addExcelControl();
					eventsTable.getTitleBar().addSearchControl();
					eventsTable.populate();

					tabBody.add(WidgetFactory.newSection(eventsTable, ContentWidth.MAXWIDTH1200PX));
					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Registrations", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.PARENT_ID_PLUS_SELF, user.getId());
					args.put(EventArg.NOT_STATUS_ID, 5);
					EventParticipantCellTable table = new EventParticipantCellTable(args);
					table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PARTICIPANT_NAME,
							ParticipantColumn.ADDED_DATE, ParticipantColumn.PRICE, ParticipantColumn.STATUS);
					table.setTitle("Event Registrations");

					table.getTitleBar().addExcelControl();
					table.getTitleBar().addSearchControl();
					table.populate();

					tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));
					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Groups", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					ArgMap<UserArg> userArgs = new ArgMap<UserArg>();
					userArgs.put(UserArg.USER_ID, user.getId());
					final UserGroupCellTable groupsTable = new UserGroupCellTable(userArgs);
					groupsTable.setUser(user);
					groupsTable.setTitle("Group Membership");
					groupsTable.setDisplayColumns(UserGroupColumn.NAME, UserGroupColumn.DESCRIPTION, UserGroupColumn.ADMINISTRATOR);
					groupsTable.getTitleBar().addExcelControl();
					if (Application.isSystemAdministrator()) {
						groupsTable.getTitleBar().addLink(new ClickLabel("Add", new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								final UserGroupSelector selector = new UserGroupSelector(new ArgMap<UserArg>());
								selector.addSubmitCommand(new Command() {
									@Override
									public void execute() {
										final ArrayList<UserGroup> groups = new ArrayList<UserGroup>(selector.getSelectedItems());
										groups.removeAll(groupsTable.getFullList());
										if (groups.isEmpty()) {
											return;
										}

										for (UserGroup g : groups) {
											if (!groupsTable.getFullList().contains(g)) {
												groupsTable.addItem(g);
											}
										}
										userService.updateUserGroupRelation(user, groups, true, new Callback<Void>() {
											@Override
											protected void doOnSuccess(Void item) {
												groups.removeAll(groupsTable.getFullList());
											}
										});
										selector.clearSelection();
									}
								});

								selector.setMultiSelect(true);
								selector.setSelectedItems(groupsTable.getFullList());
								selector.center();
							}
						}));
					}

					groupsTable.populate();

					tabBody.add(WidgetFactory.newSection(groupsTable, ContentWidth.MAXWIDTH750PX));
					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Children", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
					args.put(UserArg.PARENT_ID, user.getId());

					UserCellTable table = new UserCellTable(args);
					table.setTitle("Children");
					table.populate();

					tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH750PX));
					tabPanel.selectTabNow(tabBody);
				}
			});

			if (!Application.isAuthenticated()) {
				form.setEnabled(false);
			}

			page.add(tabPanel);
		}

		Application.getLayout().setPage(title, page);
	}

	private void save(final FormField field) {
		userService.save(user, new Callback<ServerResponseData<User>>() {
			@Override
			protected void doOnSuccess(ServerResponseData<User> r) {
				if (r.hasErrors()) {
					new ServerResponseDialog(r).center();
					if (user.isSaved()) {
						field.getSubmitButton().setEnabled(true);
					}
					return;
				}

				if (!Url.isParamValidId("userId")) {
					HistoryToken.set(PageUrl.user(r.getData().getId()));
				} else {
					user = r.getData();
					form.setDto(r.getData());
					field.setInputVisibility(false);
				}
			}
		});
	}

}
