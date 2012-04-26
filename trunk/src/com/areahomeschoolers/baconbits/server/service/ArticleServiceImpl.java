package com.areahomeschoolers.baconbits.server.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.server.spring.GWTController;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Article;

@Controller
@RequestMapping("/article")
public class ArticleServiceImpl extends GWTController implements ArticleService {

	private final class ArticleMapper implements RowMapper<Article> {
		@Override
		public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
			Article article = new Article();
			article.setId(rs.getInt("id"));
			article.setAddedById(rs.getInt("addedById"));
			article.setAddedDate(rs.getTimestamp("addedDate"));
			article.setTitle(rs.getString("title"));
			article.setArticle(rs.getString("article"));
			return article;
		}
	}

	private static final long serialVersionUID = 1L;

	private SpringWrapper wrapper;

	@Autowired
	public ArticleServiceImpl(DataSource ds) {
		wrapper = new SpringWrapper(ds);
	}

	@Override
	public ArrayList<Article> getArticles() {
		String sql = "select * from articles";
		ArrayList<Article> data = wrapper.query(sql, new ArticleMapper());

		return data;
	}

}
