package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.BlogComment;

public interface ArticleDao {
	public String createWhere();

	public Article getById(int articleId);

	public ArrayList<BlogComment> getComments(ArgMap<ArticleArg> args);

	public int getCount();

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void hideComment(int commentId);

	public ArrayList<Article> list(ArgMap<ArticleArg> args);

	@PreAuthorize("hasAnyRole('GROUP_ADMINISTRATORS', 'BLOG_CONTRIBUTORS')")
	public Article save(Article article);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public BlogComment saveComment(BlogComment comment);
}
