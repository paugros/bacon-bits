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
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
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
		String title = user.isSaved() ? user.getFullName() : "New User";
		createFieldTable();
		form.initialize();

		page.add(WidgetFactory.newSection(title, fieldTable));

		if (user.isSaved()) {
			ArgMap<EventArg> eventArgs = new ArgMap<EventArg>(EventArg.USER_ID, user.getId());
			EventParticipantCellTable eventsTable = new EventParticipantCellTable(eventArgs);
			eventsTable.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.ADDED_DATE, ParticipantColumn.AGE, ParticipantColumn.PRICE,
					ParticipantColumn.FIELDS, ParticipantColumn.STATUS);
			eventsTable.setTitle("Events");

			eventsTable.getTitleBar().addSearchControl();
			eventsTable.getTitleBar().addExcelControl();
			page.add(WidgetFactory.newSection(eventsTable));
			eventsTable.populate();

			ArgMap<UserArg> userArgs = new ArgMap<UserArg>();
			userArgs.put(UserArg.USER_ID, user.getId());
			final UserGroupCellTable groupsTable = new UserGroupCellTable(userArgs);
			groupsTable.setUser(user);
			groupsTable.setTitle("Group Membership");
			groupsTable.setDisplayColumns(UserGroupColumn.NAME, UserGroupColumn.DESCRIPTION, UserGroupColumn.ADMINISTRATOR);
			groupsTable.getTitleBar().addExcelControl();
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
			groupsTable.populate();
			page.add(WidgetFactory.newSection(groupsTable));
		}

		if (!user.isSaved()) {
			form.configureForAdd(fieldTable);
		} else {
			form.emancipate();

			if (!Application.isAuthenticated()) {
				form.setEnabled(false);
			}
		}

		Application.getLayout().setPage(title, page);
	}

	private void save(final FormField field) {
		userService.save(user, new Callback<ServerResponseData<User>>() {
			@Override
			protected void doOnSuccess(ServerResponseData<User> r) {
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
