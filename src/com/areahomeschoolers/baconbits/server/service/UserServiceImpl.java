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
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Email;
import com.areahomeschoolers.baconbits.shared.dto.GroupData;
import com.areahomeschoolers.baconbits.shared.dto.HistoryEntry;
import com.areahomeschoolers.baconbits.shared.dto.MainMenuItem;
import com.areahomeschoolers.baconbits.shared.dto.PollResponseData;
import com.areahomeschoolers.baconbits.shared.dto.PollUpdateData;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreference;
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
	public void deleteMenuItem(MainMenuItem item) {
		dao.deleteMenuItem(item);
	}

	@Override
	public User getById(int userId) {
		return dao.getById(userId);
	}

	@Override
	public ArrayList<MainMenuItem> getMenuItems(ArgMap<UserArg> args) {
		return dao.getMenuItems(args);
	}

	@Override
	public ArrayList<HistoryEntry> getNavigationHistory(int userId) {
		return dao.getNavigationHistory(userId);
	}

	@Override
	public UserPageData getPageData(int userId) {
		return dao.getPageData(userId);
	}

	@Override
	public PollResponseData getPollData(PollUpdateData pollData) {
		return dao.getPollData(pollData);
	}

	@Override
	public User getUserByUsername(String username) {
		return dao.getUserByUsername(username);
	}

	@Override
	public ArrayList<Data> linkResource(User user, int resourceId) {
		return dao.linkResource(user, resourceId);
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
	public HashMap<Integer, GroupData> refreshSecurityGroups() {
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
			String sn = ServerContext.getCurrentOrg().getOrganizationName();
			mail.setSubject(sn + " Login Information");
			String msg = "Hello,\n\n";
			if (!user.isSaved()) {
				msg += "A login account has been created for you with " + sn + ". \n\n";
			} else {
				msg += "The password for your account with " + sn + " has been reset. ";
			}
			msg += "Login information appears below. You will be required to establish a new password upon logging in.\n\n";
			msg += "Site: " + ServerContext.getBaseUrlWithoutSeparator() + "\n";
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
	public MainMenuItem saveMenuItem(MainMenuItem item) {
		return dao.saveMenuItem(item);
	}

	@Override
	public PrivacyPreference savePrivacyPreference(PrivacyPreference privacyPreference) {
		return dao.savePrivacyPreference(privacyPreference);
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
	public void setCurrentLocation(String location, double lat, double lng, int radius) {
		dao.setCurrentLocation(location, lat, lng, radius);
	}

	@Override
	public void switchToUser(int userId) {
		ServerContext.switchToUser(userId);
	}

	@Override
	public void unLinkResource(User user, int resourceId) {
		dao.unLinkResource(user, resourceId);
	}

	@Override
	public void updateMenuOrdinals(ArrayList<MainMenuItem> items) {
		dao.updateMenuOrdinals(items);
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
