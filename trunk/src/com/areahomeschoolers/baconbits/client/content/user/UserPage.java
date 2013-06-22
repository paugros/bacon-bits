package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookDialog;
import com.areahomeschoolers.baconbits.client.content.book.BookTable;
import com.areahomeschoolers.baconbits.client.content.book.BookTable.BookColumn;
import com.areahomeschoolers.baconbits.client.content.calendar.Appointment;
import com.areahomeschoolers.baconbits.client.content.calendar.AppointmentStyle;
import com.areahomeschoolers.baconbits.client.content.event.BalanceBox;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantTable;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.event.EventVolunteerTable;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.CalendarPanel;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FixedWidthLabel;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.ItemVisibilityWidget;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPage implements Page {
	public static boolean canEditUser(User user) {
		if (!Application.isAuthenticated()) {
			return false;
		}

		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			return true;
		}

		if (user.equals(Application.getCurrentUser())) {
			return true;
		}

		if (user.childOf(Application.getCurrentUser())) {
			return true;
		}

		return false;
	}

	private final Form form = new Form(new FormSubmitHandler() {
		@Override
		public void onFormSubmit(final FormField formField) {
			UserFieldTable.validateUserAddress(user, new Command() {
				@Override
				public void execute() {
					save(formField);
				}
			});
		}
	});
	private VerticalPanel page;
	private User user = new User();
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private UserFieldTable fieldTable;
	private TabPage tabPanel;
	private UserPageData pageData;
	private EventServiceAsync eventService = (EventServiceAsync) ServiceCache.getService(EventService.class);

	private BookDialog bookDialog;

	public UserPage(final VerticalPanel page) {
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
				pageData = result;

				user = result.getUser();

				if (user.isSaved()) {
					HorizontalPanel hp = new HorizontalPanel();
					hp.setWidth("100%");
					Label heading = new Label(user.getFullName());
					heading.addStyleName("hugeText");
					hp.add(heading);
					if (viewingSelf()) {
						BalanceBox bb = new BalanceBox();
						bb.populate();
						hp.add(bb);
						hp.setCellHorizontalAlignment(bb, HasHorizontalAlignment.ALIGN_RIGHT);
					}
					page.add(WidgetFactory.wrapForWidth(hp, ContentWidth.MAXWIDTH900PX));
				}

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
			tabPanel.add("Profile", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					HorizontalPanel hp = new HorizontalPanel();
					hp.setWidth("100%");

					EditableImage image = new EditableImage(DocumentLinkType.PROFILE, user.getId(), user.getImageId(), canEditUser(user));
					image.addStyleName("profilePic");
					fieldTable.removeStyleName("sectionContent");
					hp.addStyleName("sectionContent");
					hp.add(image);
					hp.add(fieldTable);
					hp.setCellWidth(image, "220px");

					tabBody.add(WidgetFactory.newSection("Basic Information", hp, ContentWidth.MAXWIDTH1100PX));
					// interests
					if (!pageData.getInterests().isEmpty() || canEditUser(user)) {
						VerticalPanel tp = new VerticalPanel();
						Label heading = new Label("Interests");
						heading.addStyleName("hugeText");
						tp.add(heading);
						String txt = "Interests can be anything: hobbies, academic topics, religions, curriculum publishers, teaching styles, recreational activities, sports, or whatever else. ";
						txt += "We'll use these interests to help you find other homeschoolers with similar interests, and to show you events, articles and books relating to your interests. ";
						txt += "The most useful interests are neither too general nor too specific.";
						Label sub = new Label(txt);
						sub.getElement().getStyle().setColor("#666666");
						if (viewingSelf()) {
							tp.add(sub);
						}
						tp.setWidth("600px");

						tabBody.add(tp);
					}
					TagSection ts = new TagSection(TagMappingType.USER, user.getId(), pageData.getInterests());
					ts.setEditingEnabled(canEditUser(user));
					ts.populate();
					tabBody.add(ts);

					tabPanel.selectTabNow(tabBody);
				}
			});

			tabPanel.add("Events", new TabPageCommand() {
				@Override
				public void execute(VerticalPanel tabBody) {
					String paymentAction = Url.getParameter("ps");
					if (paymentAction != null) {
						String text = null;
						String subText = "";
						if ("return".equals(paymentAction)) {
							text = "Thank you for your purchase.";
							subText = "Below are the events you've registered to attend. Payments may take a few minutes to be reflected here.";
						} else if ("cancel".equals(paymentAction)) {
							text = "We're sorry you canceled your purchase.";
							subText += "You can change your mind at any time.";
						}

						if (text != null) {
							VerticalPanel vp = new VerticalPanel();
							Label message = new Label(text);
							message.addStyleName("largeText");
							vp.add(message);
							vp.add(new Label(subText));
							tabBody.add(vp);
						}
					}

					ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.REGISTERED_BY_OR_ADDED_FOR_ID, user.getId());
					args.setStatus(Status.ACTIVE);
					args.put(EventArg.NOT_STATUS_ID, 5);
					EventParticipantTable table = new EventParticipantTable(args);
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

					final EventVolunteerTable vt = new EventVolunteerTable(args);

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
					ArgMap<UserGroupArg> userArgs = new ArgMap<UserGroupArg>();
					userArgs.put(UserGroupArg.USER_ID, user.getId());
					final UserGroupTable groupsTable = new UserGroupTable(userArgs);
					groupsTable.setUser(user);
					groupsTable.setTitle("Group Membership");
					groupsTable.setDisplayColumns(UserGroupColumn.GROUP, UserGroupColumn.DESCRIPTION, UserGroupColumn.ADMINISTRATOR);
					groupsTable.getTitleBar().addExcelControl();
					if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
						groupsTable.getTitleBar().addLink(new ClickLabel("Add", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								ArgMap<UserGroupArg> grpArgs = new ArgMap<UserGroupArg>(Status.ACTIVE);
								grpArgs.put(UserGroupArg.USER_NOT_MEMBER_OF, user.getId());
								if (!Application.isSystemAdministrator()) {
									grpArgs.put(UserGroupArg.USER_IS_ADMIN_OF, Application.getCurrentUserId());
								}
								final UserGroupSelector selector = new UserGroupSelector(grpArgs);
								selector.addSubmitCommand(new Command() {
									@Override
									public void execute() {
										final UserGroup group = selector.getSelectedItem();

										userService.updateUserGroupRelation(user, group, true, new Callback<Void>() {
											@Override
											protected void doOnSuccess(Void item) {
												groupsTable.addItem(group);
												selector.getCellTable().removeItem(group);
											}
										});
										selector.clearSelection();
									}
								});

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

			if (!user.isChild()) {
				tabPanel.add("Family", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
						args.put(UserArg.PARENT_ID, user.getId());

						final UserTable table = new UserTable(args);
						ClickLabel addChild = new ClickLabel("Add Family Member", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								CreateFamilyMemberDialog dialog = new CreateFamilyMemberDialog(user, new Command() {
									@Override
									public void execute() {
										table.populate();
									}
								});
								dialog.center(new User());
							}
						});
						table.getTitleBar().addLink(addChild);
						table.removeColumn(UserColumn.PHONE);
						table.removeColumn(UserColumn.STATUS);
						table.setTitle("Family Members");
						table.disablePaging();
						table.populate();

						tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));
						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (user.memberOfAny(16, 17)) {
				tabPanel.add("Books", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						Hyperlink link = new Hyperlink("Click here", PageUrl.article(65));
						String html = link.toString() + " for book seller instructions.";
						tabBody.add(new HTML(html));

						ArgMap<BookArg> args = new ArgMap<BookArg>(BookArg.STATUS_ID, 1);
						args.put(BookArg.USER_ID, user.getId());

						final BookTable table = new BookTable(args);
						table.getTitleBar().addExcelControl();
						table.getTitleBar().addSearchControl();

						table.addColumn(BookColumn.DELETE);
						table.setSelectionPolicy(SelectionPolicy.MULTI_ROW);
						table.removeColumn(BookColumn.PRICE);
						table.removeColumn(BookColumn.CONTACT);
						if (bookDialog == null) {
							bookDialog = new BookDialog(table);
						}
						table.setDialog(bookDialog);
						table.addStatusFilterBox();

						if (viewingSelf()) {
							table.getTitleBar().addLink(new ClickLabel("Add", new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									bookDialog.center(new Book());
								}
							}));
						}

						table.getTitleBar().addLink(new ClickLabel("Print labels - all", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								printLabels(table.getFullList());
							}
						}));

						table.getTitleBar().addLink(new ClickLabel("Print labels - selected", new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
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

			if (!user.isChild()) {
				tabPanel.add("Payments", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						UserPaymentSection pay = new UserPaymentSection(user, tabBody);
						pay.populate();

						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			tabPanel.add("Calendar", new TabPageCommand() {
				@Override
				public void execute(final VerticalPanel tabBody) {
					ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.REGISTERED_BY_OR_ADDED_FOR_ID, user.getId());
					args.setStatus(Status.ACTIVE);
					args.put(EventArg.NOT_STATUS_ID, 5);
					eventService.getParticipants(args, new Callback<ArrayList<EventParticipant>>() {
						@Override
						protected void doOnSuccess(ArrayList<EventParticipant> result) {
							CalendarPanel cp = new CalendarPanel();
							tabBody.add(cp);

							Map<Integer, Appointment> appMap = new HashMap<Integer, Appointment>();
							for (EventParticipant p : result) {
								Appointment a = appMap.get(p.getEventId());
								if (a == null) {
									a = new Appointment();
									a.setStyle(AppointmentStyle.GREEN);
									appMap.put(p.getEventId(), a);
									a.setReadOnly(true);
									a.setStart(p.getEventStartDate());
									a.setEnd(p.getEventEndDate());
									a.setTitle(p.getEventTitle());
									a.setId(Integer.toString(p.getEventId()));
									a.setDescription(p.getFirstName());
								} else {
									a.setDescription(a.getDescription() + ", " + p.getFirstName());
								}
							}

							cp.getCalendar().suspendLayout();
							cp.getCalendar().addAppointments(new ArrayList<Appointment>(appMap.values()));
							cp.getCalendar().resumeLayout();

							tabPanel.selectTabNow(tabBody);
						}
					});
				}
			});

			if (Application.getCurrentUserId() == 1) {
				tabPanel.add("Privacy", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						FieldTable ft = new FieldTable();
						tabBody.add(WidgetFactory.newSection("Privacy Settings", ft, ContentWidth.MAXWIDTH900PX));

						ItemVisibilityWidget ew = new ItemVisibilityWidget();
						ft.addField("Email address:", ew);

						ItemVisibilityWidget pw = new ItemVisibilityWidget();
						ft.addField("Phone number:", pw);

						ItemVisibilityWidget aw = new ItemVisibilityWidget();
						ft.addField("Address:", aw);

						ItemVisibilityWidget vw = new ItemVisibilityWidget();
						ft.addField("Event registrations:", vw);

						ItemVisibilityWidget fw = new ItemVisibilityWidget();
						ft.addField("Family members:", fw);

						tabPanel.selectTabNow(tabBody);
					}
				});
			}

			if (!canEditUser(user)) {
				form.setEnabled(false);
			}

			page.add(tabPanel);
		}

		Application.getLayout().setPage(title, page);
	}

	private void printLabels(List<Book> books) {
		RootPanel.get().clear();
		Style rootStyle = RootPanel.get().getElement().getStyle();
		rootStyle.setPadding(0, Unit.PX);
		rootStyle.setMarginTop(0, Unit.PX);
		rootStyle.setMarginLeft(0, Unit.PX);
		rootStyle.setMarginRight(0, Unit.PX);
		rootStyle.setMarginBottom(0, Unit.PX);

		FlexTable ft = null;

		// layout variables
		int columnWidth = 225;
		int pageMarginTop = 40;
		int labelsPerPage = 30;
		int cellMarginLeft = 12;
		int cellHeight = 96;

		for (int i = 0; i < books.size(); i++) {
			if (i % labelsPerPage == 0) {
				// insist on a page break every 30 labels
				ft = new FlexTable();
				ft.getElement().getStyle().setMarginTop(pageMarginTop, Unit.PX);
				ft.addStyleName("pageBreakAfter");
				ft.setWidth("100%");

				ft.setCellPadding(0);
				ft.setCellSpacing(0);
				RootPanel.get().add(ft);
			}

			Book book = books.get(i);
			if (i % 3 == 0) {
				ft.insertRow(ft.getRowCount());
			}
			int row = ft.getRowCount() - 1;
			int cell = ft.getCellCount(row);

			VerticalPanel vp = new VerticalPanel();
			vp.setSpacing(0);
			Label title = new FixedWidthLabel(book.getTitle(), columnWidth);

			Label category = new FixedWidthLabel(book.getCategory(), columnWidth);
			category.addStyleName("smallText");
			category.getElement().getStyle().setPaddingBottom(4, Unit.PX);
			vp.add(category);

			vp.add(title);
			vp.add(category);

			// use plus sign as separator
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

			int column = ft.getCellCount(ft.getRowCount() - 1);

			SimplePanel sp = new SimplePanel(vp);
			sp.setHeight(cellHeight + "px");
			sp.getElement().getStyle().setMarginLeft(cellMarginLeft + (column * 5), Unit.PX);
			sp.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);

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

	private boolean viewingSelf() {
		return user.getId() == Application.getCurrentUserId();
	}

}
