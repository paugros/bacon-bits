package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class BookTile extends Composite {
	private Book book;

	public BookTile(Book b) {
		this.book = b;
		String h = "";

		h += "<table style=\"border-spacing: 4px;\"><tr><td style=\"vertical-align: top;\"><a href=\"#page=Book&bookId=" + book.getId() + "\">";
		h += "<img src=\"/baconbits/service/file?id=" + book.getSmallImageId() + "\"></td>";

		h += "<td style=\"vertical-align: top;\"><div><a href=\"#page=Book&bookId=" + book.getId() + "\" class=bold>" + book.getTitle() + "</a></div>";

		h += "<div class=bold>" + Formatter.formatCurrency(book.getPrice()) + "</div>";

		if (!Common.isNullOrBlank(book.getAuthor())) {
			h += "<div>By " + book.getAuthor() + "</div>";
		}

		if (!Common.isNullOrBlank(book.getCondition())) {
			h += "<div>Condition: " + book.getCondition() + "</div>";
		}

		// if (Application.isSystemAdministrator()) {
		// ddt.add(new BuyBookWidget(book));
		// }

		HTML html = new HTML(h);
		html.setWidth("600px");
		initWidget(html);
	}

}
