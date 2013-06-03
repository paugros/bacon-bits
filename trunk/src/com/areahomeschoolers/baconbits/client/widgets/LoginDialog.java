package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.CreateUserDialog;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SubmitButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginDialog extends DialogBox {

	public interface LoginHandler {
		void onLogin(ApplicationData ap);
	}

	private final LoginServiceAsync loginService;
	private final Label errorLabel = new Label();
	private final FormPanel formPanel = new FormPanel();
	private final TextBox usernameInput = new TextBox();
	private final PasswordTextBox passwordInput = new PasswordTextBox();
	private final SubmitButton submit = new SubmitButton("Sign in");
	private LoginHandler loginHandler;
	private static final String TITLE = "Log in";
	private static final String BAD_CREDENTIALS = "The username or password you entered is incorrect.";
	private static boolean isShown;

	public static void showLogin() {
		LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);
		final LoginDialog ld = new LoginDialog(loginService);
		ld.setLoginHandler(new LoginHandler() {
			@Override
			public void onLogin(ApplicationData ap) {
				Window.Location.reload();
			}
		});
		ld.center();
	}

	private CreateUserDialog createDialog = new CreateUserDialog();

	public LoginDialog(final LoginServiceAsync loginService) {
		this.loginService = loginService;

		VerticalPanel vp = new VerticalPanel();
		usernameInput.setName("username");
		passwordInput.setName("password");
		formPanel.setAction("javascript:return false;");
		formPanel.setMethod(FormPanel.METHOD_POST);
		formPanel.addSubmitHandler(new SubmitHandler() {
			@Override
			public void onSubmit(SubmitEvent event) {
				submitForm();
			}
		});
		formPanel.setWidget(vp);

		setText(TITLE);
		setAnimationEnabled(true);
		setModal(false);
		// setGlassEnabled(true);

		vp.setWidth("360px");
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vp.setSpacing(10);

		errorLabel.setStyleName("errorText");
		vp.add(errorLabel);

		Button cancel = new Button("Cancel", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		PaddedPanel buttons = new PaddedPanel();
		submit.setStyleName("gwt-Button");
		buttons.add(cancel);
		buttons.add(submit);

		usernameInput.setMaxLength(50);
		usernameInput.setWidth("180px");
		passwordInput.setWidth("180px");

		VerticalPanel password = new VerticalPanel();
		password.add(passwordInput);

		PaddedPanel createPanel = new PaddedPanel();
		ClickLabel create = new ClickLabel("Create a new one", new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				createDialog.center(new User());
				hide();
			}
		});
		create.setWordWrap(false);
		create.addStyleName("bold");

		Label msg = new Label("Don't have an account?");
		msg.setWordWrap(false);
		createPanel.add(msg);
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
		PaddedPanel p = new PaddedPanel();
		p.add(forgot);
		p.add(new Label("to reset it."));
		pp.add(p);
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
		table.setWidget(2, 1, password);
		table.setWidget(3, 1, buttons);
		table.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(1, 1, HasHorizontalAlignment.ALIGN_LEFT);
		table.getCellFormatter().setHorizontalAlignment(2, 1, HasHorizontalAlignment.ALIGN_LEFT);
		vp.add(table);

		setWidget(formPanel);
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

		loginService.loginAndGetApplicationData(usernameInput.getText(), passwordInput.getText(), new AsyncCallback<ApplicationData>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(ApplicationData ap) {
				if (ap.getCurrentUser() != null) {
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

	}
}
