package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Panel that displays a small "Loading..." message at the top of browser window.
 */
public class StatusPanel extends Composite {
	private SimplePanel sPanel = new SimplePanel();

	public StatusPanel() {
		initWidget(sPanel);
		sPanel.setWidget(new Label("Loading..."));
		setVisible(false);
		setStyleName("StatusPanel");
	}

	public void hide() {
		setVisible(false);
	}

	public void show() {
		setVisible(true);
	}
}
