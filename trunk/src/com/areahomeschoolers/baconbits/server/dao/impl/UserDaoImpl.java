package com.areahomeschoolers.baconbits.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.UserArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.User;

@Repository
public class UserDaoImpl extends SpringWrapper implements UserDao {
	private final class UserMapper implements RowMapper<User> {
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getInt("id"));
			user.setEmail(rs.getString("email"));
			user.setFirstName(rs.getString("firstName"));
			user.setLastName(rs.getString("lastName"));
			user.setHomePhone(rs.getString("homePhone"));
			user.setMobilePhone(rs.getString("mobilePhone"));
			user.setUserTypeId(rs.getInt("userTypeId"));
			user.setStartDate(rs.getTimestamp("startDate"));
			user.setEndDate(rs.getTimestamp("endDate"));
			user.setAddedDate(rs.getTimestamp("addedDate"));
			user.setLastLoginDate(rs.getTimestamp("lastLoginDate"));
			user.setActive(rs.getBoolean("isEnabled"));
			return user;
		}
	}

	private static String SELECT;

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
		String sql = "select * from users";
		ArrayList<User> data = query(sql, new UserMapper());

		return data;
	}

	@Override
	public User save(User user) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(user);

		if (user.getPassword() != null) {
			String digest = getSha1Hash(user.getPassword(), user.getEmail());
			if (user.getPasswordDigest() != null && user.getPasswordDigest().equals(digest)) {
				// retData.addError("Password must be different from the current password.");
				// return retData;
			}

			// resetting your own password
			if (user.equals(ServerContext.getCurrentUser())) {
				// retData.setErrors(Common.asArrayList(validatePortalPassword(user.getPassword())));
				// if (retData.hasErrors()) {
				// return retData;
				// }
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
			String sql = "update users set firstName = :firstName, lastName = :lastName, startDate = :startDate, endDate = :endDate, ";
			sql += "homePhone = :homePhone, mobilePhone = :mobilePhone, lastLoginDate = :lastLoginDate, passwordDigest = :passwordDigest where id = :id";
			update(sql, namedParams);
		} else {
			if (user.getStartDate() == null) {
				user.setStartDate(new Date());
			}
			String sql = "insert into users (email, firstName, lastName, passwordDigest, startDate, endDate, addedDate, homePhone, mobilePhone, userTypeId) values ";
			sql += "(:email, :firstName, :lastName, :passwordDigest, :startDate, :endDate, now(), :homePhone, :mobilePhone, :userTypeId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			user.setId(Integer.parseInt(keys.getKeys().get(Constants.GENERATED_KEY_TOKEN).toString()));
		}

		return getById(user.getId());
	}

}
