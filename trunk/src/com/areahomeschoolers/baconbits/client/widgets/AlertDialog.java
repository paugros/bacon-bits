package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AlertDialog extends DefaultDialog {
	private static final int defaultWidth = 300;

	public static void alert(String labelText) {
		alert("Message", labelText, defaultWidth);
	}

	public static void alert(String caption, String labelText) {
		alert(caption, labelText, defaultWidth);
	}

	public static void alert(String caption, String labelText, int pixelWidth) {
		Label label = new Label(labelText);

		if (pixelWidth > 0) {
			label.setWidth(pixelWidth + "px");
		}

		AlertDialog ad = new AlertDialog(caption, label);
		ad.center();
	}

	public static void alert(String caption, Widget content) {
		AlertDialog ad = new AlertDialog(caption, content);
		ad.center();
	}

	private Button okButton;

	public AlertDialog(String caption, Widget content) {
		super(false, true);
		setText(caption);

		okButton = new Button("Ok");
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setWidth("200px");
		vPanel.setSpacing(10);
		vPanel.add(content);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(okButton);
		setWidget(vPanel);
	}

	public Button getButton() {
		return okButton;
	}

	@Override
	public void show() {
		super.show();

		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				okButton.setFocus(true);
			}
		});
	}
}
