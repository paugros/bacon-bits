package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FieldDisplayLink extends Composite {
	private FocusPanel panel = new FocusPanel();
	private Hyperlink link = new Hyperlink();
	private Anchor anchor = new Anchor();
	private Label label = new Label();
	private boolean enabled = true;
	private String defaultText = Common.getDefaultIfNull(null);
	private Widget linkWidget = link;

	public FieldDisplayLink() {
		initWidget(panel);
	}

	public FieldDisplayLink(String text, String targetHistoryToken) {
		this();
		setText(text);
		setTargetHistoryToken(targetHistoryToken);
	}

	public FocusPanel getFocusPanel() {
		return panel;
	}

	public void setDefaultText(String text) {
		defaultText = text;
		label.setText(text);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		if (!enabled) {
			panel.setWidget(label);
		} else {
			panel.setWidget(linkWidget);
		}
	}

	public void setHref(String href) {
		linkWidget = anchor;
		anchor.setHref(href);
		panel.setWidget(anchor);
	}

	public void setTarget(String target) {
		linkWidget = anchor;
		anchor.setTarget(target);
		panel.setWidget(anchor);
	}

	public void setTargetHistoryToken(String token) {
		linkWidget = link;
		link.setTargetHistoryToken(token);
		panel.setWidget(link);
	}

	public void setText(String text) {
		if (!enabled || isEmpty(text)) {
			if (enabled || isEmpty(text)) {
				label.setText(defaultText);
			} else {
				label.setText(text);
			}
			panel.setWidget(label);
		} else {
			link.setText(text);
			anchor.setText(text);
			panel.setWidget(linkWidget);
		}
	}

	private boolean isEmpty(String text) {
		return text == null || text.isEmpty() || "0".equals(text);
	}
}
