package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GroupMembershipControl {
	private User user;
	private UserGroup userGroup;
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public GroupMembershipControl(User user, UserGroup userGroup) {
		this.user = user;
		this.userGroup = userGroup;
	}

	public Widget createAdminWidget(final Command onUpdate) {
		if (!Application.administratorOf(userGroup)) {
			return new Label(Common.yesNo(userGroup.getAdministrator()));
		}
		return new ClickLabel(Common.yesNo(user.administratorOf(userGroup.getId())), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String action = userGroup.getAdministrator() ? "Revoke" : "Grant";
				String confirm = action + " administrator access for " + user.getFullName() + " in the " + userGroup.getGroupName() + " group?";
				ConfirmDialog.confirm(confirm, new ConfirmHandler() {
					@Override
					public void onConfirm() {
						boolean admin = !user.administratorOf(userGroup.getId());
						userGroup.setAdministrator(admin);
						user.getGroups().get(userGroup.getId()).setAdministrator(admin);
						onUpdate.execute();
						userService.updateUserGroupRelation(user, userGroup, true, new Callback<Void>(false) {
							@Override
							protected void doOnSuccess(Void item) {
							}
						});
					}
				});
			}
		});
	}

	public Widget createMemberWidget(final Command onUpdate) {
		if (!Application.administratorOf(userGroup)) {
			return new Label("");
		}

		return new ClickLabel("X", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String confirm = "Remove " + user.getFullName() + " from the " + userGroup.getGroupName() + " group?";
				ConfirmDialog.confirm(confirm, new ConfirmHandler() {
					@Override
					public void onConfirm() {
						onUpdate.execute();
						userService.updateUserGroupRelation(user, userGroup, false, new Callback<Void>(false) {
							@Override
							protected void doOnSuccess(Void item) {
							}
						});
					}
				});
			}
		});
	}

}
