package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.BlogComment;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/article")
public interface ArticleService extends RemoteService {
	public Article getById(int articleId);

	public ArrayList<BlogComment> getComments(ArgMap<ArticleArg> args);

	public void hideComment(int commentId);

	public ArrayList<Article> list(ArgMap<ArticleArg> args);

	public Article save(Article article);

	public BlogComment saveComment(BlogComment comment);
}
