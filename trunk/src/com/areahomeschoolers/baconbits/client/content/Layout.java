package com.areahomeschoolers.baconbits.client.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.event.BalanceBox;
import com.areahomeschoolers.baconbits.client.content.minimodules.CitrusMiniModule;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.LinkPanel;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog.LoginHandler;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.StatusPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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

	private static final int HEADER_HEIGHT = 54;
	private static final int MENU_HEIGHT = 35;
	private static final int LOGO_DIV_WIDTH = 191;
	private final MainLayoutDock dock = new MainLayoutDock(Unit.PX);
	private final SearchBox searchBox;
	private final SimplePanel mobileBodyPanel = new SimplePanel();
	private final MainMenu menu;
	private final ScrollPanel bodyPanel = new ScrollPanel();
	private final AbsolutePanel ap = new AbsolutePanel();
	private boolean headerIsVisible = true;
	// Holds a reference to the latest panel queued to be displayed on the page when setPage is called
	private VerticalPanel currentPagePanel;
	private final HorizontalPanel headerPanel = new HorizontalPanel();
	private final HorizontalPanel menuPanel = new HorizontalPanel();
	private boolean isMobileBrowser = false;
	private HTML logoDiv;

	/**
	 *
	 */
	public Layout() {
		isMobileBrowser = ClientUtils.isMobileBrowser();

		headerPanel.setHeight(HEADER_HEIGHT + "px");
		headerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		if (!isMobileBrowser) {
			RootLayoutPanel.get().setStyleName("overflowHidden");
		}

		// status panel
		StatusPanel sp = new StatusPanel();
		Callback.setStatusPanel(sp);
		RootPanel.get().add(sp);

		logoDiv = new HTML();
		setLogo(Application.getCurrentOrg().getLogoId());

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

		SimplePanel spacer = new SimplePanel();
		headerPanel.add(spacer);
		headerPanel.setCellWidth(spacer, "100%");

		searchBox = new SearchBox();

		headerPanel.add(searchBox);

		LinkPanel sessionPanel = new LinkPanel();
		sessionPanel.addStyleName("sessionPanel");

		headerPanel.add(sessionPanel);

		final ClickLabel logInOrOut = new ClickLabel();
		if (Application.isAuthenticated()) {
			HorizontalPanel upn = new PaddedPanel(4);
			final Hyperlink name = new Hyperlink("Hello, " + Application.getCurrentUser().getFirstName(), PageUrl.user(Application.getCurrentUserId()));
			name.addStyleName("nowrap");

			if (Application.canSwitchUser()) {
				final UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
				final EntitySuggestBox userSearchBox = new EntitySuggestBox("User");
				userSearchBox.setSelectionHandler(new ParameterHandler<HtmlSuggestion>() {
					@Override
					public void execute(HtmlSuggestion sug) {
						userService.switchToUser(sug.getEntityId(), new Callback<Void>() {
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
						name.setVisible(true);
						userSearchBox.setVisible(false);
						userSearchBox.getTextBox().setFocus(false);
					}
				});
				userSearchBox.getElement().getStyle().setMarginLeft(20, Unit.PX);
				userSearchBox.setClearOnFocus(true);

				userSearchBox.setVisible(false);
				Image swap = new Image(MainImageBundle.INSTANCE.swap());
				swap.setTitle("Switch user");
				swap.getElement().getStyle().setCursor(Cursor.POINTER);
				swap.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						boolean show = name.isVisible();

						if (show) {
							name.setVisible(false);
							userSearchBox.setVisible(true);
							userSearchBox.getTextBox().setFocus(true);
						} else {
							name.setVisible(true);
							userSearchBox.setVisible(false);
							userSearchBox.getTextBox().setFocus(false);
						}
					}
				});

				upn.add(swap);
				upn.add(userSearchBox);
			}

			upn.add(name);
			sessionPanel.add(upn);

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

		menu = new MainMenu();
		menuPanel.setHeight(MENU_HEIGHT + "px");
		menuPanel.setWidth("100%");
		menuPanel.add(menu);
		menuPanel.setCellWidth(menu, "100%");

		if (Application.isAuthenticated()) {
			BalanceBox bb = new BalanceBox();
			menuPanel.add(bb);
		}

		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");
		hp.add(logoDiv);
		hp.setCellHorizontalAlignment(logoDiv, HasHorizontalAlignment.ALIGN_CENTER);
		hp.setCellVerticalAlignment(logoDiv, HasVerticalAlignment.ALIGN_MIDDLE);
		hp.setCellWidth(logoDiv, LOGO_DIV_WIDTH + "px");
		hp.setHeight(HEADER_HEIGHT + MENU_HEIGHT + "px");
		hp.addStyleName("headerPanel");

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(headerPanel);
		vp.add(menuPanel);
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
			dock.addNorth(hp, HEADER_HEIGHT + MENU_HEIGHT + 1);
			dock.add(bodyPanel);
			RootLayoutPanel.get().add(dock);
		}
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

	public void setLogo(Integer documentId) {
		String text = "";

		if (Application.isCitrus() || documentId == null) {
			CitrusMiniModule cm = new CitrusMiniModule();
			cm.getElement().getStyle().setMarginLeft(20, Unit.PX);
			text += cm;
		} else {
			text += "<a href=\"" + PageUrl.home() + "\">";
			text += new Image(Constants.DOCUMENT_URL_PREFIX + documentId);
			text += "</a>";
		}

		logoDiv.setHTML(text);
	}

	public void setPage(String title, Sidebar sidebar, VerticalPanel page) {
		if (currentPagePanel == page) {
			Grid pageGrid = new Grid(1, 2);
			pageGrid.setWidth("100%");
			pageGrid.setWidget(0, 0, sidebar);
			pageGrid.setWidget(0, 1, page);
			pageGrid.getCellFormatter().setWidth(0, 0, "250px");
			pageGrid.getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
			pageGrid.getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
			addPageToBodyPanel(title, pageGrid);
		}
	}

	public void setPage(String title, VerticalPanel page) {
		if (currentPagePanel == page) {
			addPageToBodyPanel(title, page);
		}
	}

	private void addPageToBodyPanel(String title, Widget page) {
		String yr = Formatter.formatDate(new Date(), "yyyy");
		HTML copyright = new HTML("Copyright &copy; 2013-" + yr + " Citrus Groups. All rights reserved.");
		copyright.setStylePrimaryName("footer");

		Map<Integer, String> items = new LinkedHashMap<>();
		items.put(83, "About Us");
		items.put(78, "Services");
		items.put(101, "Advertise With Us");
		items.put(76, "FAQs");
		items.put(84, "Contact Us");

		String txt = "";

		List<String> linkItems = new ArrayList<>();
		for (Integer id : items.keySet()) {
			Hyperlink link = new Hyperlink(items.get(id), PageUrl.article(id));
			linkItems.add(link.toString());
		}
		txt += Common.join(linkItems, "&nbsp;&nbsp;|&nbsp;&nbsp;");

		String legalText = "<a href=\"" + Constants.TOS_URL + "&noTitle=true\">Terms of service</a>&nbsp;&nbsp;|&nbsp;&nbsp;";
		legalText += "<a href=\"" + Constants.PRIVACY_POLICY_URL + "&noTitle=true\">Privacy policy</a>";

		HTML links = new HTML(txt);
		links.setStylePrimaryName("footer");

		HTML legal = new HTML(legalText);
		legal.setStylePrimaryName("footer");

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.add(page);
		vp.add(links);
		vp.add(copyright);
		vp.add(legal);

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
