package com.areahomeschoolers.baconbits.client;

import java.util.Date;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.content.Layout;
import com.areahomeschoolers.baconbits.client.generated.Factory;
import com.areahomeschoolers.baconbits.client.generated.ReflectiveFactory;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.GenericEntity;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;

/**
 * Application supplies access to main client systems: history, layout, session and factory.
 */
public final class Application {

	// private static SystemServiceAsync systemService;
	private static Layout layout;
	// private static final String DEFAULT_TOKEN = "page=Home";
	private static final Factory factory = (Factory) GWT.create(ReflectiveFactory.class);
	private static ApplicationData applicationData;
	// private static boolean preserveSearchValues;
	// private static boolean reloadBeforeNextPageLoad;
	private static Command rpcFailureCommand;
	public static final String APPLICATION_NAME = "AHS";
	// private static final int pollInterval = 60 * 1000;
	// private static boolean pollIsPending = false;
	// private static boolean pollImmediately = true;
	private static boolean confirmNavigation = false;

	// private static Timer pollTimer;
	// private static Timer inactivityTimer;
	// private static String confirmNavigationPreviousUrl;
	// private static Command confirmNavigatonCommand;

	public static ApplicationData getApplicationData() {
		return applicationData;
	}

	public static Date getBuildDate() {
		return applicationData.getBuildDate();
	}

	public static int getBuildNumber() {
		return applicationData.getBuildNumber();
	}

	public static User getCurrentUser() {
		return applicationData.getCurrentUser();
	}

	public static Factory getFactory() {
		return factory;
	}

	public static Layout getLayout() {
		return layout;
	}

	public static GenericEntity getLogoImage() {
		return applicationData.getLogoImage();
	}

	public static Date getNextReleaseDate() {
		return applicationData.getNextReleaseDate();
	}

	public static Command getRpcFailureCommand() {
		return rpcFailureCommand;
	}

	public static Map<Integer, Date> getUserActivity() {
		return applicationData.getUserActivity();
	}

	public static GenericEntity getUserPreferences() {
		return applicationData.getUserPreferences();
	}

	// public static void printPage() {
	// VerticalPanel page = layout.getPagePanel();
	//
	// printPage(page.toString());
	// }

	// public static void printPage(String data) {
	// String html = "<html style=\"overflow: visible; height: auto;\"><head>\n";
	// html += "<!-- DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" -->\n";
	// html += "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n";
	// html += "<title>" + Window.getTitle() + "</title>\n";
	// html += "<link href=\"/ribeye/standard.css\" rel=\"stylesheet\">\n";
	// html += "<link href=\"/ribeye/RibEye.css\" rel=\"stylesheet\">\n";
	// html += "</style></head>\n<body style=\"overflow: visible; height: auto;\">\n";
	// html += data + "\n<script>setTimeout('window.print()', 500);</script>\n</body></html>";
	//
	// DocumentServiceAsync documentService = (DocumentServiceAsync) ServiceCache.getService(DocumentService.class);
	// Document document = new Document();
	// document.setSystemDocument(true);
	// document.setStringData(html);
	// document.setFileExtension("html");
	// document.setDescription("__TEMPORARY_PRINT_FILE__");
	// document.setFileName("print.html");
	// document.setFileType("text/html; charset=UTF-8");
	// document.setAddedById(Application.getCurrentUser().getId());
	//
	// documentService.save(document, new Callback<Document>(false) {
	// @Override
	// protected void doOnSuccess(Document doc) {
	// BrowserWindow window = new BrowserWindow("/ribeye/service/file?id=" + doc.getId() + "&deleteAfterServing=1&inline=1");
	// window.setMenuBar(true);
	// window.setWidth(830);
	// window.open();
	// }
	// });
	// }

	// public static void reloadPage() {
	// createNewPage(HistoryToken.getElement("page"));
	// }
	//
	// public static void sendPortalUserHome() {
	// if (!isPortalSession()) {
	// return;
	// }
	//
	// HistoryToken.set(PageUrl.home());
	// }

	public static boolean isLive() {
		return applicationData.isLive();
	}

	public static void printPage() {
		// TODO Auto-generated method stub

	}

	// private static void createNewPage(String page) {
	// // Callback.hideStatusPanel();
	// if (confirmNavigatonCommand != null) {
	// confirmNavigatonCommand.execute();
	// confirmNavigatonCommand = null;
	// }
	// confirmNavigation = false;
	// confirmNavigationPreviousUrl = null;
	//
	// if (reloadBeforeNextPageLoad) {
	// reloadBeforeNextPageLoad = false;
	// Window.Location.reload();
	// return;
	// }

	// create new page
	// try {
	// // tell layout to clear the flag panel if the new page doesn't initialize it
	// Layout.setClearFlagPanel(true);
	// factory.newPageInstance(page + "Page", layout.getNewPagePanel(), new ModuleClient() {
	// @Override
	// public void onSuccess(Object pageObject) {
	// if (Application.isPortalSession() && !(pageObject instanceof IsCustomerViewable)) {
	// new ErrorPage(PageError.NOT_AUTHORIZED);
	// }
	// }
	//
	// @Override
	// public void onUnavailable(Throwable err) {
	// Window.Location.reload();
	// // Callback.handleRpcExeption(err);
	// // new ErrorPage(PageError.SYSTEM_ERROR);
	// }
	// });
	// } catch (ClientClassNotFoundException e) {
	// new ErrorPage(PageError.PAGE_NOT_FOUND);
	// }

	// if (!preserveSearchValues && !Application.isPortalSession()) {
	// layout.getSearchBox().reset();
	// }
	// preserveSearchValues = false;
	// }

	public static void reloadPage() {
		// TODO Auto-generated method stub

	}

	public static void setRpcFailureCommand(Command command) {
		rpcFailureCommand = command;
	}

	public static void setTitle(String title) {
		Window.setTitle(title + " - " + Application.APPLICATION_NAME);
	}

	private static void setLayout(Layout layout) {
		Application.layout = layout;
	}

	public Application(ApplicationData ap) {
		// set session information
		applicationData = ap;

		if (!isLive()) {
			NodeList<Element> links = com.google.gwt.dom.client.Document.get().getElementsByTagName("link");
			for (int i = 0; i < links.getLength(); i++) {
				LinkElement link = LinkElement.as(links.getItem(i));
				if (link.getRel() != null && "SHORTCUT ICON".equals(link.getRel().toUpperCase())) {
					link.setHref("/ribeyeFavicon.png");
					Element head = link.getParentElement();
					link.removeFromParent();
					head.appendChild(link);
					break;
				}
			}
		}

		// History.addValueChangeHandler(Application.this);

		// initialize layout
		Application.setLayout(new Layout());

		// initialize history
		String initToken = History.getToken();
		if (initToken != null && initToken.length() > 0) {
			History.newItem(initToken);
			History.fireCurrentHistoryState();
		} else {
			// if (getCurrentUser().isPortalUser()) {
			// sendPortalUserHome();
			// } else {
			// History.newItem(DEFAULT_TOKEN);
			// }
		}

		// if ((isPortalSession() || isVendorSession()) && getCurrentUser().getResetPassword()) {
		// ResetPasswordDialog dialog = new ResetPasswordDialog(false);
		// dialog.center();
		// }
		//
		// initPollTimers();
		//
		// addPollReturnHandler(new ParameterHandler<PollResponseData>() {
		// @Override
		// public void execute(PollResponseData item) {
		// if (item.getBuildNumber() > 0 && Application.getBuildNumber() > 0 && item.getBuildNumber() != Application.getBuildNumber()) {
		// Application.setReloadBeforeNextPageLoad(true);
		// }
		//
		// Application.getApplicationData().updateUserActivityFromMap(item.getUserActivity());
		// UserStatusIndicator.updateAllStatusIndicators();
		// }
		// });
		// pollForData();

		Window.addWindowClosingHandler(new ClosingHandler() {
			@Override
			public void onWindowClosing(ClosingEvent event) {
				if (confirmNavigation) {
					event.setMessage("Confirm Action");
				}
			}
		});
	}

	// @Override
	// public void onValueChange(ValueChangeEvent<String> event) {
	// String token = event.getValue();
	// HistoryToken.createMapFromToken(token);
	// final String page = HistoryToken.getElement("page") == null ? "Home" : HistoryToken.getElement("page");
	//
	// if ("Home".equals(page) && getCurrentUser().isPortalUser()) {
	// sendPortalUserHome();
	// return;
	// }
	//
	// if (confirmNavigation) {
	// ConfirmDialog dialog = new ConfirmDialog("Really leave this page? Data you have entered will not be saved.", new ConfirmHandler() {
	// @Override
	// public void onConfirm() {
	// createNewPage(page);
	// }
	// });
	// dialog.addCancelHandler(new CancelHandler() {
	// @Override
	// public void onCancel() {
	// HistoryToken.set(confirmNavigationPreviousUrl, false);
	// }
	// });
	// dialog.setConfirmButtonText("Leave Page");
	// dialog.setCancelButtonText("Stay on Page");
	// dialog.center();
	// } else {
	// createNewPage(page);
	// }
	// }
	//
	// private void initPollTimers() {
	// pollTimer = new Timer() {
	// @Override
	// public void run() {
	// pollForData();
	//
	// pollIsPending = false;
	// }
	// };
	//
	// inactivityTimer = new Timer() {
	// @Override
	// public void run() {
	// if (!pollIsPending) {
	// pollImmediately = true;
	// }
	// }
	// };
	//
	// Event.addNativePreviewHandler(new NativePreviewHandler() {
	// @Override
	// public void onPreviewNativeEvent(final NativePreviewEvent event) {
	// final int eventType = event.getTypeInt();
	// switch (eventType) {
	// case Event.ONMOUSEMOVE:
	// schedulePoll();
	// break;
	// case Event.ONKEYDOWN:
	// schedulePoll();
	// break;
	// default:
	// }
	// }
	// });
	// }
	//
	// private void pollForData() {
	// pollImmediately = false;
	//
	// if (systemService == null) {
	// systemService = (SystemServiceAsync) ServiceCache.getService(SystemService.class);
	// }
	//
	// systemService.getPollData(Application.getCurrentUser().getId(), new Callback<PollResponseData>(false) {
	// @Override
	// protected void doOnSuccess(PollResponseData summary) {
	// for (ParameterHandler<PollResponseData> handler : pollReturnHandlers) {
	// handler.execute(summary);
	// }
	// }
	// });
	// }
	//
	// private void schedulePoll() {
	// if (!pollIsPending) {
	// pollIsPending = true;
	// if (pollImmediately) {
	// pollForData();
	// }
	// pollTimer.schedule(pollInterval);
	// inactivityTimer.schedule((pollInterval * 2) + 1);
	// }
	// }
}
