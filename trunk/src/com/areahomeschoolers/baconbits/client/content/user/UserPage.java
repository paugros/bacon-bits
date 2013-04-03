package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookCellTable;
import com.areahomeschoolers.baconbits.client.content.book.BookCellTable.BookColumn;
import com.areahomeschoolers.baconbits.client.content.book.BookDialog;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantCellTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.event.EventVolunteerCellTable;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupCellTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.FixedWidthLabel;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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
	private BookDialog bookDialog;

	public UserPage(final VerticalPanel page) {
		if (!Application.isAuthenticated()) {
			new ErrorPage(PageError.NOT_AUTHORIZED);
			return;
		}

		int userId = Url.getIntegerParameter("userId");
		if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS) && userId < 0) {
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

				Label heading = new Label(user.getFullName());
				heading.addStyleName("hugeText");
				page.add(heading);

				initializePage();
			}
		});
	}

	private native void createBarcode(String bookId) /*-{
		$wnd.$('#barcode_' + bookId).barcode({
			code : 'code39'
		});
	}-*/;

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
					eventArgs.setStatus(Status.ALL);
					eventArgs.put(EventArg.NOT_STATUS_ID, 5);
					EventParticipantCellTable eventsTable = new EventParticipantCellTable(eventArgs);
					eventsTable.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PRICE, ParticipantColumn.FIELDS,
							ParticipantColumn.STATUS);
					eventsTable.setTitle("Events");

					eventsTable.addStatusFilterBox();

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

					table.addStatusFilterBox();

					table.getTitleBar().addExcelControl();
					table.getTitleBar().addSearchControl();
					table.populate();

					tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));
					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Volunteer Positions", new TabPageCommand() {
				@Override
				public void execute(final VerticalPanel tabBody) {
					final ArgMap<EventArg> args = new ArgMap<EventArg>();
					args.put(EventArg.USER_ID, user.getId());

					final EventVolunteerCellTable vt = new EventVolunteerCellTable(args);

					vt.addDataReturnHandler(new DataReturnHandler() {
						@Override
						public void onDataReturn() {
							vt.removeColumn(4);
							vt.removeColumn(3);
							tabPanel.selectTabNow(tabBody);
						}
					});

					tabBody.add(WidgetFactory.newSection(vt, ContentWidth.MAXWIDTH750PX));

					vt.populate();
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

			if (user.memberOfAny(16, 17)) {
				tabPanel.add("Books", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						Hyperlink link = new Hyperlink("Click here", PageUrl.article(65));
						String html = link.toString() + " for book seller instructions.";
						tabBody.add(new HTML(html));

						ArgMap<BookArg> args = new ArgMap<BookArg>(BookArg.STATUS_ID, 1);
						args.put(BookArg.USER_ID, user.getId());

						final BookCellTable table = new BookCellTable(args);
						table.addColumn(BookColumn.DELETE);
						table.setSelectionPolicy(SelectionPolicy.MULTI_ROW);
						table.removeColumn(BookColumn.PRICE);
						table.removeColumn(BookColumn.CONTACT);
						if (bookDialog == null) {
							bookDialog = new BookDialog(table);
						}
						table.setDialog(bookDialog);
						table.addStatusFilterBox();

						if (Application.getCurrentUserId() == user.getId()) {
							table.getTitleBar().addLink(new ClickLabel("Add", new MouseDownHandler() {
								@Override
								public void onMouseDown(MouseDownEvent event) {
									bookDialog.center(new Book());
								}
							}));
						}

						table.getTitleBar().addLink(new ClickLabel("Print labels - all", new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								printLabels(table.getFullList());
							}
						}));

						table.getTitleBar().addLink(new ClickLabel("Print labels - selected", new MouseDownHandler() {
							@Override
							public void onMouseDown(MouseDownEvent event) {
								printLabels(table.getSelectedItems());
							}
						}));

						table.setTitle("Books");
						table.removeColumn(BookColumn.USER);
						table.populate();

						tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));
						tabPanel.selectTabNow(tabBody);
					}
				});
			}

			if (!Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS) && !user.equals(Application.getCurrentUser())) {
				form.setEnabled(false);
			}

			page.add(tabPanel);
		}

		Application.getLayout().setPage(title, page);
	}

	private void printLabels(List<Book> books) {
		FlexTable ft = new FlexTable();
		ft.setWidth("100%");

		ft.setCellPadding(0);
		ft.setCellSpacing(0);

		RootPanel.get().clear();
		Style rootStyle = RootPanel.get().getElement().getStyle();
		rootStyle.setPadding(0, Unit.PX);
		rootStyle.setMarginTop(41, Unit.PX);
		rootStyle.setMarginLeft(0, Unit.PX);
		rootStyle.setMarginRight(0, Unit.PX);
		rootStyle.setMarginBottom(0, Unit.PX);
		RootPanel.get().add(ft);

		for (int i = 0; i < books.size(); i++) {
			Book book = books.get(i);
			if (i % 3 == 0) {
				ft.insertRow(ft.getRowCount());
			}
			int row = ft.getRowCount() - 1;
			int cell = ft.getCellCount(row);

			VerticalPanel vp = new VerticalPanel();
			vp.setSpacing(0);
			Label title = new FixedWidthLabel(book.getTitle(), 225);

			Label category = new FixedWidthLabel(book.getCategory(), 225);
			category.addStyleName("smallText");
			category.getElement().getStyle().setPaddingBottom(4, Unit.PX);
			vp.add(category);

			vp.add(title);
			vp.add(category);

			String html = "<div id=\"barcode_" + book.getId() + "\" style=\"width:200px;height:25px;\">" + book.getId() + "+</div>";
			HTML barcode = new HTML(html);
			vp.add(barcode);

			PaddedPanel pp = new PaddedPanel(10);
			Label price = new Label(Formatter.formatCurrency(book.getPrice()));
			price.addStyleName("bold");
			pp.add(price);
			Label ids = new Label("S:" + user.getId() + " / I:" + book.getId());
			pp.add(ids);
			vp.add(pp);

			SimplePanel sp = new SimplePanel(vp);
			sp.setHeight("96px");
			sp.getElement().getStyle().setMarginLeft(12, Unit.PX);
			vp.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

			ft.setWidget(row, cell, sp);
			createBarcode(Integer.toString(book.getId()));
		}

		Window.setTitle(" ");
		Window.print();
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
					fieldTable.setUser(user);
					field.setInputVisibility(false);
				}
			}
		});
	}

}
