package com.areahomeschoolers.baconbits.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.areahomeschoolers.baconbits.shared.Common;
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
			article.setDocumentCount(rs.getInt("documentCount"));
			article.setTagCount(rs.getInt("tagCount"));
			article.setVisibilityLevel(rs.getString("visibilityLevel"));
			article.setVisibilityLevelId(rs.getInt("visibilityLevelId"));
			article.setOwningOrgId(rs.getInt("owningOrgId"));
			return article;
		}
	}

	@Autowired
	public ArticleDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	public String createSqlBase() {
		String sql = "select a.*, g.groupName, l.visibilityLevel, \n";
		sql += "(select count(id) from documentArticleMapping where articleId = a.id) as documentCount, \n";
		sql += "(select count(id) from tagArticleMapping where articleId = a.id) as tagCount \n";
		sql += "from articles a \n";
		sql += "left join groups g on g.id = a.groupId \n";
		sql += "join itemVisibilityLevels l on l.id = a.visibilityLevelId \n";

		int userId = ServerContext.getCurrentUserId();
		sql += "left join userGroupMembers ugm on ugm.groupId = a.groupId and ugm.userId = " + userId + " \n";
		sql += "left join userGroupMembers org on org.groupId = a.owningOrgId and org.userId = " + userId + " \n";
		sql += "where 1 = 1 \n";

		int auth = ServerContext.isAuthenticated() ? 1 : 0;
		if (!ServerContext.isSystemAdministrator()) {
			sql += "and case a.visibilityLevelId ";
			sql += "when 1 then 1 ";
			sql += "when 2 then " + auth + " \n";
			sql += "when 4 then (ugm.id > 0 or org.isAdministrator) \n";
			sql += "else 0 end > 0 \n";
		}

		return sql;
	}

	@Override
	public Article getById(int articleId) {
		String sql = createSqlBase() + "and a.id = ?";

		return queryForObject(sql, new ArticleMapper(), articleId);
	}

	@Override
	public ArrayList<Article> list(ArgMap<ArticleArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int top = args.getInt(ArticleArg.MOST_RECENT);
		String idString = args.getString(ArticleArg.IDS);

		String sql = createSqlBase();

		if (!Common.isNullOrBlank(idString)) {
			List<String> scrubbedIds = new ArrayList<String>();
			scrubbedIds.add("0");
			String[] ids = idString.split(",");
			for (int i = 0; i < ids.length; i++) {
				if (Common.isNumeric(ids[i])) {
					scrubbedIds.add(ids[i]);
				}
			}

			sql += "and a.id in(" + Common.join(scrubbedIds, ", ") + ") ";
			sql += "order by field(a.id, " + Common.join(scrubbedIds, ", ") + ")";
		} else if (top > 0) {
			sql += "order by a.id desc limit ?";
			sqlArgs.add(top);
		}

		ArrayList<Article> data = query(sql, new ArticleMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public Article save(Article article) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(article);

		if (article.isSaved()) {
			String sql = "update articles set title = :title, article = :article, startDate = :startDate, endDate = :endDate, ";
			sql += "groupId = :groupId, visibilityLevelId = :visibilityLevelId ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			if (article.getStartDate() == null) {
				article.setStartDate(new Date());
			}
			article.setAddedById(ServerContext.getCurrentUser().getId());

			String sql = "insert into articles (addedById, startDate, endDate, addedDate, title, article, groupId, visibilityLevelId, owningOrgId) values ";
			sql += "(:addedById, :startDate, :endDate, now(), :title, :article, :groupId, :visibilityLevelId, :owningOrgId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			article.setId(ServerUtils.getIdFromKeys(keys));
		}

		return getById(article.getId());
	}

}
