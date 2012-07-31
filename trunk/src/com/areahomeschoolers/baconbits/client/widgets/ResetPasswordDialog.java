package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.content.user.PasswordInputs;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResetPasswordDialog extends DefaultDialog {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private Button submit;
	private PasswordInputs passwordInputs = new PasswordInputs(new Command() {
		@Override
		public void execute() {
			submitForm();
		}
	});
	private PasswordTextBox passwordInput = passwordInputs.getPasswordInput();
	private PasswordTextBox confirmPasswordInput = passwordInputs.getConfirmPasswordInput();

	public ResetPasswordDialog(boolean allowCancel) {
		setText("Reset Password");

		setGlassEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("360px");
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vp.setSpacing(10);

		submit = new Button("Submit", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitForm();
			}
		});

		ButtonPanel bp = new ButtonPanel(this);
		bp.addCenterButton(submit);

		if (!allowCancel) {
			bp.getCloseButton().removeFromParent();
		} else {
			bp.addCenterButton(bp.getCloseButton());
		}

		FieldTable ft = new FieldTable();
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);
		ft.addField("New password:", passwordInput);
		ft.addField("Confirm password:", confirmPasswordInput);

		vp.add(ft);
		vp.add(bp);

		setWidget(vp);
	}

	@Override
	public void hide() {
		super.hide();
		passwordInputs.hideErrors();
	}

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				passwordInput.setFocus(true);
			}
		});
	}

	private void submitForm() {
		if (!Common.equals(confirmPasswordInput.getText(), passwordInput.getText())) {
			passwordInputs.showErrors(Common.asList("Passwords do not match."));
			return;
		}
		submit.setEnabled(false);

		final User user = Application.getCurrentUser();

		user.setPassword(confirmPasswordInput.getText());
		userService.save(user, new Callback<ServerResponseData<User>>() {
			@Override
			protected void doOnSuccess(ServerResponseData<User> result) {
				hide();
				User u = result.getData();
				user.setPassword(null);
				if (u != null) {
					user.setPasswordDigest(u.getPasswordDigest());
				}
				user.setResetPassword(false);
			}
		});

		user.setResetPassword(false);

	}
}
