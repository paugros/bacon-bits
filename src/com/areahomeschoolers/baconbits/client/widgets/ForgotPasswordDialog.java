package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ForgotPasswordDialog extends DialogBox {
	private final LoginServiceAsync loginService;
	private final TextBox usernameInput;
	private final Command command;
	private final HTML errorLabel = new HTML();

	public ForgotPasswordDialog(LoginServiceAsync service, Command finishedCommand) {
		command = finishedCommand;
		loginService = service;
		setText("Recover Account Access");
		setGlassEnabled(true);
		VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(10);
		vp.setWidth("360px");
		Image logo = new Image(MainImageBundle.INSTANCE.logo());
		vp.add(logo);
		vp.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_CENTER);
		Label instructions = new Label("Submit your registered username/e-mail address below to receive an email which will allow you to reset your password.");
		String helpText = "NOTE: Allow a few minutes for the e-mail to be delivered. If you have any questions, please contact Kristin Augros at kaugros@gmail.com.";
		Label help = new Label(helpText);
		help.addStyleName("smallText");
		vp.add(instructions);
		vp.add(help);
		errorLabel.setStyleName("errorText");
		vp.add(errorLabel);

		usernameInput = new TextBox();
		HorizontalPanel field = new HorizontalPanel();
		field.setSpacing(5);
		field.add(new Label("Email address:"));
		field.add(usernameInput);
		vp.add(field);

		ButtonPanel bp = new ButtonPanel();
		bp.addCenterButton(new Button("Submit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitForm();
			}
		}));

		bp.addCenterButton(new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				closeOut();
			}
		}));

		vp.add(bp);
		setWidget(vp);
	}

	private void closeOut() {
		hide();
		command.execute();
	}

	private void submitForm() {
		final String userName = usernameInput.getText();

		loginService.sendPasswordResetEmail(userName, new AsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Boolean success) {
				if (!success) {
					String txt = "The username provided is not registered. Please register for an account. ";
					txt += "If you have any questions, please contact the Kristin Augros at kaugros@gmail.com.";
					errorLabel.setHTML(txt);
					return;
				}

				String msg = "To get back into your account, follow the instructions we've sent to " + usernameInput.getText().toLowerCase() + ".";
				AlertDialog dialog = new AlertDialog("Success", new Label(msg));
				dialog.getButton().addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						closeOut();
					}
				});

				hide();
				dialog.setGlassEnabled(true);
				dialog.center();
			}
		});
	}
}
