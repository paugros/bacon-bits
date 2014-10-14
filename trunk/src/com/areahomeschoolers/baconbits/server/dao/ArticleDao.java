package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

public interface ArticleDao {
	public void clickResource(int adId);

	public Article getById(int articleId);

	public ArrayList<NewsBulletinComment> getComments(ArgMap<ArticleArg> args);

	public ArrayList<Resource> getResources(ArgMap<ResourceArg> args);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void hideComment(int commentId);

	public ArrayList<Article> list(ArgMap<ArticleArg> args);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public Article save(Article article);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public NewsBulletinComment saveComment(NewsBulletinComment comment);

	@PreAuthorize("hasRole('SYSTEM_ADMINISTRATORS')")
	public Resource saveResource(Resource ad);
}
