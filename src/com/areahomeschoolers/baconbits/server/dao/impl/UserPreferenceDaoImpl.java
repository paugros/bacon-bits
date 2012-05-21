package com.areahomeschoolers.baconbits.server.dao.impl;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.UserPreferenceDao;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Data;

@Repository
public class UserPreferenceDaoImpl extends SpringWrapper implements UserPreferenceDao {

	@Autowired
	public UserPreferenceDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Data getPreferencesByGroupName(int userId, String group) {
		String sql = "";

		return queryForObject(sql, ServerUtils.getGenericRowMapper(), group);
	}

	@Override
	public void set(int userId, String key, String value) {

	}

	@Override
	public Data setPreferencesByGroupPrefix(int userId, String group, Data prefs) {
		return null;
	}

}
