package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;

public interface ArticleDao {
	public Article getById(int articleId);

	public ArrayList<NewsBulletinComment> getComments(ArgMap<ArticleArg> args);

	public int getNewsCount();

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void hideComment(int commentId);

	public ArrayList<Article> list(ArgMap<ArticleArg> args);

	@PreAuthorize("hasAnyRole('GROUP_ADMINISTRATORS', 'BLOG_CONTRIBUTORS')")
	public Article save(Article article);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public NewsBulletinComment saveComment(NewsBulletinComment comment);
}
