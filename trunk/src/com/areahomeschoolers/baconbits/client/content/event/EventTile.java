package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventTile extends Composite {

	public EventTile(final Event item) {
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken.set(PageUrl.event(item.getId()));
			}
		}, ClickEvent.getType());

		HorizontalPanel hp = new PaddedPanel(10);
		hp.setWidth("300px");

		Image i = new Image(MainImageBundle.INSTANCE.defaultSmall());
		if (item.getSmallImageId() != null) {
			i = new Image(ClientUtils.createDocumentUrl(item.getSmallImageId(), item.getImageExtension()));
		}
		HTML image = new HTML(i.toString());
		hp.add(image);
		hp.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_TOP);

		DefaultHyperlink link = new DefaultHyperlink(item.getTitle(), PageUrl.event(item.getId()));
		link.addStyleName("bold");

		String text = "<div style=\"overflow: hidden; height: 110px; width: 190px;\">";
		text += "<div style=\"white-space: nowrap;\">" + link + "<br>";
		if (!Common.isNullOrBlank(item.getAddress())) {
			String a = "<div style=\"font-style: italic;\">";
			if (!Common.isNullOrBlank(item.getCity())) {
				a += item.getCity();
				if (!Common.isNullOrBlank(item.getState())) {
					a += ", ";
				}
			}
			if (!Common.isNullOrBlank(item.getState())) {
				a += item.getState();
			}
			text += a + "</div>";
		}
		if (!Common.isNullOrBlank(item.getFacilityName())) {
			text += "<div style=\"font-style: italic;\">" + item.getFacilityName() + "</div>";
		}
		text += Formatter.formatDateTime(item.getStartDate()) + "<br>";
		text += "</div>";

		String desc = new HTML(item.getDescription().replaceAll("<br>", " ")).getText();

		text += "<div class=smallText>" + desc + "</div></div>";

		HTML h = new HTML(text);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		VerticalPanel vp = new VerticalPanel();
		vp.addStyleName("itemTile");
		vp.add(hp);
		String tags = item.getTags() == null ? "No tags" : item.getTags();
		Label t = new Label(tags);
		t.setWidth("280px");
		t.setWordWrap(false);
		t.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		vp.add(t);

		initWidget(vp);
	}

}
