package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DropDownMenu extends Composite implements MouseDownHandler, NativePreviewHandler {

	private final VerticalPanel popup = new VerticalPanel();

	private final FocusPanel fp;
	private HandlerRegistration hr;
	// private HorizontalAlignmentConstant alignment = HasHorizontalAlignment.ALIGN_RIGHT;
	private boolean isOpen = false;

	public DropDownMenu() {
		this(true);
	}

	public DropDownMenu(boolean hasLabel) {
		this(hasLabel ? "Options" : null);
	}

	public DropDownMenu(String label) {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		if (!com.areahomeschoolers.baconbits.shared.Common.isNullOrBlank(label)) {
			Label optLabel = new Label(label);
			optLabel.getElement().getStyle().setPaddingLeft(4, Unit.PX);
			hp.add(optLabel);
		}

		Image arrow = new Image(MainImageBundle.INSTANCE.sortDescending());
		arrow.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		hp.add(arrow);

		fp = new FocusPanel(hp);
		fp.setStylePrimaryName("DropDownMenu");
		initWidget(fp);

		fp.addMouseDownHandler(this);
		popup.setStylePrimaryName("DropDownMenu-popup");
	}

	public ClickLabel addItem(ClickLabel link) {
		link.getElement().addClassName("item");
		popup.add(link);
		return link;
	}

	public Hyperlink addItem(Hyperlink link) {
		AnchorElement ae = (AnchorElement) link.getElement().getChild(0);
		ae.addClassName("item");
		popup.add(link);
		return link;
	}

	public Label addItem(String text, final Command cmd) {
		Label item = createItem(text, cmd);
		popup.add(item);
		return item;
	}

	public void addSeparator() {
		HTML hr = new HTML("<hr>");
		popup.add(hr);
		return;
	}

	public void hide() {
		if (isOpen) {
			isOpen = false;
			fp.removeStyleDependentName("open");
			// Application.getLayout().removePositionOnPage(popup);
			hr.removeHandler();
		}
	}

	public Label insertItem(String text, final Command cmd, int beforeIndex) throws IndexOutOfBoundsException {
		Label item = createItem(text, cmd);
		popup.insert(item, beforeIndex);
		return item;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		show();
	}

	@Override
	public void onPreviewNativeEvent(final NativePreviewEvent event) {
		Event nativeEvent = Event.as(event.getNativeEvent());
		if (nativeEvent.getTypeInt() == Event.ONMOUSEDOWN) {

			if (!eventTargetsPopup(nativeEvent)) {
				hide();
			}
		}
	}

	// public void setHorizontalAlignment(HorizontalAlignmentConstant alignment) {
	// this.alignment = alignment;
	// }

	public void show() {
		if (!isOpen) {
			isOpen = true;
			fp.addStyleDependentName("open");
			// Application.getLayout().positionRelativeTo(popup, fp, alignment);
			hr = Event.addNativePreviewHandler(DropDownMenu.this);
		}
	}

	private Label createItem(final String text, final Command cmd) {
		final Label item = new Label(text);
		item.setStylePrimaryName("item");
		item.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						hide();
						cmd.execute();

						// hack to get IE to stop highlighting selected item
						Label dup = createItem(text, cmd);
						popup.insert(dup, popup.getWidgetIndex(item));
						popup.remove(item);
					}
				});
			}
		});

		return item;
	}

	private boolean eventTargetsPopup(NativeEvent event) {
		EventTarget target = event.getEventTarget();
		if (Element.is(target)) {
			return popup.getElement().isOrHasChild(Element.as(target));
		}
		return false;
	}
}
