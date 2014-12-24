package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
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

		HorizontalPanel hp = new HorizontalPanel();
		int textWidth = 200;
		hp.setWidth((textWidth + 80) + "px");
		hp.setHeight("117px");
		hp.addStyleName("itemTile");
		// hp.getElement().getStyle().setBackgroundColor(TagMappingType.BOOK.getColor());

		Image i = new Image(MainImageBundle.INSTANCE.defaultSmall());
		if (item.getSmallImageId() != null) {
			i = new Image(ClientUtils.createDocumentUrl(item.getSmallImageId(), item.getImageExtension()));
		}

		HTML image = new HTML("<div style=\"width: 80px; margin-right: 10px; text-align: center;\">" + i.toString() + "</div>");
		hp.add(image);
		hp.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_MIDDLE);

		Hyperlink link = new Hyperlink(item.getTitle(), PageUrl.book(item.getId()));
		link.addStyleName("bold");
		// title/link
		String text = "<div style=\"overflow: hidden; max-height: 38px; width: " + textWidth + "px;\">" + link.toString() + "</div>";
		text += "<div style=\"overflow: hidden; width: " + textWidth + "px; white-space: nowrap;\">";

		// price/condition
		text += "<span style=\"font-size: 16px; font-weight: bold;\">" + Formatter.formatCurrency(item.getPrice()) + "</span> / ";
		text += item.getCondition() + " condition<br>";

		// author
		if (!Common.isNullOrBlank(item.getAuthor())) {
			text += "by " + item.getAuthor() + "<br>";
		}

		// publisher
		if (!Common.isNullOrBlank(item.getPublisher())) {
			String publish = item.getPublisher();
			if (item.getPublishDate() != null) {
				publish += ", " + Formatter.formatDate(item.getPublishDate(), "yyyy");
			}

			text += publish + "<br>";
		}
		text += "</div>";

		HTML h = new HTML(text);

		hp.add(h);
		hp.setCellHorizontalAlignment(h, HasHorizontalAlignment.ALIGN_LEFT);

		initWidget(hp);
	}
}
