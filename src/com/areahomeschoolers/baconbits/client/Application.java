package com.areahomeschoolers.baconbits.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.content.Layout;
import com.areahomeschoolers.baconbits.client.content.article.ArticleGroupPage;
import com.areahomeschoolers.baconbits.client.content.article.ArticleListPage;
import com.areahomeschoolers.baconbits.client.content.article.ArticlePage;
import com.areahomeschoolers.baconbits.client.content.article.NewsPage;
import com.areahomeschoolers.baconbits.client.content.book.BookManagementPage;
import com.areahomeschoolers.baconbits.client.content.book.BookPage;
import com.areahomeschoolers.baconbits.client.content.book.BookReceiptPage;
import com.areahomeschoolers.baconbits.client.content.book.BookSearchPage;
import com.areahomeschoolers.baconbits.client.content.event.EventCalendarPage;
import com.areahomeschoolers.baconbits.client.content.event.EventListPage;
import com.areahomeschoolers.baconbits.client.content.event.EventPage;
import com.areahomeschoolers.baconbits.client.content.event.PaymentPage;
import com.areahomeschoolers.baconbits.client.content.event.RegistrationManagementPage;
import com.areahomeschoolers.baconbits.client.content.home.HomePage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupListPage;
import com.areahomeschoolers.baconbits.client.content.user.UserGroupPage;
import com.areahomeschoolers.baconbits.client.content.user.UserListPage;
import com.areahomeschoolers.baconbits.client.content.user.UserPage;
import com.areahomeschoolers.baconbits.client.content.user.UserStatusIndicator;
import com.areahomeschoolers.baconbits.client.event.CancelHandler;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.ResetPasswordDialog;
import com.areahomeschoolers.baconbits.client.widgets.UserAgreementDialog;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.GroupData;
import com.areahomeschoolers.baconbits.shared.dto.HasGroupOwnership;
import com.areahomeschoolers.baconbits.shared.dto.HistoryEntry;
import com.areahomeschoolers.baconbits.shared.dto.PollResponseData;
import com.areahomeschoolers.baconbits.shared.dto.PollUpdateData;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Application supplies access to main client systems: history, layout, session and factory.
 */
public final class Application implements ValueChangeHandler<String> {
	private static Layout layout;
	private static final String DEFAULT_TOKEN = "page=Home";
	private static ApplicationData applicationData;
	private static Command rpcFailureCommand;
	public static String APPLICATION_NAME = "Citrus Groups";
	private static boolean confirmNavigation = false;
	private static List<ParameterHandler<PollResponseData>> pollReturnHandlers = new ArrayList<ParameterHandler<PollResponseData>>();
	private static Timer pollTimer;
	private static final int pollInterval = 60 * 1000;
	private static final int inactivityInterval = pollInterval * 2 + 1;
	private static InactivityManager inactivityManager = new InactivityManager(inactivityInterval);
	private static UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private static String confirmNavigationPreviousUrl;
	private static Command confirmNavigatonCommand;
	private static PollUpdateData pollUpdateData = null;
	private static List<HistoryEntry> historyEntries = new ArrayList<HistoryEntry>();

	public static void addHistoryEntry(String title, String url) {
		if (historyEntries == null) {
			historyEntries = new ArrayList<HistoryEntry>();
		}

		HistoryEntry item = new HistoryEntry(title, History.getToken());
		if (!historyEntries.isEmpty() && historyEntries.get(0).equals(item)) {
			return;
		}

		if (historyEntries.contains(item)) {
			historyEntries.remove(item);
		}
		historyEntries.add(0, item);
		pollUpdateData.addHistoryUpdate(title, url);
	}

	public static HandlerRegistration addPollReturnHandler(final ParameterHandler<PollResponseData> handler) {
		pollReturnHandlers.add(handler);

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				pollReturnHandlers.remove(handler);
			}
		};
	}

	public static boolean administratorOf(Book book) {
		return isAuthenticated() && (isSystemAdministrator() || book.getUserId() == Application.getCurrentUserId());
	}

	public static boolean administratorOf(HasGroupOwnership item) {
		if (!isAuthenticated()) {
			return false;
		}

		if (applicationData.getCurrentUser().administratorOfAny(item.getGroupId(), item.getOwningOrgId())) {
			return true;
		}

		if (item.getAddedById() == getCurrentUserId()) {
			return true;
		}

		if (item.getId() == 0) {
			return true;
		}

		return false;
	}

	public static boolean administratorOf(Integer groupId) {
		return isAuthenticated() && applicationData.getCurrentUser().administratorOf(groupId);
	}

	public static boolean administratorOf(User u) {
		return isAuthenticated() && applicationData.getCurrentUser().administratorOf(u);
	}

	public static boolean administratorOfAny(Integer... groupIds) {
		return isAuthenticated() && applicationData.getCurrentUser().administratorOfAny(groupIds);
	}

	public static boolean administratorOfCurrentOrg() {
		return administratorOf(getCurrentOrgId());
	}

	public static boolean canSwitchUser() {
		return isAuthenticated() && getCurrentUser().canSwitch();
	}

	public static ApplicationData getApplicationData() {
		return applicationData;
	}

	public static UserGroup getCurrentOrg() {
		return applicationData.getCurrentOrg();
	}

	public static int getCurrentOrgId() {
		UserGroup ug = getCurrentOrg();
		if (ug == null) {
			return 0;
		}

		return ug.getId();
	}

	public static User getCurrentUser() {
		return applicationData.getCurrentUser();
	}

	public static int getCurrentUserId() {
		User u = getCurrentUser();
		if (u == null) {
			return 0;
		}

		return u.getId();
	}

	public static InactivityManager getInactivityManager() {
		return inactivityManager;
	}

	public static Layout getLayout() {
		return layout;
	}

	public static Command getRpcFailureCommand() {
		return rpcFailureCommand;
	}

	public static Map<Integer, Date> getUserActivity() {
		return applicationData.getUserActivity();
	}

	public static ArrayList<Tag> getUserInterests() {
		return applicationData.getUserInterests();
	}

	public static Data getUserPreferences() {
		return applicationData.getUserPreferences();
	}

	public static boolean hasRole(AccessLevel level) {
		return isAuthenticated() && applicationData.getCurrentUser().hasRole(level);
	}

	public static boolean isAuthenticated() {
		return applicationData.getCurrentUser() != null;
	}

	public static boolean isCitrus() {
		return applicationData.getCurrentOrg().isCitrus();
	}

	public static boolean isIdle() {
		return inactivityManager.isIdle();
	}

	public static boolean isLive() {
		return applicationData.isLive();
	}

	public static boolean isSystemAdministrator() {
		return isAuthenticated() && applicationData.getCurrentUser().getSystemAdministrator();
	}

	public static boolean memberOf(Integer groupId) {
		return isAuthenticated() && applicationData.getCurrentUser().memberOf(groupId);
	}

	public static void refreshSecurityGroups(final Command command) {
		userService.refreshSecurityGroups(new Callback<HashMap<Integer, GroupData>>() {
			@Override
			protected void doOnSuccess(HashMap<Integer, GroupData> result) {
				applicationData.getCurrentUser().setGroups(result);
				command.execute();
			}
		});
	}

	public static void reloadPage() {
		createNewPage(HistoryToken.getElement("page"));
	}

	public static void setConfirmNavigation(boolean confirm) {
		setConfirmNavigation(confirm, null);
	}

	public static void setConfirmNavigation(boolean confirm, Command confirmCommand) {
		confirmNavigation = confirm;
		confirmNavigationPreviousUrl = confirm ? History.getToken() : null;
		confirmNavigatonCommand = confirmCommand;
	}

	public static void setRpcFailureCommand(Command command) {
		rpcFailureCommand = command;
	}

	public static void setTitle(String title) {
		Window.setTitle(title + " - " + Application.APPLICATION_NAME);
		if (pollUpdateData != null && (!isAuthenticated() || !getCurrentUser().isSwitched())) {
			addHistoryEntry(title, History.getToken());
		}
	}

	private static void createNewPage(String page) {
		if (confirmNavigatonCommand != null) {
			confirmNavigatonCommand.execute();
			confirmNavigatonCommand = null;
		}
		confirmNavigation = false;
		confirmNavigationPreviousUrl = null;

		VerticalPanel vp = layout.getNewPagePanel();

		// create new page
		if ("Home".equals(page)) {
			new HomePage(vp);
		} else if ("Article".equals(page)) {
			new ArticlePage(vp);
		} else if ("Book".equals(page)) {
			new BookPage(vp);
		} else if ("Event".equals(page)) {
			new EventPage(vp);
		} else if ("User".equals(page)) {
			new UserPage(vp);
		} else if ("UserGroup".equals(page)) {
			new UserGroupPage(vp);
		} else if ("UserList".equals(page)) {
			new UserListPage(vp);
		} else if ("EventList".equals(page)) {
			new EventListPage(vp);
		} else if ("UserGroupList".equals(page)) {
			new UserGroupListPage(vp);
		} else if ("ArticleGroup".equals(page)) {
			new ArticleGroupPage(vp);
		} else if ("Payment".equals(page)) {
			new PaymentPage(vp);
		} else if ("BookReceipt".equals(page)) {
			new BookReceiptPage(vp);
		} else if ("BookManagement".equals(page)) {
			new BookManagementPage(vp);
		} else if ("BookSearch".equals(page)) {
			new BookSearchPage(vp);
		} else if ("EventCalendar".equals(page)) {
			new EventCalendarPage(vp);
		} else if ("RegistrationManagement".equals(page)) {
			new RegistrationManagementPage(vp);
		} else if ("ArticleList".equals(page)) {
			new ArticleListPage(vp);
		} else if ("News".equals(page)) {
			new NewsPage(vp);
		} else {
			new ErrorPage(PageError.PAGE_NOT_FOUND);
		}

		layout.getSearchBox().reset();
	}

	private static void setLayout(Layout layout) {
		Application.layout = layout;
	}

	public Application(ApplicationData ap) {
		// set session information
		applicationData = ap;
		APPLICATION_NAME = ap.getCurrentOrg().getShortName();

		if (getCurrentOrg() != null && getCurrentOrg().getFaviconId() != null) {
			NodeList<Element> links = com.google.gwt.dom.client.Document.get().getElementsByTagName("link");
			for (int i = 0; i < links.getLength(); i++) {
				LinkElement link = LinkElement.as(links.getItem(i));
				if (link.getRel() != null && "SHORTCUT ICON".equals(link.getRel().toUpperCase())) {
					link.setHref(Constants.DOCUMENT_URL_PREFIX + getCurrentOrg().getFaviconId());
					Element head = link.getParentElement();
					link.removeFromParent();
					head.appendChild(link);
					break;
				}
			}
		}

		History.addValueChangeHandler(this);

		// initialize layout
		Application.setLayout(new Layout());

		// initialize history
		String initToken = History.getToken();
		if (initToken != null && initToken.length() > 0) {
			History.newItem(initToken);
			History.fireCurrentHistoryState();
		} else {
			History.newItem(DEFAULT_TOKEN);
		}

		if (isAuthenticated() && getCurrentUser().getResetPassword()) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess() {
					new ResetPasswordDialog(false).center();
				}
			});
		}

		pollTimer = new Timer() {
			@Override
			public void run() {
				pollForData();
			}
		};
		pollTimer.scheduleRepeating(pollInterval);

		inactivityManager.addWakeUpCommand(new Command() {
			@Override
			public void execute() {
				pollForData();
				pollTimer.scheduleRepeating(pollInterval);
			}
		});

		inactivityManager.addOnSleepCommand(new Command() {
			@Override
			public void execute() {
				pollTimer.cancel();
			}
		});

		addPollReturnHandler(new ParameterHandler<PollResponseData>() {
			@Override
			public void execute(PollResponseData item) {
				getApplicationData().updateUserActivityFromMap(item.getUserActivity());
				UserStatusIndicator.updateAllStatusIndicators();
			}
		});

		pollUpdateData = new PollUpdateData(getCurrentOrgId());

		pollForData();

		Window.addWindowClosingHandler(new ClosingHandler() {
			@Override
			public void onWindowClosing(ClosingEvent event) {
				pollForData();

				if (confirmNavigation) {
					event.setMessage("Confirm Action");
				}
			}
		});
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String token = event.getValue();
		HistoryToken.createMapFromToken(token);
		final String page = HistoryToken.getElement("page") == null ? "Home" : HistoryToken.getElement("page");

		if (confirmNavigation) {
			GWT.runAsync(new RunAsyncCallback() {
				@Override
				public void onFailure(Throwable reason) {
				}

				@Override
				public void onSuccess() {
					ConfirmDialog dialog = new ConfirmDialog("Really leave this page? Data you have entered will not be saved.", new ConfirmHandler() {
						@Override
						public void onConfirm() {
							createNewPage(page);
						}
					});
					dialog.addCancelHandler(new CancelHandler() {
						@Override
						public void onCancel() {
							HistoryToken.set(confirmNavigationPreviousUrl, false);
						}
					});
					dialog.setConfirmButtonText("Leave Page");
					dialog.setCancelButtonText("Stay on Page");
					dialog.center();
				}
			});
		} else {
			createNewPage(page);
		}

		boolean viewingPolicies = false;
		int articleId = Url.getIntegerParameter("articleId");
		if ("Article".equals(page) && (articleId == 72 || articleId == 73)) {
			viewingPolicies = true;
		}

		if (isAuthenticated() && getCurrentUser().getShowUserAgreement() && !viewingPolicies) {
			UserAgreementDialog dialog = new UserAgreementDialog();
			dialog.center();
		}
	}

	private void pollForData() {
		userService.getPollData(pollUpdateData, new Callback<PollResponseData>(false) {
			@Override
			protected void doOnSuccess(PollResponseData summary) {
				pollUpdateData.clearHistoryUpdates();
				for (ParameterHandler<PollResponseData> handler : pollReturnHandlers) {
					handler.execute(summary);
				}
				historyEntries = summary.getHistoryItems();
			}
		});
	}

}
