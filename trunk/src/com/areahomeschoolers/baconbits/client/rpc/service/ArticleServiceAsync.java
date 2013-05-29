package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArticleServiceAsync {
	public void getById(int articleId, AsyncCallback<Article> callback);

	public void list(ArgMap<ArticleArg> args, AsyncCallback<ArrayList<Article>> callback);

	public void save(Article article, AsyncCallback<Article> callback);
}
