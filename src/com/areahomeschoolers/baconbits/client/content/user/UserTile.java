package com.areahomeschoolers.baconbits.client.content.user;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.User;

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

public class UserTile extends Composite {

	public UserTile(final User item) {
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken.set(PageUrl.user(item.getId()));
			}
		}, ClickEvent.getType());

		HorizontalPanel hp = new PaddedPanel(10);
		hp.setWidth("300px");
		hp.addStyleName("itemTile");
		hp.getElement().getStyle().setBackgroundColor(TagMappingType.USER.getColor());

		Image i = new Image(Constants.DOCUMENT_URL_PREFIX + item.getSmallImageId());
		i.getElement().getStyle().setBorderColor("#c7c7c7");
		i.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		i.getElement().getStyle().setBorderWidth(1, Unit.PX);

		hp.add(new HTML(i.toString()));

		Hyperlink link = new Hyperlink(item.getFullName(), PageUrl.user(item.getId()));
		link.addStyleName("largeText bold");
		String text = link.toString() + "<br>";
		if (item.getEmail() != null) {
			text += WidgetFactory.createEmailLink(item.getEmail()).toString() + "<br>";
		}

		if (item.getCommonInterestCount() > 0) {
			text += item.getCommonInterestCount() + " common interests<br>";
		}

		HTML h = new HTML(text);
		h.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		h.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		h.getElement().getStyle().setWidth(190, Unit.PX);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		initWidget(hp);
	}

}