package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.GroupData;
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
			return new Label(Common.yesNo(user.administratorOf(userGroup.getId())));
		}
		return new ClickLabel(Common.yesNo(user.administratorOf(userGroup.getId())), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String action = user.administratorOf(userGroup.getId()) ? "Revoke" : "Grant";
				String confirm = action + " administrator access for " + user.getFullName() + " in the " + userGroup.getGroupName() + " group?";
				ConfirmDialog.confirm(confirm, new ConfirmHandler() {
					@Override
					public void onConfirm() {
						boolean admin = !user.administratorOf(userGroup.getId());
						userGroup.setAdministrator(admin);
						// user.getGroups().get(userGroup.getId()).setAdministrator(admin);
						userService.updateUserGroupRelation(user, userGroup, true, new Callback<Void>(false) {
							@Override
							protected void doOnSuccess(Void item) {
								onUpdate.execute();
							}
						});
					}
				});
			}
		});
	}

	public Widget createApprovalWidget(final Command onUpdate) {
		if (userGroup.getGroupApproved() && userGroup.getUserApproved()) {
			return new Label("Approved");
		}

		// if pending, and you don't have the ability to approve, just display pending
		if ((!userGroup.getGroupApproved() && !Application.administratorOf(userGroup))
				|| (!userGroup.getUserApproved() && !user.equals(Application.getCurrentUser()))) {
			return new Label("Pending");
		}

		return new ClickLabel("Approve", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String confirm = "Approve membership for " + user.getFullName() + " in the " + userGroup.getGroupName() + " group?";
				ConfirmDialog.confirm(confirm, new ConfirmHandler() {
					@Override
					public void onConfirm() {
						userGroup.setGroupApproved(true);
						userGroup.setUserApproved(true);
						user.setGroupApproved(true);
						user.setUserApproved(true);
						GroupData gd = new GroupData();
						gd.setOrganization(userGroup.getOrganization());
						gd.setOrganizationId(userGroup.getOwningOrgId());
						user.getGroups().put(userGroup.getId(), gd);

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
		if (!Application.administratorOf(userGroup) && !user.equals(Application.getCurrentUser())) {
			return new Label("");
		}

		return new ClickLabel("X", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String confirm = "";
				if (user.equals(Application.getCurrentUser())) {
					confirm = "Are you sure you want to leave the " + userGroup.getGroupName() + " group?";
				} else {
					confirm = "Remove " + user.getFullName() + " from the " + userGroup.getGroupName() + " group?";
				}
				ConfirmDialog.confirm(confirm, new ConfirmHandler() {
					@Override
					public void onConfirm() {
						userService.updateUserGroupRelation(user, userGroup, false, new Callback<Void>(false) {
							@Override
							protected void doOnSuccess(Void item) {
								onUpdate.execute();
							}
						});
					}
				});
			}
		});
	}

}
