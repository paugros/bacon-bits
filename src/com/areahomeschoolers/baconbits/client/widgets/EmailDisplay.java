package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class EmailDisplay extends Composite {

	private HorizontalPanel panel = new HorizontalPanel();

	public EmailDisplay() {
		initWidget(panel);
	}

	public EmailDisplay(String email) {
		this();
		setEmail(email);
	}

	public void addEmail(String email) {
		panel.add(new HTML(";&nbsp;"));
		panel.add(createAnchorWidget(email));
	}

	public void setEmail(String email) {
		panel.clear();
		panel.add(createAnchorWidget(email));
	}

	private Anchor createAnchor(String email) {
		Anchor anchor = new Anchor();
		anchor.setText(email);
		anchor.setHref("mailto:" + email);

		return anchor;
	}

	private Widget createAnchorWidget(String email) {
		if (email != null && !email.isEmpty()) {
			return createAnchor(email);
		}

		return new Label(Common.getDefaultIfNull(null));
	}
}
