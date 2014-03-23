package com.areahomeschoolers.baconbits.client.content.book;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.BuyBookWidget;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BookDetailsDialog extends DefaultDialog {
	private PaddedPanel pp = new PaddedPanel();
	private VerticalPanel vp = new VerticalPanel();
	private VerticalPanel vvp = new VerticalPanel();
	private Book book;
	private ButtonPanel bp = new ButtonPanel(this);
	private MaxHeightScrollPanel sp = new MaxHeightScrollPanel(vp);

	public BookDetailsDialog(Book b) {
		this.book = b;
		setText("Book Details");
		setModal(false);
		vp.setSpacing(10);

		Image cover = new Image("/baconbits/service/file?id=" + book.getImageId());
		cover.addLoadHandler(new LoadHandler() {
			@Override
			public void onLoad(LoadEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						sp.adjustHeightNow();
					}
				});
			}
		});
		pp.add(cover);

		VerticalPanel dt = new VerticalPanel();
		dt.setSpacing(2);

		Hyperlink title = new Hyperlink(book.getTitle(), PageUrl.book(book.getId()));
		title.addStyleName("largeText bold");
		dt.add(title);

		if (!Common.isNullOrBlank(book.getSubTitle())) {
			dt.add(new Label(book.getSubTitle()));
		}

		Label price = new Label(Formatter.formatCurrency(book.getPrice()));
		price.addStyleName("hugeText bold");
		dt.add(price);

		if (!Common.isNullOrBlank(book.getAuthor())) {
			Label author = new HTML("Author: " + book.getAuthor());
			dt.add(author);
		}

		if (!Common.isNullOrBlank(book.getIsbn())) {
			Label isbn = new Label("ISBN: " + book.getIsbn());
			dt.add(isbn);
		}

		if (book.getPublishDate() != null) {
			String ptext = Formatter.formatDate(book.getPublishDate());
			if (!Common.isNullOrBlank(book.getPublisher())) {
				ptext += " by " + book.getPublisher();
			}
			Label published = new Label("Published: " + ptext);
			dt.add(published);
		}

		if (book.getPageCount() > 0) {
			dt.add(new Label(book.getPageCount() + " pages"));
		}

		if (!Common.isNullOrBlank(book.getCondition())) {
			Label condition = new Label("Condition: " + book.getCondition());
			dt.add(condition);
		}

		if (!Common.isNullOrBlank(book.getNotes())) {
			Label notes = new Label(book.getNotes());
			dt.add(notes);
		}

		dt.add(new HTML("&nbsp;"));

		VerticalPanel ddt = new VerticalPanel();
		ddt.setSpacing(6);

		if (Application.isSystemAdministrator()) {
			ddt.add(new BuyBookWidget(book));
		}

		ClickLabel contact = new ClickLabel("Contact seller", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				BookTable.showEmailDialog(book);
			}
		});
		ddt.add(contact);

		Hyperlink link = new Hyperlink("See all from this seller", PageUrl.bookSearch() + "&sellerId=" + book.getUserId());
		ddt.add(link);

		dt.add(ddt);

		pp.add(dt);
		vp.add(pp);

		if (!Common.isNullOrBlank(book.getDescription())) {
			vp.add(new HTML(book.getDescription()));
		}

		vvp.setWidth("600px");
		vp.setWidth("100%");

		vvp.add(sp);
		vvp.add(bp);
		setWidget(vvp);
	}
}
