package com.areahomeschoolers.baconbits.client.content.user;

import java.util.List;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PasswordInputs {
	private PasswordTextBox confirmPasswordInput = new PasswordTextBox();
	private PasswordTextBox passwordInput = new PasswordTextBox();
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private PopupPanel errorPopupPanel;
	private VerticalPanel errorPanel;

	public PasswordInputs(final Command submitCommand) {
		KeyDownHandler kdh = new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					submitCommand.execute();
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

		passwordInput.setVisibleLength(22);
		confirmPasswordInput.setVisibleLength(22);
	}

	public PasswordTextBox getConfirmPasswordInput() {
		return confirmPasswordInput;
	}

	public PasswordTextBox getPasswordInput() {
		return passwordInput;
	}

	public void hideErrors() {
		if (errorPopupPanel != null) {
			errorPopupPanel.hide();
		}
	}

	public void showErrors(List<String> errors) {
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
}
