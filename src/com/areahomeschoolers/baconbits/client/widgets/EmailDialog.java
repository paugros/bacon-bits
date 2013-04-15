package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Email;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EmailDialog extends DefaultDialog {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private Email email = new Email();
	private VerticalPanel vp = new VerticalPanel();
	private SimplePanel fromPanel = new SimplePanel();
	private EmailTextBox fromBox = new EmailTextBox();
	private TextArea textArea = new DefaultTextArea();
	private ButtonPanel bp = new ButtonPanel(this);
	private String fromEmail;
	private String hiddenAboveText;
	private String hiddenBelowText;

	public EmailDialog() {
		vp.setSpacing(10);
		vp.add(new Label("You will be copied on this email."));

		PaddedPanel fp = new PaddedPanel();
		fp.add(new Label("Your email:"));
		fp.add(fromPanel);

		vp.add(fp);

		if (Application.isAuthenticated()) {
			setFrom(Application.getCurrentUser().getEmail());
		} else {
			setFrom(null);
		}

		vp.add(textArea);

		final Button send = new Button("Send Email");
		send.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (Common.isNullOrBlank(fromEmail)) {
					if (!fromBox.getValidator().validate()) {
						return;
					}

					fromEmail = fromBox.getValue();
					email.addCc(fromEmail);
				}

				send.setEnabled(false);
				String body = "NOTE: DO NOT REPLY TO THIS EMAIL. Reply to the sender listed below instead.\n\nSender: " + fromEmail + "\n\n";
				if (!Common.isNullOrBlank(hiddenAboveText)) {
					body += hiddenAboveText;
				}

				body += textArea.getValue();

				if (!Common.isNullOrBlank(hiddenBelowText)) {
					body += hiddenBelowText;
				}

				body += "NOTE: DO NOT REPLY TO THIS EMAIL.\n\n";

				email.setBody(body);

				userService.sendEmail(email, new Callback<Void>() {
					@Override
					protected void doOnSuccess(Void result) {
						hide();
					}
				});
			}
		});
		bp.addRightButton(send);
		vp.add(bp);

		setWidget(vp);
		setText("Send Email");
	}

	public void addBcc(String bccEmail) {
		email.addBcc(bccEmail);
	}

	public void addTo(String toEmail) {
		email.addTo(toEmail);
	}

	public void setFrom(String fromEmail) {
		this.fromEmail = fromEmail;
		if (Common.isNullOrBlank(fromEmail)) {
			fromPanel.setWidget(fromBox);
			fromBox.setRequired(true);
		} else {
			fromPanel.setWidget(new Label(fromEmail));
			email.addCc(fromEmail);
			fromBox.setRequired(false);
		}
	}

	public void setHiddenAboveText(String text) {
		hiddenAboveText = text;
	}

	public void setHiddenBelowText(String text) {
		hiddenBelowText = text;
	}

	public void setSubject(String subject) {
		email.setSubject(subject);
	}

	@Override
	public void show() {
		super.show();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				textArea.setFocus(true);
			}
		});
	}

}
