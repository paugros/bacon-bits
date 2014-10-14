package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/article")
public interface ArticleService extends RemoteService {
	public void clickResource(int adId);

	public Article getById(int articleId);

	public ArrayList<NewsBulletinComment> getComments(ArgMap<ArticleArg> args);

	public ArrayList<Resource> getResources(ArgMap<ResourceArg> args);

	public void hideComment(int commentId);

	public ArrayList<Article> list(ArgMap<ArticleArg> args);

	public Article save(Article article);

	public NewsBulletinComment saveComment(NewsBulletinComment comment);

	public Resource saveResource(Resource ad);
}
