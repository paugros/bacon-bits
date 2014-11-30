package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
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
		hp.setHeight("117px");
		hp.addStyleName("itemTile");
		hp.getElement().getStyle().setBackgroundColor(TagMappingType.RESOURCE.getColor());

		// Image i = new Image(Constants.DOCUMENT_URL_PREFIX + 1222);
		Image i = new Image(MainImageBundle.INSTANCE.logo());
		// i.getElement().getStyle().setBorderColor("#6b48f");
		// i.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		// i.getElement().getStyle().setBorderWidth(1, Unit.PX);

		hp.add(new HTML("<div style=\"width: 80px; margin-right: 10px; text-align: center;\">" + i.toString() + "</div>"));

		Hyperlink link = new Hyperlink(item.getName(), PageUrl.resource(item.getId()));
		link.addStyleName("bold");
		// title/link
		String text = "<div style=\"overflow: hidden; width: " + textWidth + "px;\">" + link.toString() + "</div>";

		text += "<div style=\"overflow: hidden; width: " + textWidth + "px; white-space: nowrap;\">";
		// address
		if (!Common.isNullOrBlank(item.getAddress())) {
			String a = "";
			if (!Common.isNullOrBlank(item.getStreet())) {
				a += item.getStreet() + "<br>";
			}
			if (!Common.isNullOrBlank(item.getCity())) {
				a += item.getCity() + " ";
			}
			if (!Common.isNullOrBlank(item.getState())) {
				a += item.getState() + " ";
			}
			if (!Common.isNullOrBlank(item.getZip())) {
				a += item.getZip();
			}
			text += a + "<br>";
		} else if (!Common.isNullOrBlank(item.getPhone())) {
			text += item.getPhone() + "<br>";
		}
		text += "</div>";

		// description
		text += "<div class=smallText style=\"overflow: hidden; max-height: 38px; width: " + textWidth + "px;\">" + item.getDescription() + "</div>";

		HTML h = new HTML(text);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		initWidget(hp);
	}

}
