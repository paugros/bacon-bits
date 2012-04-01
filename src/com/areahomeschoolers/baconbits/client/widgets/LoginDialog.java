package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginDialog extends DialogBox {

	public interface LoginHandler {
		void onLogin(ApplicationData ap);
	}

	private final LoginServiceAsync loginService;
	private final Label errorLabel = new Label();
	private final TextBox usernameInput = new TextBox();
	private final PasswordTextBox passwordInput = new PasswordTextBox();
	private final Button submit;
	private LoginHandler loginHandler;
	private final boolean sessionExpired;
	private static final String TITLE = "Dash - DSCI Portal";
	private static final String BAD_CREDENTIALS = "The username or password you entered is incorrect.";
	private static final String SESSION_EXPIRED = "Your session has expired.  Please sign in again.";
	private static boolean isShown;

	public LoginDialog(LoginServiceAsync loginService) {
		this(loginService, false);
	}

	public LoginDialog(final LoginServiceAsync loginService, boolean sessionExpired) {
		this.loginService = loginService;
		this.sessionExpired = sessionExpired;
		setText(TITLE);
		setGlassEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("360px");
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vp.setSpacing(10);

		// vp.add(new Image(MainImageBundle.INSTANCE.dsciLogo()));

		if (sessionExpired) {
			errorLabel.setText(SESSION_EXPIRED);
		}

		errorLabel.setStyleName("errorText");
		vp.add(errorLabel);

		submit = new Button("Sign in", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitForm();
			}
		});

		KeyDownHandler kdh = new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					submitForm();
				}
			}
		};

		usernameInput.addKeyDownHandler(kdh);
		passwordInput.addKeyDownHandler(kdh);
		usernameInput.setMaxLength(50);
		usernameInput.setWidth("180px");
		passwordInput.setWidth("180px");

		VerticalPanel pp = new VerticalPanel();
		pp.add(passwordInput);

		Grid g = new Grid(3, 2);
		g.setCellPadding(5);
		g.setWidget(0, 0, new Label("Username:"));
		g.setWidget(0, 1, usernameInput);
		Label pl = new Label("Password:");
		pl.getElement().getStyle().setPaddingTop(3, Unit.PX);
		g.setWidget(1, 0, pl);
		g.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		if (sessionExpired) {
			g.setWidget(1, 1, passwordInput);
		} else {
			g.setWidget(1, 1, pp);
		}
		g.setWidget(2, 1, submit);
		g.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		g.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		g.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		g.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);
		g.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_LEFT);
		vp.add(g);

		String html = "All login attempts and sessions are logged. Please refer to our ";
		html += "<a href=\"http://www.dscicorp.com/wua.php\">online usage</a> policies for more information.<br><br>";
		html += "&copy; 2005-2012 DSCI Corporation. All rights reserved. Proprietary & Confidential.  For more information, visit ";
		html += "<a href=\"http://www.dscicorp.com/\">www.dscicorp.com</a>.";
		HTML legal = new HTML(html);
		legal.setStyleName("smallText");
		vp.add(legal);

		setWidget(vp);
	}

	@Override
	public void hide() {
		super.hide();
		isShown = false;
	}

	public void setLoginHandler(LoginHandler loginHandler) {
		this.loginHandler = loginHandler;
	}

	public void setUsername(String username) {
		usernameInput.setText(username);
	}

	@Override
	public void show() {
		if (isShown) {
			return;
		}

		passwordInput.setText("");
		submit.setEnabled(true);
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				if (usernameInput.getText().isEmpty()) {
					usernameInput.setFocus(true);
				} else {
					passwordInput.setFocus(true);
				}
			}
		});
		super.show();
		isShown = true;
	}

	private void submitForm() {
		submit.setEnabled(false);

		if (!sessionExpired) {
			loginService.loginAndGetApplicationData(usernameInput.getText(), passwordInput.getText(), new AsyncCallback<ApplicationData>() {
				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(ApplicationData ap) {

					if (ap != null) {
						hide();

						if (loginHandler != null) {
							loginHandler.onLogin(ap);
						}

					} else {
						submit.setEnabled(true);
						errorLabel.setText(BAD_CREDENTIALS);
						center();
					}
				}
			});
		} else {
			loginService.login(usernameInput.getText(), passwordInput.getText(), new AsyncCallback<Boolean>() {
				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(Boolean result) {

					if (result) {
						hide();

						if (loginHandler != null) {
							loginHandler.onLogin(null);
						}

					} else {
						submit.setEnabled(true);
						errorLabel.setText(BAD_CREDENTIALS);
						center();
					}
				}
			});

		}

	}
}
