package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class CreateUserDialog extends EntityEditDialog<User> {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private LoginServiceAsync loginService = (LoginServiceAsync) ServiceCache.getService(LoginService.class);

	public CreateUserDialog() {
		setText("Create Account");

		form.getSubmitButton().setText("Create");
		form.addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
				userService.save(entity, new Callback<ServerResponseData<User>>() {
					@Override
					protected void doOnSuccess(ServerResponseData<User> result) {
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

	@Override
	protected Widget createContent() {
		FieldTable ft = new UserFieldTable(form, entity);
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);
		return ft;
	}

}
