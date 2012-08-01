package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.content.user.CreateUserDialog;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
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
	private static final String TITLE = "Log in";
	private static final String BAD_CREDENTIALS = "The username or password you entered is incorrect.";
	private static final String SESSION_EXPIRED = "Your session has expired.  Please sign in again.";
	private static boolean isShown;
	private CreateUserDialog createDialog = new CreateUserDialog();

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

		Button cancel = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		PaddedPanel buttons = new PaddedPanel();
		buttons.add(cancel);
		buttons.add(submit);

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

		VerticalPanel password = new VerticalPanel();
		password.add(passwordInput);

		PaddedPanel createPanel = new PaddedPanel();
		ClickLabel create = new ClickLabel("Create new account", new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				createDialog.center(new User());
				hide();
			}
		});
		create.addStyleName("bold mediumPadding");
		createPanel.add(new Label("New to the site?"));
		createPanel.add(create);

		VerticalPanel pp = new VerticalPanel();
		pp.add(new Label("Did you forget your password?"));
		ClickLabel forgot = new ClickLabel("Click here", new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				hide();
				ForgotPasswordDialog dialog = new ForgotPasswordDialog(loginService, new Command() {
					@Override
					public void execute() {
						center();
					}
				});
				dialog.center();
			}
		});
		pp.add(forgot);
		pp.add(new Label("to reset it."));
		pp.addStyleName("smallText");

		password.add(pp);

		FlexTable table = new FlexTable();
		table.setCellPadding(5);
		table.setWidget(0, 1, createPanel);
		table.setWidget(1, 0, new Label("Email:"));
		table.setWidget(1, 1, usernameInput);
		Label pl = new Label("Password:");
		pl.getElement().getStyle().setPaddingTop(3, Unit.PX);
		table.setWidget(2, 0, pl);
		table.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		table.getCellFormatter().setVerticalAlignment(2, 0, HasVerticalAlignment.ALIGN_TOP);
		if (sessionExpired) {
			table.setWidget(2, 1, passwordInput);
		} else {
			table.setWidget(2, 1, password);
		}
		table.setWidget(3, 1, buttons);
		table.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_LEFT);
		vp.add(table);

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
