package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.Url;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class AddLink extends Composite {

	public AddLink(String name, String url) {
		url = Url.getBaseUrl() + "#" + url;
		if (!Application.isAuthenticated()) {
			url = "javascript:;";
		}
		String text = "<a href=\"" + url + "\">";
		Image i = new Image(MainImageBundle.INSTANCE.plus());
		i.getElement().getStyle().setMarginRight(5, Unit.PX);
		text += "<span style=\"vertical-align: middle; width: 16\">" + i + "</span>";
		text += "<span style=\"font-size: 16px; white-space: nowrap; width: 1%;\">" + name + "</span></a>";

		HTML html = new HTML(text);
		html.setWidth("auto");

		if (!Application.isAuthenticated()) {
			html.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					LoginDialog.showLogin();
				}
			}, ClickEvent.getType());
		}
		initWidget(html);
	}

}
