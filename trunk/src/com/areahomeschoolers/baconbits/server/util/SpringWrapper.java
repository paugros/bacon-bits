package com.areahomeschoolers.baconbits.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.areahomeschoolers.baconbits.shared.Common;

public class SpringWrapper {

	private final DataSource dataSource;
	private final JdbcTemplate template;
	private final NamedParameterJdbcTemplate namedTemplate;

	// private final NamedParameterJdbcTemplate namedTemplate;

	public SpringWrapper(DataSource dataSource) {
		this.dataSource = dataSource;
		template = new JdbcTemplate(dataSource);
		namedTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public SpringWrapper(JdbcDaoSupport daoSupport) {
		dataSource = daoSupport.getDataSource();
		template = new JdbcTemplate(dataSource);
		namedTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
		return template.batchUpdate(sql, batchArgs);
	}

	public int[] batchUpdate(String sql, SqlParameterSource[] source) {
		return namedTemplate.batchUpdate(sql, source);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public JdbcTemplate getTemplate() {
		return template;
	}

	public <T> ArrayList<T> query(String sql, RowMapper<T> mapper, List<Object> args) {
		return query(sql, mapper, args.toArray());
	}

	public <T> ArrayList<T> query(String sql, RowMapper<T> mapper, Object... args) {
		return Common.asArrayList(template.query(sql, mapper, args));
	}

	public <T> ArrayList<T> query(String sql, RowMapper<T> mapper, SqlParameterSource source) {
		return Common.asArrayList(namedTemplate.query(sql, source, mapper));
	}

	public int queryForInt(int defaultValue, String sql, List<Object> args) {
		return queryForInt(defaultValue, sql, args.toArray());
	}

	public int queryForInt(int defaultValue, String sql, Object... args) {
		try {
			return template.queryForInt(sql, args);
		} catch (EmptyResultDataAccessException e) {
			return defaultValue;
		}
	}

	public int queryForInt(int defaultValue, String sql, SqlParameterSource source) {
		try {
			return namedTemplate.queryForInt(sql, source);
		} catch (EmptyResultDataAccessException e) {
			return defaultValue;
		}
	}

	public int queryForInt(String sql, List<Object> args) {
		return queryForInt(sql, args.toArray());
	}

	public int queryForInt(String sql, Object... args) {
		return template.queryForInt(sql, args);
	}

	public int queryForInt(String sql, SqlParameterSource source) {
		return namedTemplate.queryForInt(sql, source);
	}

	public Map<String, Object> queryForMap(String sql) {
		return template.queryForMap(sql);
	}

	public <T> T queryForObject(String sql, Class<T> cls, List<Object> args) {
		return queryForObject(sql, cls, args.toArray());
	}

	public <T> T queryForObject(String sql, Class<T> cls, Object... args) {
		T object;

		try {
			object = template.queryForObject(sql, cls, args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

		return object;
	}

	public <T> T queryForObject(String sql, Class<T> cls, SqlParameterSource source) {
		T object;

		try {
			object = namedTemplate.queryForObject(sql, source, cls);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

		return object;
	}

	public <T> T queryForObject(String sql, RowMapper<T> mapper, List<Object> args) {
		return queryForObject(sql, mapper, args.toArray());
	}

	public <T> T queryForObject(String sql, RowMapper<T> mapper, Object... args) {
		T object;

		try {
			object = template.queryForObject(sql, mapper, args);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

		return object;
	}

	public <T> T queryForObject(String sql, RowMapper<T> mapper, SqlParameterSource source) {
		T object;

		try {
			object = namedTemplate.queryForObject(sql, source, mapper);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}

		return object;
	}

	public SqlRowSet queryForRowSet(String sql, List<Object> args) {
		return queryForRowSet(sql, args.toArray());
	}

	public SqlRowSet queryForRowSet(String sql, Map<String, ?> map) {
		return template.queryForRowSet(sql, map);
	}

	public SqlRowSet queryForRowSet(String sql, Object... args) {
		return template.queryForRowSet(sql, args);
	}

	public SqlRowSet queryForRowSet(String sql, Object[] args, int[] argTypes) {
		return template.queryForRowSet(sql, args, argTypes);
	}

	public SqlRowSet queryForRowSet(String sql, SqlParameterSource source) {
		return namedTemplate.queryForRowSet(sql, source);
	}

	public <T> int update(String sql, List<Object> args) {
		return update(sql, args.toArray());
	}

	public <T> int update(String sql, Object... args) {
		return template.update(sql, args);
	}

	public <T> int update(String sql, SqlParameterSource source) {
		return namedTemplate.update(sql, source);
	}

	public int update(String sql, SqlParameterSource source, KeyHolder keyHolder) {
		return namedTemplate.update(sql, source, keyHolder);
	}
}
