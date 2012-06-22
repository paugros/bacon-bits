package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.dao.impl.UserDaoImpl;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.server.util.Mailer;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
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
	public ServerResponseData<User> save(User user) {
		String password = "";
		if (user.getGeneratePassword()) {
			password = UserDaoImpl.generatePassword();
			user.setPassword(password);
		}

		ServerResponseData<User> response = dao.save(user);

		if (user.getGeneratePassword() && !response.hasErrors()) {
			Mailer mail = new Mailer();
			mail.useSystemFrom();
			mail.addTo(user.getFullName() + " <" + user.getEmail() + ">");
			mail.setSubject("Area Homeschoolers Login Information");
			String msg = "Hello,\n\n";
			if (!user.isSaved()) {
				msg += "A login account has been created for you at Area Homeschoolers. \n\n";
			} else {
				msg += "The password for your Area Homeschoolers account has been reset. ";
			}
			msg += "Login information appears below. You will be required to establish a new password upon logging in.\n\n";
			msg += "Site: http://areahomeschoolers.appspot.com/\n";
			msg += "User name: " + user.getUserName() + "\n";
			msg += "Password: " + password + "\n\n";
			msg += "Thank you.\n\n";
			mail.setBody(msg);
			mail.send();

			response.getData().setGeneratePassword(false);
		}
		return response;
	}

	@Override
	public ServerResponseData<String> validatePassword(String password) {
		ServerResponseData<String> data = new ServerResponseData<String>();
		data.setErrors(Common.asArrayList(dao.validatePassword(password)));
		return data;
	}

}
