package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.util.Url;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
		vp.setHeight("267px");
		vp.setWidth("238px");
		vp.setSpacing(12);
		// vp.getElement().getStyle().setBackgroundColor(config.getColor());

		SimplePanel sp = new SimplePanel();
		sp.getElement().getStyle().setHeight(200, Unit.PX);
		Image image = config.getImage();

		sp.setWidget(image);
		vp.add(sp);
		vp.setCellHorizontalAlignment(sp, HasHorizontalAlignment.ALIGN_CENTER);

		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");

		String url = config.getUrl();
		if (url.startsWith("page=")) {
			url = Url.getBaseUrl() + "#" + url;
		}

		String textSize = null;
		int countLength = 0;
		if (config.getCount() != null) {
			countLength = Integer.toString(config.getCount()).length();
		}
		int width = 205;
		textSize = "largeText";
		width -= countLength * 10;

		String htmlText = "<a href=\"" + url + "\" class=\"" + textSize + "\" style=\"color: black;\">" + config.getText() + "</a>";
		HTML link = new HTML(htmlText);
		if (config.getCenterText()) {
			link.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		}

		link.setWordWrap(false);
		link.setWidth(width + "px");
		link.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		hp.add(link);

		if (config.getCount() != null) {
			Label countDisplay = new Label(Integer.toString(config.getCount()));
			countDisplay.addStyleName(textSize);
			hp.add(countDisplay);
			hp.setCellHorizontalAlignment(countDisplay, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		vp.add(hp);

		initWidget(vp);
	}
}
