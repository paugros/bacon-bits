package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;

public interface ArticleDao {
	public Article getById(int articleId);

	public ArrayList<Article> list(ArgMap<ArticleArg> args);

	@PreAuthorize("hasRole('ROLE_BASIC_USER')")
	public Article save(Article article);
}
