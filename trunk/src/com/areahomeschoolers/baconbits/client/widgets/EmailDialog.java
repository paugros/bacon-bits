package com.areahomeschoolers.baconbits.client.widgets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.UserSelector;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Email;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
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
	private String insertHtml;
	private boolean allowEditRecipients;
	private UserSelector userSelector;
	private EmailTextBox emailTextBox;
	private Button send = new Button("Send Email");
	private String aboveText;
	// private List<User> recipients = new ArrayList<>();
	private Set<User> recipients = new HashSet<>();
	private boolean ccSender = true;

	public EmailDialog() {
		setModal(false);
		setWidget(vp);
		setText("Send Email");
	}

	public void addBcc(List<User> fullList) {
		for (User u : fullList) {
			addBcc(u);
		}
	}

	public void addBcc(String... bccEmail) {
		for (String e : bccEmail) {
			email.addBcc(e);
		}
	}

	public void addBcc(User u) {
		if (!Common.isNullOrBlank(u.getEmail())) {
			addBcc(u.getEmail());
			recipients.add(u);
		}
	}

	public void addCc(List<User> fullList) {
		for (User u : fullList) {
			addCc(u);
		}
	}

	public void addCc(String... ccEmail) {
		for (String e : ccEmail) {
			email.addCc(e);
		}
	}

	public void addCc(User u) {
		if (!Common.isNullOrBlank(u.getEmail())) {
			addCc(u.getEmail());
			recipients.add(u);
		}
	}

	public void addTo(List<User> fullList) {
		for (User u : fullList) {
			addTo(u);
		}
	}

	public void addTo(String... toEmail) {
		for (String e : toEmail) {
			email.addTo(e);
		}
	}

	public void addTo(User u) {
		if (!Common.isNullOrBlank(u.getEmail())) {
			addTo(u.getEmail());
			recipients.add(u);
		}
	}

	public String getAboveText() {
		return aboveText;
	}

	public Button getSubmitButton() {
		return send;
	}

	public void insertHtml(String html) {
		this.insertHtml = html;
	}

	public void setAboveText(String aboveText) {
		this.aboveText = aboveText;
	}

	public void setAllowEditRecipients(boolean allow) {
		this.allowEditRecipients = allow;
	}

	public void setCcSender(boolean cc) {
		ccSender = cc;
	}

	public void setFormattingEnabled(boolean enabled) {
		textArea.getToolbar().setVisible(enabled);
	}

	public void setFrom(String fromEmail) {
		this.fromEmail = fromEmail;
		if (Common.isNullOrBlank(fromEmail)) {
			fromPanel.setWidget(fromBox);
			fromBox.setRequired(true);
		} else {
			fromPanel.setWidget(new Label(fromEmail));
			if (ccSender) {
				email.addCc(fromEmail);
			}
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
			textArea.getTextArea().setHeight("300px");
			bp = new ButtonPanel(this);
			vp.setSpacing(10);
			if (aboveText != null) {
				vp.add(new Label(aboveText));
			}
			PaddedPanel fp = new PaddedPanel();
			fromBox.setVisibleLength(30);
			fp.add(new Label("Your email:"));
			fp.add(fromPanel);
			vp.add(fp);

			if (allowEditRecipients) {
				PaddedPanel tp = new PaddedPanel();
				tp.add(new Label("To (bcc):"));
				email.addTo(Application.getCurrentUser().getEmail());
				final ClickLabel uc = new ClickLabel("Click to choose recipients");

				ArgMap<UserArg> ua = new ArgMap<UserArg>(Status.ACTIVE);
				ua.put(UserArg.ORGANIZATION_ID, Application.getCurrentOrgId());
				ua.put(UserArg.HAS_EMAIL, true);
				userSelector = new UserSelector(ua);
				userSelector.setMultiSelect(true);
				if (!Common.isNullOrEmpty(recipients)) {
					userSelector.getCellTable().populate(recipients);
					userSelector.getCellTable().setSelectedItems(recipients);
					uc.setText(recipients.size() + " recipients");
				}

				userSelector.addSubmitCommand(new Command() {
					@Override
					public void execute() {
						email.getBccs().clear();
						recipients.clear();
						for (User u : userSelector.getSelectedItems()) {
							addBcc(u);
						}

						uc.setText(recipients.size() + " recipients");
					}
				});

				uc.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						userSelector.center();
					}
				});

				tp.add(uc);

				PaddedPanel op = new PaddedPanel();
				op.add(new Label("Other recipients:"));
				emailTextBox = new EmailTextBox();
				emailTextBox.setVisibleLength(50);
				emailTextBox.setMultiEmail(true);
				op.add(emailTextBox);

				vp.add(tp);
				vp.add(op);
			}

			if (showSubjectBox) {
				VerticalPanel sp = new VerticalPanel();
				subjectBox.setVisibleLength(68);
				subjectBox.addStyleName("largeText");
				subjectBox.setText(email.getSubject());
				Label subjectLabel = new Label("Subject");
				subjectLabel.addStyleName("smallText grayText");
				sp.add(subjectLabel);
				sp.add(subjectBox);
				vp.add(sp);
			}

			if (Application.isAuthenticated()) {
				setFrom(Application.getCurrentUser().getEmail());
			} else {
				setFrom(null);
			}

			vp.add(textArea);

			send.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (Common.isNullOrBlank(fromEmail)) {
						if (!fromBox.getValidator().validate()) {
							return;
						}

						fromEmail = fromBox.getValue();
						if (ccSender) {
							email.addCc(fromEmail);
						}
					}

					if (showSubjectBox) {
						email.setSubject(subjectBox.getText());
					}

					if (allowEditRecipients) {
						email.addBcc(emailTextBox.getEmailSet());
						if (email.getBccs().isEmpty()) {
							AlertDialog.alert("Please specify at least one recipient.");
							return;
						}
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

				if (insertHtml != null) {
					textArea.getTextArea().setFocus(true);

					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							Timer t = new Timer() {
								@Override
								public void run() {
									textArea.insertHtml(insertHtml);
								}
							};

							t.schedule(500);
						}
					});
				}
			}
		});

	}
}
