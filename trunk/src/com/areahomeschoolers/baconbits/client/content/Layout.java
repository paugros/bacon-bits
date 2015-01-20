package com.areahomeschoolers.baconbits.client.content;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
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
import com.areahomeschoolers.baconbits.client.widgets.DropDownMenu;
import com.areahomeschoolers.baconbits.client.widgets.HtmlSuggestion;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog.LoginHandler;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.client.widgets.StatusPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
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

		@Override
		public Double getWidgetSize(Widget widget) {
			LayoutData ld = (LayoutData) widget.getLayoutData();
			if (ld == null) {
				return 0.0;
			}
			return ld.size;
		}
	}

	private static final int HEADER_HEIGHT = 89;
	private final MainLayoutDock dock = new MainLayoutDock(Unit.PX);
	private final SimplePanel mobileBodyPanel = new SimplePanel();
	private final MainMenu menu;
	private final ScrollPanel bodyPanel = new ScrollPanel();
	private final AbsolutePanel ap = new AbsolutePanel();
	private boolean headerIsVisible = true;
	// Holds a reference to the latest panel queued to be displayed on the page when setPage is called
	private VerticalPanel currentPagePanel;
	private final HorizontalPanel headerPanel = new HorizontalPanel();
	private boolean isMobileBrowser = false;
	private HTML logoDiv;

	public Layout() {
		isMobileBrowser = ClientUtils.isMobileBrowser();
		headerPanel.setWidth("100%");
		headerPanel.addStyleName("headerPanel");
		headerPanel.getElement().getStyle().setOverflowX(Overflow.AUTO);
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
		logoDiv.getElement().getStyle().setMarginRight(20, Unit.PX);
		setLogo(Application.getCurrentOrg().getLogoId());

		headerPanel.add(logoDiv);
		headerPanel.setCellWidth(logoDiv, "1%");

		if (!Application.isCitrus()) {
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
		}

		menu = new MainMenu();
		headerPanel.add(menu);
		headerPanel.setCellVerticalAlignment(menu, HasVerticalAlignment.ALIGN_BOTTOM);
		// headerPanel.setCellWidth(menu, "100%");

		final PaddedPanel searchPanel = new PaddedPanel();
		Image search = new Image(MainImageBundle.INSTANCE.searchLarge());
		search.getElement().getStyle().setMarginRight(2, Unit.PX);

		Label searchText = new Label("Search");
		searchText.getElement().getStyle().setColor("#555555");
		searchText.getElement().getStyle().setFontSize(14, Unit.PX);
		searchText.getElement().getStyle().setFontWeight(FontWeight.BOLD);

		searchPanel.getElement().getStyle().setCursor(Cursor.POINTER);
		searchPanel.add(search);
		searchPanel.setCellVerticalAlignment(search, HasVerticalAlignment.ALIGN_MIDDLE);
		searchPanel.add(searchText);
		searchPanel.setCellVerticalAlignment(searchText, HasVerticalAlignment.ALIGN_MIDDLE);

		final PaddedPanel rightPanel = new PaddedPanel(15);

		searchPanel.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final SearchBox searchBox = new SearchBox();

				rightPanel.insert(searchBox, rightPanel.getWidgetIndex(searchPanel));
				searchPanel.removeFromParent();

				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						searchBox.setFocus(true);
					}
				});

				searchBox.addBlurHandler(new BlurHandler() {
					@Override
					public void onBlur(BlurEvent event) {
						rightPanel.insert(searchPanel, rightPanel.getWidgetIndex(searchBox));
						searchBox.removeFromParent();
					}
				});

				searchBox.setSelectionHandler(new Command() {
					@Override
					public void execute() {
						if (searchBox.isAttached()) {
							rightPanel.insert(searchPanel, rightPanel.getWidgetIndex(searchBox));
							searchBox.removeFromParent();
						}
					}
				});
			}
		}, ClickEvent.getType());

		rightPanel.add(searchPanel);
		headerPanel.add(rightPanel);
		headerPanel.setCellHorizontalAlignment(rightPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		rightPanel.setCellVerticalAlignment(searchPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		headerPanel.setCellVerticalAlignment(rightPanel, HasVerticalAlignment.ALIGN_MIDDLE);

		if (Application.isAuthenticated()) {
			final DropDownMenu dm = new DropDownMenu(Application.getCurrentUser().getFirstName());
			dm.addItem("My Profile", PageUrl.user(Application.getCurrentUserId()));
			dm.addItem("Log out", new Command() {
				@Override
				public void execute() {
					LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
					loginService.logout(new Callback<Void>(false) {
						@Override
						protected void doOnSuccess(Void result) {
							Window.Location.reload();
						}
					});
				}
			});

			if (Application.canSwitchUser()) {
				dm.addItem("Switch User", new Command() {
					@Override
					public void execute() {
						final DecoratedPopupPanel popup = new DecoratedPopupPanel();
						popup.setAutoHideEnabled(true);

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

						PaddedPanel pp = new PaddedPanel();
						Label text = new Label("Switch to:");
						pp.add(text);
						pp.setCellVerticalAlignment(text, HasVerticalAlignment.ALIGN_MIDDLE);
						pp.add(userSearchBox);
						pp.getElement().getStyle().setMargin(20, Unit.PX);
						popup.setWidget(pp);

						popup.showRelativeTo(dm);
						popup.setPopupPosition(popup.getAbsoluteLeft(), popup.getAbsoluteTop() + 4);

						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							@Override
							public void execute() {
								userSearchBox.getTextBox().setFocus(true);
								popup.setPopupPosition(popup.getAbsoluteLeft(), popup.getAbsoluteTop() + 2);
							}
						});
					}
				});
			}
			rightPanel.add(dm);
			rightPanel.setCellVerticalAlignment(dm, HasVerticalAlignment.ALIGN_MIDDLE);
		} else {
			final ClickLabel login = new ClickLabel("Log in / Create account");
			login.getElement().getStyle().setMarginRight(15, Unit.PX);
			login.addClickHandler(new ClickHandler() {
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
						ld.showRelativeTo(login);
					} else {
						ld.center();
					}
				}
			});

			login.setWordWrap(false);
			rightPanel.add(login);
			rightPanel.setCellVerticalAlignment(login, HasVerticalAlignment.ALIGN_MIDDLE);
		}

		if (!isMobileBrowser) {
			ap.setHeight("100%");
			ap.getElement().getStyle().setOverflow(Overflow.VISIBLE);
			bodyPanel.setStyleName("bodyPanel");
			bodyPanel.add(ap);
		}

		if (Application.isAuthenticated()) {
			// BalanceBox bb = new BalanceBox();
			// menuPanel.add(bb);
		}

		if (isMobileBrowser) {
			mobileBodyPanel.addStyleName("bodyPanel");
			mobileBodyPanel.setWidth("100%");
			VerticalPanel vvp = new VerticalPanel();
			vvp.setWidth("100%");
			vvp.add(headerPanel);
			vvp.add(mobileBodyPanel);
			RootPanel.get().add(vvp);
		} else {
			dock.addStyleName("Dock");
			dock.addNorth(headerPanel, HEADER_HEIGHT + 1);
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
		} else {
			dock.setWidgetSize(headerPanel, HEADER_HEIGHT);
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
			DefaultHyperlink link = new DefaultHyperlink(items.get(id), PageUrl.article(id));
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
