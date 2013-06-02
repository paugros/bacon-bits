package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.dao.impl.UserDaoImpl;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.server.util.Mailer;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Email;
import com.areahomeschoolers.baconbits.shared.dto.PollResponseData;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
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
	public PollResponseData getPollData() {
		return dao.getPollData();
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
	public ArrayList<UserGroup> listGroups(ArgMap<UserGroupArg> args) {
		return dao.listGroups(args);
	}

	@Override
	public HashMap<Integer, Boolean> refreshSecurityGroups() {
		return dao.refreshSecurityGroups();
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
			mail.addTo(user.getFullName() + " <" + user.getEmail() + ">");
			mail.setSubject("WHE Login Information");
			String msg = "Hello,\n\n";
			if (!user.isSaved()) {
				msg += "A login account has been created for you at WHE. \n\n";
			} else {
				msg += "The password for your WHE account has been reset. ";
			}
			msg += "Login information appears below. You will be required to establish a new password upon logging in.\n\n";
			msg += "Site: " + ServerContext.getBaseUrl() + "\n";
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
	public UserGroup saveUserGroup(UserGroup group) {
		return dao.saveUserGroup(group);
	}

	@Override
	public void sendEmail(Email email) {
		Mailer mailer = new Mailer();
		mailer.setEmail(email);

		mailer.send();
	}

	@Override
	public void switchToUser(int userId) {
		ServerContext.switchToUser(userId);
	}

	@Override
	public void updateUserGroupRelation(ArrayList<User> users, UserGroup g, boolean add) {
		dao.updateUserGroupRelation(users, g, add);
	}

	@Override
	public void updateUserGroupRelation(User u, ArrayList<UserGroup> g, boolean add) {
		dao.updateUserGroupRelation(u, g, add);
	}

	@Override
	public void updateUserGroupRelation(User u, UserGroup g, boolean add) {
		dao.updateUserGroupRelation(u, g, add);
	}

	@Override
	public ServerResponseData<String> validatePassword(String password) {
		ServerResponseData<String> data = new ServerResponseData<String>();
		data.setErrors(Common.asArrayList(dao.validatePassword(password)));
		return data;
	}

}
