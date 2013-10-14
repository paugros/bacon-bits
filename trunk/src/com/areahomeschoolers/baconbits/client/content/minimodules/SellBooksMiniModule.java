package com.areahomeschoolers.baconbits.client.content.minimodules;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SellBooksMiniModule extends Composite {
	private final BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private boolean paying = false;

	public SellBooksMiniModule() {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.setSpacing(8);

		Label label = new Label("Sell Your Books");
		label.addStyleName("moduleTitle");
		vp.add(label);
		Button payButton = new Button("Sign Up ($5.00)", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!Application.isAuthenticated()) {
					LoginDialog.showLogin();
					return;
				}

				if (paying) {
					return;
				}

				paying = true;

				bookService.signUpToSell(new Callback<PaypalData>() {
					@Override
					protected void doOnFailure(Throwable caught) {
						super.doOnFailure(caught);
						paying = false;
					}

					@Override
					protected void doOnSuccess(PaypalData result) {
						if (result.getAuthorizationUrl() != null) {
							Window.Location.replace(result.getAuthorizationUrl());
						} else {
							HistoryToken.set(PageUrl.home() + "&ps=return");
						}
					}
				});
			}
		});
		String sellText = "You can sell your used homeschool curriculum with us. Click below to sign up as a book seller and begin listing your items.";
		vp.add(new Label(sellText));
		vp.add(payButton);

		initWidget(vp);
	}

}
