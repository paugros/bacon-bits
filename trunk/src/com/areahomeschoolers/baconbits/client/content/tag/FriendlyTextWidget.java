package com.areahomeschoolers.baconbits.client.content.tag;

import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class FriendlyTextWidget extends Composite {

	public FriendlyTextWidget(TagMappingType type) {
		HTML h = new HTML(type.getHelpText());
		h.addStyleName("friendlyText");

		initWidget(h);
	}

}
