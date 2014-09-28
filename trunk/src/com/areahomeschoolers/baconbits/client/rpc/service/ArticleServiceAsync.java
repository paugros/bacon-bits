package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Ad;
import com.areahomeschoolers.baconbits.shared.dto.Arg.AdArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ArticleServiceAsync {
	public void getById(int articleId, AsyncCallback<Article> callback);

	public void list(ArgMap<ArticleArg> args, AsyncCallback<ArrayList<Article>> callback);

	public void save(Article article, AsyncCallback<Article> callback);

	void getAds(ArgMap<AdArg> args, AsyncCallback<ArrayList<Ad>> callback);

	void getComments(ArgMap<ArticleArg> args, AsyncCallback<ArrayList<NewsBulletinComment>> callback);

	void hideComment(int commentId, AsyncCallback<Void> callback);

	void saveAd(Ad ad, AsyncCallback<Ad> callback);

	void saveComment(NewsBulletinComment comment, AsyncCallback<NewsBulletinComment> callback);
}
