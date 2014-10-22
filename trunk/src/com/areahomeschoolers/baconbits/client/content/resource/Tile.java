package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Document;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Tile extends Composite {
	private VerticalPanel vp = new VerticalPanel();

	public Tile(final TileConfig config) {
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (config.getUrl().startsWith("page=")) {
					HistoryToken.set(config.getUrl());
				} else {
					Window.Location.assign(config.getUrl());
				}
			}
		}, ClickEvent.getType());

		vp.addStyleName("tile");
		vp.setSpacing(12);
		vp.getElement().getStyle().setBackgroundColor("#" + Long.toString(config.getColor(), 16));
		long bordercolor = (config.getColor() & 0x3e3e3e) >> 1 | (config.getColor() & 0x808080);
		vp.getElement().getStyle().setBorderColor("#" + Long.toString(bordercolor, 16));
		vp.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		vp.getElement().getStyle().setBorderWidth(1, Unit.PX);

		SimplePanel sp = new SimplePanel();
		sp.getElement().getStyle().setHeight(200, Unit.PX);
		Image image = new Image(Document.toUrl(config.getImageId()));
		sp.setWidget(image);
		vp.add(sp);
		vp.setCellHorizontalAlignment(sp, HasHorizontalAlignment.ALIGN_CENTER);

		PaddedPanel pp = new PaddedPanel();
		pp.setWidth("100%");
		Label label = new Label(config.getText());
		label.addStyleName("hugeText");
		pp.add(label);

		Label countDisplay = new Label(Integer.toString(config.getCount()));
		countDisplay.addStyleName("hugeText");
		pp.add(countDisplay);
		pp.setCellHorizontalAlignment(countDisplay, HasHorizontalAlignment.ALIGN_RIGHT);

		vp.add(pp);

		initWidget(vp);
	}
}
