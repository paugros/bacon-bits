package com.areahomeschoolers.baconbits.client.content.tag;

import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SearchSection extends Composite {
	private HorizontalPanel hp = new HorizontalPanel();
	private VerticalPanel vp = new VerticalPanel();

	public SearchSection(TagMappingType type, Widget searchBox) {
		FriendlyTextWidget ftw = new FriendlyTextWidget(type);
		if (ClientUtils.isMobileBrowser()) {
			ftw.getElement().getStyle().setMarginBottom(10, Unit.PX);
			vp.add(ftw);
			vp.add(searchBox);

			initWidget(vp);
		} else {
			ftw.getElement().getStyle().setMarginLeft(10, Unit.PX);
			hp.add(searchBox);
			hp.add(ftw);

			initWidget(hp);
		}
	}

}
