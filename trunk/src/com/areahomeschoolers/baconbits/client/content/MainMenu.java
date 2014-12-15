package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.document.FileUploadDialog;
import com.areahomeschoolers.baconbits.client.event.UploadCompleteHandler;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.GroupPolicy;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;

public final class MainMenu extends MenuBar {
	// private static native boolean isExternalUrl(String url) /*-{
	// var pattern = /(http|https|ftp|telnet):\/\//;
	// return pattern.test(url);
	// }-*/;

	private Timer closeTimer = new Timer() {
		@Override
		public void run() {
			closeAllChildren(false);
		}
	};

	public MainMenu() {
		// prevents auto-selection bug
		setFocusOnHoverEnabled(false);
		setAutoOpen(true);
		setAnimationEnabled(true);
		addMenuHandlers(this);

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

		setWidth("1%");
		getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);

		// addDynamicItems(Application.getApplicationData().getDynamicMenuItems(), this, null, 0);

		addLinkToMenu(this, "Books", PageUrl.tagGroup(TagMappingType.BOOK.toString()));
		addItem("Events", getEventsMenu());
		addLinkToMenu(this, "Resources", PageUrl.tagGroup(TagMappingType.RESOURCE.toString()));
		addLinkToMenu(this, "Blog", PageUrl.blog(0));

		if (Application.isAuthenticated()) {
			addLinkToMenu(this, "Homeschoolers", PageUrl.tagGroup(TagMappingType.USER.toString()));
		}

		if (Application.administratorOfCurrentOrg() || Application.administratorOf(17)) {
			addItem("Admin", getAdminMenu());
		}

	}

	@Override
	public MenuItem addItem(String text, final MenuBar popup) {
		addMenuHandlers(popup);

		popup.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				popup.getParent().getParent().getElement().getStyle().setMarginTop(1, Unit.PX);
			}
		});
		return super.addItem(text, popup);
	}

	private void addCommandToMenu(MenuBar menu, String text, ScheduledCommand command) {
		MenuItem mi = menu.addItem(text, command);
		mi.addStyleName("menuCommandItem");
	}

	// private void addDynamicItems(final List<MainMenuItem> items, MenuBar parentMenu, final MainMenuItem parentItem, int depth) {
	// for (MainMenuItem item : items) {
	// if (item.isSubMenu()) {
	// final MenuBar subMenu = new MenuBar(true);
	// addMenuHandlers(subMenu);
	// addDynamicItems(item.getItems(), subMenu, item, depth + 1);
	// MenuItem mi = parentMenu.addItem(item.getName(), subMenu);
	// mi.addStyleName("menuCommandItem");
	// } else {
	// String url = "";
	// if (item.getArticleIds() != null) {
	// url = PageUrl.articleGroup(item.getArticleIds());
	// } else {
	// url = item.getUrl();
	// }
	// addLinkToMenu(parentMenu, item.getName(), url);
	// }
	// }
	//
	// if (Application.administratorOfCurrentOrg()) {
	// ScheduledCommand scm = new ScheduledCommand() {
	// @Override
	// public void execute() {
	// MainMenuEditDialog dialog = new MainMenuEditDialog(items, parentItem);
	// dialog.center();
	// }
	// };
	//
	// if (depth > 0) {
	// parentMenu.addSeparator();
	// parentMenu.addItem("Edit This Menu", scm);
	// }
	// }
	// }

	private void addLinkToMenu(MenuBar mb, String name, String url) {
		url = url.startsWith("page=") ? Url.getBaseUrl() + "#" + url : url;
		Anchor link = new Anchor(name, url);
		if (url.startsWith("page=")) {
			link.setTarget("_blank");
		}
		link.addStyleName("menuLink");
		if (mb.equals(this)) {
			link.addStyleName("rootMenuLink");
		}

		MenuItem mi = mb.addItem(link.toString(), true, new ScheduledCommand() {
			@Override
			public void execute() {
			}
		});

		mi.getElement().getStyle().setPadding(0, Unit.PX);
	}

	private void addMenuHandlers(MenuBar menu) {
		menu.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				closeTimer.cancel();
			}
		}, MouseOverEvent.getType());

		menu.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				closeTimer.schedule(300);
			}
		}, MouseOutEvent.getType());
	}

	private void addMenuToMenu(MenuBar menu, String text, final MenuBar subMenu) {
		MenuItem mi = menu.addItem(text, subMenu);
		mi.addStyleName("menuCommandItem");

		subMenu.addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				subMenu.getParent().getParent().addStyleName("menuTopBorder");
			}
		});

		addMenuHandlers(subMenu);
	}

	private MenuBar getAdminMenu() {
		final MenuBar menu = new MenuBar(true);

		if (Application.administratorOfCurrentOrg()) {
			addLinkToMenu(menu, "Add Resource", PageUrl.resource(0));
			addLinkToMenu(menu, "Add Event", PageUrl.event(0));
			addLinkToMenu(menu, "Add Article", PageUrl.article(0));
			addLinkToMenu(menu, "Add User", PageUrl.user(0));
			addLinkToMenu(menu, "Article Management", PageUrl.articleManagement());
			addLinkToMenu(menu, "Group Management", PageUrl.userGroupList());
			addLinkToMenu(menu, "Book Seller Summary", PageUrl.bookManagement());
		}

		if (Application.administratorOf(17)) {
			addLinkToMenu(menu, "Create Book Receipt", PageUrl.bookReceipt());
		}

		if (Application.isSystemAdministrator()) {
			addLinkToMenu(menu, "Tag Management", PageUrl.tagManagement());
			addLinkToMenu(menu, "Resource Management", PageUrl.resourceManagement());
			menu.addSeparator();

			addCommandToMenu(menu, "Change Logo", new ScheduledCommand() {
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

		if (Application.administratorOfCurrentOrg()) {
			addCommandToMenu(menu, "Edit Main Menu", new ScheduledCommand() {
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

			addMenuToMenu(menu, "Group Policies", pol);
		}

		if (Application.isSystemAdministrator()) {
			menu.addSeparator();

			addCommandToMenu(menu, "Reload Page", new ScheduledCommand() {
				@Override
				public void execute() {
					Application.reloadPage();
				}
			});
		}

		return menu;
	}

	private MenuBar getEventsMenu() {
		MenuBar menu = new MenuBar(true);
		addLinkToMenu(menu, "Event Listing", PageUrl.eventList());
		addLinkToMenu(menu, "Calendar", PageUrl.eventCalendar());

		if (Application.isAuthenticated()) {
			menu.addSeparator();
			addLinkToMenu(menu, "Add Event", PageUrl.event(0));
		}

		if (Application.hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS)) {
			addLinkToMenu(menu, "Registration Management", PageUrl.registrationManagement());
		}

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