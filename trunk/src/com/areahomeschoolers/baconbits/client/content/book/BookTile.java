package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Book;

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

public class BookTile extends Composite {

	public BookTile(final Book item) {
		addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HistoryToken.set(PageUrl.book(item.getId()));
			}
		}, ClickEvent.getType());

		HorizontalPanel hp = new PaddedPanel(10);
		hp.setWidth("400px");
		hp.addStyleName("bookTile");

		Image i = new Image(Constants.DOCUMENT_URL_PREFIX + item.getSmallImageId());
		i.getElement().getStyle().setBorderColor("#c7c7c7");
		i.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
		i.getElement().getStyle().setBorderWidth(1, Unit.PX);

		hp.add(new HTML(i.toString()));

		Hyperlink link = new Hyperlink(item.getTitle(), PageUrl.user(item.getId()));
		link.addStyleName("bold");
		String text = link.toString() + "<br>";

		if (!Common.isNullOrBlank(item.getAuthor())) {
			text += item.getAuthor() + "<br>";
		}

		if (!Common.isNullOrBlank(item.getPublisher())) {
			String publish = item.getPublisher();
			if (item.getPublishDate() != null) {
				publish += ", " + Formatter.formatDate(item.getPublishDate(), "yyyy");
			}

			text += publish + "<br>";
		}

		text += "<span style=\"font-size: 16px; font-weight: bold;\">" + Formatter.formatCurrency(item.getPrice()) + "</span> / ";
		text += item.getCondition() + " condition<br>";

		HTML h = new HTML(text);
		h.getElement().getStyle().setWhiteSpace(WhiteSpace.NOWRAP);
		h.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		h.getElement().getStyle().setWidth(315, Unit.PX);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		initWidget(hp);
	}

}
