package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

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

		Image i = new Image(MainImageBundle.INSTANCE.defaultSmall());
		if (item.getSmallImageId() != null) {
			i = new Image(ClientUtils.createDocumentUrl(item.getSmallImageId(), item.getImageExtension()));
		}

		HTML image = new HTML("<div style=\"width: 80px; margin-right: 10px; text-align: center;\">" + i.toString() + "</div>");
		hp.add(image);
		hp.setCellVerticalAlignment(image, HasVerticalAlignment.ALIGN_TOP);

		Hyperlink link = new Hyperlink(item.getTitle(), PageUrl.book(item.getId()));
		link.addStyleName("bold");
		// title/link
		String text = "<div style=\"overflow: hidden; height: 100px; width: 190px;\">";
		text += "<div style=\"overflow: hidden; max-height: 38px; width: " + textWidth + "px;\">" + link.toString() + "</div>";
		text += "<div style=\"white-space: nowrap;\">";

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
		text += "</div></div>";

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
