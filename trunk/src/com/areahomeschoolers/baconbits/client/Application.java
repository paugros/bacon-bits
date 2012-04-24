package com.areahomeschoolers.baconbits.client;

import java.util.Date;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.content.Layout;
import com.areahomeschoolers.baconbits.client.content.home.HomePage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage;
import com.areahomeschoolers.baconbits.client.content.system.ErrorPage.PageError;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
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
	public static final String APPLICATION_NAME = "AHS";
	private static boolean confirmNavigation = false;

	public static ApplicationData getApplicationData() {
		return applicationData;
	}

	public static User getCurrentUser() {
		return applicationData.getCurrentUser();
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

	public static Data getUserPreferences() {
		return applicationData.getUserPreferences();
	}

	public static boolean isLive() {
		return applicationData.isLive();
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
		} else if ("".equals(page)) {

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

}
