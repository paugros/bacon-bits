package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

@Controller
@RequestMapping("/user")
public class UserServiceImpl extends GwtController implements UserService {

	private static final long serialVersionUID = 1L;
	private final UserDao dao;

	@Autowired
	public UserServiceImpl(UserDao dao) {
		this.dao = dao;

	}

	@Override
	public User getById(int userId) {
		return dao.getById(userId);
	}

	@Override
	public UserPageData getPageData(int userId) {
		return dao.getPageData(userId);
	}

	@Override
	public User getUserByUsername(String username) {
		return dao.getUserByUsername(username);
	}

	@Override
	public ArrayList<User> list(ArgMap<UserArg> args) {
		return dao.list(args);
	}

	@Override
	public User save(User user) {
		return dao.save(user);
	}

}
