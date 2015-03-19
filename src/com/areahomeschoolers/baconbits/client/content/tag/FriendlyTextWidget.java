package com.areahomeschoolers.baconbits.client.content.tag;

import com.areahomeschoolers.baconbits.client.widgets.FeedbackWidget;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FriendlyTextWidget extends Composite {
	private VerticalPanel vp = new VerticalPanel();

	public FriendlyTextWidget(TagType type) {
		HTML h = new HTML(type.getHelpText() + "<br><br>");
		h.addStyleName("friendlyText");

		vp.add(h);
		FeedbackWidget fw = new FeedbackWidget();
		vp.add(fw);
		initWidget(vp);
	}

}
