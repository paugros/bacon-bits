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
	public Tag addMapping(Tag mappingTag) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(mappingTag);

		String sql = "insert into " + mappingTag.getMappingTable() + "(" + mappingTag.getMappingColumn() + ", tagId) ";
		sql += "values(:entityId, :id)";

		KeyHolder keys = new GeneratedKeyHolder();
		update(sql, namedParams, keys);

		ArgMap<TagArg> args = new ArgMap<TagArg>(TagArg.MAPPING_ID, ServerUtils.getIdFromKeys(keys));
		args.put(TagArg.MAPPING_TYPE, mappingTag.getMappingType().toString());
		return list(args).get(0);
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
		String sql = "select t.name, t.addedDate, t.addedById, tm.id, tm.tagId, tm.addedDate as mappingAddedDate from tags t ";
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
		}

		return query(sql, new RowMapper<Tag>() {
			@Override
			public Tag mapRow(ResultSet rs, int rowNum) throws SQLException {
				Tag t = new Tag();
				t.setId(rs.getInt("tagId"));
				t.setMappingId(rs.getInt("id"));
				t.setName(rs.getString("name"));
				t.setAddedDate(rs.getTimestamp("addedDate"));
				t.setMappingAddedDate(rs.getTimestamp("mappingAddedDate"));
				t.setAddedById(rs.getInt("addedById"));
				t.setEntityId(entityId);
				t.setMappingType(mappingType);
				return t;
			}
		}, sqlArgs.toArray());
	}

	@Override
	public Tag save(Tag tag) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(tag);

		if (tag.isSaved()) {
			String sql = "update tags set title = :title, tag = :tag, startDate = :startDate, endDate = :endDate, ";
			sql += "groupId = :groupId, accessLevelId = :accessLevelId ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			String sql = "insert into tags (addedById, startDate, endDate, addedDate, title, tag, groupId, accessLevelId) values ";
			sql += "(:addedById, :startDate, :endDate, now(), :title, :tag, :groupId, :accessLevelId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			tag.setId(ServerUtils.getIdFromKeys(keys));
		}

		return tag;
	}

}
