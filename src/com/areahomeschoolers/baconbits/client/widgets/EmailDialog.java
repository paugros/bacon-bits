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
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EmailDialog extends DefaultDialog {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private Email email = new Email();
	private VerticalPanel vp = new VerticalPanel();
	private SimplePanel fromPanel = new SimplePanel();
	private EmailTextBox fromBox = new EmailTextBox();
	private ControlledRichTextArea textArea = new ControlledRichTextArea();
	private TextBox subjectBox = new TextBox();
	private boolean showSubjectBox;
	private ButtonPanel bp;
	private String fromEmail;
	private String hiddenAboveText;
	private String hiddenBelowText;

	public EmailDialog() {
		setModal(false);
		setWidget(vp);
		setText("Send Email");
	}

	public void addBcc(String bccEmail) {
		email.addBcc(bccEmail);
	}

	public void addCc(String ccEmail) {
		email.addCc(ccEmail);
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

	public void setShowSubjectBox(boolean show) {
		showSubjectBox = show;
	}

	public void setSubject(String subject) {
		email.setSubject(subject);
		if (showSubjectBox) {
			subjectBox.setText(subject);
		}
	}

	@Override
	public void show() {
		if (bp == null) {
			email.setHtmlMail(true);
			textArea.getTextArea().setWidth("600px");
			textArea.getTextArea().setHeight("400px");
			bp = new ButtonPanel(this);
			vp.setSpacing(10);
			PaddedPanel fp = new PaddedPanel();
			fromBox.setVisibleLength(30);
			fp.add(new Label("Your email:"));
			fp.add(fromPanel);
			vp.add(fp);

			if (showSubjectBox) {
				subjectBox.setVisibleLength(68);
				subjectBox.addStyleName("largeText");
				subjectBox.setText(email.getSubject());
				vp.add(subjectBox);
			}

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

					if (showSubjectBox) {
						email.setSubject(subjectBox.getText());
					}

					send.setEnabled(false);
					String body = "<font face=arial>NOTE: DO NOT REPLY TO THIS EMAIL. Reply to the sender listed below instead.<br><br>";
					body += "Sender: " + fromEmail + "<br><br>";
					if (!Common.isNullOrBlank(hiddenAboveText)) {
						body += hiddenAboveText;
					}
					body += "</font>";

					body += textArea.getTextArea().getHTML();

					body += "<font face=arial>";
					if (!Common.isNullOrBlank(hiddenBelowText)) {
						body += hiddenBelowText;
					}

					body += "<br><br>NOTE: DO NOT REPLY TO THIS EMAIL.</font><br><br>";

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
		}

		super.show();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (showSubjectBox && Common.isNullOrBlank(subjectBox.getText())) {
					subjectBox.setFocus(true);
				} else {
					textArea.getTextArea().setFocus(true);
				}
			}
		});

	}
}
