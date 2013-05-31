package com.areahomeschoolers.baconbits.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.Suggestible;
import com.areahomeschoolers.baconbits.server.dao.TagDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;
import com.areahomeschoolers.baconbits.shared.dto.Tag;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

@Repository
public class TagDaoImpl extends SpringWrapper implements TagDao, Suggestible {

	@Autowired
	public TagDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Tag addMapping(Tag tag) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(tag);
		String sql = "";
		if (!tag.isSaved()) {
			// new tag dupe validation
			String name = tag.getName();

			name = name.trim();
			name = name.replaceAll("[^0-9A-Za-z ]+", "");
			name = name.replaceAll("\\s+", " ");

			String[] words = name.split(" ");
			for (int i = 0; i < words.length; i++) {
				String word = words[i];
				if (Common.isAllLowerCase(word)) {
					words[i] = Common.ucWords(word);
				}
			}

			String nameout = Common.join(words, " ");
			int tagId = queryForInt(0, "select id from tags where lower(name) = ?", nameout.toLowerCase());
			if (tagId > 0) {
				tag.setId(tagId);
			} else {
				tag.setName(nameout);
				tag.setAddedById(ServerContext.getCurrentUserId());
				sql = "insert into tags (name, addedById) values(:name, :addedById)";
				KeyHolder keys = new GeneratedKeyHolder();
				update(sql, namedParams, keys);
				tag.setId(ServerUtils.getIdFromKeys(keys));
			}
		}

		sql = "insert into " + tag.getMappingTable() + "(" + tag.getMappingColumn() + ", tagId) ";
		sql += "values(:entityId, :id)";

		KeyHolder keys = new GeneratedKeyHolder();
		update(sql, namedParams, keys);

		ArgMap<TagArg> args = new ArgMap<TagArg>(TagArg.MAPPING_ID, ServerUtils.getIdFromKeys(keys));
		args.put(TagArg.MAPPING_TYPE, tag.getMappingType().toString());
		Tag mapped = list(args).get(0);
		mapped.setEntityId(tag.getEntityId());
		return mapped;
	}

	@Override
	public void deleteMapping(Tag tag) {
		String sql = "delete from " + tag.getMappingTable() + " where id = ?";
		update(sql, tag.getMappingId());
	}

	@Override
	public ArrayList<ServerSuggestion> getSuggestions(String token, int limit, Data options) {
		String sql = "select t.id, t.name as Suggestion, 'Tag' as entityType ";
		sql += "from tags t ";
		sql += "where t.name like ? ";
		sql += "order by t.name ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";
		return query(sql, ServerUtils.getSuggestionMapper(), search);
	}

	@Override
	public ArrayList<Tag> list(ArgMap<TagArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		final int entityId = args.getInt(TagArg.ENTITY_ID);
		final TagMappingType mappingType = Common.isNullOrBlank(args.getString(TagArg.MAPPING_TYPE)) ? null : TagMappingType.valueOf(args
				.getString(TagArg.MAPPING_TYPE));
		int mappingId = args.getInt(TagArg.MAPPING_ID);

		// tags
		String sql = "select t.id, t.name, t.addedDate, t.addedById ";
		if (mappingType != null) {
			sql += ", tm.id as mappingId, tm.addedDate as mappingAddedDate ";
		}
		sql += "from tags t ";
		if (mappingType != null) {
			sql += "join tag" + Common.ucWords(mappingType.toString()) + "Mapping tm on tm.tagId = t.id ";
			if (entityId > 0) {
				sql += "and tm." + mappingType.toString().toLowerCase() + "Id = ? ";
				sqlArgs.add(entityId);
			}

			if (mappingId > 0) {
				sql += "and tm.id = ? ";
				sqlArgs.add(mappingId);
			}

			sql += "order by tm.id";
		} else {
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
				if (entityId > 0) {
					t.setEntityId(entityId);
				}
				if (mappingType != null) {
					t.setMappingType(mappingType);
					t.setMappingId(rs.getInt("mappingId"));
					t.setMappingAddedDate(rs.getTimestamp("mappingAddedDate"));
				}
				return t;
			}
		}, sqlArgs.toArray());
	}

}
