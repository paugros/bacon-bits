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
import com.areahomeschoolers.baconbits.server.dao.Suggestible;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ArticleArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Article;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.NewsBulletinComment;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestionData;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

@Repository
public class ArticleDaoImpl extends SpringWrapper implements ArticleDao, Suggestible {

	private final class AdMapper implements RowMapper<Resource> {
		@Override
		public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
			Resource ad = new Resource();
			ad.setId(rs.getInt("id"));
			ad.setAddedById(rs.getInt("addedById"));
			ad.setStartDate(rs.getTimestamp("startDate"));
			ad.setEndDate(rs.getTimestamp("endDate"));
			ad.setAddedDate(rs.getTimestamp("addedDate"));
			ad.setName(rs.getString("name"));
			ad.setClickCount(rs.getInt("clickCount"));
			ad.setLastClickDate(rs.getTimestamp("lastClickDate"));
			ad.setUrl(rs.getString("url"));
			ad.setDocumentId(rs.getInt("documentId"));
			ad.setDescription(rs.getString("description"));
			ad.setCategory(rs.getString("category"));
			ad.setCategoryId(rs.getInt("categoryId"));
			ad.setPhone(rs.getString("phone"));
			ad.setCity(rs.getString("city"));
			ad.setZip(rs.getString("zip"));
			ad.setState(rs.getString("state"));
			ad.setAddress(rs.getString("address"));
			ad.setStreet(rs.getString("street"));
			ad.setLat(rs.getDouble("lat"));
			ad.setLng(rs.getDouble("lng"));

			return ad;
		}
	}

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
			article.setAddedByFirstName(rs.getString("firstName"));
			article.setAddedByLastName(rs.getString("lastName"));
			article.setCommentCount(rs.getInt("commentCount"));
			article.setLastCommentDate(rs.getTimestamp("lastCommentDate"));
			article.setImageDocumentId(rs.getInt("smallImageId"));
			return article;
		}
	}

	private final class NewsBulletinCommentMapper implements RowMapper<NewsBulletinComment> {
		@Override
		public NewsBulletinComment mapRow(ResultSet rs, int rowNum) throws SQLException {
			NewsBulletinComment comment = new NewsBulletinComment();
			comment.setId(rs.getInt("id"));
			comment.setUserId(rs.getInt("userId"));

			String safeHtml = new SafeHtmlBuilder().appendEscaped(rs.getString("comment")).toSafeHtml().asString();
			comment.setComment(Common.makeUrlsClickable(safeHtml).replaceAll("\\\n", "<br/>"));
			comment.addedBy(rs.getString("addedBy"));
			comment.setArticleId(rs.getInt("articleId"));
			comment.setAddedDate(rs.getTimestamp("addedDate"));
			comment.setImageDocumentId(rs.getInt("smallImageId"));
			return comment;
		}
	}

	@Autowired
	public ArticleDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public void clickResource(int adId) {
		String sql = "update resources set clickCount = clickCount + 1, lastClickDate = now() where id = ?";
		update(sql, adId);

		sql = "insert into adClicks (resourceId, ipAddress, clickDate) values(?, ?, now())";
		update(sql, adId, ServerContext.getRequest().getRemoteAddr());
	}

	public String createSqlBase() {
		String sql = "select a.*, g.groupName, l.visibilityLevel, u.firstName, u.lastName, u.smallImageId, \n";
		sql += "(select count(id) from documentArticleMapping where articleId = a.id) as documentCount, \n";
		sql += "(select count(id) from comments where articleId = a.id and endDate is null) as commentCount, \n";
		sql += "(select addedDate from comments where articleId = a.id and endDate is null order by addedDate desc limit 1) as lastCommentDate, \n";
		sql += "(select count(id) from tagArticleMapping where articleId = a.id) as tagCount \n";
		sql += "from articles a \n";
		sql += "join users u on u.id = a.addedById \n";
		sql += "left join groups g on g.id = a.groupId \n";
		sql += "join itemVisibilityLevels l on l.id = a.visibilityLevelId \n";

		sql += createWhere();
		return sql;
	}

	@Override
	public Article getById(int articleId) {
		String sql = createSqlBase() + "and a.id = ?";

		return queryForObject(sql, new ArticleMapper(), articleId);
	}

	@Override
	public ArrayList<NewsBulletinComment> getComments(ArgMap<ArticleArg> args) {
		int newsId = args.getInt(ArticleArg.ARTICLE_ID);
		int commentId = args.getInt(ArticleArg.COMMENT_ID);
		List<Object> sqlArgs = new ArrayList<Object>();

		String sql = "select c.*, concat(u.firstName, ' ', u.LastName) as addedBy, u.smallImageId ";
		sql += "from comments c ";
		sql += "join users u on u.id = c.userId ";
		sql += "where 1 = 1 and c.endDate is null ";
		if (newsId > 0) {
			sql += "and c.articleId = ? ";
			sqlArgs.add(newsId);
		}
		if (commentId > 0) {
			sql += "and c.id = ? ";
			sqlArgs.add(commentId);
		}
		sql += "order by c.addedDate desc";

		return query(sql, new NewsBulletinCommentMapper(), sqlArgs.toArray());
	}

	@Override
	public ArrayList<Resource> getResources(ArgMap<ResourceArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int id = args.getInt(ResourceArg.ID);
		int limit = args.getInt(ResourceArg.LIMIT);
		boolean random = args.getBoolean(ResourceArg.RANDOM);

		String sql = "select r.*, u.firstName, u.lastName, rc.category \n";
		sql += "from resources r \n";
		sql += "join users u on u.id = r.addedById \n";
		sql += "join resourceCategories rc on rc.id = r.categoryId \n";
		sql += "where 1 = 1 ";

		if (args.getStatus() != Status.ALL) {
			sql += "and isActive(r.startDate, r.endDate) = " + (args.getStatus() == Status.ACTIVE ? "1" : "0") + " \n";
		}

		if (id > 0) {
			sql += "and r.id = ? ";
			sqlArgs.add(id);
		}

		if (random) {
			sql += "order by rand() ";
		}

		if (limit > 0) {
			sql += "limit " + limit;
		}

		ArrayList<Resource> data = query(sql, new AdMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public ServerSuggestionData getSuggestionData(String token, int limit, Data options) {
		ServerSuggestionData data = new ServerSuggestionData();

		String sql = "select a.id, a.title as Suggestion, 'Article' as entityType ";
		sql += "from articles a ";
		sql += createWhere();
		sql += "and isActive(a.startDate, a.endDate) = 1 ";
		sql += "and a.title like ? ";
		sql += "and a.newsItem = 0 ";
		sql += "order by a.title ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";
		data.setSuggestions(query(sql, ServerUtils.getSuggestionMapper(), search));
		return data;
	}

	@Override
	public void hideComment(int commentId) {
		String sql = "update comments set endDate = now(), hiddenById = ? where id = ?";
		update(sql, commentId, ServerContext.getCurrentUserId());
	}

	@Override
	public ArrayList<Article> list(ArgMap<ArticleArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int top = args.getInt(ArticleArg.MOST_RECENT);
		String idString = args.getString(ArticleArg.IDS);
		int orgId = args.getInt(ArticleArg.OWNING_ORG_ID);
		boolean newsOnly = args.getBoolean(ArticleArg.NEWS_ONLY);
		int beforeId = args.getInt(ArticleArg.BEFORE_ID);
		String search = args.getString(ArticleArg.SEARCH);
		Date beforeDate = args.getDate(ArticleArg.BEFORE_DATE);
		Date afterDate = args.getDate(ArticleArg.AFTER_DATE);
		int articleId = args.getInt(ArticleArg.ARTICLE_ID);

		String sql = createSqlBase();

		sql += "and a.owningOrgId = " + ServerContext.getCurrentOrgId() + " \n";

		if (orgId > 0) {
			sql += "and a.owningOrgId = ? ";
			sqlArgs.add(orgId);
		}

		if (newsOnly) {
			sql += "and a.newsItem = 1 ";
		} else {
			sql += "and a.newsItem = 0 ";
		}

		if (beforeId > 0) {
			sql += "and a.id < ? ";
			sqlArgs.add(beforeId);
		}

		if (!search.isEmpty()) {
			sql += "and concat(a.title, ' ', a.article) like ? ";
			sqlArgs.add("%" + search + "%");
		}

		if (beforeDate != null) {
			sql += "and a.addedDate < ? ";
			sqlArgs.add(beforeDate);
		}

		if (afterDate != null) {
			sql += "and a.addedDate > ? ";
			sqlArgs.add(afterDate);
		}

		if (articleId > 0) {
			sql += "and a.id = ? ";
			sqlArgs.add(articleId);
		}

		if (args.getStatus() != Status.ALL) {
			sql += "and isActive(a.startDate, a.endDate) = " + (args.getStatus() == Status.ACTIVE ? "1" : "0") + " \n";
		}

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
			sql += "groupId = :groupId, visibilityLevelId = :visibilityLevelId, newsItem = :newsItem ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			if (article.getStartDate() == null) {
				article.setStartDate(new Date());
			}
			article.setAddedById(ServerContext.getCurrentUserId());
			article.setOwningOrgId(ServerContext.getCurrentOrgId());

			String sql = "insert into articles (addedById, startDate, endDate, addedDate, title, article, groupId, visibilityLevelId, owningOrgId, newsItem) values ";
			sql += "(:addedById, :startDate, :endDate, now(), :title, :article, :groupId, :visibilityLevelId, :owningOrgId, :newsItem)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			article.setId(ServerUtils.getIdFromKeys(keys));

			if (article.getGroupPolicy() != null) {
				sql = "update groups set " + article.getGroupPolicy().getColumn() + " = ? where id = ?";
				update(sql, article.getId(), article.getOwningOrgId());
				ServerContext.getCurrentOrg().setPolicyId(article.getGroupPolicy(), article.getId());
			}
		}

		return getById(article.getId());
	}

	@Override
	public NewsBulletinComment saveComment(NewsBulletinComment comment) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(comment);

		if (comment.isSaved()) {
			String sql = "update comments set comment = :comment ";
			sql += "where id = :id";

			update(sql, namedParams);
		} else {
			comment.setUserId(ServerContext.getCurrentUser().getId());
			String sql = "insert comments (comment, userId, articleId) ";
			sql += "values (:comment, :userId, :articleId)";
			KeyHolder key = new GeneratedKeyHolder();
			update(sql, namedParams, key);

			int id = ServerUtils.getIdFromKeys(key);
			return getComments(new ArgMap<ArticleArg>(ArticleArg.COMMENT_ID, id)).get(0);
		}

		return comment;
	}

	@Override
	public Resource saveResource(Resource r) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(r);

		if (r.isSaved()) {
			String sql = "update resources set name = :name, startDate = :startDate, endDate = :endDate, url = :url, description = :description, ";
			sql += "address = :address, street = :street, city = :city, state = :state, zip = :zip, lat = :lat, lng = :lng, phone = :phone ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			if (r.getStartDate() == null) {
				r.setStartDate(new Date());
			}
			r.setAddedById(ServerContext.getCurrentUserId());

			String sql = "insert into resources (addedById, startDate, endDate, addedDate, name, url, description, ";
			sql += "address, street, city, state, zip, lat, lng, phone, categoryId) ";
			sql += "values(:addedById, :startDate, :endDate, now(), :name, :url, :description, ";
			sql += ":address, :street, :city, :state, :zip, :lat, :lng, :phone, :categoryId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			r.setId(ServerUtils.getIdFromKeys(keys));
		}

		return getResources(new ArgMap<ResourceArg>(ResourceArg.ID, r.getId())).get(0);
	}

	private String createWhere() {
		int userId = ServerContext.getCurrentUserId();
		String sql = "left join userGroupMembers ugm on ugm.groupId = a.groupId and ugm.userId = " + userId + " \n";
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

}
