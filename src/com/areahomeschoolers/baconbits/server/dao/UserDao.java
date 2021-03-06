package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;

import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
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

public interface UserDao {
	public String createWhere();

	@PreAuthorize("hasRole('ORGANIZATION_ADMINISTRATORS')")
	public void deleteMenuItem(MainMenuItem item);

	public void doUpdateUserActivity(int userId);

	public User getById(int userId);

	public User getById(int userId, boolean useSecureMapper);

	public int getCount();

	public ArrayList<MainMenuItem> getMenuItems(ArgMap<UserArg> args);

	public ArrayList<HistoryEntry> getNavigationHistory(int userId);

	public UserGroup getOrgForCurrentRequest();

	public UserPageData getPageData(int userId);

	public PollResponseData getPollData(PollUpdateData pollData);

	public User getUserByUsername(String username);

	public ArrayList<Data> linkResource(User user, int resourceId);

	public ArrayList<User> list(ArgMap<UserArg> args);

	public ArrayList<UserGroup> listGroups(ArgMap<UserGroupArg> args);

	public void recordLogin(String username);

	public HashMap<Integer, GroupData> refreshSecurityGroups();

	public ServerResponseData<User> save(User user);

	@PreAuthorize("hasRole('ORGANIZATION_ADMINISTRATORS')")
	public MainMenuItem saveMenuItem(MainMenuItem item);

	public PrivacyPreference savePrivacyPreference(PrivacyPreference privacyPreference);

	public UserGroup saveUserGroup(UserGroup group);

	public boolean sendPasswordResetEmail(String username);

	public void setCurrentLocation(String location, double lat, double lng, int radius);

	public User setPasswordFromDigest(int id, String digest);

	public void unLinkResource(User user, int resourceId);

	@PreAuthorize("hasRole('ORGANIZATION_ADMINISTRATORS')")
	public void updateMenuOrdinals(ArrayList<MainMenuItem> items);

	public void updateUserGroupRelation(User u, UserGroup g, boolean add);

	public List<String> validatePassword(String password);
}
