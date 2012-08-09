package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.generated.Instantiable;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.LinkPanel;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog.LoginHandler;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.ResetPasswordDialog;
import com.areahomeschoolers.baconbits.client.widgets.StatusPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.SidebarEntity;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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

		public double getWidgetSize(Widget widget) {
			LayoutData ld = (LayoutData) widget.getLayoutData();
			if (ld == null) {
				return 0;
			}
			return ld.size;
		}
	}

	private static int HEADER_HEIGHT = 52;
	private static int MENU_HEIGHT = 22;
	private static int SPLITTER_WIDTH = 9;
	private static int DEFAULT_SIDEBAR_WIDTH = 200;
	private static int sidebarWidth = DEFAULT_SIDEBAR_WIDTH;
	private final MainLayoutDock dock = new MainLayoutDock(Unit.PX);
	private final SearchBox searchBox;
	private final MainMenu menu;
	private final ScrollPanel sidePanel = new ScrollPanel();
	private final Grid splitter = new Grid(1, 1);
	private final Image arrow = new Image(MainImageBundle.INSTANCE.arrowLeft());
	private final ScrollPanel bodyPanel = new ScrollPanel();
	private final AbsolutePanel ap = new AbsolutePanel();
	// private final User currentUser = Application.getCurrentUser();
	private Widget sidebar;
	private final SimplePanel logoPanel = new SimplePanel();
	private boolean sbIsCollapsed = false;
	private boolean sbIsVisible = false;
	private boolean headerIsVisible = true;
	/**
	 * Holds a reference to the latest panel queued to be displayed on the page when setPage is called
	 */
	private VerticalPanel currentPagePanel;
	private final HorizontalPanel headerPanel;

	private final HorizontalPanel menuPanel;

	public Layout() {
		headerPanel = new HorizontalPanel();
		headerPanel.setStyleName("headerPanel");
		headerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		// logo
		headerPanel.add(logoPanel);
		PaddedPanel pp = new PaddedPanel();
		pp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		pp.add(new Image(MainImageBundle.INSTANCE.logo()));

		HTML logoDiv = new HTML("<a href=\"#" + PageUrl.home() + "\">" + pp + "</a>");
		logoDiv.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				if ("Home".equals(HistoryToken.getElement("page"))) {
					Application.reloadPage();
				}
			}
		});
		logoPanel.setWidget(logoDiv);

		SimplePanel spacer = new SimplePanel();
		headerPanel.add(spacer);
		headerPanel.setCellWidth(spacer, "100%");

		searchBox = new SearchBox();
		headerPanel.add(searchBox);

		LinkPanel sessionPanel = new LinkPanel();
		headerPanel.add(sessionPanel);
		sessionPanel.addStyleName("sessionPanel");

		ClickLabel logInOrOut = new ClickLabel();
		if (Application.isAuthenticated()) {
			Hyperlink name = new Hyperlink("Hello, " + Application.getCurrentUser().getFirstName(), PageUrl.user(Application.getCurrentUserId()));
			name.addStyleName("nowrap");
			sessionPanel.add(name);

			logInOrOut.setText("Log out");
			logInOrOut.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
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
			logInOrOut.setText("Log in");
			logInOrOut.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);

					if (!GWT.isProdMode()) {
						loginService.loginAndGetApplicationData("paul.augros@gmail.com", "Borrow99dolls?", new AsyncCallback<ApplicationData>() {
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
					ld.center();
				}
			});
		}
		logInOrOut.addStyleName("nowrap");
		sessionPanel.add(logInOrOut);

		if (Application.isAuthenticated()) {
			ClickLabel resetLabel = new ClickLabel("Change password", new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					GWT.runAsync(new RunAsyncCallback() {
						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess() {
							new ResetPasswordDialog(true).center();
						}
					});
				}
			});
			resetLabel.addStyleName("nowrap");
			sessionPanel.add(resetLabel);
		}

		ap.setHeight("100%");
		ap.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		bodyPanel.setStyleName("bodyPanel");
		bodyPanel.add(ap);

		sidePanel.setStyleName("sidePanel");
		sidePanel.setWidth(sidebarWidth + "px");
		splitter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setSidebarCollapsed(!sbIsCollapsed);
			}
		});
		splitter.setStyleName("splitter");
		splitter.setWidget(0, 0, arrow);

		menu = new MainMenu();
		menuPanel = new HorizontalPanel();
		menuPanel.setWidth("100%");
		menuPanel.add(menu);
		menuPanel.setCellWidth(menu, "100%");

		dock.addStyleName("Dock");
		dock.addNorth(headerPanel, HEADER_HEIGHT);
		dock.addNorth(menuPanel, MENU_HEIGHT);
		dock.addWest(sidePanel, 0);
		dock.addWest(splitter, 0);
		dock.add(bodyPanel);
		RootLayoutPanel.get().add(dock);

		// status panel
		StatusPanel sp = new StatusPanel();
		Callback.setStatusPanel(sp);
		RootPanel.get().add(sp);
	}

	public ScrollPanel getBodyPanel() {
		return bodyPanel;
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

	public ScrollPanel getScrollPanel() {
		return bodyPanel;
	}

	public SearchBox getSearchBox() {
		return searchBox;
	}

	public Widget getSidebar() {
		return sidebar;
	}

	public boolean getSidebarCollapsed() {
		return sbIsCollapsed;
	}

	public boolean getSidebarVisible() {
		return sbIsVisible;
	}

	public void setHeaderVisible(boolean visible) {
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
			setSidebarVisible(false);
			addPageToBodyPanel(title, page);
		}
	}

	public void setPage(String title, VerticalPanel page, Class<? extends Instantiable> sbType) {
		if (currentPagePanel == page) {
			String newSbType = Common.getSimpleClassName(sbType);
			String oldSbType = null;
			if (sidebar != null) {
				oldSbType = Common.getSimpleClassName(sidebar.getClass());
			}

			if (!newSbType.equals(oldSbType)) {
				sidebarWidth = DEFAULT_SIDEBAR_WIDTH;
				sidePanel.clear();
				dock.setWidgetSize(sidePanel, sidebarWidth);
				dock.forceLayout();
				sidePanel.setWidth("100%");

				// sidebar = (Composite) Application.getFactory().newInstance(newSbType);
				sidePanel.add(sidebar);
			}

			setSidebarVisible(true);
			addPageToBodyPanel(title, page);
		}
	}

	public void setPage(String title, VerticalPanel page, Widget customSidebar) {
		setPage(title, page, customSidebar, DEFAULT_SIDEBAR_WIDTH);
	}

	public void setPage(String title, VerticalPanel page, Widget customSidebar, int width) {
		sidebar = customSidebar;
		sidePanel.clear();
		sidePanel.add(customSidebar);
		sidePanel.setWidth("100%");

		sbIsCollapsed = false;
		sbIsVisible = true;

		addPageToBodyPanel(title, page);

		sidebarWidth = width;
		dock.setWidgetSize(sidePanel, width);
		dock.setWidgetSize(splitter, SPLITTER_WIDTH);
		dock.forceLayout();
	}

	public void setSidebarCollapsed(boolean collapsed) {
		if (collapsed == sbIsCollapsed) {
			return;
		}

		if (collapsed) {
			dock.setWidgetSize(sidePanel, 0);
			arrow.setResource(MainImageBundle.INSTANCE.arrowRight());
		} else {
			dock.setWidgetSize(sidePanel, sidebarWidth);
			arrow.setResource(MainImageBundle.INSTANCE.arrowLeft());
		}
		dock.forceLayout();
		sbIsCollapsed = collapsed;
	}

	@SuppressWarnings("unchecked")
	public <T extends SidebarEntity> void setSideBarEntity(T entity) {
		if (sidebar instanceof EntitySidebar<?>) {
			((EntitySidebar<T>) sidebar).setEntity(entity);
		}
	}

	public void setSidebarVisible(boolean visible) {
		if (!visible) {
			dock.setWidgetSize(sidePanel, 0);
			dock.setWidgetSize(splitter, 0);
			dock.forceLayout();
		} else {
			if (!sbIsCollapsed) {
				dock.setWidgetSize(sidePanel, sidebarWidth);
			}
			dock.setWidgetSize(splitter, SPLITTER_WIDTH);
			dock.forceLayout();
		}
		sbIsVisible = visible;
	}

	private void addPageToBodyPanel(String title, VerticalPanel page) {
		HTML footer = new HTML("&copy; 2005-2012 WHE. All rights reserved. Proprietary & Confidential.");
		footer.setStylePrimaryName("footer");

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(page);
		vp.add(footer);

		Application.setTitle(title);
		ap.clear();
		ap.add(vp);

		bodyPanel.scrollToTop();
	}

}
