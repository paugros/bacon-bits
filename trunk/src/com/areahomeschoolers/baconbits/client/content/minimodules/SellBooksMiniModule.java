package com.areahomeschoolers.baconbits.client.content.minimodules;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookServiceAsync;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.LoginDialog;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SellBooksMiniModule extends Composite {
	private final BookServiceAsync bookService = (BookServiceAsync) ServiceCache.getService(BookService.class);
	private ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
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
				showDialog();
			}
		});

		String sellText = "You can sell your used homeschool curriculum with us. Click below to sign up as a book seller and begin listing your items.";
		vp.add(new Label(sellText));
		vp.add(payButton);

		initWidget(vp);
	}

	public void showDialog() {
		if (!Application.isAuthenticated()) {
			LoginDialog.showLogin();
			return;
		}

		articleService.getById(Constants.BOOK_TC_ARTICLE_ID, new Callback<Article>() {
			@Override
			protected void doOnSuccess(Article result) {
				VerticalPanel content = new VerticalPanel();
				content.setWidth("500px");
				content.setSpacing(8);
				DefaultDialog dialog = new DefaultDialog();

				dialog.setText(result.getTitle());

				dialog.setWidget(content);
				content.add(new MaxHeightScrollPanel(new HTML(result.getArticle())));

				PaddedPanel pp = new PaddedPanel();
				pp.add(new Label("I want to sell books"));
				final DefaultListBox lb = new DefaultListBox();
				lb.addItem("at both the physical sale and online", 1);
				lb.addItem("online only", 2);
				lb.addItem("at the physical sale only", 3);
				pp.add(lb);
				content.add(pp);

				CheckBox cb = new CheckBox("I accept the terms and conditions above");
				content.add(cb);

				ButtonPanel bp = new ButtonPanel(dialog);
				bp.getCloseButton().setText("Cancel");
				final Button confirm = new Button("Confirm");
				bp.addRightButton(confirm);
				confirm.setEnabled(false);
				cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						confirm.setEnabled(event.getValue());
					}
				});

				content.add(bp);

				confirm.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						confirm.setEnabled(false);

						if (paying) {
							return;
						}

						paying = true;

						bookService.signUpToSell(lb.getIntValue(), new Callback<PaypalData>() {
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

				dialog.center();
			}
		});

	}

}
