package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;

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
		hp.addStyleName("userTile");

		Image i = new Image(Constants.DOCUMENT_URL_PREFIX + 1222);
		i.getElement().getStyle().setBorderColor("#6b48f");
		i.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		i.getElement().getStyle().setBorderWidth(1, Unit.PX);

		hp.add(new HTML(i.toString()));

		Hyperlink link = new Hyperlink(item.getTitle(), PageUrl.event(item.getId()));
		link.addStyleName("bold");
		String text = link.toString() + "<br>";
		text += Formatter.formatDateTime(item.getStartDate()) + "<br>";
		text += item.getDescription() + "<br>";

		HTML h = new HTML(text);
		h.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		h.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		h.getElement().getStyle().setWidth(190, Unit.PX);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		initWidget(hp);
	}

}
