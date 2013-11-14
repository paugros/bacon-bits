package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;

public class RequestMembershipLink extends Composite {
	private ClickLabel label;
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);

	public RequestMembershipLink(final UserGroup userGroup) {
		label = new ClickLabel("Click here to request membership in " + userGroup.getGroupName(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				ConfirmDialog.confirm("Send a request to join this group?", new ConfirmHandler() {
					@Override
					public void onConfirm() {
						userGroup.setUserApproved(true);
						userGroup.setGroupApproved(false);
						userService.updateUserGroupRelation(Application.getCurrentUser(), userGroup, true, new Callback<Void>() {
							@Override
							protected void doOnSuccess(Void result) {
								Hyperlink link = new Hyperlink("group membership tab", PageUrl.user(Application.getCurrentUserId()) + "&tab=2");
								String msg = "Message sent! You can view the status of your reqeust on your " + link + ".";
								AlertDialog.alert(msg);
							}
						});
					}
				});
			}
		});

		label.addStyleName("largeText");

		initWidget(label);
	}
}
