package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreference;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreferenceType;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UserAgreementDialog extends DefaultDialog {
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private VerticalPanel vp = new VerticalPanel();

	public UserAgreementDialog() {
		vp.setWidth("600px");
		vp.setSpacing(10);
		setModal(true);
		setGlassEnabled(true);

		setText("End User License Agreement");

		setWidget(vp);
	}

	@Override
	public void show() {
		if (vp.getWidgetCount() > 0) {
			super.show();
			return;
		}

		String msg = "Hello,<br><br>Our site policies have changed. To continue using our site, please review them, then check the box below and continue on to your privacy settings.";
		HTML h = new HTML(msg);
		h.addStyleName("mediumText heavyPadding");
		vp.add(h);

		String txt = "I agree to the site ";
		txt += "<a href=\"" + Constants.TOS_URL + "&noTitle=true\" target=\"_blank\">terms of service</a> ";
		txt += "and <a href=\"" + Constants.PRIVACY_POLICY_URL + "&noTitle=true\" target=\"_blank\">privacy policy</a> ";
		final CheckBox scb = new CheckBox(txt, true);
		scb.addStyleName("heavyPadding");
		vp.add(scb);

		ButtonPanel bp = new ButtonPanel();

		final Button settings = new Button("Continue to Privacy Settings");
		settings.setEnabled(false);
		settings.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!scb.getValue()) {
					scb.addStyleName("gwt-TextBoxError");
					return;
				}

				settings.setEnabled(false);
				User u = Application.getCurrentUser();
				u.setShowUserAgreement(false);
				u.setDirectoryOptOut(false);
				// this will ensure that the kids settings are synchronized with the parent's
				userService.savePrivacyPreference(u.getPrivacyPreference(PrivacyPreferenceType.FAMILY), new Callback<PrivacyPreference>() {
					@Override
					protected void doOnSuccess(PrivacyPreference result) {
					}
				});
				userService.save(u, new Callback<ServerResponseData<User>>() {
					@Override
					protected void doOnSuccess(ServerResponseData<User> result) {
					}
				});

			}
		});
		bp.addCenterButton(settings);

		scb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				settings.setEnabled(event.getValue());
			}
		});

		vp.add(bp);

		UserAgreementDialog.super.show();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				UserAgreementDialog.super.center();
			}
		});

	}
}
