package com.areahomeschoolers.baconbits.client.content.minimodules;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class CitrusMiniModule extends Composite {
	public CitrusMiniModule() {
		Image image = new Image(MainImageBundle.INSTANCE.littleLogo());
		String logo = "<a href=\"" + Constants.CG_URL + "\">" + image + "</a>";
		String text = "<a href=\"" + Constants.CG_URL + "\" style=\"color: #f06000; font-size: 16px; font-weight: bold;\">" + "Citrus&nbsp;Groups</a>";
		text += "<div style=\"color: #555555; margin-left: 1px;\">Homeschool network</div>";
		PaddedPanel linkPanel = new PaddedPanel();
		linkPanel.add(new HTML(logo));
		linkPanel.add(new HTML(text));
		initWidget(linkPanel);
	}
}
