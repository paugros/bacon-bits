package com.areahomeschoolers.baconbits.client.content.minimodules;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class CitrusMiniModule extends Composite {
	public CitrusMiniModule() {
		String url = "#" + PageUrl.home();
		Image image = new Image(MainImageBundle.INSTANCE.littleLogo());
		String logo = "<a href=\"" + url + "\">" + image + "</a>";
		String text = "<a href=\"" + url + "\" style=\"color: #f06000; font-size: 14px; font-weight: bold;\">";
		text += "Citrus&nbsp;Groups</a></div>";
		text += "<div style=\"color: #555555; margin-left: 1px; white-space: nowrap;\">Homeschool network</div>";
		PaddedPanel linkPanel = new PaddedPanel();
		linkPanel.add(new HTML(logo));
		linkPanel.add(new HTML(text));
		initWidget(linkPanel);
	}
}
