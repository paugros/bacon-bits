package com.areahomeschoolers.baconbits.client.content.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.book.BookEditDialog;
import com.areahomeschoolers.baconbits.client.content.book.BookTable;
import com.areahomeschoolers.baconbits.client.content.book.BookTable.BookColumn;
import com.areahomeschoolers.baconbits.client.content.calendar.Appointment;
import com.areahomeschoolers.baconbits.client.content.calendar.AppointmentStyle;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantTable;
import com.areahomeschoolers.baconbits.client.content.event.EventParticipantTable.ParticipantColumn;
import com.areahomeschoolers.baconbits.client.content.event.EventVolunteerTable;
import com.areahomeschoolers.baconbits.client.content.event.EventVolunteerTable.VolunteerColumn;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule.AdDirection;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.tag.TagSection;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupTable.UserGroupColumn;
import com.areahomeschoolers.baconbits.client.content.user.UserTable.UserColumn;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.AddressField;
import com.areahomeschoolers.baconbits.client.widgets.CalendarPanel;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.CookieCrumb;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.areahomeschoolers.baconbits.client.widgets.EditableImage;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FixedWidthLabel;
import com.areahomeschoolers.baconbits.client.widgets.Form;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.client.widgets.TabPage;
import com.areahomeschoolers.baconbits.client.widgets.TabPage.TabPageCommand;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreferenceType;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserPage implements Page {
	public static boolean canEditUser(User user) {
		if (!Application.isAuthenticated()) {
			return false;
		}

		if (Application.administratorOf(user)) {
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
			AddressField.validateAddress(user, new Command() {
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
	private Set<PrivacyPreferenceWidget> privacyPreferenceWidgets = new HashSet<PrivacyPreferenceWidget>();
	private BookEditDialog bookDialog;
	private TagSection tagSection;
	private FormField tagField;
	private String title;

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

				if (Url.getBooleanParameter("details") && !canEditUser(user)) {
					new ErrorPage(PageError.NOT_AUTHORIZED);
					return;
				}

				title = user.isSaved() ? user.getFullName() : "New User";

				CookieCrumb cc = new CookieCrumb();
				cc.add(new DefaultHyperlink("Homeschoolers By Interest", PageUrl.tagGroup("USER")));
				cc.add(new DefaultHyperlink("Homeschoolers", PageUrl.userList()));
				if (user.getParentId() != null && user.getParentId() > 0) {
					DefaultHyperlink parent = new DefaultHyperlink(user.getParentFirstName() + " " + user.getParentLastName(), PageUrl.user(user.getParentId()));
					cc.add(parent);
				}
				if (Url.getBooleanParameter("details")) {
					cc.add(new DefaultHyperlink(title, PageUrl.user(user.getId())));
					cc.add("Edit details");
				} else {
					cc.add(title);
				}
				page.add(cc);

				if (user.isSaved() && !Url.getBooleanParameter("details")) {
					createViewPage();
				} else {
					createDetailsPage();
				}

				Application.getLayout().setPage(title, page);
			}

		});
	}

	private native void createBarcode(String bookId) /*-{
		$wnd.$('#barcode_' + bookId).barcode({
			code : 'code39'
		});
	}-*/;

	private void createDetailsPage() {
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

					EditableImage image = new EditableImage(DocumentLinkType.PROFILE, user.getId());
					if (user.getImageId() != null) {
						image.setImage(new Image(ClientUtils.createDocumentUrl(user.getImageId(), user.getImageExtension())));
					}
					image.setImageResource(MainImageBundle.INSTANCE.blankProfile());
					image.setEnabled(canEditUser(user));
					image.populate();
					image.addStyleName("profilePic");
					fieldTable.removeStyleName("sectionContent");
					hp.addStyleName("sectionContent");
					hp.add(image);
					hp.add(fieldTable);
					hp.setCellWidth(image, "220px");

					tabBody.add(WidgetFactory.newSection("", hp, ContentWidth.MAXWIDTH1100PX));
					// interests
					// if (!pageData.getInterests().isEmpty() || canEditUser(user)) {
					// VerticalPanel tp = new VerticalPanel();
					// Label heading = new Label("Interests");
					// heading.addStyleName("hugeText");
					// tp.add(heading);
					// String txt =
					// "Interests can be anything: hobbies, academic topics, religions, curriculum publishers, teaching styles, recreational activities, sports, or whatever else. ";
					// txt +=
					// "We'll use these interests to help you find other homeschoolers with similar interests, and to show you events, articles and books relating to your interests. ";
					// txt += "The most useful interests are neither too general nor too specific.";
					// Label sub = new Label(txt);
					// sub.getElement().getStyle().setColor("#666666");
					// if (viewingSelf()) {
					// tp.add(sub);
					// }
					// tp.setWidth("600px");
					//
					// tabBody.add(tp);
					// }

					createTagSection();
					fieldTable.addField(tagField);

					tabPanel.selectTabNow(tabBody);
				}
			});

			if (user.userCanSee(Application.getCurrentUser(), PrivacyPreferenceType.EVENTS)) {
				tabPanel.add("Events", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						// payment return message
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

						// volunteer positions
						ArgMap<EventArg> args = new ArgMap<EventArg>();
						args.put(EventArg.USER_ID, user.getId());

						final EventVolunteerTable vt = new EventVolunteerTable(args);
						vt.removeColumn(VolunteerColumn.NAME);
						if (!Application.administratorOf(user)) {
							vt.removeColumn(VolunteerColumn.FULFILLED);
							vt.addColumn(VolunteerColumn.FULFILLED_READ_ONLY);
						}

						tabBody.add(WidgetFactory.newSection(vt, ContentWidth.MAXWIDTH1000PX));

						vt.populate();

						// events
						args = new ArgMap<EventArg>(EventArg.REGISTERED_BY_OR_ADDED_FOR_ID, user.getId());
						args.setStatus(Status.ACTIVE);
						EventParticipantTable table = new EventParticipantTable(args);
						table.setDisplayColumns(ParticipantColumn.EVENT, ParticipantColumn.EVENT_DATE, ParticipantColumn.PARTICIPANT_NAME,
								ParticipantColumn.ADDED_DATE, ParticipantColumn.PRICE, ParticipantColumn.STATUS);
						table.setTitle("Event Registrations");

						if (!user.userCanSee(Application.getCurrentUser(), PrivacyPreferenceType.FAMILY)) {
							table.removeColumn(ParticipantColumn.PARTICIPANT_NAME);
						}

						if (canEditUser(user)) {
							table.addStatusFilterBox();
							args.put(EventArg.NOT_STATUS_ID, 5);
						} else {
							table.removeColumn(ParticipantColumn.PRICE);
							args.put(EventArg.STATUS_ID, 2);
						}

						table.getTitleBar().addExcelControl();
						table.getTitleBar().addSearchControl();
						table.populate();

						tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1000PX));
						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (canEditUser(user)) {
				tabPanel.add("Groups", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						ArgMap<UserGroupArg> userArgs = new ArgMap<UserGroupArg>(Status.ACTIVE);
						userArgs.put(UserGroupArg.USER_ID, user.getId());
						final UserGroupTable groupsTable = new UserGroupTable(userArgs);
						groupsTable.setUser(user);
						groupsTable.setTitle("Group Membership");
						groupsTable.setDisplayColumns(UserGroupColumn.GROUP, UserGroupColumn.DESCRIPTION, UserGroupColumn.ADMINISTRATOR,
								UserGroupColumn.APPROVAL);
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
											group.setGroupApproved(true);

											userService.updateUserGroupRelation(user, group, true, new Callback<Void>() {
												@Override
												protected void doOnSuccess(Void item) {
													groupsTable.populate();
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

						tabBody.add(WidgetFactory.newSection(groupsTable, ContentWidth.MAXWIDTH900PX));
						tabPanel.selectTabNow(tabBody);
					}
				});
			}

			if (!user.isChild() && user.userCanSee(Application.getCurrentUser(), PrivacyPreferenceType.FAMILY)) {
				tabPanel.add("Family", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						ArgMap<UserArg> args = new ArgMap<UserArg>(Status.ACTIVE);
						args.put(UserArg.PARENT_ID, user.getId());

						final UserTable table = new UserTable(args);
						if (canEditUser(user)) {
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
						}
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

			if (user.memberOfAny(Constants.ONLINE_BOOK_SELLERS_GROUP_ID, Constants.PHYSICAL_BOOK_SELLERS_GROUP_ID)) {
				tabPanel.add("Books", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						DefaultHyperlink link = new DefaultHyperlink("Click here", PageUrl.article(65));
						String html = "<b>Need help?</b> " + link.toString() + " for book seller instructions, or ";
						html += "<a href=\"mailto:" + Constants.SUPPORT_EMAIL + "?subject=Adding books\">contact us</a> with questions.";
						HTML info = new HTML(html);
						info.addStyleName("mediumText mediumPadding");
						tabBody.add(info);

						ArgMap<BookArg> args = new ArgMap<BookArg>(BookArg.STATUS_ID, 1);
						args.put(BookArg.USER_ID, user.getId());

						final BookTable table = new BookTable(args);
						table.addColumn(BookColumn.ADDED_DATE);
						table.getTitleBar().addExcelControl();
						table.getTitleBar().addSearchControl();

						if (Application.isSystemAdministrator()) {
							table.addColumn(BookColumn.DELETE);
						}
						if (viewingSelf()) {
							table.setSelectionPolicy(SelectionPolicy.MULTI_ROW);
						}
						if (bookDialog == null) {
							bookDialog = new BookEditDialog(table);
						}
						table.addStatusFilterBox();

						if (canEditUser(user)) {
							table.getTitleBar().addLink(new ClickLabel("Add", new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									bookDialog.center(new Book());
								}
							}));

							table.getTitleBar().addLink(new Label("Print labels:"));

							table.getTitleBar().addLink(new ClickLabel("all", new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									printLabels(table.getFullList());
								}
							}));

							table.getTitleBar().addLink(new ClickLabel("selected", new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									printLabels(table.getSelectedItems());
								}
							}));
						}

						table.setTitle("Books");
						table.removeColumn(BookColumn.USER);
						table.populate();

						tabBody.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1100PX));
						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (!user.isChild() && canEditUser(user)) {
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

			if (user.userCanSee(Application.getCurrentUser(), PrivacyPreferenceType.EVENTS)) {
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
			} else {
				tabPanel.addSkipIndex();
			}

			if (canEditUser(user)) {
				tabPanel.add("Privacy", new TabPageCommand() {
					@Override
					public void execute(VerticalPanel tabBody) {
						String helpText = "<p><b>Public</b><br>Choosing to make your information 'Public' is exactly what it \n";
						helpText += "sounds like: anyone, including people off of Citrus Group's Network, will be able to see it. \n";
						helpText += "Choosing to make your information 'Public' also means that this information: \n";
						helpText += "<ul><li>can be associated with you (i.e., your name, profile pictures, etc.) even off Citrus Groups; \n";
						helpText += "<li>can show up when someone does a search on Citrus Groups or on a public search engine;</ul></p> \n";
						helpText += "<p><b>All network members</b><br>Choosing to make your information available \n";
						helpText += "to 'All network members' means any member logged into the Citrus Group's community of sites will be able to see it.</p>\n";
						helpText += "<p><b>All my groups</b><br>Choosing to make your information available to 'All my groups' \n";
						helpText += "will allow all current and future members within the groups with which you are associated to see \n";
						helpText += "your information. To see a listing of all members in your groups, access the group \n";
						helpText += "member listing page.</p>\n";
						helpText += "<p><b>Members of</b><br>You can select a specific pre-established  group to share your information with.</p>\n";
						helpText += "<p><b>Private</b><br>Information can only be seen by you, and administrators.</p>";
						HTML help = new HTML(helpText);
						help.getElement().getStyle().setMargin(10, Unit.PX);
						help.getElement().getStyle().setPadding(10, Unit.PX);
						help.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
						help.getElement().getStyle().setBorderColor("#cccccc");
						help.getElement().getStyle().setBorderWidth(1, Unit.PX);
						help.getElement().getStyle().setBackgroundColor("#ffffff");
						help.addStyleName("smallText");
						help.setWidth("600px");

						FieldTable ft = new FieldTable();
						ft.getFlexTable().setCellSpacing(8);
						tabBody.add(WidgetFactory.newSection("Privacy Settings", ft, ContentWidth.MAXWIDTH900PX));

						if (user.getGroups().isEmpty()) {
							String text = "NOTE: Since ";
							if (user.equals(Application.getCurrentUser())) {
								text += "you are ";
							} else {
								text += "this user is ";
							}
							text += "not yet a member of any groups, the \"All my groups\" setting is effectively the same as the Private setting.";

							ft.addField("", text);
						}

						CheckBox cb = new CheckBox("Exclude me from the directory entirely");
						ft.addField("Directory:", cb);

						ft.addField("Email address:", makePrivacyWidget(PrivacyPreferenceType.EMAIL));

						ft.addField("Home phone:", makePrivacyWidget(PrivacyPreferenceType.HOME_PHONE));

						ft.addField("Mobile phone:", makePrivacyWidget(PrivacyPreferenceType.MOBILE_PHONE));

						VerticalPanel ap = new VerticalPanel();
						PrivacyPreferenceWidget aw = makePrivacyWidget(PrivacyPreferenceType.ADDRESS);
						ap.add(aw);
						ap.add(new Label("NOTE: Your city will be displayed regardless of this setting."));
						ft.addField("Address:", ap);

						ft.addField("Event registrations:", makePrivacyWidget(PrivacyPreferenceType.EVENTS));

						PrivacyPreferenceWidget fw = makePrivacyWidget(PrivacyPreferenceType.FAMILY);
						fw.getVisibilityWidget().removeItem(VisibilityLevel.GROUP_MEMBERS);
						fw.getVisibilityWidget().removeItem(VisibilityLevel.MY_GROUPS);
						ft.addField("Family members:", fw);

						if (Url.getBooleanParameter("gb")) {
							ft.addField("", new Button("All done, go back", new ClickHandler() {
								@Override
								public void onClick(ClickEvent event) {
									History.back();
								}
							}));
						}

						cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(ValueChangeEvent<Boolean> event) {
								user.setDirectoryOptOut(event.getValue());
								userService.save(user, new Callback<ServerResponseData<User>>() {
									@Override
									protected void doOnSuccess(ServerResponseData<User> result) {
										for (PrivacyPreferenceWidget pw : privacyPreferenceWidgets) {
											pw.setEnabled(!user.getDirectoryOptOut());
										}
									}
								});
							}
						});
						cb.setValue(user.getDirectoryOptOut(), true);

						CheckBox rb = new CheckBox("Send me information about news and events");
						rb.setValue(user.getReceiveNews());
						ft.addField("", rb);

						rb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
							@Override
							public void onValueChange(ValueChangeEvent<Boolean> event) {
								user.setReceiveNews(event.getValue());
								userService.save(user, new Callback<ServerResponseData<User>>(false) {
									@Override
									protected void doOnSuccess(ServerResponseData<User> result) {
									}
								});
							}
						});

						ft.addSpanningWidget(help);

						tabPanel.selectTabNow(tabBody);
					}
				});
			} else {
				tabPanel.addSkipIndex();
			}

			if (!canEditUser(user)) {
				form.setEnabled(false);
			}

			page.add(tabPanel);
		}

	}

	private void createFieldTable() {
		fieldTable = new UserFieldTable(form, user);
		fieldTable.setWidth("100%");
	}

	private void createTagSection() {
		tagSection = new TagSection(TagMappingType.USER, user.getId());
		tagSection.setEditingEnabled(canEditUser(user));
		tagSection.populate(pageData.getInterests());

		tagField = form.createFormField("Interests:", tagSection);
		tagField.removeEditLabel();
	}

	private void createViewPage() {
		PaddedPanel pp = new PaddedPanel(10);
		pp.setWidth("100%");
		pp.getElement().getStyle().setMarginTop(10, Unit.PX);
		pp.getElement().getStyle().setMarginLeft(10, Unit.PX);

		EditableImage image = new EditableImage(DocumentLinkType.PROFILE, user.getId());
		if (user.getImageId() != null) {
			image.setImage(new Image(ClientUtils.createDocumentUrl(user.getImageId(), user.getImageExtension())));
		} else {
			image.setImage(new Image(MainImageBundle.INSTANCE.blankProfile()));
		}
		image.setEnabled(Application.isSystemAdministrator());
		image.populate();
		pp.add(image);
		pp.setCellWidth(image, "1%");

		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(3);

		Label titleLabel = new Label(user.getFullName());
		titleLabel.addStyleName("hugeText");

		vp.add(titleLabel);

		if (!Common.isNullOrBlank(user.getEmail())) {
			vp.add(new HTML("<a href=\"mailto:" + user.getEmail() + "\">" + Formatter.formatNoteText(user.getEmail()) + "</a>"));
		}

		if (!Common.isNullOrBlank(user.getAddress())) {
			Anchor address = new Anchor(user.getAddress(), "http://maps.google.com/maps?q=" + user.getAddress());
			address.setTarget("_blank");
			vp.add(address);
		}

		if (!Common.isNullOrBlank(user.getHomePhone())) {
			vp.add(new Label(user.getHomePhone()));
		}

		if (!Common.isNullOrBlank(user.getMobilePhone())) {
			vp.add(new Label(user.getMobilePhone() + " (mobile)"));
		}

		if (!user.isChild() && user.isSaved() && Application.getUserActivity().get(user.getId()) != null) {
			UserStatusIndicator st = new UserStatusIndicator();
			st.setShowWeeksAndMonths(true);
			st.setUserId(user.getId());
			vp.add(st);
		}

		if (!Common.isNullOrBlank(user.getFacebookUrl())) {
			Image fb = new Image(MainImageBundle.INSTANCE.faceBook());
			fb.getElement().getStyle().setMarginTop(5, Unit.PX);
			String fbLink = "<a href=\"" + user.getFacebookUrl() + "\" target=_blank>" + fb + "</a>";
			vp.add(new HTML(fbLink));
		}

		pp.add(vp);

		if (canEditUser(user)) {
			DefaultHyperlink edit = new DefaultHyperlink("Edit details", PageUrl.user(user.getId()) + "&details=true");
			edit.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
			edit.getElement().getStyle().setMarginRight(5, Unit.PX);
			pp.add(edit);
			pp.setCellHorizontalAlignment(edit, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		VerticalPanel ovp = new VerticalPanel();
		ovp.addStyleName("sectionContent");

		ovp.add(pp);

		TagSection ts = new TagSection(TagMappingType.USER, user.getId());
		ts.setEditingEnabled(false);
		ts.populate();

		ovp.add(ts);

		VerticalPanel outerPanel = new VerticalPanel();
		outerPanel.add(ovp);
		outerPanel.add(new AdsMiniModule(AdDirection.HORIZONTAL));

		page.add(outerPanel);
	}

	private PrivacyPreferenceWidget makePrivacyWidget(PrivacyPreferenceType privacyType) {
		PrivacyPreferenceWidget w = new PrivacyPreferenceWidget(user.getPrivacyPreference(privacyType));
		privacyPreferenceWidgets.add(w);

		return w;
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
		int labelsPerPage = 27;
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
