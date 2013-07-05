package com.areahomeschoolers.baconbits.client.content;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.event.ParameterHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.ConfirmDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.DndVerticalPanel;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.LinkPanel;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.MainMenuItem;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MainMenuEditDialog extends DefaultDialog {
	private VerticalPanel vp = new VerticalPanel();
	private ButtonPanel bp = new ButtonPanel(this);
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private List<MainMenuItem> items;
	private DndVerticalPanel<MainMenuItem> dnd = new DndVerticalPanel<MainMenuItem>(new ParameterHandler<List<MainMenuItem>>() {
		@Override
		public void execute(List<MainMenuItem> items) {
			saveItemsOrder(items);
		}
	});
	private MenuItemEditDialog dialog = new MenuItemEditDialog();
	private boolean changed;

	public MainMenuEditDialog(final List<MainMenuItem> menuItems, final MainMenuItem parent) {
		bp.getCloseButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (changed) {
					Window.Location.reload();
				}
			}
		});

		String text = "Edit Menu Items";
		if (parent != null) {
			text = parent.getName();
		}
		setText(text);
		items = menuItems;
		dnd.setWidth("400px");
		dnd.setSpacing(4);
		dialog.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				show();
			}
		});

		dialog.addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				changed = true;
				final boolean drillDown = !dialog.getEntity().isSaved() && dialog.getEntity().isSubMenu();
				userService.saveMenuItem(dialog.getEntity(), new Callback<MainMenuItem>() {
					@Override
					protected void doOnSuccess(MainMenuItem result) {
						addItem(result);

						if (drillDown) {
							hide();

							MainMenuEditDialog md = new MainMenuEditDialog(new ArrayList<MainMenuItem>(), result);
							md.center();
						}
					}
				});
			}
		});

		ClickLabel add = new ClickLabel("+ Add new item", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MainMenuItem item = new MainMenuItem();
				if (parent != null) {
					item.setParentNodeId(parent.getId());
				}
				item.setOrdinal(dnd.getWidgetCount());

				hide();
				dialog.center(item);
			}
		});

		add.addStyleName("bold mediumPadding");

		vp.add(add);
		vp.setCellHorizontalAlignment(add, HasHorizontalAlignment.ALIGN_RIGHT);

		Label desc = new Label("Click and drag items to rearrange.");
		desc.addStyleName("smallText mediumPadding");
		vp.add(desc);

		vp.add(dnd);
		vp.add(bp);

		for (MainMenuItem item : items) {
			addItem(item);
		}

		setWidget(vp);
	}

	private void addItem(final MainMenuItem item) {
		final HorizontalPanel hp = new PaddedPanel();
		LinkPanel lp = new LinkPanel();
		ClickLabel edit = new ClickLabel("Edit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialog.center(item);
			}
		});
		ClickLabel delete = new ClickLabel("X", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ConfirmDialog.confirm("Delete " + item.getName() + " from the menu?", new ConfirmHandler() {
					@Override
					public void onConfirm() {
						changed = true;
						userService.deleteMenuItem(item, new Callback<Void>() {
							@Override
							protected void doOnSuccess(Void result) {
								dnd.remove(hp);
							}
						});
					}
				});
			}
		});

		lp.add(edit);
		lp.add(delete);

		Label name = new Label(item.getName());
		name.addStyleName("mainMenuItemEdit");
		hp.add(name);
		hp.add(lp);
		hp.setCellHorizontalAlignment(lp, HasHorizontalAlignment.ALIGN_RIGHT);
		hp.setCellVerticalAlignment(lp, HasVerticalAlignment.ALIGN_MIDDLE);

		dnd.add(hp, name, item);
	}

	private void saveItemsOrder(List<MainMenuItem> orderedItems) {
		if (!orderedItems.isEmpty()) {
			changed = true;
			bp.getCloseButton().setEnabled(false);
			userService.updateMenuOrdinals(Common.asArrayList(orderedItems), new Callback<Void>(false) {
				@Override
				protected void doOnSuccess(Void result) {
					bp.getCloseButton().setEnabled(true);
				}
			});
		}
	}
}
