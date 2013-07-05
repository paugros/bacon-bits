package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
import com.areahomeschoolers.baconbits.shared.dto.MainMenuItem;
import com.areahomeschoolers.baconbits.shared.dto.PollResponseData;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreference;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreferenceType;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;
import com.areahomeschoolers.baconbits.shared.dto.UserPageData;

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
			return g;
		}
	}

	private final class UserMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setSystemAdministrator(rs.getBoolean("isSystemAdministrator"));
			user.setId(rs.getInt("id"));
			User cu = ServerContext.getCurrentUser();

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
					int orgId = Integer.parseInt(cells[2]);

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

			if (user.userCanSee(cu, PrivacyPreferenceType.EMAIL)) {
				user.setEmail(rs.getString("email"));
			}
			user.setFirstName(rs.getString("firstName"));
			user.setLastName(rs.getString("lastName"));
			user.setPasswordDigest(rs.getString("passwordDigest"));
			if (user.userCanSee(cu, PrivacyPreferenceType.HOME_PHONE)) {
				user.setHomePhone(rs.getString("homePhone"));
			}
			if (user.userCanSee(cu, PrivacyPreferenceType.MOBILE_PHONE)) {
				user.setMobilePhone(rs.getString("mobilePhone"));
			}
			user.setStartDate(rs.getTimestamp("startDate"));
			user.setEndDate(rs.getTimestamp("endDate"));
			user.setResetPassword(rs.getBoolean("resetPassword"));
			user.setAddedDate(rs.getTimestamp("addedDate"));
			user.setLastLoginDate(rs.getTimestamp("lastLoginDate"));
			user.setActive(rs.getBoolean("isEnabled"));
			user.setBirthDate(rs.getTimestamp("birthDate"));
			user.setParentId(rs.getInt("parentId"));
			user.setCity(rs.getString("city"));
			user.setZip(rs.getString("zip"));
			user.setState(rs.getString("state"));
			if (user.userCanSee(cu, PrivacyPreferenceType.ADDRESS)) {
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
			user.setDirectoryOptOut(rs.getBoolean("directoryOptOut"));
			return user;
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
	public void deleteMenuItem(MainMenuItem item) {
		String sql = "delete from menuItems where id = ?";
		update(sql, item.getId());
	}

	@Override
	public synchronized void doUpdateUserActivity(int userId) {
		// we remove first so that the new entry goes to the bottom
		userActivity.remove(userId);
		userActivity.put(userId, new Date());

		String sql = "update users set lastActivityDate = now() where id = ?";
		update(sql, userId);
	}

	@Override
	public User getById(int userId) {
		User u = queryForObject(createSqlBase() + createSqlWhere() + "and u.id = ?", new UserMapper(), userId);

		if (u == null) {
			return null;
		}

		if (!ServerContext.isSystemAdministrator() && u.getDirectoryOptOut() && !u.equals(ServerContext.getCurrentUser())) {
			if (!ServerContext.isAuthenticated() || !ServerContext.getCurrentUser().administratorOf(u) || !ServerContext.getCurrentUser().parentOf(u)) {
				if (!ServerContext.getCurrentUser().isSwitched()) {
					return null;
				}
			}
		}

		return u;
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
	public UserGroup getOrgBySubDomain(String subDomain) {
		ArgMap<UserGroupArg> args = new ArgMap<UserGroupArg>();
		args.put(UserGroupArg.ORG_SUB_DOMAIN, subDomain);

		List<UserGroup> grps = listGroups(args);
		if (!grps.isEmpty()) {
			return grps.get(0);
		}
		args.remove(UserGroupArg.ORG_SUB_DOMAIN);
		// TODO change this default
		args.put(UserGroupArg.ID, 11);

		return listGroups(args).get(0);
	}

	@Override
	public UserPageData getPageData(final int userId) {
		UserPageData pd = new UserPageData();
		if (userId > 0) {
			pd.setUser(getById(userId));
			TagDao tagDao = ServerContext.getDaoImpl("tag");
			ArgMap<TagArg> args = new ArgMap<TagArg>(TagArg.ENTITY_ID, userId);
			args.put(TagArg.MAPPING_TYPE, TagMappingType.USER.toString());
			pd.setInterests(tagDao.list(args));

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
	public PollResponseData getPollData() {
		PollResponseData data = new PollResponseData();
		data.setUserActivity(getUserActivitySinceLastPoll(ServerContext.getCurrentUserId()));

		return data;
	}

	@Override
	public ArrayList<ServerSuggestion> getSuggestions(String token, int limit, Data options) {
		String sql = "select u.id, concat(u.firstName, ' ', u.lastName, ' - ', u.email) as Suggestion, 'User' as entityType ";
		sql += "from users u ";
		sql += "where u.email is not null and u.email != '' ";
		sql += "and (concat(u.firstName, ' ', u.lastName) like ? or u.email like ?) and isActive(u.startDate, u.endDate) = 1 ";
		sql += "order by concat(u.firstName, ' ', u.lastName) ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";
		return query(sql, ServerUtils.getSuggestionMapper(), search, search);
	}

	@Override
	public User getUserByUsername(String username) {
		if (username == null) {
			throw new UsernameNotFoundException("Username null not found");
		}

		User user = queryForObject(createSqlBase() + "where u.email = ?", new UserMapper(), username.toLowerCase());
		if (user == null) {
			throw new UsernameNotFoundException("Username not found or duplicate: " + username);
		}

		return user;
	}

	@Override
	public ArrayList<User> list(ArgMap<UserArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();

		int parentIdPlusSelf = args.getInt(UserArg.PARENT_ID_PLUS_SELF);
		int parentId = args.getInt(UserArg.PARENT_ID);
		int registrationId = args.getInt(UserArg.NOT_ON_REGISTRATION_ID);
		int groupId = args.getInt(UserArg.GROUP_ID);
		boolean onlyParents = args.getBoolean(UserArg.PARENTS);
		boolean onlyChildren = args.getBoolean(UserArg.CHILDREN);
		boolean parentsOfBoys = args.getBoolean(UserArg.PARENTS_OF_BOYS);
		boolean parentsOfGirls = args.getBoolean(UserArg.PARENTS_OF_GIRLS);
		boolean onlyCommonInterests = args.getBoolean(UserArg.ONLY_COMMON_INTERESTS);
		String sex = args.getString(UserArg.SEX);
		List<Integer> ages = args.getIntList(UserArg.AGES);
		String addressSearch = args.getString(UserArg.ADDRESS_SEARCH);
		int withinMiles = args.getInt(UserArg.WITHIN_MILES);
		String withinLat = args.getString(UserArg.WITHIN_LAT);
		String withinLng = args.getString(UserArg.WITHIN_LNG);
		String distanceCol = null;
		int minAge = 0;
		int maxAge = 0;

		if (ages.size() > 1) {
			minAge = ages.get(0);
			maxAge = ages.get(1);
		}

		if (withinMiles > 0 && !Common.isNullOrBlank(withinLat) && !Common.isNullOrBlank(withinLng)) {
			distanceCol = "(3959 * acos( cos( radians(" + withinLat + ") ) * cos( radians( u.lat ) ) * cos( radians( u.lng ) - radians(" + withinLng + ") ) ";
			distanceCol += "+ sin( radians(" + withinLat + ") ) * sin( radians( u.lat ) ) ) ) as distance, ";
		}

		String sql = createSqlBase(distanceCol);

		if (groupId > 0) {
			sql += "join userGroupMembers ugm on ugm.userId = u.id and ugm.groupId = ? \n";
			sqlArgs.add(groupId);
		}

		if (registrationId > 0) {
			sql += "left join eventRegistrationParticipants p on p.userId = u.id and p.eventRegistrationId = ? \n";
			sqlArgs.add(registrationId);
		}
		sql += createSqlWhere();

		// start directory logic //
		if (onlyParents) {
			sql += "and u.birthDate < date_add(now(), interval -18 year) ";
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

		if (distanceCol != null) {
			sql += "having distance < " + withinMiles + " ";
		}

		sql += "order by u.lastName, u.firstName";
		ArrayList<User> data = query(sql, new UserMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public ArrayList<UserGroup> listGroups(ArgMap<UserGroupArg> args) {
		int userId = args.getInt(UserGroupArg.USER_ID);
		int userNotMemberId = args.getInt(UserGroupArg.USER_NOT_MEMBER_OF);
		int userAdminId = args.getInt(UserGroupArg.USER_IS_ADMIN_OF);
		int id = args.getInt(UserGroupArg.ID);
		String subDomain = args.getString(UserGroupArg.ORG_SUB_DOMAIN);

		List<Object> sqlArgs = new ArrayList<Object>();
		String sql = "select *, o.groupName as organizationName from groups g ";
		sql += "join groups o on o.id = g.organizationId ";

		if (userId > 0) {
			sql += "join userGroupMembers uugm on uugm.groupId = g.id and uugm.userId = ? ";
			sqlArgs.add(userId);
		}

		if (userAdminId > 0) {
			sql += "join userGroupMembers augm on augm.groupId = g.id and augm.isAdministrator = 1 and augm.userId = ? ";
			sqlArgs.add(userAdminId);
		}

		if (userNotMemberId > 0) {
			sql += "left join userGroupMembers nugm on nugm.groupId = g.id and nugm.userId = ? ";
			sqlArgs.add(userNotMemberId);
		}

		sql += "where 1 = 1 ";

		if (args.getStatus() != Status.ALL) {
			sql += "and isActive(g.startDate, g.endDate) = " + (args.getStatus() == Status.ACTIVE ? "1" : "0") + " \n";
		}

		if (userNotMemberId > 0) {
			sql += "and nugm.id is null ";
		}

		if (id > 0) {
			sql += "and g.id = ? ";
			sqlArgs.add(id);
		}

		if (!Common.isNullOrBlank(subDomain)) {
			sql += "and g.orgSubDomain = ? ";
			sqlArgs.add(subDomain);
		}

		sql += "order by o.groupName, g.isOrganization desc, g.groupName";

		if (userId > 0 || userAdminId > 0 || userNotMemberId > 0) {
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
		return u.getGroups();
	}

	@Override
	public ServerResponseData<User> save(User user) {
		ServerResponseData<User> retData = new ServerResponseData<User>();
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(user);

		// verify no duplicate email/login
		String sql = "select count(id) from users where email = ? and id != ?";
		int conflict = queryForInt(sql, user.getEmail(), user.getId());

		if (conflict > 0) {
			retData.addError("Email address is already registered to another account.");
			retData.setData(user);
			return retData;
		}

		// verify no duplicate name in same family
		if (user.isChild()) {
			sql = "select count(id) from users where lower(firstName) = ? and lower(lastName) = ? and parentId = ? and id != ?";
			conflict = queryForInt(sql, user.getFirstName().toLowerCase(), user.getLastName().toLowerCase(), user.getParentId(), user.getId());

			if (conflict > 0) {
				retData.addError("That name has already been added to this family.");
				retData.setData(user);
				return retData;
			}
		}

		if (user.getPassword() != null) {
			String digest = getSha1Hash(user.getPassword(), user.getEmail());
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

		if (user.isSaved()) {
			sql = "update users set firstName = :firstName, lastName = :lastName, startDate = :startDate, endDate = :endDate, email = :email, ";
			sql += "resetPassword = :resetPassword, homePhone = :homePhone, mobilePhone = :mobilePhone, isSystemAdministrator = :systemAdministrator, ";
			sql += "address = :address, birthDate = :birthDate, parentId = :parentId, passwordDigest = :passwordDigest, sex = :sex, ";
			sql += "street = :street, city = :city, state = :state, zip = :zip, lat = :lat, lng = :lng, directoryOptOut = :directoryOptOut ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			if (user.getStartDate() == null) {
				user.setStartDate(new Date());
			}

			if ("m".equals(user.getSex())) {
				user.setSmallImageId(Constants.BLANK_PROFILE_MALE_SMALL);
				user.setImageId(Constants.BLANK_PROFILE_MALE_LARGE);
			} else {
				user.setSmallImageId(Constants.BLANK_PROFILE_FEMALE_SMALL);
				user.setImageId(Constants.BLANK_PROFILE_FEMALE_LARGE);
			}
			sql = "insert into users (email, firstName, lastName, passwordDigest, startDate, endDate, addedDate, homePhone, mobilePhone, ";
			sql += "address, isSystemAdministrator, resetPassword, birthDate, parentId, sex, ";
			sql += "street, city, state, zip, lat, lng, imageId, smallImageId, directoryOptOut) values ";
			sql += "(:email, :firstName, :lastName, :passwordDigest, :startDate, :endDate, now(), :homePhone, :mobilePhone, ";
			sql += ":address, :systemAdministrator, :resetPassword, :birthDate, :parentId, :sex, ";
			sql += ":street, :city, :state, :zip, :lat, :lng, :imageId, :smallImageId, :directoryOptOut)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			user.setId(Integer.parseInt(keys.getKeys().get(Constants.GENERATED_KEY_TOKEN).toString()));
		}

		retData.setData(getById(user.getId()));
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

			item.setId(Integer.parseInt(keys.getKeys().get(Constants.GENERATED_KEY_TOKEN).toString()));
		}

		return item;
	}

	@Override
	public PrivacyPreference savePrivacyPreference(PrivacyPreference privacyPreference) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(privacyPreference);

		if (privacyPreference.isSaved()) {
			String sql = "update userPrivacyPreferences set visibilityLevelId = :visibilityLevelId, groupId = :groupId where id = :id";
			update(sql, namedParams);
		} else {
			String sql = "insert into userPrivacyPreferences (userId, preferenceType, visibilityLevelId, groupId) ";
			sql += "values(:userId, :preferenceType, :visibilityLevelId, :groupId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			privacyPreference.setId(Integer.parseInt(keys.getKeys().get(Constants.GENERATED_KEY_TOKEN).toString()));
		}

		return privacyPreference;
	}

	@Override
	public UserGroup saveUserGroup(UserGroup group) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(group);

		if (group.isSaved()) {
			String sql = "update groups set groupName = :groupName, description = :description, startDate = :startDate, ";
			sql += "publicGreetingId = :publicGreetingId, privateGreetingId = :privateGreetingId, generalPolicyId = :generalPolicyId, ";
			sql += "eventPolicyId = :eventPolicyId, coopPolicyId, = :coopPolicyId, ";
			sql += "shortName = :shortName, orgDomain = :orgDomain, orgSubDomain = :orgSubDomain, endDate = :endDate where id = :id";
			update(sql, namedParams);
		} else {
			if (group.getStartDate() == null) {
				group.setStartDate(new Date());
			}

			String sql = "insert into groups (groupName, description, isOrganization, organizationId, startDate, ";
			sql += "shortName, orgDomain, orgSubDomain, endDate) ";
			sql += "values(:groupName, :description, :organization, :owningOrgId, :startDate, ";
			sql += ":shortName, :orgDomain, :orgSubDomain, :endDate)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			group.setId(Integer.parseInt(keys.getKeys().get(Constants.GENERATED_KEY_TOKEN).toString()));
			if (group.getOrganization()) {
				update("update groups set organizationId = id where id = ?", group.getId());
				group.setOrganizationName(group.getGroupName());
			}
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
			String sn = ServerContext.getCurrentOrg().getShortName();
			mailer.setSubject(sn + " Password Assistance");
			String body = "To initiate the password reset process for your " + username + " " + sn + " account, click the link below: \n\n";
			body += ServerContext.getBaseUrlWithCodeServer() + "#rr=" + u.getPasswordDigest() + "&uu=" + u.getId() + "\n\n";
			body += "If clicking the link above doesn't work, please copy and paste the URL in a new browser window instead.\n\n";
			body += "If you've received this mail in error, it's likely that another user entered your email address by mistake while trying to reset a password. ";
			body += "If you didn't initiate the request, you don't need to take any further action and can safely disregard this email.\n\n";
			body += "If you have further difficulty or any questions, please contact Kristin Augros at kaugros@gmail.com.\n\n";
			body += "Thank you for using " + sn + " services.\n\n";
			body += "This is a post-only mailing.  Replies to this message are not monitored or answered.";
			mailer.setBody(body);

			mailer.send();
			return true;
		} catch (UsernameNotFoundException e) {
			return false;
		}
	}

	@Override
	public User setPasswordFromDigest(int userId, String digest) {
		String sql = "select id from users where id = ? and passwordDigest = ?";
		try {
			int id = queryForInt(sql, userId, digest);
			User u = getById(id);
			String pwd = UserDaoImpl.getSha1Hash(digest, u.getUserName());
			u.setPasswordDigest(pwd);
			u.setResetPassword(true);
			ServerResponseData<User> result = save(u);
			if (!result.hasErrors()) {
				return result.getData();
			}
		} catch (DataAccessException e) {
			return null;
		}

		return null;
	}

	@Override
	public void updateMenuOrdinals(ArrayList<MainMenuItem> items) {
		for (MainMenuItem item : items) {
			String sql = "update menuItems set ordinal = " + item.getOrdinal() + " where id = " + item.getId();
			update(sql);
		}
	}

	@Override
	public void updateUserGroupRelation(ArrayList<User> users, UserGroup g, boolean add) {
		for (User u : users) {
			updateUserGroupRelation(u, g, add);
		}
	}

	@Override
	public void updateUserGroupRelation(User u, ArrayList<UserGroup> g, boolean add) {
		for (UserGroup ug : g) {
			updateUserGroupRelation(u, ug, add);
		}
	}

	@Override
	public void updateUserGroupRelation(User u, UserGroup g, boolean add) {
		if (add) {
			// Check if relation already exists
			String sql = "select count(*) from userGroupMembers where userId = ? and groupId = ?";
			if (queryForInt(sql, u.getId(), g.getId()) > 0) {
				sql = "update userGroupMembers set isAdministrator = ? where userId = ? and groupId = ?";
				update(sql, g.getAdministrator(), u.getId(), g.getId());
				return;
			}

			// associate to org if needed
			sql = "select count(*) from userGroupMembers where userId = ? and groupId = ?";
			if (g.getOrganization() || (!g.getOrganization() && queryForInt(sql, u.getId(), g.getOwningOrgId()) == 0)) {
				addToOrganization(u.getId(), g.getOwningOrgId());
			} else {
				sql = "insert into userGroupMembers (userId, groupId, isAdministrator) values(?, ?, ?)";
				update(sql, u.getId(), g.getId(), g.getAdministrator());
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

	private void addToOrganization(int userId, int orgId) {
		// children too
		String sql = "insert into userGroupMembers (userId, groupId, isAdministrator) ";
		sql += "select id, ?, ? from users where (parentId = ? or id = ?)";
		update(sql, orgId, false, userId, userId);
	}

	private String createSqlBase() {
		return createSqlBase(null);
	}

	private String createSqlBase(String specialCols) {
		int currentUserId = ServerContext.getCurrentUserId();
		String sql = "select isActive(u.startDate, u.endDate) as isEnabled, u.*, uu.firstName as parentFirstName, uu.lastName as parentLastName, \n";
		sql += "case when u.birthDate < date_add(now(), interval -18 year) then 0 else 1 end as isChild, i.commonInterests, \n";
		sql += "floor(datediff(now(), u.birthDate) / 365.25) as age, \n";
		if (specialCols != null) {
			sql += specialCols;
		}
		// groups
		sql += "(select group_concat(concat(g.id, ':', g.groupName, ':', gm.isAdministrator, ':', g.organizationId) separator '\n') ";
		sql += "from groups g join userGroupMembers gm on gm.groupId = g.id where gm.userId = u.id \n";
		sql += "and isActive(g.startDate, g.endDate) = 1) as groups, \n";
		// privacy prefs
		sql += "(select group_concat(concat(p.id, ':', p.preferenceType, ':', p.visibilityLevelId, ':', ifnull(p.groupId, '0'), ':', ifnull(gg.organizationId, '0')) separator '\n') \n";
		sql += "from userPrivacyPreferences p \n";
		sql += "left join groups gg on gg.id = p.groupId \n";
		sql += "where p.userId = u.id) as privacyPrefs \n";
		sql += "from users u \n";
		sql += "left join users uu on uu.id = u.parentId \n";
		// common interests
		sql += "left join ( \n";
		sql += "select userId, count(id) as commonInterests from tagUserMapping where tagId in( \n";
		sql += "select tagId from tagUserMapping where userId = " + currentUserId + ") and userId != " + currentUserId + " \n";
		sql += "group by userId \n";
		sql += ") as i on i.userId = u.id \n";

		return sql;
	}

	private String createSqlWhere() {
		String sql = "where 1 = 1 ";
		if (!ServerContext.isAuthenticated()) {
			sql += "and u.directoryOptOut = 0 \n";
		} else if (!(ServerContext.isSystemAdministrator() || ServerContext.getCurrentUser().isSwitched() || ServerContext.getCurrentUser().hasRole(
				AccessLevel.ORGANIZATION_ADMINISTRATORS))) {
			int id = ServerContext.getCurrentUserId();
			sql += "and (u.directoryOptOut = 0 or u.id = " + id + ") \n";
		}

		return sql;
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
		return group;
	}

}
