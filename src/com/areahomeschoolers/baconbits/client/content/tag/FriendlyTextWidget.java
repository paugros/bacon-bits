package com.areahomeschoolers.baconbits.client.content.tag;

import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class FriendlyTextWidget extends Composite {

	public FriendlyTextWidget(TagType type) {
		HTML h = new HTML(type.getHelpText());
		h.addStyleName("friendlyText");

		initWidget(h);
	}

}
