package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BookDetailsDialog extends DefaultDialog {
	private PaddedPanel pp = new PaddedPanel();
	private VerticalPanel vp = new VerticalPanel();
	private Book book;
	private ButtonPanel bp = new ButtonPanel(this);

	public BookDetailsDialog(Book b) {
		this.book = b;
		setText("Book Details");
		vp.setSpacing(10);

		pp.add(new Image("/baconbits/service/file?id=" + book.getImageId()));

		VerticalPanel dt = new VerticalPanel();
		dt.setSpacing(2);

		Label title = new Label(book.getTitle());
		title.addStyleName("largeText bold");
		dt.add(title);

		Label price = new Label(Formatter.formatCurrency(book.getPrice()));
		price.addStyleName("hugeText bold");
		dt.add(price);

		if (!Common.isNullOrBlank(book.getIsbn())) {
			Label isbn = new Label("ISBN: " + book.getIsbn());
			dt.add(isbn);
		}

		if (!Common.isNullOrBlank(book.getCondition())) {
			Label condition = new Label("Condition: " + book.getCondition());
			dt.add(condition);
		}

		if (!Common.isNullOrBlank(book.getNotes())) {
			Label notes = new Label(book.getNotes());
			dt.add(notes);
		}

		pp.add(dt);
		vp.add(pp);
		vp.add(bp);

		vp.setWidth("600px");
		setWidget(vp);
	}
}
