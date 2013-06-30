package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.LinkPanel;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog.LoginHandler;
import com.areahomeschoolers.baconbits.client.widgets.StatusPanel;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Accessor class (available through Application) to major page layout components.
 */
public final class Layout {

	class MainLayoutDock extends DockLayoutPanel {
		public MainLayoutDock(Unit unit) {
			super(unit);
			addStyleName("MainLayoutDock");
		}

		@Override
		public Double getWidgetSize(Widget widget) {
			LayoutData ld = (LayoutData) widget.getLayoutData();
			if (ld == null) {
				return 0.0;
			}
			return ld.size;
		}
	}

	private static final int HEADER_HEIGHT = 53;
	private static final int MENU_HEIGHT = 35;
	private static final int LOGO_DIV_WIDTH = 125;
	private final MainLayoutDock dock = new MainLayoutDock(Unit.PX);
	private final SearchBox searchBox;
	private final SimplePanel mobileBodyPanel = new SimplePanel();
	private final MainMenu menu;
	private final ScrollPanel bodyPanel = new ScrollPanel();
	private final AbsolutePanel ap = new AbsolutePanel();
	// private final User currentUser = Application.getCurrentUser();
	private boolean headerIsVisible = true;
	// Holds a reference to the latest panel queued to be displayed on the page when setPage is called
	private VerticalPanel currentPagePanel;
	private final HorizontalPanel headerPanel = new HorizontalPanel();
	private final HorizontalPanel menuPanel = new HorizontalPanel();
	private boolean isMobileBrowser = false;

	public Layout() {
		isMobileBrowser = ClientUtils.isMobileBrowser();

		// headerPanel.setStyleName("headerPanel");
		headerPanel.setHeight(HEADER_HEIGHT + "px");
		headerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (!isMobileBrowser) {
			RootLayoutPanel.get().setStyleName("overflowHidden");
		}

		// logo
		Image logo = new Image(MainImageBundle.INSTANCE.logo());

		HTML logoDiv = new HTML("<a href=\"#" + PageUrl.home() + "\">" + logo + "</a>");
		logoDiv.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if ("Home".equals(HistoryToken.getElement("page"))) {
					Application.reloadPage();
				} else {
					HistoryToken.set(PageUrl.home());
				}
			}
		});
		// headerPanel.add(logoDiv);

		SimplePanel spacer = new SimplePanel();
		headerPanel.add(spacer);
		headerPanel.setCellWidth(spacer, "100%");

		searchBox = new SearchBox();
		// headerPanel.add(searchBox);

		LinkPanel sessionPanel = new LinkPanel();
		headerPanel.add(sessionPanel);
		sessionPanel.addStyleName("sessionPanel");

		final ClickLabel logInOrOut = new ClickLabel();
		if (Application.isAuthenticated()) {
			if (Application.getCurrentUser().canSwitch()) {
				final UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
				EntitySuggestBox userSearchBox = new EntitySuggestBox("User");
				userSearchBox.setSelectionHandler(new ParameterHandler<Integer>() {
					@Override
					public void execute(Integer userId) {
						userService.switchToUser(userId, new Callback<Void>() {
							@Override
							protected void doOnSuccess(Void result) {
								Window.Location.reload();
							}
						});
					}
				});
				userSearchBox.setResetHandler(new ParameterHandler<SuggestBox>() {
					@Override
					public void execute(SuggestBox suggestBox) {
						suggestBox.setText(Application.getCurrentUser().getFullName());
					}
				});
				userSearchBox.getElement().getStyle().setMarginLeft(20, Unit.PX);
				userSearchBox.getTextBox().addDoubleClickHandler(new DoubleClickHandler() {
					@Override
					public void onDoubleClick(DoubleClickEvent event) {
						HistoryToken.set(PageUrl.user(Application.getCurrentUser().getId()));
					}
				});
				userSearchBox.setClearOnFocus(true);

				sessionPanel.add(userSearchBox);
			} else {
				Hyperlink name = new Hyperlink("Hello, " + Application.getCurrentUser().getFirstName(), PageUrl.user(Application.getCurrentUserId()));
				name.addStyleName("nowrap");
				sessionPanel.add(name);
			}

			logInOrOut.setText("Log out");
			logInOrOut.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
					loginService.logout(new Callback<Void>(false) {
						@Override
						protected void doOnSuccess(Void result) {
							Window.Location.reload();
						}
					});
				}
			});
		} else {
			logInOrOut.setText("Log in / Create account");
			logInOrOut.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);

					if (!GWT.isProdMode()) {
						// loginService.loginAndGetApplicationData("kaugros@gmail.com", "Redball1", new AsyncCallback<ApplicationData>() {
						// loginService.loginAndGetApplicationData("kristin@wearehomeeducators.com", "Redball1", new AsyncCallback<ApplicationData>() {
						loginService.loginAndGetApplicationData("paul.augros@gmail.com", "L33nfiatna", new AsyncCallback<ApplicationData>() {
							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(ApplicationData result) {
								Window.Location.reload();
							}
						});
						return;
					}

					final LoginDialog ld = new LoginDialog(loginService);
					ld.setLoginHandler(new LoginHandler() {
						@Override
						public void onLogin(ApplicationData ap) {
							Window.Location.reload();
						}
					});

					if (isMobileBrowser) {
						ld.showRelativeTo(logInOrOut);
					} else {
						ld.center();
					}
				}
			});
		}
		logInOrOut.setWordWrap(false);
		sessionPanel.add(logInOrOut);

		if (!isMobileBrowser) {
			ap.setHeight("100%");
			ap.getElement().getStyle().setOverflow(Overflow.VISIBLE);
			bodyPanel.setStyleName("bodyPanel");
			bodyPanel.add(ap);
		}

		// status panel
		StatusPanel sp = new StatusPanel();
		Callback.setStatusPanel(sp);
		RootPanel.get().add(sp);

		menu = new MainMenu();
		menuPanel.setHeight(MENU_HEIGHT + "px");
		menuPanel.setWidth("100%");
		menuPanel.add(menu);
		menuPanel.setCellWidth(menu, "100%");
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.add(logoDiv);
		hp.setCellWidth(logoDiv, LOGO_DIV_WIDTH + "px");
		hp.setHeight(HEADER_HEIGHT + MENU_HEIGHT + "px");

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(headerPanel);
		vp.add(menuPanel);
		hp.addStyleName("headerPanel");
		hp.add(vp);
		if (isMobileBrowser) {
			mobileBodyPanel.addStyleName("bodyPanel");
			mobileBodyPanel.setWidth("100%");
			VerticalPanel vvp = new VerticalPanel();
			vvp.setWidth("100%");
			vvp.add(hp);
			vvp.add(mobileBodyPanel);
			RootPanel.get().add(vvp);
		} else {
			dock.addStyleName("Dock");
			dock.addNorth(hp, HEADER_HEIGHT + MENU_HEIGHT);
			dock.add(bodyPanel);
			RootLayoutPanel.get().add(dock);
		}
	}

	/**
	 * @return Returns a new {@link VerticalPanel} queued to be displayed as the current page once it is shown by a call to
	 *         <code>Application.getLayout().setPage(panel)</code>, unless preempted by another call to getNewPagePanel
	 */
	public VerticalPanel getNewPagePanel() {
		currentPagePanel = WidgetFactory.createPagePanel();
		return currentPagePanel;
	}

	public VerticalPanel getPagePanel() {
		return currentPagePanel;
	}

	public SearchBox getSearchBox() {
		return searchBox;
	}

	public void setHeaderVisible(boolean visible) {
		if (isMobileBrowser) {
			headerPanel.setVisible(visible);
			return;
		}

		if (visible == headerIsVisible) {
			return;
		}
		if (!visible) {
			dock.setWidgetSize(headerPanel, 0);
			if (menu != null) {
				dock.setWidgetSize(menuPanel, 0);
			}
		} else {
			dock.setWidgetSize(headerPanel, HEADER_HEIGHT);
			if (menu != null) {
				dock.setWidgetSize(menuPanel, MENU_HEIGHT);
			}
		}
		dock.forceLayout();

		headerIsVisible = visible;
	}

	public void setPage(String title, VerticalPanel page) {
		if (currentPagePanel == page) {
			addPageToBodyPanel(title, page);
		}
	}

	private void addPageToBodyPanel(String title, VerticalPanel page) {
		HTML footer = new HTML("Copyright &copy; 2005-2013 " + Application.getCurrentOrg().getShortName() + ". All rights reserved.");
		footer.setStylePrimaryName("footer");

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(page);
		vp.add(footer);

		Application.setTitle(title);
		if (isMobileBrowser) {
			mobileBodyPanel.clear();
			mobileBodyPanel.setWidget(vp);
			Window.scrollTo(0, 0);
		} else {
			ap.clear();
			ap.add(vp);
			bodyPanel.scrollToTop();
		}

	}

}
