package com.areahomeschoolers.baconbits.client.content.user;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CreateUserDialog extends EntityEditDialog<User> {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);

	public CreateUserDialog() {
		setText("Create Account");
		setAutoHide(false);

		form.getSubmitButton().setText("Create");
		form.addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				// verify age
				if ((ClientDateUtils.daysBetween(new Date(), entity.getBirthDate()) / 365) < 13) {
					AlertDialog.alert("Children under the age of 13 must have their account created by a parent or guardian.");
					form.getSubmitButton().setEnabled(true);
					return;
				}

				UserFieldTable.validateUserAddress(entity, new Command() {
					@Override
					public void execute() {
						userService.save(entity, new Callback<ServerResponseData<User>>() {
							@Override
							protected void doOnSuccess(ServerResponseData<User> result) {
								if (result.hasErrors()) {
									new ServerResponseDialog(result).center();
									form.getSubmitButton().setEnabled(true);
									return;
								}

								loginService.login(entity.getUserName(), entity.getPassword(), new Callback<Boolean>() {
									@Override
									protected void doOnSuccess(Boolean result) {
										Window.Location.reload();
									}
								});
							}
						});
					}
				});
			}
		});
	}

	@Override
	protected Widget createContent() {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("530px");
		vp.setSpacing(6);

		FieldTable ft = new UserFieldTable(form, entity);
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);

		HTML terms = new HTML();
		String sn = Application.getCurrentOrg().getShortName();
		String text = "<p align=center><b>" + sn + " Terms & Conditions</b></p><ul>";
		text += "<li>Sign up for activities are on a first come, first serve basis, as some activities are limited to a certain number of children.";
		text += "<li>Children are officially registered when payment is received.";
		text += "<li>If payment is not received within 24 hours of registering, your registration will be canceled.";
		text += "<li>Due to unexpected last minute changes, " + sn + " reserves the right to substitute comparable programs without notification.";
		text += "<li>Always check email the day of events for program updates or cancellation.  ";
		text += sn + " will always attempt to notify you of a change, but it is up to you to check for emails.";
		text += "<li>" + sn + " will not be held responsible for direction inaccuracies from GPS devices or mapping websites.";
		text += "<li>Absolutely no refunds.";
		text += "</ul>";

		terms.setHTML(text);
		MaxHeightScrollPanel sp = new MaxHeightScrollPanel(150);
		sp.setWidget(terms);

		vp.add(ft);
		vp.add(sp);

		final CheckBox cb = new CheckBox("I accept the terms and conditions above");
		vp.add(cb);

		form.addFormValidatorCommand(new ValidatorCommand() {
			@Override
			public void validate(Validator validator) {
				if (!cb.getValue()) {
					validator.setError(true);
					validator.setErrorMessage("You must accept the terms and conditions in order to join");
				} else {
					validator.setError(false);
				}
			}
		});

		return vp;
	}

}
