package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArticleServiceAsync {

	void getArticles(AsyncCallback<ArrayList<Article>> callback);

	void getById(int articleId, AsyncCallback<Article> callback);

	void save(Article article, AsyncCallback<Article> callback);
}
