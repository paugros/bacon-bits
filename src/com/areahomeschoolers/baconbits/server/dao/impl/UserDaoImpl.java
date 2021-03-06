package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.server.dao.PaymentDao;
import com.areahomeschoolers.baconbits.server.dao.ResourceDao;
import com.areahomeschoolers.baconbits.server.dao.Suggestible;
import com.areahomeschoolers.baconbits.server.dao.TagDao;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.util.Mailer;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserGroupArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.GroupData;
import com.areahomeschoolers.baconbits.shared.dto.HistoryEntry;
import com.areahomeschoolers.baconbits.shared.dto.MainMenuItem;
import com.areahomeschoolers.baconbits.shared.dto.PollResponseData;
import com.areahomeschoolers.baconbits.shared.dto.PollUpdateData;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreference;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreferenceType;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestionData;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.vt.middleware.password.CharacterCharacteristicsRule;
import edu.vt.middleware.password.CharacterRule;
import edu.vt.middleware.password.DigitCharacterRule;
import edu.vt.middleware.password.LengthRule;
import edu.vt.middleware.password.LowercaseCharacterRule;
import edu.vt.middleware.password.MessageResolver;
import edu.vt.middleware.password.Password;
import edu.vt.middleware.password.PasswordData;
import edu.vt.middleware.password.PasswordGenerator;
import edu.vt.middleware.password.PasswordValidator;
import edu.vt.middleware.password.RepeatCharacterRegexRule;
import edu.vt.middleware.password.Rule;
import edu.vt.middleware.password.RuleResult;
import edu.vt.middleware.password.UppercaseCharacterRule;
import edu.vt.middleware.password.WhitespaceRule;

@Repository
public class UserDaoImpl extends SpringWrapper implements UserDao, Suggestible {
	private final class GroupMapper implements RowMapper<UserGroup> {
		@Override
		public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
			return createUserGroup(rs);
		}
	}

	private final class GroupMemberMapper implements RowMapper<UserGroup> {
		@Override
		public UserGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserGroup g = createUserGroup(rs);
			g.setAdministrator(rs.getBoolean("isAdministrator"));
			g.setGroupApproved(rs.getBoolean("groupApproved"));
			g.setUserApproved(rs.getBoolean("userApproved"));

			return g;
		}
	}

	private final class InsecureUserMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return createUser(rs, false);
		}
	}

	private final class SecureUserMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			return createUser(rs, true);
		}
	}

	// used only on the members tab of the group page
	private final class SpecialGroupUserMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User u = createUser(rs, true);
			u.setGroupApproved(rs.getBoolean("groupApproved"));
			u.setUserApproved(rs.getBoolean("userApproved"));
			GroupData ug = new GroupData();
			ug.setAdministrator(rs.getBoolean("isAdministrator"));
			u.getGroups().put(rs.getInt("groupId"), ug);
			return u;
		}
	}

	private static Map<Integer, Date> userActivity;

	public static MessageResolver resolver = null;

	public final static String generatePassword() {
		// create a password generator
		PasswordGenerator generator = new PasswordGenerator();

		// create character rules to generate passwords with
		List<CharacterRule> rules = new ArrayList<CharacterRule>();
		rules.add(new DigitCharacterRule(2));
		// rules.add(new NonAlphanumericCharacterRule(1));
		rules.add(new UppercaseCharacterRule(2));
		rules.add(new LowercaseCharacterRule(3));

		return generator.generatePassword(8, rules);
	}

	public static LinkedHashMap<Integer, Date> getAllUserActivity() {
		synchronized (userActivity) {
			return new LinkedHashMap<Integer, Date>(userActivity);
		}
	}

	public final static String getSha1Hash(String input, String saltText) {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(512);

		return encoder.encodePassword(input, saltText);
	}

	public static LinkedHashMap<Integer, Date> getUserActivitySinceLastPoll(int userId) {
		LinkedHashMap<Integer, Date> map = new LinkedHashMap<Integer, Date>();
		boolean beginRecording = false;
		if (userId == 0) {
			beginRecording = true;
		}

		Set<Integer> keySet = userActivity.keySet();
		synchronized (userActivity) {
			for (Integer i : keySet) {
				if (beginRecording) {
					map.put(i, userActivity.get(i));
				}

				if (i == userId) {
					beginRecording = true;
				}
			}
		}

		if (userId > 0 && !ServerContext.getCurrentUser().isSwitched()) {
			map.put(userId, new Date());
		}

		return map;
	}

	public static void updateUserActivity(int userId) {
		ApplicationContext ctx = ServerContext.getApplicationContext();
		UserDao userDao = (UserDao) ctx.getBean("userDaoImpl");
		userDao.doUpdateUserActivity(userId);
	}

	@Autowired
	public UserDaoImpl(DataSource dataSource) {
		super(dataSource);

		// initialize user activity data
		userActivity = Collections.synchronizedMap(new LinkedHashMap<Integer, Date>());
		String sql = "select u.id, u.lastActivityDate ";
		sql += "from users u ";
		sql += "where isActive(u.startDate, u.endDate) = 1 ";
		sql += "and u.lastActivityDate is not null order by u.lastActivityDate";
		query(sql, new RowMapper<Void>() {
			@Override
			public Void mapRow(ResultSet rs, int rowNum) throws SQLException {
				userActivity.put(rs.getInt("id"), rs.getTimestamp("lastActivityDate"));
				return null;
			}
		});
	}

	@Override
	public String createWhere() {
		String sql = "where 1 = 1 ";
		if (!ServerContext.isAuthenticated()) {
			sql += "and u.directoryOptOut = 0 \n";
		} else if (!ServerContext.isSystemAdministrator()) {
			int id = ServerContext.getCurrentUserId();
			if (ServerContext.getCurrentUser().administratorOf(ServerContext.getCurrentOrgId())) {
				sql += "and org.id is not null \n";
			} else {
				sql += "and (u.directoryOptOut = 0 or u.id = " + id + " or u.parentId = " + id + ") \n";
			}
		}

		return sql;
	}

	@Override
	public void deleteMenuItem(MainMenuItem item) {
		String sql = "delete from menuItems where id = ?";
		update(sql, item.getId());
	}

	@Override
	public synchronized void doUpdateUserActivity(int userId) {
		// don't update if switched
		if (ServerContext.getCurrentUser().isSwitched()) {
			return;
		}

		// we remove first so that the new entry goes to the bottom
		userActivity.remove(userId);
		userActivity.put(userId, new Date());

		String sql = "update users set lastActivityDate = now() where id = ?";
		update(sql, userId);
	}

	@Override
	public User getById(int userId) {
		return getById(userId, true);
	}

	@Override
	public User getById(int userId, boolean useSecureMapper) {
		// we can't do any sort of isAuthenticate or currentUser checks unless we're using the secure mapper
		// otherwise we won't be able to initialize a logging-in user
		String sql = createSqlBase();
		if (useSecureMapper) {
			sql += createWhere();
		} else {
			sql += "where 1 = 1 ";
		}
		sql += "and u.id = ?";

		User u = queryForObject(sql, useSecureMapper ? new SecureUserMapper() : new InsecureUserMapper(), userId);

		if (u == null) {
			return null;
		}

		if (useSecureMapper) {
			// if you're not an org admin, and the user has opted out, and the user isn't you...
			if (!ServerContext.getCurrentUser().hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS) && u.getDirectoryOptOut()
					&& !u.equals(ServerContext.getCurrentUser())) {
				// and either you're not authenticated, or you're not an admin or parent of the person
				if (!ServerContext.isAuthenticated() || !(ServerContext.getCurrentUser().administratorOf(u) || ServerContext.getCurrentUser().parentOf(u))) {
					// and you aren't switched to that person, then you can't see them
					if (!ServerContext.getCurrentUser().isSwitched()) {
						return null;
					}
				}
			}
		}

		return u;
	}

	@Override
	public int getCount() {
		String sql = "select count(*) from users u ";
		Double latD = ServerContext.getCurrentLat();
		String lat = latD == null ? null : Double.toString(latD);
		Double lngD = ServerContext.getCurrentLng();
		String lng = lngD == null ? null : Double.toString(lngD);
		sql += TagDaoImpl.createWhere(TagType.USER, Constants.DEFAULT_SEARCH_RADIUS, lat, lng, null);

		return queryForInt(sql);
	}

	@Override
	public ArrayList<MainMenuItem> getMenuItems(ArgMap<UserArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();

		int organizationId = args.getInt(UserArg.ORGANIZATION_ID);
		String sql = "select * from menuItems m ";
		sql += "where m.organizationId = ? ";
		sqlArgs.add(organizationId);
		sql += "order by m.parentNodeId, m.ordinal";

		final HashMap<Integer, MainMenuItem> treeMap = new HashMap<Integer, MainMenuItem>();
		final ArrayList<MainMenuItem> items = new ArrayList<MainMenuItem>();

		query(sql, new RowMapper<MainMenuItem>() {
			@Override
			public MainMenuItem mapRow(ResultSet rs, int rowNum) throws SQLException {
				MainMenuItem m = new MainMenuItem();
				m.setId(rs.getInt("id"));
				m.setAddedById(rs.getInt("addedById"));
				m.setAddedDate(rs.getTimestamp("addedDate"));
				m.setArticleIds(rs.getString("articleIds"));
				m.setGroupId(rs.getInt("groupId"));
				m.setName(rs.getString("name"));
				m.setOwningOrgId(rs.getInt("organizationId"));
				m.setParentNodeId(rs.getInt("parentNodeId"));
				m.setUrl(rs.getString("url"));
				m.setVisibilityLevelId(rs.getInt("visibilityLevelId"));
				m.setOrdinal(rs.getInt("ordinal"));

				treeMap.put(m.getId(), m);

				// add item to list if it's a root node, otherwise to its parent
				if (m.getParentNodeId() == null) {
					items.add(m);
				} else {
					treeMap.get(m.getParentNodeId()).addItem(m);
				}

				return m;
			}
		}, sqlArgs.toArray());

		return items;
	}

	@Override
	public ArrayList<HistoryEntry> getNavigationHistory(int userId) {
		String sql = "select distinct historyToken, pageTitle from ";
		sql += "(select historyToken, pageTitle from ";
		sql += "userHistory where userId = ? and organizationId = ? order by id desc limit 200) as tbl limit 25";

		return query(sql, new RowMapper<HistoryEntry>() {
			@Override
			public HistoryEntry mapRow(ResultSet arg0, int arg1) throws SQLException {
				return new HistoryEntry(arg0.getString("pageTitle"), arg0.getString("historyToken"));
			}
		}, userId, ServerContext.getCurrentOrgId());
	}

	@Override
	public UserGroup getOrgForCurrentRequest() {
		String subDomain = "";
		String domain = "";

		String cdn = ServerContext.getRequest().getServerName();
		if (cdn != null) {
			if (cdn.contains(".")) {
				String[] parts = cdn.split("\\.");
				subDomain = parts[0];
				domain = parts[parts.length - 2] + "." + parts[parts.length - 1];
			} else {
				subDomain = domain = cdn;
			}
		}

		// try sub-domain first
		List<UserGroup> grps;
		ArgMap<UserGroupArg> args = new ArgMap<UserGroupArg>();
		if (!subDomain.isEmpty()) {
			args.put(UserGroupArg.ORG_SUB_DOMAIN, subDomain);

			grps = listGroups(args);
			if (!grps.isEmpty()) {
				return grps.get(0);
			}
		}

		// then the domain
		if (!domain.isEmpty()) {
			args.remove(UserGroupArg.ORG_SUB_DOMAIN);
			args.put(UserGroupArg.ORG_DOMAIN, domain);
			grps = listGroups(args);
			if (!grps.isEmpty()) {
				return grps.get(0);
			}
		}

		args.remove(UserGroupArg.ORG_SUB_DOMAIN);
		args.remove(UserGroupArg.ORG_DOMAIN);
		args.put(UserGroupArg.ID, Constants.CG_ORG_ID);

		return listGroups(args).get(0);
	}

	@Override
	public UserPageData getPageData(final int userId) {
		UserPageData pd = new UserPageData();
		if (userId > 0) {
			if (!ServerContext.isSystemAdministrator() && !ServerContext.isPhantomJsRequest()) {
				update("update users set viewCount = viewCount + 1 where id = ?", userId);
			}

			pd.setUser(getById(userId));
			TagDao tagDao = ServerContext.getDaoImpl("tag");
			ArgMap<TagArg> args = new ArgMap<TagArg>(TagArg.ENTITY_ID, userId);
			args.put(TagArg.TYPE, TagType.USER.toString());
			pd.setInterests(tagDao.list(args));

			pd.setResources(getResources(pd.getUser()));

			if (pd.getUser() == null) {
				return null;
			}
		} else {
			User u = new User();
			u.setGeneratePassword(true);
			pd.setUser(u);
		}

		return pd;
	}

	@Override
	public PollResponseData getPollData(PollUpdateData pollData) {
		Integer userId = ServerContext.getCurrentUserId();
		if (userId == 0) {
			userId = null;
		}
		PollResponseData data = new PollResponseData();

		if (ServerContext.isAuthenticated()) {
			// update the user's last active date
			UserDaoImpl.updateUserActivity(userId);

			PaymentDao paymentDao = ServerContext.getDaoImpl("payment");
			data.setUnpaidBalance(paymentDao.getUnpaidBalance(ServerContext.getCurrentUserId()));
		}

		data.setUserActivity(getUserActivitySinceLastPoll(ServerContext.getCurrentUserId()));

		if (pollData.hasHistoryUpdates()) {
			ArrayList<Object[]> historyUpdate = new ArrayList<Object[]>();
			for (HistoryEntry he : pollData.getHistoryUpdates()) {
				Integer orgId = ServerContext.getCurrentOrgId();
				if (orgId == 0) {
					orgId = null;
				}
				historyUpdate.add(new Object[] { orgId, userId, he.getTitle(), he.getUrl(), ServerContext.getRequest().getRemoteAddr() });
			}
			batchUpdate("insert into userHistory(organizationId, userID, pageTitle, historyToken, ipAddress) values(?, ?, ?, ?, ?)", historyUpdate);
		}

		if (userId != null) {
			data.setHistoryItems(getNavigationHistory(userId));
		}

		return data;
	}

	@Override
	public ServerSuggestionData getSuggestionData(String token, int limit, Data options) {
		ServerSuggestionData data = new ServerSuggestionData();
		if (!ServerContext.isAuthenticated()) {
			return data;
		}

		String sql = "select u.id, concat(u.firstName, ' ', u.lastName, ' - ', u.email) as Suggestion, 'User' as entityType ";
		sql += "from users u ";
		sql += "where u.email is not null and u.email != '' ";
		sql += "and (concat(u.firstName, ' ', u.lastName) like ? or u.email like ?) and isActive(u.startDate, u.endDate) = 1 ";
		if (!ServerContext.getCurrentUser().hasRole(AccessLevel.ORGANIZATION_ADMINISTRATORS) && !ServerContext.getCurrentUser().isSwitched()) {
			sql += "and u.directoryOptOut = 0 ";
		}
		sql += "order by concat(u.firstName, ' ', u.lastName) ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";

		data.setSuggestions(query(sql, ServerUtils.getSuggestionMapper(), search, search));

		return data;
	}

	@Override
	public User getUserByUsername(String username) {
		// this is only to be used after a successful login. otherwise it could expose private user data.
		if (username == null) {
			throw new UsernameNotFoundException("Username null not found");
		}

		User user = queryForObject(createSqlBase() + "where u.email = ?", new InsecureUserMapper(), username.toLowerCase());
		if (user == null) {
			throw new UsernameNotFoundException("Username not found or duplicate: " + username);
		}

		return user;
	}

	@Override
	public ArrayList<Data> linkResource(User user, int resourceId) {
		String sql = "";
		try {
			sql = "insert into resourceUserMapping(resourceId, userId) values(?, ?)";
			update(sql, resourceId, user.getId());
		} catch (Exception e) {
		}

		return getResources(user);
	}

	@Override
	public ArrayList<User> list(ArgMap<UserArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		List<Integer> tagIds = args.getIntList(UserArg.HAS_TAGS);
		int parentIdPlusSelf = args.getInt(UserArg.PARENT_ID_PLUS_SELF);
		int parentId = args.getInt(UserArg.PARENT_ID);
		int registrationId = args.getInt(UserArg.NOT_ON_REGISTRATION_ID);
		int groupId = args.getInt(UserArg.GROUP_ID);
		int adminOfId = args.getInt(UserArg.ADMIN_OF_GROUP_ID);
		int activeNumber = args.getInt(UserArg.ACTIVE_NUMBER);
		int newNumber = args.getInt(UserArg.NEW_NUMBER);
		boolean onlyParents = args.getBoolean(UserArg.PARENTS);
		boolean onlyChildren = args.getBoolean(UserArg.CHILDREN);
		boolean parentsOfBoys = args.getBoolean(UserArg.PARENTS_OF_BOYS);
		boolean parentsOfGirls = args.getBoolean(UserArg.PARENTS_OF_GIRLS);
		boolean onlyCommonInterests = args.getBoolean(UserArg.ONLY_COMMON_INTERESTS);
		boolean hasEmail = args.getBoolean(UserArg.HAS_EMAIL);
		String sex = args.getString(UserArg.SEX);
		List<Integer> ages = args.getIntList(UserArg.AGES);
		String addressSearch = args.getString(UserArg.ADDRESS_SEARCH);
		boolean locationFilter = args.getBoolean(UserArg.LOCATION_FILTER);
		int withinMiles = ServerContext.getCurrentRadius();
		String withinLat = Double.toString(ServerContext.getCurrentLat());
		String withinLng = Double.toString(ServerContext.getCurrentLng());
		String loc = ServerContext.getCurrentLocation();
		String state = null;
		if (ServerContext.getCurrentLocation() != null && loc.length() == 2 && ServerContext.getCurrentLat() == 0) {
			state = loc;
		}
		String groupCols = "";
		int minAge = 0;
		int maxAge = 0;

		if (ages.size() > 1) {
			minAge = ages.get(0);
			maxAge = ages.get(1);
		}

		if (groupId > 0) {
			groupCols += "ugm.userApproved, ugm.groupApproved, ugm.isAdministrator, ugm.groupId, \n";
		}

		String sql = createSqlBase(groupCols);

		if (groupId > 0) {
			sql += "join userGroupMembers ugm on ugm.userId = u.id and ugm.groupId = ? \n";
			sqlArgs.add(groupId);
		}

		if (adminOfId > 0) {
			sql += "join userGroupMembers ugm on ugm.userId = u.id and ugm.groupId = ? and ugm.isAdministrator = 1 \n";
			sqlArgs.add(adminOfId);
		}

		if (registrationId > 0) {
			sql += "left join eventRegistrationParticipants p on p.userId = u.id and p.eventRegistrationId = ? \n";
			sqlArgs.add(registrationId);
		}
		sql += createWhere();

		if (!Common.isNullOrEmpty(tagIds)) {
			sql += "and u.id in(select tm.userId from tagUserMapping tm ";
			sql += "join tags t on t.id = tm.tagId ";
			sql += "where t.id in(" + Common.join(tagIds, ", ") + ")) ";
		}

		if (locationFilter && !Common.isNullOrBlank(state) && state.matches("^[A-Z]{2}$")) {
			sql += "and u.state = '" + state + "' \n";
		}

		if (hasEmail) {
			sql += "and u.email is not null and u.email != '' \n";
		}

		if (locationFilter && withinMiles > 0) {
			sql += "and " + ServerUtils.getDistanceSql("u", withinLat, withinLng) + " < " + withinMiles + " ";
		}

		// start directory logic //
		if (onlyParents) {
			sql += "and u.birthDate < date_add(now(), interval -18 year) and u.passwordDigest is not null ";
			if (ages.size() > 1) {
				sql += "and (select count(id) from users where parentId = u.id ";
				sql += "and (datediff(now(), birthDate) / 365.25) between " + minAge + " and " + maxAge + ") > 0 ";
			}
		}

		if (parentsOfBoys) {
			sql += "and (select count(id) from users where parentId = u.id and sex = 'm' ";
			if (ages.size() > 1) {
				sql += "and (datediff(now(), birthDate) / 365.25) between " + minAge + " and " + maxAge;
			}
			sql += ") > 0 ";
		}

		if (parentsOfGirls) {
			sql += "and (select count(id) from users where parentId = u.id and sex = 'f' ";
			if (ages.size() > 1) {
				sql += "and (datediff(now(), birthDate) / 365.25) between " + minAge + " and " + maxAge;
			}
			sql += ") > 0 ";
		}

		if (onlyChildren) {
			sql += "and u.birthDate > date_add(now(), interval -18 year) ";
			if (ages.size() > 1) {
				sql += "and (datediff(now(), u.birthDate) / 365.25) between " + minAge + " and " + maxAge + " ";
			}
		}

		if (!Common.isNullOrBlank(sex)) {
			sql += "and u.sex = ? ";
			sqlArgs.add(sex);
		}

		// end directory logic //

		if (onlyCommonInterests) {
			sql += "and i.commonInterests > 0 ";
		}

		if (!Common.isNullOrBlank(addressSearch)) {
			sql += "and u.address like ? ";
			sqlArgs.add("%" + addressSearch + "%");
		}

		if (args.getStatus() != Status.ALL) {
			sql += "and isActive(u.startDate, u.endDate) = " + (args.getStatus() == Status.ACTIVE ? "1" : "0") + " \n";
		}

		if (registrationId > 0) {
			sql += "and p.id is null ";
		}

		if (parentIdPlusSelf > 0) {
			sql += "and (u.parentId = ? or u.id = ?) ";
			sqlArgs.add(parentIdPlusSelf);
			sqlArgs.add(parentIdPlusSelf);
		}

		if (parentId > 0) {
			sql += "and u.parentId = ? ";
			sqlArgs.add(parentId);
		}

		if (activeNumber > 0 || newNumber > 0) {
			if (ServerContext.isAuthenticated()) {
				sql += "and u.id != ? ";
				sqlArgs.add(ServerContext.getCurrentUserId());
			}
			if (newNumber > 0) {
				sql += "and u.passwordDigest is not null and date_add(now(), interval -2 week) < u.addedDate ";
			} else if (activeNumber > 0) {
				sql += "and date_add(now(), interval -5 minute) < u.lastActivityDate ";
			}
		}

		if (activeNumber > 0) {
			sql += "order by u.lastActivityDate desc limit " + activeNumber;
		} else if (newNumber > 0) {
			sql += "order by u.addedDate desc limit " + newNumber;
		} else {
			sql += "order by u.lastName, u.firstName";
		}

		if (groupId > 0) {
			return query(sql, new SpecialGroupUserMapper(), sqlArgs.toArray());
		}

		return query(sql, new SecureUserMapper(), sqlArgs.toArray());
	}

	@Override
	public ArrayList<UserGroup> listGroups(ArgMap<UserGroupArg> args) {
		int userId = args.getInt(UserGroupArg.USER_ID);
		int userNotMemberId = args.getInt(UserGroupArg.USER_NOT_MEMBER_OF);
		int userAdminId = args.getInt(UserGroupArg.USER_IS_ADMIN_OF);
		int id = args.getInt(UserGroupArg.ID);
		String subDomain = args.getString(UserGroupArg.ORG_SUB_DOMAIN);
		String domain = args.getString(UserGroupArg.ORG_DOMAIN);
		int orgId = args.getInt(UserGroupArg.ORGANIZATION_ID);

		List<Object> sqlArgs = new ArrayList<Object>();
		String sql = "select g.*, concat(u.firstName, ' ', u.lastName) as contact, ";
		if (userId > 0) {
			sql += "uugm.isAdministrator, uugm.groupApproved, uugm.userApproved, ";
		}
		if (userAdminId > 0) {
			sql += "augm.isAdministrator, augm.groupApproved, augm.userApproved, ";
		}
		sql += "o.groupName as organizationName from groups g ";
		sql += "join groups o on o.id = g.organizationId ";
		sql += "left join users u on u.id = g.contactId ";

		if (userId > 0) {
			sql += "join userGroupMembers uugm on uugm.groupId = g.id and uugm.userId = ? ";
			sqlArgs.add(userId);
		}

		if (userAdminId > 0) {
			sql += "join userGroupMembers augm on augm.groupId = g.id and augm.isAdministrator = 1 ";
			sql += "and augm.groupApproved = 1 and augm.userApproved = 1 and augm.userId = ? ";
			sqlArgs.add(userAdminId);
		}

		if (userNotMemberId > 0) {
			sql += "left join userGroupMembers nugm on nugm.groupId = g.id and nugm.userId = ? ";
			sqlArgs.add(userNotMemberId);
		}

		sql += "where 1 = 1 ";

		if (orgId > 0) {
			sql += "and g.organizationId = ? ";
			sqlArgs.add(orgId);
		}

		if (args.getStatus() != Status.ALL) {
			sql += "and isActive(g.startDate, g.endDate) = " + (args.getStatus() == Status.ACTIVE ? "1" : "0") + " \n";
		}

		if (userNotMemberId > 0) {
			sql += "and nugm.id is null or (nugm.userApproved != 1 or nugm.groupApproved != 1) ";
		}

		if (id > 0) {
			sql += "and g.id = ? ";
			sqlArgs.add(id);
		}

		if (!Common.isNullOrBlank(subDomain)) {
			sql += "and g.orgSubDomain = ? ";
			sqlArgs.add(subDomain);
		}

		if (!Common.isNullOrBlank(domain)) {
			sql += "and g.orgDomain = ? ";
			sqlArgs.add(domain);
		}

		sql += "order by o.groupName, g.isOrganization desc, g.groupName";

		if (userId > 0 || userAdminId > 0) {
			return query(sql, new GroupMemberMapper(), sqlArgs.toArray());
		}

		return query(sql, new GroupMapper(), sqlArgs.toArray());
	}

	@Override
	public void recordLogin(String username) {
		String sql = "update users set lastLoginDate = now() where email = ?";
		update(sql, username);
	}

	@Override
	public HashMap<Integer, GroupData> refreshSecurityGroups() {
		if (ServerContext.getCurrentUser() == null) {
			return null;
		}

		User u = getById(ServerContext.getCurrentUserId());
		ServerContext.updateUserCache(u);

		return u.getGroups();
	}

	@Override
	public ServerResponseData<User> save(User user) {
		ServerResponseData<User> retData = new ServerResponseData<User>();
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(user);

		String sql = "";
		int conflict = 0;

		// verify no duplicate email/login
		if (!Common.isNullOrBlank(user.getEmail())) {
			sql = "select count(id) from users where email = ? and id != ?";
			conflict = queryForInt(sql, user.getEmail(), user.getId());

			if (conflict > 0) {
				retData.addError("Email address is already registered to another account.");
				retData.setData(user);
				return retData;
			}
		} else {
			// ensure email is null, not empty, or it'll produce sql errors
			user.setEmail(null);
		}

		// verify no duplicate name in same family
		sql = "select count(id) from users where lower(firstName) = ? and lower(lastName) = ? and parentId = ? and id != ?";
		conflict = queryForInt(sql, user.getFirstName().toLowerCase(), user.getLastName().toLowerCase(), user.getParentId(), user.getId());
		if (conflict > 0) {
			retData.addError("That name has already been added to this family.");
			retData.setData(user);
			return retData;
		}

		if (user.getPassword() != null) {
			user.setGuid(UUID.randomUUID().toString());
			String digest = getSha1Hash(user.getPassword(), user.getGuid());
			if (user.getPasswordDigest() != null && user.getPasswordDigest().equals(digest)) {
				retData.addError("Password must be different from the current password.");
				return retData;
			}

			// resetting your own password
			if (user.equals(ServerContext.getCurrentUser())) {
				retData.setErrors(Common.asArrayList(validatePassword(user.getPassword())));
				if (retData.hasErrors()) {
					return retData;
				}
				User cur = ServerContext.getCurrentUser();
				user.setResetPassword(false);
				cur.setPasswordDigest(digest);
				cur.setResetPassword(false);
			} else {
				if (user.getGeneratePassword()) {
					user.setResetPassword(true);
				}
			}
			user.setPasswordDigest(digest);
			user.setPassword(null);
		}

		boolean adding = !user.isSaved();

		if (user.isSaved()) {
			sql = "update users set firstName = :firstName, lastName = :lastName, startDate = :startDate, endDate = :endDate, email = :email, ";
			sql += "resetPassword = :resetPassword, homePhone = :homePhone, mobilePhone = :mobilePhone, isSystemAdministrator = :systemAdministrator, ";
			sql += "birthDate = :birthDate, parentId = :parentId, passwordDigest = :passwordDigest, sex = :sex, showUserAgreement = :showUserAgreement, ";
			sql += "address = :address, street = :street, city = :city, state = :state, zip = :zip, lat = :lat, lng = :lng, payPalEmail = :payPalEmail, ";
			sql += "directoryOptOut = :directoryOptOut, receiveNews = :receiveNews, facebookUrl = :facebookUrl, guid = :guid where id = :id";
			update(sql, namedParams);
		} else {
			if (user.getStartDate() == null) {
				user.setStartDate(new Date());
			}

			// when adding your own user, look up and record location if not specified
			if (!ServerContext.isAuthenticated()) {
				addLocationInfo(user);
			}

			sql = "insert into users (email, firstName, lastName, passwordDigest, startDate, endDate, addedDate, homePhone, mobilePhone, ";
			sql += "isSystemAdministrator, resetPassword, birthDate, parentId, sex, guid, ";
			sql += "address, street, city, state, zip, lat, lng, payPalEmail, ";
			sql += "imageId, smallImageId, directoryOptOut, showUserAgreement) values ";
			sql += "(:email, :firstName, :lastName, :passwordDigest, :startDate, :endDate, now(), :homePhone, :mobilePhone, ";
			sql += ":systemAdministrator, :resetPassword, :birthDate, :parentId, :sex, :guid, ";
			sql += ":address, :street, :city, :state, :zip, :lat, :lng, :payPalEmail, ";
			sql += ":imageId, :smallImageId, :directoryOptOut, :showUserAgreement)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			user.setId(ServerUtils.getIdFromKeys(keys));
		}

		// children must inherit opt-out status
		if (user.getParentId() == null) {
			update("update users set directoryOptOut = ? where parentId = ?", user.getDirectoryOptOut(), user.getId());
		}

		boolean secureMapper = true;
		// if you're not authenticated, it's because this user you just added is for yourself. no need for secure mapper.
		if (!ServerContext.isAuthenticated() || ServerContext.getCurrentUser().administratorOf(user)) {
			secureMapper = false;
		}

		User returnUser = getById(user.getId(), secureMapper);

		retData.setData(returnUser);

		// auto-request membership in current org, whether user self-added, or an admin added them
		if (adding) {
			ArgMap<UserGroupArg> args = new ArgMap<>();
			args.put(UserGroupArg.ID, ServerContext.getCurrentOrgId());
			UserGroup group = listGroups(args).get(0);

			if (ServerContext.isCitrus()) {
				// auto-approve members of main citrus site
				group.setGroupApproved(true);
				group.setUserApproved(true);
			}

			if (!ServerContext.isAuthenticated()) {
				group.setUserApproved(true);
			} else {
				group.setGroupApproved(true);
			}

			updateUserGroupRelation(returnUser, group, true);

			// auto-associate user to a certain group, if requested
			if (user.getAutoAddToGroupId() > 0) {
				args.put(UserGroupArg.ID, user.getAutoAddToGroupId());
				group = listGroups(args).get(0);

				group.setUserApproved(true);
				group.setGroupApproved(true);

				updateUserGroupRelation(returnUser, group, true);
			}
		}

		// update the user cache (using insecure mapper to ensure all data is present)
		if (ServerContext.getCache().contains("user_" + user.getId())) {
			ServerContext.updateUserCache(getById(user.getId(), false));
		}

		return retData;
	}

	@Override
	public MainMenuItem saveMenuItem(MainMenuItem item) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(item);

		if (item.isSaved()) {
			String sql = "update menuItems set name = :name, articleIds = :articleIds, url = :url, parentNodeId = :parentNodeId, ordinal = :ordinal, ";
			sql += "organizationId = :owningOrgId, visibilityLevelId = :visibilityLevelId, groupId = :groupId where id = :id";
			update(sql, namedParams);
		} else {
			item.setAddedById(ServerContext.getCurrentUserId());
			item.setOwningOrgId(ServerContext.getCurrentOrgId());
			String sql = "insert into menuItems (name, articleIds, url, parentNodeId, organizationId, visibilityLevelId, groupId, addedById, ordinal) ";
			sql += "values(:name, :articleIds, :url, :parentNodeId, :owningOrgId, :visibilityLevelId, :groupId, :addedById, 100)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			item.setId(ServerUtils.getIdFromKeys(keys));
		}

		return item;
	}

	@Override
	public PrivacyPreference savePrivacyPreference(PrivacyPreference pref) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(pref);

		if (pref.isSaved()) {
			String sql = "update userPrivacyPreferences set visibilityLevelId = :visibilityLevelId, groupId = :groupId where id = :id";
			update(sql, namedParams);
		} else {
			String sql = "insert into userPrivacyPreferences (userId, preferenceType, visibilityLevelId, groupId) ";
			sql += "values(:userId, :preferenceTypeString, :visibilityLevelId, :groupId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			pref.setId(ServerUtils.getIdFromKeys(keys));
		}

		if (pref.getPreferenceType().equals(PrivacyPreferenceType.FAMILY)) {
			int optOut = pref.getVisibilityLevel().equals(VisibilityLevel.PRIVATE) ? 1 : 0;
			String sql = "update users set directoryOptOut = " + optOut + " where parentId = ?";
			update(sql, pref.getUserId());
		}

		if (pref.getUserId() == ServerContext.getCurrentUserId()) {
			ServerContext.getCurrentUser().setPrivacyPreference(pref);
		}

		return pref;
	}

	@Override
	public UserGroup saveUserGroup(UserGroup group) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(group);

		if (group.isSaved()) {
			String sql = "update groups set groupName = :groupName, description = :description, startDate = :startDate, contactId = :contactId, ";
			sql += "publicGreetingId = :publicGreetingId, privateGreetingId = :privateGreetingId, generalPolicyId = :generalPolicyId, ";
			sql += "markupOverride = :markupOverride, markupPercent = :markupPercent, markupDollars = :markupDollars, ";
			sql += "eventPolicyId = :eventPolicyId, coopPolicyId = :coopPolicyId, payPalEmail = :payPalEmail, religious = :religious, ";
			sql += "address = :address, street = :street, city = :city, state = :state, zip = :zip, lat = :lat, lng = :lng, ";
			sql += "membershipFee = :membershipFee, facebookUrl = :facebookUrl, ";
			sql += "shortName = :shortName, orgDomain = :orgDomain, orgSubDomain = :orgSubDomain, endDate = :endDate where id = :id";
			update(sql, namedParams);
		} else {
			if (queryForInt("select count(*) from groups where isActive(startDate, endDate) = 1 and orgSubDomain = ?", group.getOrgSubDomain()) > 0) {
				return null;
			}

			if (group.getStartDate() == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				group.setStartDate(calendar.getTime());
			}

			String sql = "insert into groups (groupName, description, isOrganization, startDate, religious, membershipFee, facebookUrl, ";
			if (group.getOwningOrgId() > 0) {
				sql += "organizationId, ";
			}
			sql += "address, street, city, state, zip, lat, lng, ";
			sql += "markupOverride, markupPercent, markupDollars, ";
			sql += "shortName, orgDomain, orgSubDomain, payPalEmail, endDate, contactId) ";
			sql += "values(:groupName, :description, :organization, :startDate, :religious, :membershipFee, :facebookUrl, ";
			if (group.getOwningOrgId() > 0) {
				sql += ":owningOrgId, ";
			}
			sql += ":address, :street, :city, :state, :zip, :lat, :lng, ";
			sql += ":markupOverride, :markupPercent, :markupDollars, ";
			sql += ":shortName, :orgDomain, :orgSubDomain, :payPalEmail, :endDate, :contactId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			group.setId(ServerUtils.getIdFromKeys(keys));
			if (group.getOrganization()) {
				update("update groups set organizationId = id where id = ?", group.getId());
				group.setOrganizationName(group.getGroupName());

				// add the adder as a admin
				sql = "insert into userGroupMembers (userId, groupId, isAdministrator, groupApproved, userApproved) ";
				sql += "values(?, ?, 1, 1, 1)";
				update(sql, ServerContext.getCurrentUserId(), group.getId());
				// need to update cache
				ServerContext.deleteCacheKey("user_" + ServerContext.getCurrentUserId());

				// auto-add a resource
				Resource r = new Resource();
				r.setName(group.getOrganizationName());
				r.setUrl("http://" + group.getOrgSubDomain() + Constants.CG_DOMAIN);
				r.setFacebookUrl(group.getFacebookUrl());
				r.setAddress(group.getAddress());
				r.setLat(group.getLat());
				r.setLng(group.getLng());
				r.setZip(group.getZip());
				r.setStreet(group.getStreet());
				r.setCity(group.getCity());
				r.setState(group.getState());
				r.setDescription(group.getDescription());
				User u = ServerContext.getCurrentUser();
				r.setContactEmail(u.getEmail());
				r.setContactName(u.getFullName());

				ResourceDao resourceDao = ServerContext.getDaoImpl("resource");
				r = resourceDao.save(r);

				int tagId = queryForInt(0, "select id from tags where name = 'Homeschool Groups'");
				sql = "update resources set firstTagId = ? where id = ?";
				update(sql, tagId, r.getId());
				sql = "insert into tagResourceMapping (resourceId, tagId) values(?, ?)";
				update(sql, r.getId(), tagId);

				sql = "insert into resourceUserMapping (resourceId, userId) values(?, ?)";
				update(sql, r.getId(), u.getId());
			}
		}

		group = listGroups(new ArgMap<UserGroupArg>(UserGroupArg.ID, group.getId())).get(0);

		if (group.getOrganization()) {
			ServerContext.updateGroupCache(group);
		}

		return group;
	}

	@Override
	public boolean sendPasswordResetEmail(String username) {
		if (username == null) {
			return false;
		}

		username = username.toLowerCase();
		try {
			User u = getUserByUsername(username);
			if (!u.isActive()) {
				return false;
			}

			Mailer mailer = new Mailer();
			mailer.addTo(username);
			String sn = ServerContext.getCurrentOrg().getGroupName();
			mailer.setSubject(sn + " Password Assistance");
			String body = "To initiate the password reset process for your " + username + " " + sn + " account, click the link below: \n\n";
			body += ServerContext.getBaseUrlWithCodeServer() + "rr=" + u.getPasswordDigest() + "&uu=" + u.getId() + "\n\n";
			body += "If clicking the link above doesn't work, please copy and paste the URL in a new browser window instead.\n\n";
			body += "If you've received this mail in error, it's likely that another user entered your email address by mistake while trying to reset a password. ";
			body += "If you didn't initiate the request, you don't need to take any further action and can safely disregard this email.\n\n";
			body += "If you have any questions, please contact site support at " + Constants.SYSTEM_FROM_EMAIL + ".\n\n";
			body += "Thank you for using " + sn + " services.\n\n";
			mailer.setBody(body);

			mailer.send();
			return true;
		} catch (UsernameNotFoundException e) {
			return false;
		}
	}

	@Override
	public void setCurrentLocation(String location, double lat, double lng, int radius) {
		ServerContext.setCurrentLocation(location);
		ServerContext.setCurrentLat(lat);
		ServerContext.setCurrentLng(lng);
		ServerContext.setCurrentRadius(radius);
	}

	@Override
	public User setPasswordFromDigest(int userId, String digest) {
		String sql = "select id from users where id = ? and passwordDigest = ?";
		try {
			int id = queryForInt(sql, userId, digest);
			User u = queryForObject(createSqlBase() + "where u.id = ?", new InsecureUserMapper(), id);
			sql = "update users set passwordDigest = ?, resetPassword = 1 where id = ?";
			update(sql, UserDaoImpl.getSha1Hash(digest, u.getGuid()), id);

			return u;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void unLinkResource(User user, int resourceId) {
		String sql = "delete from resourceUserMapping where userId = ? and resourceId = ?";
		update(sql, user.getId(), resourceId);
	}

	@Override
	public void updateMenuOrdinals(ArrayList<MainMenuItem> items) {
		for (MainMenuItem item : items) {
			String sql = "update menuItems set ordinal = " + item.getOrdinal() + " where id = " + item.getId();
			update(sql);
		}
	}

	@Override
	public void updateUserGroupRelation(User u, UserGroup g, boolean add) {
		if (add) {
			Mailer m = new Mailer();
			m.setHtmlMail(true);
			// Check if relation already exists
			String sql = "select * from userGroupMembers where userId = ? and groupId = ?";
			Data existing = queryForObject(sql, ServerUtils.getGenericRowMapper(), u.getId(), g.getId());
			if (existing != null) {
				sql = "update userGroupMembers set isAdministrator = ?, groupApproved = ?, userApproved = ? where userId = ? and groupId = ?";
				update(sql, g.getAdministrator(), g.getGroupApproved(), g.getUserApproved(), u.getId(), g.getId());

				ServerContext.updateUserCache(getById(u.getId(), false));

				// send approval granted emails to user or group here
				if (g.getGroupApproved() && g.getUserApproved() && !(existing.getBoolean("groupApproved") && existing.getBoolean("userApproved"))) {
					String subject = "Membership Approved: ";
					String body = "";
					if (!existing.getBoolean("groupApproved")) {
						// email user
						m.addTo(u);
						subject += g.getGroupName();
						body += "Your request to join ";
						body += "<a href=\"" + ServerContext.getBaseUrlWithCodeServer() + "\">";
						body += g.getGroupName() + "</a> has been approved.";
					} else {
						if (ServerContext.getCurrentUserId() != u.getId()) {
							return;
						}
						// email group
						m.addTo(getAdminsForGroup(g));
						subject += u.getFullName();
						body += u.getFullName() + " has accepted the invitation to join " + g.getGroupName() + ".";
					}

					m.setSubject(subject);
					m.setBody(body);
					m.send();
				}
				return;
			}

			// associate to org if needed
			sql = "select count(*) from userGroupMembers where userId = ? and groupId = ?";
			if (g.getOrganization() || (!g.getOrganization() && queryForInt(sql, u.getId(), g.getOwningOrgId()) == 0)) {
				addToOrganization(u.getId(), g);
			}

			if (!g.getOrganization()) {
				sql = "insert into userGroupMembers (userId, groupId, isAdministrator, groupApproved, userApproved) values(?, ?, ?, ?, ?)";
				update(sql, u.getId(), g.getId(), g.getAdministrator(), g.getGroupApproved(), g.getUserApproved());
			}

			if (!g.getUserApproved()) {
				// email the user
				m.addTo(u);
				m.setSubject("Invitation from " + g.getGroupName());
				String body = "You have been invited to join the " + g.getGroupName() + " ";
				if (!g.getOrganization()) {
					body += "group, within the " + g.getOrganizationName() + " ";
				}
				body += "homeschool organization, a member of <a href=\"" + Constants.CG_URL + "\">Citrus Groups</a> homeschool network.<br><br>";
				body += "<a href=\"" + ServerContext.getBaseUrlWithCodeServer() + PageUrl.user(u.getId()) + "&tab=2\">";
				body += "Click here</a> to view the invitation on the group membership tab of your profile, ";
				body += "then click \"Approve\" if you wish to accept.<br><br>";
				body += "Regards,<br>" + ServerContext.getCurrentUser().getFullName() + "<br>";
				body += "<a mailto:\"" + ServerContext.getCurrentUser().getEmail() + "\">" + ServerContext.getCurrentUser().getEmail() + "</a>";

				m.setBody(body);
				m.send();
			} else if (!g.getGroupApproved()) {
				// email the group admins
				m.addTo(getAdminsForGroup(g));

				m.setSubject("Membership request from " + u.getFullName());
				String body = u.getFullName() + " (<a mailto:\"" + u.getEmail() + "\">" + u.getEmail() + "</a>) ";
				body += "has asked to join the " + g.getGroupName() + " ";
				if (!g.getOrganization()) {
					body += "group, within the " + g.getOrganizationName() + " ";
				}
				body += "homeschool organization.<br><br>";
				body += "<a href=\"" + ServerContext.getBaseUrlWithCodeServer() + PageUrl.userGroup(g.getId()) + "&tab=1\">";
				body += "Click here</a> to view the request on the members tab of the group page, ";
				body += "then click \"Approve\" if you wish to accept.";

				m.setBody(body);
				m.send();
			}
		} else {
			String sql = "delete from userGroupMembers where ";
			// when deleting from org, cascade
			if (g.getOrganization()) {
				sql += "groupId in(select id from groups where organizationId = ?) ";
			} else {
				sql += "groupId = ? ";
			}
			sql += "and (userId = ? or userId in(select id from users where parentId = ?)) ";
			update(sql, g.getId(), u.getId(), u.getId());
		}

		ServerContext.updateUserCache(getById(u.getId(), false));
	}

	@Override
	public List<String> validatePassword(String password) {
		// password must be between 8 and 16 chars long
		LengthRule lengthRule = new LengthRule(8, 20);

		// don't allow whitespace
		WhitespaceRule whitespaceRule = new WhitespaceRule();

		// control allowed characters
		CharacterCharacteristicsRule charRule = new CharacterCharacteristicsRule();
		// require at least 1 digit in passwords
		charRule.getRules().add(new DigitCharacterRule(1));
		// require at least 1 non-alphanumeric char
		// charRule.getRules().add(new NonAlphanumericCharacterRule(1));
		// require at least 1 upper case char
		charRule.getRules().add(new UppercaseCharacterRule(1));
		// require at least 1 lower case char
		charRule.getRules().add(new LowercaseCharacterRule(1));
		// require at least 3 of the previous rules be met
		charRule.setNumberOfCharacteristics(2);

		// don't allow alphabetical sequences
		// AlphabeticalSequenceRule alphaSeqRule = new AlphabeticalSequenceRule();

		// don't allow numerical sequences of length 3
		// NumericalSequenceRule numSeqRule = new NumericalSequenceRule(3, false);

		// don't allow qwerty sequences
		// QwertySequenceRule qwertySeqRule = new QwertySequenceRule();

		// don't allow 4 repeat characters
		RepeatCharacterRegexRule repeatRule = new RepeatCharacterRegexRule(4);

		// create a case sensitive word list and sort it
		// if (wordList == null) {
		// try {
		// FileReader r = new FileReader(ServerContext.getServletContext().getRealPath("/WEB-INF/words"));
		// wordList = WordLists.createFromReader(new FileReader[] { r }, true, new ArraysSort());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// group all rules together in a List
		List<Rule> ruleList = new ArrayList<Rule>();
		// if (wordList != null) {
		// // create a dictionary for searching
		// WordListDictionary dict = new WordListDictionary(wordList);
		//
		// DictionarySubstringRule dictRule = new DictionarySubstringRule(dict);
		// dictRule.setWordLength(4); // size of words to check in the password
		// dictRule.setMatchBackwards(true); // match dictionary words backwards
		// ruleList.add(dictRule);
		// }

		ruleList.add(lengthRule);
		ruleList.add(whitespaceRule);
		ruleList.add(charRule);
		// ruleList.add(alphaSeqRule);
		// ruleList.add(numSeqRule);
		// ruleList.add(qwertySeqRule);
		ruleList.add(repeatRule);

		if (resolver == null) {
			try {
				Properties props = new Properties();
				props.load(new FileInputStream(ServerContext.getServletContext().getRealPath("/WEB-INF/vt-password.properties")));
				resolver = new MessageResolver(props);
			} catch (Exception e) {
			}
		}

		PasswordValidator validator;
		if (resolver != null) {
			validator = new PasswordValidator(resolver, ruleList);
		} else {
			validator = new PasswordValidator(ruleList);
		}
		PasswordData passwordData = new PasswordData(new Password(password));

		RuleResult result = validator.validate(passwordData);
		if (!result.isValid()) {
			return validator.getMessages(result);
		}
		return new ArrayList<String>();
	}

	private void addLocationInfo(User u) {
		try {
			String stringData = ServerUtils.getUrlContents("http://ip-api.com/json/" + ServerContext.getRequest().getRemoteAddr());
			JsonObject locationData = new JsonParser().parse(stringData).getAsJsonObject();
			String status = ServerUtils.getStringFromJsonObject(locationData, "status");
			if (status != null && status.equals("success")) {
				String lat = ServerUtils.getStringFromJsonObject(locationData, "lat");
				String lng = ServerUtils.getStringFromJsonObject(locationData, "lon");
				u.setCity(ServerUtils.getStringFromJsonObject(locationData, "city"));
				if (Common.isDouble(lat)) {
					u.setLat(Double.parseDouble(lat));
				}
				if (Common.isDouble(lng)) {
					u.setLng(Double.parseDouble(lng));
				}
				u.setState(ServerUtils.getStringFromJsonObject(locationData, "region"));
				u.setZip(ServerUtils.getStringFromJsonObject(locationData, "zip"));

				String address = "";
				int partCount = 0;
				if (!Common.isNullOrBlank(u.getCity())) {
					address = u.getCity();
					partCount++;
				}

				if (!Common.isNullOrBlank(u.getState())) {
					if (partCount > 0) {
						address += ", ";
					}
					address += u.getState();
					partCount++;
				}

				if (!Common.isNullOrBlank(u.getZip())) {
					if (partCount > 0) {
						address += " ";
					}
					address += u.getZip();
				}

				u.setAddress(address);
			}
		} catch (IOException e) {
			Logger logger = Logger.getLogger(this.getClass().toString());
			logger.log(Level.WARNING, e.getMessage());
		}
	}

	private void addToOrganization(int userId, UserGroup g) {
		// children too
		String sql = "insert into userGroupMembers (userId, groupId, isAdministrator, groupApproved, userApproved) ";
		sql += "select id, ?, ?, ?, ? from users where (parentId = ? or id = ?)";
		update(sql, g.getOwningOrgId(), false, g.getGroupApproved(), g.getUserApproved(), userId, userId);
	}

	private String createSqlBase() {
		return createSqlBase(null);
	}

	private String createSqlBase(String specialCols) {
		int currentUserId = ServerContext.getCurrentUserId();
		String sql = "select isActive(u.startDate, u.endDate) as isEnabled, u.*, uu.firstName as parentFirstName, uu.lastName as parentLastName, \n";
		sql += "case when u.birthDate < date_add(now(), interval -18 year) then 0 else 1 end as isChild, i.commonInterests, d.fileExtension, \n";
		sql += "floor(datediff(now(), u.birthDate) / 365.25) as age, \n";
		if (!Common.isNullOrBlank(specialCols)) {
			sql += specialCols;
		}
		// groups
		sql += "(select group_concat(concat(g.id, ':', g.groupName, ':', gm.isAdministrator, ':', g.organizationId) separator '\n') ";
		sql += "from groups g join userGroupMembers gm on gm.groupId = g.id where gm.userId = u.id and gm.groupApproved = 1 and gm.userApproved = 1 \n";
		sql += "and isActive(g.startDate, g.endDate) = 1) as groups, \n";
		// privacy prefs
		sql += "(select group_concat(concat(p.id, ':', p.preferenceType, ':', p.visibilityLevelId, ':', ifnull(p.groupId, '0'), ':', ifnull(gg.organizationId, '0')) separator '\n') \n";
		sql += "from userPrivacyPreferences p \n";
		sql += "left join groups gg on gg.id = p.groupId \n";
		sql += "where p.userId = u.id) as privacyPrefs \n";
		sql += "from users u \n";
		sql += "left join userGroupMembers org on org.userId = u.id and org.groupId = " + ServerContext.getCurrentOrgId() + " \n";
		sql += "left join documents d on d.id = u.imageId \n";
		sql += "left join users uu on uu.id = u.parentId \n";
		// common interests
		sql += "left join ( \n";
		sql += "select userId, count(id) as commonInterests from tagUserMapping where tagId in( \n";
		sql += "select tagId from tagUserMapping where userId = " + currentUserId + ") and userId != " + currentUserId + " \n";
		sql += "group by userId \n";
		sql += ") as i on i.userId = u.id \n";

		return sql;
	}

	private User createUser(ResultSet rs, boolean security) throws SQLException {
		User user = new User();
		user.setSystemAdministrator(rs.getBoolean("isSystemAdministrator"));
		user.setId(rs.getInt("id"));
		user.setViewCount(rs.getInt("viewCount"));
		User cu = null;
		if (security) {
			cu = ServerContext.getCurrentUser();
		}

		String groupText = rs.getString("groups");
		final HashMap<Integer, GroupData> groups = new HashMap<Integer, GroupData>();
		final HashSet<AccessLevel> levels = new HashSet<AccessLevel>();
		String gt = "";
		if (!Common.isNullOrBlank(groupText)) {
			String[] groupRows = groupText.split("\n");
			for (int i = 0; i < groupRows.length; i++) {
				String[] cells = groupRows[i].split(":");
				int groupId = Integer.parseInt(cells[0]);
				boolean isAdmin = cells[2].equals("1");
				int orgId = Integer.parseInt(cells[3]);

				// levels
				levels.add(AccessLevel.GROUP_MEMBERS);
				if (isAdmin) {
					levels.add(AccessLevel.GROUP_ADMINISTRATORS);
					if (groupId > 0 && groupId == orgId) {
						levels.add(AccessLevel.ORGANIZATION_ADMINISTRATORS);
					}
				}

				// groups
				GroupData gd = new GroupData();
				gd.setAdministrator(isAdmin);
				gd.setOrganizationId(orgId);
				gd.setOrganization(groupId > 0 && groupId == orgId);
				groups.put(groupId, gd);
				gt += cells[1] + "\n";
			}
			user.setGroupsText(gt);
		}

		levels.add(AccessLevel.SITE_MEMBERS);
		if (user.getSystemAdministrator()) {
			levels.add(AccessLevel.SYSTEM_ADMINISTRATORS);
			levels.add(AccessLevel.ORGANIZATION_ADMINISTRATORS);
			levels.add(AccessLevel.GROUP_ADMINISTRATORS);
			levels.add(AccessLevel.GROUP_MEMBERS);
			levels.add(AccessLevel.BLOG_CONTRIBUTORS);
		}

		user.setGroups(groups);
		user.setAccessLevels(levels);

		String prefText = rs.getString("privacyPrefs");
		final HashMap<PrivacyPreferenceType, PrivacyPreference> privacyPreferences = new HashMap<PrivacyPreferenceType, PrivacyPreference>();
		if (!Common.isNullOrBlank(prefText)) {
			String[] prefRows = prefText.split("\n");
			for (int i = 0; i < prefRows.length; i++) {
				String[] cells = prefRows[i].split(":");
				PrivacyPreference p = new PrivacyPreference();
				p.setId(Integer.parseInt(cells[0]));
				p.setPreferenceType(cells[1]);
				p.setUserId(user.getId());
				p.setVisibilityLevelId(Integer.parseInt(cells[2]));
				p.setGroupId(Integer.parseInt(cells[3]));
				p.setOrganizationId(Integer.parseInt(cells[4]));
				privacyPreferences.put(PrivacyPreferenceType.valueOf(cells[1]), p);
			}
		}

		user.setPrivacyPreferences(privacyPreferences);

		if (!security || user.userCanSee(cu, PrivacyPreferenceType.EMAIL)) {
			user.setEmail(rs.getString("email"));
		}
		user.setFirstName(rs.getString("firstName"));
		user.setLastName(rs.getString("lastName"));
		user.setPasswordDigest(rs.getString("passwordDigest"));
		user.setGuid(rs.getString("guid"));
		if (!security || user.userCanSee(cu, PrivacyPreferenceType.HOME_PHONE)) {
			user.setHomePhone(rs.getString("homePhone"));
		}
		if (!security || user.userCanSee(cu, PrivacyPreferenceType.MOBILE_PHONE)) {
			user.setMobilePhone(rs.getString("mobilePhone"));
		}
		user.setStartDate(rs.getTimestamp("startDate"));
		user.setEndDate(rs.getTimestamp("endDate"));
		user.setPayPalEmail(rs.getString("payPalEmail"));
		user.setResetPassword(rs.getBoolean("resetPassword"));
		user.setAddedDate(rs.getTimestamp("addedDate"));
		user.setLastLoginDate(rs.getTimestamp("lastLoginDate"));
		user.setFacebookUrl(rs.getString("facebookUrl"));
		user.setActive(rs.getBoolean("isEnabled"));
		user.setBirthDate(rs.getTimestamp("birthDate"));
		user.setShowUserAgreement(rs.getBoolean("showUserAgreement"));
		user.setParentId(rs.getInt("parentId"));
		user.setCity(rs.getString("city"));
		user.setZip(rs.getString("zip"));
		user.setState(rs.getString("state"));
		if (!security || user.userCanSee(cu, PrivacyPreferenceType.ADDRESS)) {
			user.setAddress(rs.getString("address"));
			user.setStreet(rs.getString("street"));
			user.setLat(rs.getDouble("lat"));
			user.setLng(rs.getDouble("lng"));
		}
		user.setParentFirstName(rs.getString("parentFirstName"));
		user.setParentLastName(rs.getString("parentLastName"));
		user.setSex(rs.getString("sex"));
		user.setChild(rs.getBoolean("isChild"));
		user.setCommonInterestCount(rs.getInt("commonInterests"));
		user.setAge(rs.getInt("age"));
		user.setImageId(rs.getInt("imageId"));
		user.setSmallImageId(rs.getInt("smallImageId"));
		user.setImageExtension(rs.getString("fileExtension"));
		user.setDirectoryOptOut(rs.getBoolean("directoryOptOut"));
		user.setReceiveNews(rs.getBoolean("receiveNews"));
		return user;
	}

	private UserGroup createUserGroup(ResultSet rs) throws SQLException {
		UserGroup group = new UserGroup();
		group.setId(rs.getInt("id"));
		group.setGroupName(rs.getString("groupName"));
		group.setDescription(rs.getString("description"));
		group.setStartDate(rs.getTimestamp("startDate"));
		group.setEndDate(rs.getTimestamp("endDate"));
		group.setOrganization(rs.getBoolean("isOrganization"));
		group.setOwningOrgId(rs.getInt("organizationId"));
		group.setOrganizationName(rs.getString("organizationName"));
		group.setShortName(rs.getString("shortName"));
		group.setOrgDomain(rs.getString("orgDomain"));
		group.setOrgSubDomain(rs.getString("orgSubDomain"));
		group.setPublicGreetingId(rs.getInt("publicGreetingId"));
		group.setPrivateGreetingId(rs.getInt("privateGreetingId"));
		group.setGeneralPolicyId(rs.getInt("generalPolicyId"));
		group.setEventPolicyId(rs.getInt("eventPolicyId"));
		group.setCoopPolicyId(rs.getInt("coopPolicyId"));
		group.setBooksPolicyId(rs.getInt("booksPolicyId"));
		group.setPayPalEmail(rs.getString("payPalEmail"));
		group.setLogoId(rs.getInt("logoId"));
		group.setFaviconId(rs.getInt("faviconId"));
		group.setCity(rs.getString("city"));
		group.setZip(rs.getString("zip"));
		group.setState(rs.getString("state"));
		group.setAddress(rs.getString("address"));
		group.setStreet(rs.getString("street"));
		group.setLat(rs.getDouble("lat"));
		group.setLng(rs.getDouble("lng"));
		group.setReligious(rs.getBoolean("religious"));
		group.setContact(rs.getString("contact"));
		group.setMembershipFee(rs.getDouble("membershipFee"));
		group.setFacebookUrl(rs.getString("facebookUrl"));
		group.setMarkupDollars(rs.getDouble("markupDollars"));
		group.setMarkupPercent(rs.getDouble("markupPercent"));
		group.setMarkupOverride(rs.getBoolean("markupOverride"));

		return group;
	}

	private List<User> getAdminsForGroup(UserGroup g) {
		ArgMap<UserArg> args = new ArgMap<>();
		args.setStatus(Status.ACTIVE);
		args.put(UserArg.ADMIN_OF_GROUP_ID, g.getId());

		return list(args);
	}

	private ArrayList<Data> getResources(User user) {
		String sql = "select r.id, r.name \n";
		sql += "from resources r \n";
		sql += "join resourceUserMapping rm on rm.resourceId = r.id \n";
		sql += "where rm.userId = ? order by r.name";

		return query(sql, ServerUtils.getGenericRowMapper(), user.getId());
	}

}
