package com.areahomeschoolers.baconbits.server.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.server.spring.GWTController;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
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
			article.setStartDate(rs.getTimestamp("startDate"));
			article.setEndDate(rs.getTimestamp("endDate"));
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
	public Article getById(int articleId) {
		String sql = "select * from articles where id = ?";

		return wrapper.queryForObject(sql, new ArticleMapper(), articleId);
	}

	@Override
	public ArrayList<Article> list(ArgMap<ArticleArg> args) {
		String sql = "select * from articles";
		ArrayList<Article> data = wrapper.query(sql, new ArticleMapper());

		return data;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_BASIC_USER')")
	public Article save(Article article) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(article);

		if (article.isSaved()) {
			String sql = "update articles set title = :title, article = :article, startDate = :startDate, endDate = :endDate, groupId = :groupId where id = :id";
			wrapper.update(sql, namedParams);
		} else {
			if (article.getStartDate() == null) {
				article.setStartDate(new Date());
			}
			String sql = "insert into articles (addedById, startDate, endDate, addedDate, title, article, groupId) values ";
			sql += "(:addedById, :startDate, :endDate, now(), :title, :article, :groupId)";

			KeyHolder keys = new GeneratedKeyHolder();
			wrapper.update(sql, namedParams, keys);

			article.setId(ServerUtils.getIdFromKeys(keys));
		}

		return getById(article.getId());
	}

}
