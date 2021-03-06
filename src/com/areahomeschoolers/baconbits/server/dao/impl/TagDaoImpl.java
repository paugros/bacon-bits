package com.areahomeschoolers.baconbits.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.ArticleDao;
import com.areahomeschoolers.baconbits.server.dao.EventDao;
import com.areahomeschoolers.baconbits.server.dao.Suggestible;
import com.areahomeschoolers.baconbits.server.dao.TagDao;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestionData;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;

@Repository
public class TagDaoImpl extends SpringWrapper implements TagDao, Suggestible {
	public static String createWhere(TagType type, int withinMiles, String withinLat, String withinLng, String state) {
		String sql = "";
		switch (type) {
		case ARTICLE:
			ArticleDao ad = ServerContext.getDaoImpl("article");
			sql += ad.createWhere();
			sql += "and a.newsItem = 0 and isActive(a.startDate, a.endDate) = 1 and a.firstTagId is not null \n";
			break;
		case BOOK:
			sql += "join users u on u.id = b.userId \n";
			sql += "where b.statusId = 1 \n";
			sql += "and b.userId in(select userId from userGroupMembers where groupId = " + Constants.ONLINE_BOOK_SELLERS_GROUP_ID + ") \n";
			sql += "and isActive(u.startDate, u.endDate) = 1 \n";
			break;
		case EVENT:
			EventDao ed = ServerContext.getDaoImpl("event");
			sql += ed.createWhere() + " and e.endDate > now() and e.active = 1 and e.firstTagId is not null \n";
			break;
		case RESOURCE:
			sql += "where isActive(r.startDate, r.endDate) = 1 and r.firstTagId is not null \n";
			break;
		case USER:
			UserDao ud = ServerContext.getDaoImpl("user");
			sql += "left join userGroupMembers org on org.userId = u.id and org.groupId = " + ServerContext.getCurrentOrgId() + " \n";
			sql += ud.createWhere() + " ";
			sql += "and isActive(u.startDate, u.endDate) = 1 \n";
			break;
		default:
			break;
		}

		if (!type.equals(TagType.ARTICLE)) {
			String tableAlias = type.toString().substring(0, 1).toLowerCase();
			if (type.equals(TagType.BOOK)) {
				tableAlias = "u";
			}

			if (!Common.isNullOrBlank(state) && state.matches("^[A-Z]{2}$")) {
				sql += "and (" + tableAlias + ".state = '" + state + "'";
				if (EnumSet.of(TagType.RESOURCE, TagType.EVENT).contains(type)) {
					sql += "or (" + tableAlias + ".address is null or " + tableAlias + ".address = '')";
				}
				sql += ") \n";
			}

			if (withinMiles > 0 && !Common.isNullOrBlank(withinLat) && !Common.isNullOrBlank(withinLng)) {
				String within = ServerUtils.getDistanceSql(tableAlias, withinLat, withinLng) + " < " + withinMiles + " ";
				if (EnumSet.of(TagType.RESOURCE, TagType.EVENT).contains(type)) {
					sql += "and ((" + within + ") or " + tableAlias + ".address is null or " + tableAlias + ".address = '') ";
				} else {
					sql += "and " + within + "\n";
				}
			}
		}

		return sql;
	}

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	@Autowired
	public TagDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Tag addMapping(Tag tag) {
		if (!tag.isSaved()) {
			tag = addNewTag(tag);
		}

		// this is when we're adding tags on an add page
		if (tag.getEntityId() == 0) {
			return tag;
		}

		Tag mapped = null;
		try {
			SqlParameterSource namedParams = new BeanPropertySqlParameterSource(tag);
			String sql = "insert into " + tag.getMappingTable() + "(" + tag.getMappingColumn() + ", tagId) ";
			sql += "values(:entityId, :id)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			ArgMap<TagArg> args = new ArgMap<TagArg>(TagArg.MAPPING_ID, ServerUtils.getIdFromKeys(keys));
			args.put(TagArg.TYPE, tag.getMappingType().toString());
			mapped = list(args).get(0);
			mapped.setEntityId(tag.getEntityId());

			updateFirstTagColumn(tag);
		} catch (DataIntegrityViolationException e) {
			logger.warning(e.getMessage());
		}

		return mapped;
	}

	@Override
	public void addMappings(int entityId, HashSet<Tag> tags) {
		for (Tag tag : tags) {
			tag.setEntityId(entityId);
			addMapping(tag);
		}
	}

	@Override
	public void delete(int tagId) {
		update("delete from tags where id = ?", tagId);
		updateFirstTags();
	}

	@Override
	public void deleteMapping(Tag tag) {
		String sql = "delete from " + tag.getMappingTable() + " where id = ?";
		update(sql, tag.getMappingId());

		updateFirstTagColumn(tag);
	}

	@Override
	public ServerSuggestionData getSuggestionData(String token, int limit, Data options) {
		ServerSuggestionData data = new ServerSuggestionData();

		String sql = "select t.id, t.name as Suggestion, 'Tag' as entityType ";
		sql += "from tags t ";
		sql += "where t.name like ? ";
		sql += "order by t.name ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";
		data.setSuggestions(query(sql, ServerUtils.getSuggestionMapper(), search));

		return data;
	}

	@Override
	public ArrayList<Tag> list(ArgMap<TagArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		final int entityId = args.getInt(TagArg.ENTITY_ID);
		final int tagId = args.getInt(TagArg.TAG_ID);
		final boolean getCounts = args.getBoolean(TagArg.GET_COUNTS);
		final boolean getAllCounts = args.getBoolean(TagArg.GET_ALL_COUNTS);
		final TagType mappingType = Common.isNullOrBlank(args.getString(TagArg.TYPE)) ? null : TagType.valueOf(args.getString(TagArg.TYPE));
		int mappingId = args.getInt(TagArg.MAPPING_ID);

		boolean locationFilter = args.getBoolean(TagArg.LOCATION_FILTER);
		int withinMiles = ServerContext.getCurrentRadius();
		String withinLat = Double.toString(ServerContext.getCurrentLat());
		String withinLng = Double.toString(ServerContext.getCurrentLng());
		String loc = ServerContext.getCurrentLocation();
		String state = null;
		if (ServerContext.getCurrentLocation() != null && loc.length() == 2 && ServerContext.getCurrentLat() == 0) {
			state = loc;
		}
		if (!locationFilter) {
			state = null;
			withinMiles = 0;
		}

		// tags
		String always = "t.id, t.name, t.addedDate, t.addedById, t.imageId, t.smallImageId, d.fileExtension \n";
		String sql = "select " + always;

		if (mappingType != null) {
			if (getCounts) {
				sql += ", count(tm.id) as count \n";
			} else {
				sql += ", tm.id as mappingId, tm.addedDate as mappingAddedDate \n";
			}
		}
		if (getAllCounts) {
			sql += ", tm.count \n";
		}
		sql += "from tags t \n";
		sql += "left join documents d on d.id = t.imageId \n";
		if (getAllCounts) {
			sql += "left join (select tagId, count(id) as count from (\n";
			EnumSet<TagType> types = EnumSet.allOf(TagType.class);
			Iterator<TagType> i = types.iterator();
			while (i.hasNext()) {
				String type = i.next().toString();
				sql += "select id, tagId from tag" + Common.ucWords(type.toString()) + "Mapping \n";
				if (i.hasNext()) {
					sql += "union all \n";
				}
			}
			sql += ") tcm group by tagId) tm on tm.tagId = t.id \n";
		}

		String where = "where 1 = 1 ";
		if (tagId > 0) {
			where += "and t.id = ? ";
			sqlArgs.add(tagId);
		}

		if (mappingType != null) {
			sql += "join tag" + Common.ucWords(mappingType.toString()) + "Mapping tm on tm.tagId = t.id \n";
			// add in joins for tag type
			String alias = mappingType.toString().substring(0, 1).toLowerCase();
			String lc = mappingType.toString().toLowerCase();
			sql += "join " + lc + "s " + alias + " on " + alias + ".id = tm." + lc + "Id \n";

			if (entityId > 0) {
				sql += "and tm." + mappingType.toString().toLowerCase() + "Id = ? \n";
				sqlArgs.add(entityId);
			}

			if (mappingId > 0) {
				sql += "and tm.id = ? ";
				sqlArgs.add(mappingId);
			}

			if (getCounts) {
				sql += createWhere(mappingType, withinMiles, withinLat, withinLng, state);
				sql += "group by " + always;
				sql += "order by t.name";
			} else {
				sql += where;
				sql += "order by tm.id";
			}
		} else {
			sql += where;
			sql += "order by t.name";
		}

		return query(sql, new RowMapper<Tag>() {
			@Override
			public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
				Tag t = new Tag();
				t.setId(rs.getInt("id"));
				t.setName(rs.getString("name"));
				t.setAddedDate(rs.getTimestamp("addedDate"));
				t.setAddedById(rs.getInt("addedById"));
				t.setImageId(rs.getInt("imageId"));
				t.setSmallImageId(rs.getInt("smallImageId"));
				t.setImageExtension(rs.getString("fileExtension"));
				if (entityId > 0) {
					t.setEntityId(entityId);
				}
				if (getAllCounts) {
					t.setCount(rs.getInt("count"));
				}

				if (mappingType != null) {
					if (getCounts) {
						t.setCount(rs.getInt("count"));
					} else {
						t.setMappingType(mappingType);
						t.setMappingId(rs.getInt("mappingId"));
						t.setMappingAddedDate(rs.getTimestamp("mappingAddedDate"));
					}
				}
				return t;
			}
		}, sqlArgs.toArray());
	}

	@Override
	public Tag merge(Tag tag, HashSet<Integer> tagIds) {
		Tag newTag = addNewTag(tag);

		// in case the new tag is already in the selected
		tagIds.remove(newTag.getId());

		for (TagType type : TagType.values()) {
			String item = Common.ucWords(type.toString());
			String sql = "update ignore tag" + item + "Mapping set tagId = ? where tagId in(" + Common.join(tagIds, ", ") + ") ";
			update(sql, newTag.getId());
		}

		update("delete from tags where id in(" + Common.join(tagIds, ", ") + ")");

		updateFirstTags();

		ArgMap<TagArg> args = new ArgMap<>(TagArg.TAG_ID, newTag.getId());
		args.put(TagArg.GET_ALL_COUNTS);

		return list(args).get(0);
	}

	@Override
	public Tag save(Tag tag) {
		if (tag.isSaved()) {
			update("update tags set name = ? where id = ?", tag.getName(), tag.getId());
			return tag;
		}

		tag = addNewTag(tag);

		return list(new ArgMap<TagArg>(TagArg.TAG_ID, tag.getId())).get(0);
	}

	private Tag addNewTag(Tag tag) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(tag);

		// new tag dupe validation
		String name = tag.getName();

		name = name.trim();
		name = name.replaceAll("[^0-9A-Za-z \\&\\-\\$]+", "");
		name = name.replaceAll("\\s+", " ");

		String[] words = name.split(" ");
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if (Common.isAllLowerCase(word)) {
				words[i] = Common.ucWords(word);
			}
		}

		String nameout = Common.join(words, " ");
		String sql = "select id, name from tags where lower(name) = ? or lower(name) = ? limit 1";
		Data existingName = queryForObject(sql, ServerUtils.getGenericRowMapper(), nameout.toLowerCase(), nameout.toLowerCase() + "s");
		if (existingName != null) {
			tag.setId(existingName.getId());
			tag.setName(existingName.get("name"));
		} else {
			tag.setName(nameout);
			tag.setAddedById(ServerContext.getCurrentUserId());
			sql = "insert into tags (name, addedById) values(:name, :addedById)";
			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);
			tag.setId(ServerUtils.getIdFromKeys(keys));
		}

		return tag;
	}

	private void updateFirstTagColumn(Tag tag) {
		String col = Common.ucWords(tag.getMappingType().toString());
		String sql = "update " + col.toLowerCase() + "s tbl set firstTagId = ";
		sql += "(select tagId from tag" + col + "Mapping tm where " + tag.getMappingColumn() + " = tbl.id order by tm.id limit 1)";
		sql += "where id = ?";
		update(sql, tag.getEntityId());
	}

	private void updateFirstTags() {
		for (TagType t : TagType.values()) {
			String tbl = t.toString().toLowerCase();
			String sql = "update " + tbl + "s tbl set firstTagId = ";
			sql += "(select tagId from tag" + Common.ucWords(tbl) + "Mapping tm where " + tbl + "Id = tbl.id order by tm.id limit 1) where firstTagId is null";
			update(sql);
		}
	}

}
