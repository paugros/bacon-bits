package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AlertPopupPanel {

	public static PopupPanel showAlert(String text, final Command onClick) {
		final PopupPanel alertPanel = new PopupPanel();

		alertPanel.setStyleName("notificationAlertPanel");
		alertPanel.setAnimationEnabled(true);
		VerticalPanel vp = new VerticalPanel();
		ClickLabel delete = new ClickLabel("X", new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				alertPanel.hide();
			}
		});
		delete.addStyleName("mediumPadding bold");
		vp.add(delete);
		vp.setCellHorizontalAlignment(delete, HasHorizontalAlignment.ALIGN_RIGHT);

		SimplePanel innerPanel = new SimplePanel();
		innerPanel.setStyleName("notificationAlertContent");
		ClickLabel alertLabel = new ClickLabel(text, new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				onClick.execute();
			}
		});
		alertLabel.removeStyleName("ClickLabel");
		alertLabel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		alertLabel.setHeight("48px");
		alertLabel.setWidth("250px");
		Style s = alertLabel.getElement().getStyle();
		s.setFontWeight(FontWeight.BOLD);
		s.setColor("#1A4DA0");
		s.setCursor(Cursor.POINTER);

		innerPanel.setWidget(alertLabel);
		vp.add(innerPanel);
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		alertPanel.setWidget(vp);

		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				alertPanel.setPopupPositionAndShow(new PositionCallback() {
					@Override
					public void setPosition(int offsetWidth, int offsetHeight) {
						alertPanel.setPopupPosition(Window.getClientWidth() - offsetWidth, Window.getClientHeight() - offsetHeight);
					}
				});
			}
		});

		return alertPanel;
	}
}
