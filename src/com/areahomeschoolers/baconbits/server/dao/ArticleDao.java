package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Ad;
import com.areahomeschoolers.baconbits.shared.dto.Arg.AdArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;

public interface ArticleDao {
	public ArrayList<Ad> getAds(ArgMap<AdArg> args);

	public Article getById(int articleId);

	public ArrayList<NewsBulletinComment> getComments(ArgMap<ArticleArg> args);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void hideComment(int commentId);

	public ArrayList<Article> list(ArgMap<ArticleArg> args);

	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public Article save(Article article);

	@PreAuthorize("hasRole('SYSTEM_ADMINISTRATORS')")
	public Ad saveAd(Ad ad);

	@PreAuthorize("hasRole('SITE_MEMBERS')")
	public NewsBulletinComment saveComment(NewsBulletinComment comment);
}
