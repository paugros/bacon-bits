package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.server.dao.ArticleDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;
import com.areahomeschoolers.baconbits.shared.dto.Resource;

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
	public void clickResource(int adId) {
		dao.clickResource(adId);
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
	public ArrayList<Resource> getResources(ArgMap<ResourceArg> args) {
		return dao.getResources(args);
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
	public NewsBulletinComment saveComment(NewsBulletinComment comment) {
		return dao.saveComment(comment);
	}

	@Override
	public Resource saveResource(Resource ad) {
		return dao.saveResource(ad);
	}

}
