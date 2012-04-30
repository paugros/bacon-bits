package com.areahomeschoolers.baconbits.server.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.UserPreferenceService;
import com.areahomeschoolers.baconbits.server.spring.GWTController;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Data;

@Controller
@RequestMapping("/userPreference")
public class UserPreferenceServiceImpl extends GWTController implements UserPreferenceService {

	private static final long serialVersionUID = 1L;

	private SpringWrapper wrapper;

	@Autowired
	public UserPreferenceServiceImpl(DataSource ds) {
		wrapper = new SpringWrapper(ds);
	}

	@Override
	public Data getPreferencesByGroupName(int userId, String group) {
		String sql = "";

		return wrapper.queryForObject(sql, ServerUtils.getGenericRowMapper(), group);
	}

	@Override
	public void set(int userId, String key, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public Data setPreferencesByGroupPrefix(int userId, String group, Data prefs) {
		// TODO Auto-generated method stub
		return null;
	}

}
