package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class BuyBookWidget extends Composite {
	private PaddedPanel cartPanel = new PaddedPanel();
	private BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private Book book;

	public BuyBookWidget(Book book) {
		this.book = book;

		initWidget(cartPanel);
		init();
	}

	private void init() {
		cartPanel.clear();

		if (book.getInMyShoppingCart()) {
			cartPanel.add(new Label("Added to cart - "));
			Hyperlink link = new Hyperlink("checkout", PageUrl.payment());
			cartPanel.add(link);
			return;
		}

		Image cart = new Image(MainImageBundle.INSTANCE.shoppingCart());
		cart.getElement().getStyle().setCursor(Cursor.POINTER);
		cartPanel.add(cart);
		ClickLabel add = new ClickLabel("Add to cart");

		ClickHandler cl = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!Application.isAuthenticated()) {
					LoginDialog.showLogin();
					return;
				}

				bookService.addBookToCart(book.getId(), Application.getCurrentUserId(), new Callback<Boolean>(false) {
					@Override
					protected void doOnSuccess(Boolean result) {
						if (!result) {
							AlertDialog.alert("Sorry, this book is no longer available.");
							BuyBookWidget.this.removeFromParent();
						} else {
							book.setInMyShoppingCart(true);
							init();
						}
					}
				});
			}
		};

		add.addClickHandler(cl);
		cart.addClickHandler(cl);
		cartPanel.add(add);
	}

}
