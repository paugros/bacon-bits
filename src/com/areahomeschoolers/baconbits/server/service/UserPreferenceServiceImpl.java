package com.areahomeschoolers.baconbits.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.UserPreferenceService;
import com.areahomeschoolers.baconbits.server.dao.UserPreferenceDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Data;

@Controller
@RequestMapping("/userPreference")
public class UserPreferenceServiceImpl extends GwtController implements UserPreferenceService {

	private static final long serialVersionUID = 1L;
	private final UserPreferenceDao dao;

	@Autowired
	public UserPreferenceServiceImpl(UserPreferenceDao dao) {
		this.dao = dao;
	}

	@Override
	public Data getPreferencesByGroupName(int userId, String group) {
		return dao.getPreferencesByGroupName(userId, group);
	}

	@Override
	public void set(int userId, String key, String value) {
		dao.set(userId, key, value);
	}

	@Override
	public Data setPreferencesByGroupPrefix(int userId, String group, Data prefs) {
		return dao.setPreferencesByGroupPrefix(userId, group, prefs);
	}

}
