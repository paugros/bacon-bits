package com.areahomeschoolers.baconbits.client.content.minimodules;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class CitrusMiniModule extends Composite {
	public CitrusMiniModule() {
		String url = Constants.URL_SEPARATOR + PageUrl.home();
		Image image = new Image(MainImageBundle.INSTANCE.littleLogo());
		String text = "<a href=\"" + url + "\" style=\"color: #f06000; font-size: 14px; font-weight: bold;\">";
		text += "<table><tr><td>" + image + "</td>";
		if (!ClientUtils.isMobileBrowser()) {
			text += "<td>Citrus<br>Groups</a></td>";
		}
		text += "</tr></table>";
		HTML html = new HTML(text);
		initWidget(html);
		addStyleName("CitrusMiniModule");
	}
}
