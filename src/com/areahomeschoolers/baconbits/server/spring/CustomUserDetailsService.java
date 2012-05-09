package com.areahomeschoolers.baconbits.server.spring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
	private DataSource dataSource;
	private JdbcTemplate sjt;

	public DataSource getDataSource() {
		return dataSource;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		RowMapper<User> mapper = new RowMapper<User>() {
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new User(rs.getString("email"), rs.getString("passwordDigest"), rs.getBoolean("isEnabled"), rs.getBoolean("isEnabled"), true, true,
						getAuthorities(rs));
			}
		};

		User user;

		String sql = "select u.id, u.email, u.passwordDigest, ";
		sql += "isActive(u.StartDate, u.EndDate) as isEnabled from users u ";
		sql += "where u.email = ? and u.passwordDigest is not null limit 1";

		try {
			user = sjt.queryForObject(sql, mapper, username);
		} catch (DataAccessException e) {
			throw new UsernameNotFoundException(username);
		}

		return user;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		sjt = new JdbcTemplate(dataSource);
	}

	private List<GrantedAuthority> getAuthorities(ResultSet rs) throws SQLException {
		List<GrantedAuthority> authList = new ArrayList<GrantedAuthority>();
		authList.add(new SimpleGrantedAuthority("ROLE_BASIC_USER"));

		return authList;
	}
}
