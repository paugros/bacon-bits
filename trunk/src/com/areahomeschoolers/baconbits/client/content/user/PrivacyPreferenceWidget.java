package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserServiceAsync;
import com.areahomeschoolers.baconbits.client.widgets.ItemVisibilityWidget;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreference;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class PrivacyPreferenceWidget extends Composite {
	private ItemVisibilityWidget visibilityWidget;
	private UserServiceAsync userService = (UserServiceAsync) ServiceCache.getService(UserService.class);
	private HorizontalPanel hp = new PaddedPanel(2);
	private Button save;

	public PrivacyPreferenceWidget(PrivacyPreference privacyPreference) {
		visibilityWidget = new ItemVisibilityWidget(privacyPreference);
		hp.add(visibilityWidget);
		save = new Button("Save", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				save.setEnabled(false);

				userService.savePrivacyPreference(visibilityWidget.getPrivacyPreference(), new Callback<PrivacyPreference>() {
					@Override
					protected void doOnSuccess(PrivacyPreference result) {
						if (visibilityWidget.getPrivacyPreference().getUserId() == Application.getCurrentUserId()) {
							Application.getCurrentUser().setPrivacyPreference(result);
						}
						save.setVisible(false);
					}
				});
			}
		});

		save.setVisible(false);

		visibilityWidget.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				save.setEnabled(true);
				save.setVisible(true);
			}
		});

		hp.add(save);
		initWidget(hp);
	}

	public ItemVisibilityWidget getVisibilityWidget() {
		return visibilityWidget;
	}

	public void setEnabled(boolean enabled) {
		visibilityWidget.setEnabled(enabled);
	}
}
