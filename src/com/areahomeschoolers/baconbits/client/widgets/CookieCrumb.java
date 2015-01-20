package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.PageUrl;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;

public class CookieCrumb extends Composite {
	private HTML html = new HTML();
	private final String SEPARATOR = "&nbsp;>&nbsp;";

	public CookieCrumb() {
		add(new DefaultHyperlink("Home", PageUrl.home()));

		initWidget(html);
	}

	public void add(DefaultHyperlink link) {
		add(link.toString());
	}

	public void add(String text) {
		String current = html.getHTML();
		html.setHTML(current + (current.isEmpty() ? "" : SEPARATOR) + text);
	}

}
