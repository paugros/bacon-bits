package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.EntityEditDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.ServerResponseDialog;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class CreateFamilyMemberDialog extends EntityEditDialog<User> {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private User parent;

	public CreateFamilyMemberDialog(User parent, final Command saveCommand) {
		setText("Add Family Member");
		setAutoHide(false);

		this.parent = parent;

		form.getSubmitButton().setText("Add");
		form.addFormSubmitHandler(new FormSubmitHandler() {
			@Override
			public void onFormSubmit(FormField formField) {
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

								saveCommand.execute();
								hide();
							}
						});
					}
				});
			}
		});
	}

	@Override
	protected Widget createContent() {
		entity.setParentId(parent.getId());
		entity.setLastName(parent.getLastName());
		entity.setAddress(parent.getAddress());
		entity.setStreet(parent.getStreet());
		entity.setZip(parent.getZip());
		entity.setHomePhone(parent.getHomePhone());

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("530px");
		vp.setSpacing(6);

		FieldTable ft = new UserFieldTable(form, entity);
		ft.setLabelColumnWidth(LabelColumnWidth.NARROW);

		MaxHeightScrollPanel sp = new MaxHeightScrollPanel(150);

		vp.add(ft);
		vp.add(sp);

		return vp;
	}

}
