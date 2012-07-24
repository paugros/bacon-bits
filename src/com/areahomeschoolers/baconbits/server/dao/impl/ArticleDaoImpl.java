package com.areahomeschoolers.baconbits.server.dao.impl;

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
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.ArticleDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Article;

@Repository
public class ArticleDaoImpl extends SpringWrapper implements ArticleDao {

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
			article.setGroupName(rs.getString("groupName"));
			return article;
		}
	}

	private static String SELECT;

	@Autowired
	public ArticleDaoImpl(DataSource dataSource) {
		super(dataSource);
		SELECT = "select a.*, g.groupName from articles a ";
		SELECT += "left join groups g on g.id = a.groupId ";
	}

	@Override
	public Article getById(int articleId) {
		String sql = SELECT + " where a.id = ?";

		return queryForObject(sql, new ArticleMapper(), articleId);
	}

	@Override
	public ArrayList<Article> list(ArgMap<ArticleArg> args) {
		String sql = SELECT;
		ArrayList<Article> data = query(sql, new ArticleMapper());

		return data;
	}

	@Override
	public Article save(Article article) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(article);

		if (article.isSaved()) {
			String sql = "update articles set title = :title, article = :article, startDate = :startDate, endDate = :endDate, groupId = :groupId ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			if (article.getStartDate() == null) {
				article.setStartDate(new Date());
			}
			article.setAddedById(ServerContext.getCurrentUser().getId());

			String sql = "insert into articles (addedById, startDate, endDate, addedDate, title, article, groupId) values ";
			sql += "(:addedById, :startDate, :endDate, now(), :title, :article, :groupId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			article.setId(ServerUtils.getIdFromKeys(keys));
		}

		return getById(article.getId());
	}

}
