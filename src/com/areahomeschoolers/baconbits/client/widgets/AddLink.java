package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.Url;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class AddLink extends Composite {

	public AddLink(String name, String url) {
		String text = "<a href=\"" + Url.getBaseUrl() + "#" + url + "\">";
		Image i = new Image(MainImageBundle.INSTANCE.plus());
		i.getElement().getStyle().setMarginRight(5, Unit.PX);
		text += "<table style=\"display: inline;\"><tr><td>" + i + "</td>";
		text += "<td style=\"font-size: 16px;\">" + name + "</td></tr></table></a>";

		HTML html = new HTML(text);
		initWidget(html);
	}

}
