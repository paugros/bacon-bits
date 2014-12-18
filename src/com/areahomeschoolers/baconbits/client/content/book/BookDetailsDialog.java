package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.user.client.ui.VerticalPanel;

public class BookDetailsDialog extends DefaultDialog {

	public BookDetailsDialog(Book book) {
		VerticalPanel vp = new VerticalPanel();
		MaxHeightScrollPanel sp = new MaxHeightScrollPanel();

		setText("Book Details");
		setModal(false);

		BookDetailsPanel dt = new BookDetailsPanel(book);

		dt.setWidth("600px");

		sp = new MaxHeightScrollPanel(dt);

		vp.add(sp);
		vp.add(new ButtonPanel(this));
		setWidget(vp);
	}
}
