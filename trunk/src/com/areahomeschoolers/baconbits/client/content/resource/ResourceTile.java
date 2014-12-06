package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;

public class ResourceTile extends Composite {

	public ResourceTile(final Resource item) {
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken.set(PageUrl.resource(item.getId()));
			}
		}, ClickEvent.getType());

		HorizontalPanel hp = new HorizontalPanel();
		int textWidth = 200;
		hp.setWidth((textWidth + 80) + "px");
		hp.setHeight("100px");
		hp.addStyleName("itemTile");
		if (item.getShowInAds()) {
			hp.getElement().getStyle().setOpacity(1);
		}
		hp.getElement().getStyle().setBackgroundColor(TagMappingType.RESOURCE.getColor());

		Image i = new Image(MainImageBundle.INSTANCE.defaultSmall());
		if (item.getSmallImageId() != null) {
			i = new Image(ClientUtils.createDocumentUrl(item.getSmallImageId(), item.getImageExtension()));
		}
		hp.add(new HTML("<div style=\"width: 80px; margin-right: 10px; text-align: center;\">" + i.toString() + "</div>"));

		Hyperlink link = new Hyperlink(item.getName(), PageUrl.resource(item.getId()));
		link.addStyleName("bold");
		// title/link
		String text = "<div style=\"overflow: hidden; width: " + textWidth + "px; white-space: nowrap;\">" + link.toString() + "<br>";

		// address
		if (!Common.isNullOrBlank(item.getAddress())) {
			String a = "";
			if (!Common.isNullOrBlank(item.getCity())) {
				a += item.getCity() + " ";
			}
			if (!Common.isNullOrBlank(item.getState())) {
				a += item.getState();
			}
			text += a + "<br>";
		} else if (!Common.isNullOrBlank(item.getPhone())) {
			text += item.getPhone() + "<br>";
		}
		text += "</div>";

		// description
		text += "<div class=smallText style=\"overflow: hidden; max-height: 41px; width: " + textWidth + "px;\">" + item.getDescription() + "</div>";

		HTML h = new HTML(text);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		initWidget(hp);
	}

}
