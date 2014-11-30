package com.areahomeschoolers.baconbits.client.content;

import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.document.FileUploadDialog;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.ResetPasswordDialog;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.MainMenuItem;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.GroupPolicy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public final class MainMenu extends MenuBar {
	public static void addLinkToMenu(MenuBar mb, String name, String url) {
		String item;

		if (isExternalUrl(url)) {
			item = "<a href=\"" + url + "\" target=\"_blank\" class=menuLink>" + name + "</a>";
		} else {
			item = "<a href=\"" + Url.getGwtCodeServerAsQueryString() + "#" + url + "\" class=menuLink>" + name + "</a>";
		}
		MenuItem mi = mb.addItem(item, true, new ScheduledCommand() {
			@Override
			public void execute() {
			}
		});

		mi.getElement().getStyle().setPadding(0, Unit.PX);
	}

	private static native boolean isExternalUrl(String url) /*-{
		var pattern = /(http|https|ftp|telnet):\/\//;
		return pattern.test(url);
	}-*/;

	private final User user = Application.getCurrentUser();

	public MainMenu() {
		// prevents auto-selection bug
		setFocusOnHoverEnabled(false);
		addStyleName("MainMenu");
		addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						handleClose();
					}
				});
			}
		});

		addDynamicItems(Application.getApplicationData().getDynamicMenuItems(), this, null, 0);

		addItem("News", getBlogMenu());
		addItem("Events", getEventsMenu());
		addItem("Book Store", getBooksMenu());

		if (Application.isAuthenticated()) {
			addItem("Members", getMembersMenu());
		}

		if (Application.isAuthenticated()) {
			addItem("My Items", getMyItemsMenu());
		}

		if (Application.administratorOfCurrentOrg()) {
			addItem("Admin", getAdminMenu());
		}

	}

	@Override
	public MenuItem addItem(String text, final MenuBar popup) {
		popup.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				popup.getParent().getParent().getElement().getStyle().setMarginTop(11, Unit.PX);
			}
		});
		return super.addItem(text, popup);
	}

	private void addDynamicItems(final List<MainMenuItem> items, MenuBar parentMenu, final MainMenuItem parentItem, int depth) {
		for (MainMenuItem item : items) {
			if (item.isSubMenu()) {
				final MenuBar menu = new MenuBar(true);
				addDynamicItems(item.getItems(), menu, item, depth + 1);
				parentMenu.addItem(item.getName(), menu);
			} else {
				String url = "";
				if (item.getArticleIds() != null) {
					url = PageUrl.articleGroup(item.getArticleIds());
				} else {
					url = item.getUrl();
				}
				addLinkToMenu(parentMenu, item.getName(), url);
			}
		}

		if (Application.administratorOfCurrentOrg()) {
			ScheduledCommand scm = new ScheduledCommand() {
				@Override
				public void execute() {
					MainMenuEditDialog dialog = new MainMenuEditDialog(items, parentItem);
					dialog.center();
				}
			};

			if (depth > 0) {
				parentMenu.addSeparator();
				parentMenu.addItem("Edit This Menu", scm);
			}
		}
	}

	private MenuBar getAdminMenu() {
		MenuBar menu = new MenuBar(true);

		addLinkToMenu(menu, "Add Article", PageUrl.article(0));
		addLinkToMenu(menu, "List Articles", PageUrl.articleManagement());
		addLinkToMenu(menu, "Add User", PageUrl.user(0));
		addLinkToMenu(menu, "List Groups", PageUrl.userGroupList());

		if (Application.isSystemAdministrator()) {
			addLinkToMenu(menu, "Resource Management", PageUrl.resourceManagement());
			addLinkToMenu(menu, "Tag Management", PageUrl.tagManagement());
			menu.addItem("Change Logo", new ScheduledCommand() {
				@Override
				public void execute() {
					final FileUploadDialog dialog = new FileUploadDialog(DocumentLinkType.LOGO, Application.getCurrentOrgId(), false,
							new UploadCompleteHandler() {
								@Override
								public void onUploadComplete(int documentId) {
									Application.getCurrentOrg().setLogoId(documentId);
									Application.getLayout().setLogo(documentId);
								}
							});

					dialog.getForm().addFormValidatorCommand(new ValidatorCommand() {
						@Override
						public void validate(Validator validator) {
							String fileName = dialog.getFileName();
							if (Common.isNullOrBlank(fileName)) {
								validator.setError(true);
							}

							if (!Document.hasImageExtension(fileName)) {
								validator.setError(true);
								validator.setErrorMessage("Invalid image file.");
							}
						}
					});

					dialog.center();
				}
			});
		}

		menu.addItem("Edit Main Menu", new ScheduledCommand() {
			@Override
			public void execute() {
				MainMenuEditDialog dialog = new MainMenuEditDialog(Application.getApplicationData().getDynamicMenuItems(), null);
				dialog.center();
			}
		});

		MenuBar pol = new MenuBar(true);
		for (GroupPolicy gp : GroupPolicy.values()) {
			int id = Application.getCurrentOrg().getPolicyId(gp);
			String url = PageUrl.article(id);
			if (id == 0) {
				url += "&gp=" + gp.toString();
			}
			addLinkToMenu(pol, gp.getTitle(), url);
		}
		menu.addItem("Group Policies", pol);

		menu.addSeparator();

		menu.addItem("Reload Page", new ScheduledCommand() {
			@Override
			public void execute() {
				Application.reloadPage();
			}
		});

		return menu;
	}

	private MenuBar getBlogMenu() {
		MenuBar menu = new MenuBar(true);

		addLinkToMenu(menu, "News Blog", PageUrl.blog(0));
		addLinkToMenu(menu, "Events Calendar", PageUrl.eventCalendar());

		if (Application.hasRole(AccessLevel.BLOG_CONTRIBUTORS)) {
			menu.addSeparator();
			addLinkToMenu(menu, "Add Blog Post", PageUrl.blog(0) + "&postId=0");
		}
		return menu;
	}

	private MenuBar getBooksMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Search Books", PageUrl.bookList());
		if (Application.isAuthenticated() && Application.getCurrentUser().administratorOf(Application.getCurrentOrgId())) {
			addLinkToMenu(menu, "Book Seller Summary", PageUrl.bookManagement());
		}

		if (Application.administratorOf(17)) {
			addLinkToMenu(menu, "Create Book Receipt", PageUrl.bookReceipt());
		}

		return menu;
	}

	private MenuBar getEventsMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Event Listing", PageUrl.eventList());
		addLinkToMenu(menu, "Calendar", PageUrl.eventCalendar());

		menu.addSeparator();
		addLinkToMenu(menu, "Add Event", PageUrl.event(0));

		if (Application.hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS)) {
			addLinkToMenu(menu, "Registration Management", PageUrl.registrationManagement());
		}

		return menu;
	}

	private MenuBar getMembersMenu() {
		MenuBar menu = new MenuBar(true);

		addLinkToMenu(menu, "Member Directory", PageUrl.userList());
		menu.addSeparator();
		addLinkToMenu(menu, "My Profile", PageUrl.user(Application.getCurrentUserId()));
		if (!Application.getCurrentUser().isChild()) {
			addLinkToMenu(menu, "My Family", PageUrl.user(Application.getCurrentUserId()) + "&tab=3");
		}
		addLinkToMenu(menu, "Privacy Settings", PageUrl.user(Application.getCurrentUserId()) + "&tab=7");
		return menu;
	}

	private MenuBar getMyItemsMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Profile", PageUrl.user(Application.getCurrentUserId()));
		addLinkToMenu(menu, "Events", PageUrl.user(Application.getCurrentUserId()) + "&tab=1");
		if (!Application.getCurrentUser().isChild()) {
			addLinkToMenu(menu, "Family", PageUrl.user(Application.getCurrentUserId()) + "&tab=3");
		}
		if (Application.getCurrentUser().memberOf(Constants.ONLINE_BOOK_SELLERS_GROUP_ID)) {
			addLinkToMenu(menu, "Books", PageUrl.user(Application.getCurrentUserId()) + "&tab=4");
		}
		if (!Application.getCurrentUser().isChild()) {
			addLinkToMenu(menu, "Payments", PageUrl.user(Application.getCurrentUserId()) + "&tab=5");
		}
		addLinkToMenu(menu, "Calendar", PageUrl.user(Application.getCurrentUserId()) + "&tab=6");
		addLinkToMenu(menu, "Privacy", PageUrl.user(Application.getCurrentUserId()) + "&tab=7");

		if (!Application.getCurrentUser().isChild()) {
			addLinkToMenu(menu, "Shopping Cart", PageUrl.payment());
		}

		menu.addSeparator();

		menu.addItem("Change Password", new ScheduledCommand() {
			@Override
			public void execute() {
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

		menu.addItem("Log Out", new ScheduledCommand() {
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

		return menu;
	}

	private void handleClose() {
		MenuItem item = getSelectedItem();
		if (item == null) {
			return;
		}

		MenuBar subMenu = item.getSubMenu();
		if (subMenu == null) {
			return;
		}

		if (!subMenu.isAttached()) {
			selectItem(null);
		}
	}
}