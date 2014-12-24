package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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
		hp.addStyleName("itemTile");
		// hp.getElement().getStyle().setBackgroundColor(TagMappingType.EVENT.getColor());

		Image i = new Image(MainImageBundle.INSTANCE.defaultSmall());
		if (item.getSmallImageId() != null) {
			i = new Image(ClientUtils.createDocumentUrl(item.getSmallImageId(), item.getImageExtension()));
		}
		HTML image = new HTML(i.toString());
		hp.add(image);
		hp.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);

		Hyperlink link = new Hyperlink(item.getTitle(), PageUrl.event(item.getId()));
		link.addStyleName("bold");
		String text = link.toString() + "<br>";
		text += Formatter.formatDateTime(item.getStartDate()) + "<br>";
		text += new HTML(item.getDescription().replaceAll("<br>", " ")).getText() + "<br>";

		HTML h = new HTML(text);
		h.setHeight("85px");
		h.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		h.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		h.getElement().getStyle().setWidth(190, Unit.PX);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		initWidget(hp);
	}

}
