package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ServerResponseDialog extends DefaultDialog {
	private VerticalPanel vp = new VerticalPanel();
	private ButtonPanel buttonPanel = new ButtonPanel(this);
	private String warningText = "", errorText = "";
	private MaxHeightScrollPanel warningPanel;
	private MaxHeightScrollPanel errorPanel;
	private ServerResponseData<?> response;

	public ServerResponseDialog(ServerResponseData<?> response) {
		this.response = response;
		vp.setWidth("600px");
		setWidget(vp);

		if (response.hasErrors()) {
			setText("Error");
			Label errorLabel = new Label("The following errors were encountered:");
			errorLabel.addStyleName("mediumPadding bold");
			vp.add(errorLabel);

			errorText = "<div><ul>";
			for (String error : response.getErrors()) {
				errorText += "<li>" + error + "</li>";
			}
			errorText += "</ul></div>";

			int height = response.hasWarnings() ? 250 : 500;
			errorPanel = new MaxHeightScrollPanel(height);
			errorPanel.setWidget(new HTML(errorText));
			vp.add(errorPanel);
		}

		if (response.hasWarnings()) {
			if (!response.hasErrors()) {
				setText("Warning");
			}
			Label warningLabel = new Label("The following warnings were encountered:");
			warningLabel.addStyleName("mediumPadding bold");
			vp.add(warningLabel);

			warningText = "<div><ul>";
			for (String warning : response.getWarnings()) {
				warningText += "<li>" + warning + "</li>";
			}
			warningText += "</ul></div>";

			int height = response.hasErrors() ? 250 : 500;
			warningPanel = new MaxHeightScrollPanel(height);
			warningPanel.setWidget(new HTML(warningText));
			vp.add(warningPanel);
		}

		vp.add(buttonPanel);
	}

	public void centerOrConfirm(ConfirmHandler warningHandler) {
		if (response.hasErrors()) {
			center();
		} else {
			String warningText = Common.join(response.getWarnings(), "<br>");
			ConfirmDialog.confirm(warningText, warningHandler);
		}
	}

	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	public void setOnDismissCommand(final Command cmd) {
		Button cb = buttonPanel.getCloseButton();
		cb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cmd.execute();
			}
		});
	}

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (errorPanel != null) {
					errorPanel.adjustHeightNow();
					errorPanel.setHeight((errorPanel.getOffsetHeight() + 30) + "px");
				}
				if (warningPanel != null) {
					warningPanel.adjustHeightNow();
					warningPanel.setHeight((warningPanel.getOffsetHeight() + 30) + "px");
				}
			}
		});

		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				buttonPanel.getCloseButton().setFocus(true);
			}
		});

	}

}
