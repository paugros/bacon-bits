package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.util.Mailer;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
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
public class UserDaoImpl extends SpringWrapper implements UserDao {
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
			user.setId(rs.getInt("id"));
			user.setEmail(rs.getString("email"));
			user.setFirstName(rs.getString("firstName"));
			user.setLastName(rs.getString("lastName"));
			user.setPasswordDigest(rs.getString("passwordDigest"));
			user.setHomePhone(rs.getString("homePhone"));
			user.setMobilePhone(rs.getString("mobilePhone"));
			user.setStartDate(rs.getTimestamp("startDate"));
			user.setEndDate(rs.getTimestamp("endDate"));
			user.setResetPassword(rs.getBoolean("resetPassword"));
			user.setAddedDate(rs.getTimestamp("addedDate"));
			user.setLastLoginDate(rs.getTimestamp("lastLoginDate"));
			user.setActive(rs.getBoolean("isEnabled"));
			user.setSystemAdministrator(rs.getBoolean("isSystemAdministrator"));
			return user;
		}
	}

	private static String SELECT;

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

	public final static String getSha1Hash(String input, String saltText) {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(512);

		return encoder.encodePassword(input, saltText);
	}

	@Autowired
	public UserDaoImpl(DataSource dataSource) {
		super(dataSource);
		SELECT = "select isActive(startDate, endDate) as isEnabled, u.* from users u ";
	}

	@Override
	public User getById(int userId) {
		return queryForObject(SELECT + "where u.id = ?", new UserMapper(), userId);
	}

	@Override
	public UserPageData getPageData(int userId) {
		UserPageData pd = new UserPageData();
		if (userId > 0) {
			pd.setUser(getById(userId));

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
	public User getUserByUsername(String username) {
		if (username == null) {
			throw new UsernameNotFoundException("Username null not found");
		}

		User user = queryForObject(SELECT + "where u.email = ?", new UserMapper(), username.toLowerCase());
		if (user == null) {
			throw new UsernameNotFoundException("Username not found or duplicate: " + username);
		}

		return user;
	}

	@Override
	public ArrayList<User> list(ArgMap<UserArg> args) {
		String sql = SELECT;
		ArrayList<User> data = query(sql, new UserMapper());

		return data;
	}

	@Override
	public ArrayList<UserGroup> listGroups(ArgMap<UserArg> args) {
		int userId = args.getInt(UserArg.USER_ID);

		List<Object> sqlArgs = new ArrayList<Object>();
		String sql = "select * from groups g ";
		if (userId > 0) {
			sql += "join userGroupMembers ugm on ugm.groupId = g.id ";
			sql += "where ugm.userId = ? ";
			sqlArgs.add(userId);
		}
		sql += "order by groupName asc";

		if (userId > 0) {
			return query(sql, new GroupMemberMapper(), sqlArgs.toArray());
		}

		return query(sql, new GroupMapper(), sqlArgs.toArray());
	}

	@Override
	public ServerResponseData<User> save(User user) {
		ServerResponseData<User> retData = new ServerResponseData<User>();
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(user);

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
				user.setResetPassword(true);
			}
			user.setPasswordDigest(digest);
			user.setPassword(null);
		}

		if (user.isSaved()) {
			String sql = "update users set firstName = :firstName, lastName = :lastName, startDate = :startDate, endDate = :endDate, isSystemAdministrator = :systemAdministrator, ";
			sql += "resetPassword = :resetPassword, homePhone = :homePhone, mobilePhone = :mobilePhone, lastLoginDate = :lastLoginDate, passwordDigest = :passwordDigest where id = :id";
			update(sql, namedParams);
		} else {
			if (user.getStartDate() == null) {
				user.setStartDate(new Date());
			}
			String sql = "insert into users (email, firstName, lastName, passwordDigest, startDate, endDate, addedDate, homePhone, mobilePhone, isSystemAdministrator, resetPassword) values ";
			sql += "(:email, :firstName, :lastName, :passwordDigest, :startDate, :endDate, now(), :homePhone, :mobilePhone, :systemAdministrator, :resetPassword)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			user.setId(Integer.parseInt(keys.getKeys().get(Constants.GENERATED_KEY_TOKEN).toString()));
		}

		retData.setData(getById(user.getId()));
		return retData;
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
			mailer.useSystemFrom();
			mailer.addTo(username);
			mailer.setSubject("Area Homeschoolers Password Assistance");
			String body = "To initiate the password reset process for your " + username + " Area Homeschoolers account, click the link below: \n\n";
			body += ServerContext.getBaseUrl() + "#rr=" + u.getPasswordDigest() + "&uu=" + u.getId() + "\n\n";
			body += "If clicking the link above doesn't work, please copy and paste the URL in a new browser window instead.\n\n";
			body += "If you've received this mail in error, it's likely that another user entered your email address by mistake while trying to reset a password. ";
			body += "If you didn't initiate the request, you don't need to take any further action and can safely disregard this email.\n\n";
			body += "If you have further difficulty or any questions, please contact Kristin Augros at kaugros@gmail.com.\n\n";
			body += "Thank you for using Area Homeschoolers services.\n\n";
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
		String sql = "select ID from dbo.Users where ID = ? and PasswordDigest = ?";
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

	private UserGroup createUserGroup(ResultSet rs) throws SQLException {
		UserGroup group = new UserGroup();
		group.setGroupName(rs.getString("groupName"));
		group.setDescription(rs.getString("description"));
		return group;
	}

}
