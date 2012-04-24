package com.areahomeschoolers.baconbits.client.content.home;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.ServiceCache;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.rpc.Callback;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleServiceAsync;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HomePage implements Page {
	private final ArticleServiceAsync articleService = (ArticleServiceAsync) ServiceCache.getService(ArticleService.class);
	private VerticalPanel page = new VerticalPanel();

	public HomePage(VerticalPanel p) {
		page = p;

		articleService.getArticles(new Callback<ArrayList<Data>>() {
			@Override
			protected void doOnSuccess(ArrayList<Data> result) {
				for (Data item : result) {
					page.add(new Label(item.get("title") + ": " + item.get("article")));
				}

				Application.getLayout().setPage("Home", page);
			}
		});
	}
}
