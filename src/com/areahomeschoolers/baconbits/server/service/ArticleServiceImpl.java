package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.server.dao.ArticleDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Ad;
import com.areahomeschoolers.baconbits.shared.dto.Arg.AdArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;

@Controller
@RequestMapping("/article")
public class ArticleServiceImpl extends GwtController implements ArticleService {

	private static final long serialVersionUID = 1L;

	private ArticleDao dao;

	@Autowired
	public ArticleServiceImpl(ArticleDao dao) {
		this.dao = dao;
	}

	@Override
	public void clickAd(int adId) {
		dao.clickAd(adId);
	}

	@Override
	public ArrayList<Ad> getAds(ArgMap<AdArg> args) {
		return dao.getAds(args);
	}

	@Override
	public Article getById(int articleId) {
		return dao.getById(articleId);
	}

	@Override
	public ArrayList<NewsBulletinComment> getComments(ArgMap<ArticleArg> args) {
		return dao.getComments(args);
	}

	@Override
	public void hideComment(int commentId) {
		dao.hideComment(commentId);
	}

	@Override
	public ArrayList<Article> list(ArgMap<ArticleArg> args) {
		return dao.list(args);
	}

	@Override
	public Article save(Article article) {
		return dao.save(article);
	}

	@Override
	public Ad saveAd(Ad ad) {
		return dao.saveAd(ad);
	}

	@Override
	public NewsBulletinComment saveComment(NewsBulletinComment comment) {
		return dao.saveComment(comment);
	}

}
