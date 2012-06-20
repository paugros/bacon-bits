package com.areahomeschoolers.baconbits.client.widgets;

import java.util.List;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ResetPasswordDialog extends DefaultDialog {
	private PasswordTextBox confirmPasswordInput;
	private PasswordTextBox passwordInput;
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private Button submit;
	private PopupPanel errorPopupPanel;
	private VerticalPanel errorPanel;

	public ResetPasswordDialog(boolean allowCancel) {
		setText("Reset Password");

		setGlassEnabled(true);

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("360px");
		vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vp.setSpacing(10);

		confirmPasswordInput = new PasswordTextBox();
		passwordInput = new PasswordTextBox();
		KeyDownHandler kdh = new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					submitForm();
				}
			}
		};
		confirmPasswordInput.addKeyDownHandler(kdh);
		passwordInput.addKeyDownHandler(kdh);
		passwordInput.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (Common.isNullOrBlank(passwordInput.getText())) {
					return;
				}
				userService.validatePassword(passwordInput.getText(), new Callback<ServerResponseData<String>>(false) {
					@Override
					protected void doOnSuccess(ServerResponseData<String> result) {
						if (result.hasErrors()) {
							showErrors(result.getErrors());
						} else {
							if (errorPopupPanel != null) {
								errorPopupPanel.hide();
							}
						}
					}
				});
			}
		});
		passwordInput.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if (errorPopupPanel != null) {
					errorPopupPanel.hide();
				}
			}
		});

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

		passwordInput.setVisibleLength(22);
		confirmPasswordInput.setVisibleLength(22);

		FieldTable ft = new FieldTable();
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);
		ft.addField("New password:", passwordInput);
		ft.addField("Confirm password:", confirmPasswordInput);

		vp.add(ft);
		vp.add(bp);

		setWidget(vp);
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

	private void showErrors(List<String> errors) {
		if (errorPopupPanel == null) {
			errorPopupPanel = new PopupPanel(true);
			errorPanel = new VerticalPanel();
			errorPanel.setSpacing(5);
			errorPopupPanel.setWidget(errorPanel);
		}
		errorPanel.clear();
		for (String error : errors) {
			errorPanel.add(new Label(error));
		}
		errorPopupPanel.showRelativeTo(passwordInput);
	}

	private void submitForm() {
		if (!Common.equals(confirmPasswordInput.getText(), passwordInput.getText())) {
			showErrors(Common.asList("Passwords do not match."));
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
				user.setPasswordDigest(u.getPasswordDigest());
				user.setResetPassword(false);
			}
		});
	}
}
