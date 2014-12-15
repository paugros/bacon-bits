package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class DropDownMenu extends Composite {
	private final VerticalPanel vp = new VerticalPanel() {
		@Override
		public void add(Widget w) {
			w.getElement().getStyle().setDisplay(Display.BLOCK);
			w.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
			super.add(w);
		}
	};
	private PopupPanel popup = new PopupPanel();
	private HorizontalPanel hp = new PaddedPanel(5);
	private Timer closeTimer = new Timer() {
		@Override
		public void run() {
			popup.hide();
		}
	};

	public DropDownMenu(String text) {
		popup.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				popup.hide();
			}
		}, ClickEvent.getType());
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		if (!Common.isNullOrBlank(text)) {
			Label optLabel = new Label(text);
			optLabel.getElement().getStyle().setPaddingLeft(4, Unit.PX);
			hp.add(optLabel);
		}

		Image arrow = new Image(MainImageBundle.INSTANCE.sortDescending());
		arrow.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
		hp.add(arrow);

		hp.setStylePrimaryName("DropDownMenu");

		hp.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (popup.isShowing()) {
					popup.hide();
				} else {
					show();
				}
			}
		}, ClickEvent.getType());

		initWidget(hp);

		addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				closeTimer.cancel();
				show();
			}
		}, MouseOverEvent.getType());

		addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				closeTimer.schedule(300);
			}
		}, MouseOutEvent.getType());

		popup.addDomHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				closeTimer.cancel();
			}
		}, MouseOverEvent.getType());

		popup.addDomHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				closeTimer.schedule(300);
			}
		}, MouseOutEvent.getType());

		vp.setStylePrimaryName("DropDownMenu-popup");
		popup.removeStyleName(popup.getStylePrimaryName());
		popup.getElement().getStyle().setBackgroundColor("#f9f9f9");
		popup.setWidget(vp);
	}

	public ClickLabel addItem(ClickLabel link) {
		link.addStyleName("item");
		vp.add(link);
		return link;
	}

	public Hyperlink addItem(Hyperlink link) {
		link.addStyleName("item");
		vp.add(link);
		return link;
	}

	public Label addItem(String text, final Command cmd) {
		final Label item = new Label(text);
		item.setStylePrimaryName("item");
		item.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						cmd.execute();
					}
				});
			}
		});
		vp.add(item);
		return item;
	}

	public Hyperlink addItem(String text, String url) {
		Hyperlink link = new InlineHyperlink(text, url);
		return addItem(link);
	}

	public void addSeparator() {
		HTML hr = new HTML("<hr>");
		vp.add(hr);
		return;
	}

	public void show() {
		if (popup.isShowing()) {
			return;
		}
		popup.showRelativeTo(this);
		popup.setPopupPosition(popup.getAbsoluteLeft(), popup.getAbsoluteTop() + 5);
	}

}
