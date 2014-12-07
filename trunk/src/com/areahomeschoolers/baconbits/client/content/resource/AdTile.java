package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceService;
import com.areahomeschoolers.baconbits.client.rpc.service.ResourceServiceAsync;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AdTile extends Composite {
	private VerticalPanel vp = new VerticalPanel();
	private ResourceServiceAsync resourceService = (ResourceServiceAsync) ServiceCache.getService(ResourceService.class);

	public AdTile(final Resource ad) {
		addDomHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				resourceService.clickResource(ad.getId(), new Callback<Void>(false) {
					@Override
					protected void doOnSuccess(Void result) {

					}
				});
			}
		}, MouseDownEvent.getType());

		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.Location.assign(ad.getUrl());
			}
		}, ClickEvent.getType());

		vp.addStyleName("tile");
		vp.setHeight("267px");
		vp.getElement().getStyle().setBackgroundColor(TagMappingType.RESOURCE.getColor());

		SimplePanel sp = new SimplePanel();
		sp.getElement().getStyle().setHeight(200, Unit.PX);
		sp.getElement().getStyle().setMarginTop(12, Unit.PX);
		Image image = new Image(ClientUtils.createDocumentUrl(ad.getImageId(), ad.getImageExtension()));

		sp.setWidget(image);
		vp.add(sp);
		vp.setCellHorizontalAlignment(sp, HasHorizontalAlignment.ALIGN_CENTER);

		VerticalPanel vvp = new VerticalPanel();
		vvp.getElement().getStyle().setMarginLeft(12, Unit.PX);
		vvp.setWidth("100%");

		String url = ad.getUrl();

		String textSize = null;
		int width = 215;
		textSize = "largeText";

		String htmlText = "<a href=\"" + url + "\" class=\"" + textSize + "\" style=\"color: black;\">" + ad.getName() + "</a>";
		HTML link = new HTML(htmlText);

		link.setWordWrap(false);
		link.setWidth(width + "px");
		link.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		vvp.add(link);

		if (!Common.isNullOrBlank(ad.getAdDescription())) {
			Label description = new Label(ad.getAdDescription());
			description.setWordWrap(false);
			description.setWidth(width + "px");
			description.getElement().getStyle().setOverflow(Overflow.HIDDEN);
			vvp.add(description);
		} else {
			link.getElement().getStyle().setMarginBottom(12, Unit.PX);
		}

		vp.add(vvp);

		initWidget(vp);
	}
}
