package com.areahomeschoolers.baconbits.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.content.Layout;
import com.areahomeschoolers.baconbits.client.content.article.ArticleGroupPage;
import com.areahomeschoolers.baconbits.client.content.article.ArticlePage;
import com.areahomeschoolers.baconbits.client.content.book.BookManagementPage;
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
import com.areahomeschoolers.baconbits.client.content.user.UserListPage;
import com.areahomeschoolers.baconbits.client.content.user.UserPage;
import com.areahomeschoolers.baconbits.client.content.user.UserStatusIndicator;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.ResetPasswordDialog;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.GroupData;
import com.areahomeschoolers.baconbits.shared.dto.HasGroupOwnership;
import com.areahomeschoolers.baconbits.shared.dto.PollResponseData;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
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
	public static final String APPLICATION_NAME = "WHE";
	private static boolean confirmNavigation = false;
	private static List<ParameterHandler<PollResponseData>> pollReturnHandlers = new ArrayList<ParameterHandler<PollResponseData>>();
	private static Timer pollTimer;
	private static final int pollInterval = 60 * 1000;
	private static final int inactivityInterval = pollInterval * 2 + 1;
	private static InactivityManager inactivityManager = new InactivityManager(inactivityInterval);
	private static UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public static HandlerRegistration addPollReturnHandler(final ParameterHandler<PollResponseData> handler) {
		pollReturnHandlers.add(handler);

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				pollReturnHandlers.remove(handler);
			}
		};
	}

	public static boolean administratorOf(HasGroupOwnership item) {
		return isAuthenticated() && applicationData.getCurrentUser().administratorOfAny(item.getGroupId(), item.getOrganizationId());
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

	public static ApplicationData getApplicationData() {
		return applicationData;
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

	public static void setRpcFailureCommand(Command command) {
		rpcFailureCommand = command;
	}

	public static void setTitle(String title) {
		Window.setTitle(title + " - " + Application.APPLICATION_NAME);
	}

	private static void createNewPage(String page) {
		VerticalPanel vp = layout.getNewPagePanel();

		// create new page
		if ("Home".equals(page)) {
			new HomePage(vp);
		} else if ("Article".equals(page)) {
			new ArticlePage(vp);
		} else if ("Event".equals(page)) {
			new EventPage(vp);
		} else if ("User".equals(page)) {
			new UserPage(vp);
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

		if (isAuthenticated()) {
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

			pollForData();
		}

		Window.addWindowClosingHandler(new ClosingHandler() {
			@Override
			public void onWindowClosing(ClosingEvent event) {
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

		createNewPage(page);
	}

	private void pollForData() {
		userService.getPollData(new Callback<PollResponseData>(false) {
			@Override
			protected void doOnSuccess(PollResponseData summary) {
				for (ParameterHandler<PollResponseData> handler : pollReturnHandlers) {
					handler.execute(summary);
				}
			}
		});
	}

}
