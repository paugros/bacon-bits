package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;

public class FeedbackWidget extends Composite {
	private ClickLabel label;

	public FeedbackWidget() {
		label = new ClickLabel("Submit site feedback", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EmailDialog dialog = new EmailDialog();
				dialog.addTo(Constants.SUPPORT_EMAIL);
				dialog.setText("Submit Feeback");
				dialog.getSubmitButton().setText("Send");
				dialog.setFormattingEnabled(false);
				dialog.setCcSender(false);
				String txt = "Our goal is to help make homeschooling parents and kids to be as effective as possible. To meet this goal, we need to know what you want. Let us know!";
				dialog.setAboveText(txt);
				if (Application.isAuthenticated()) {
					dialog.setFrom(Application.getCurrentUser().getEmail());
				}
				dialog.center();
			}
		});

		label.addStyleName("largeText");

		initWidget(label);
	}
}
