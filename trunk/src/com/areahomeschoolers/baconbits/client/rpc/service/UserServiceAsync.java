package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
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

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserServiceAsync {
	public void deleteMenuItem(MainMenuItem item, AsyncCallback<Void> callback);

	public void getById(int userId, AsyncCallback<User> callback);

	public void getMenuItems(ArgMap<UserArg> args, AsyncCallback<ArrayList<MainMenuItem>> callback);

	public void getPageData(int userId, AsyncCallback<UserPageData> callback);

	public void getPollData(PollUpdateData pollData, AsyncCallback<PollResponseData> callback);

	public void getUserByUsername(String username, AsyncCallback<User> callback);

	public void list(ArgMap<UserArg> args, AsyncCallback<ArrayList<User>> callback);

	public void listGroups(ArgMap<UserGroupArg> args, AsyncCallback<ArrayList<UserGroup>> callback);

	public void refreshSecurityGroups(AsyncCallback<HashMap<Integer, GroupData>> callback);

	public void save(User user, AsyncCallback<ServerResponseData<User>> callback);

	public void saveMenuItem(MainMenuItem item, AsyncCallback<MainMenuItem> callback);

	public void savePrivacyPreference(PrivacyPreference privacyPreference, AsyncCallback<PrivacyPreference> callback);

	public void saveUserGroup(UserGroup group, AsyncCallback<UserGroup> callback);

	public void sendEmail(Email email, AsyncCallback<Void> callback);

	public void switchToUser(int userId, AsyncCallback<Void> callback);

	public void updateMenuOrdinals(ArrayList<MainMenuItem> items, AsyncCallback<Void> callback);

	public void updateUserGroupRelation(User u, UserGroup g, boolean add, AsyncCallback<Void> callback);

	public void validatePassword(String password, AsyncCallback<ServerResponseData<String>> callback);

	void getNavigationHistory(int userId, AsyncCallback<ArrayList<HistoryEntry>> callback);

	void setCurrentLocation(String location, double lat, double lng, AsyncCallback<Void> callback);
}
