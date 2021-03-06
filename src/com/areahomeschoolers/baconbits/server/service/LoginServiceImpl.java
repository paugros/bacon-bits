package com.areahomeschoolers.baconbits.server.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.server.dao.PaymentDao;
import com.areahomeschoolers.baconbits.server.dao.TagDao;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.dao.impl.UserDaoImpl;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagType;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Controller
@RequestMapping("/login")
public class LoginServiceImpl extends GwtController implements LoginService {
	private static final long serialVersionUID = 1L;
	private final AuthenticationManager authenticationManager;
	private final UserDao userDao;

	@Autowired
	public LoginServiceImpl(AuthenticationManager authenticationManager, UserDao userDao) {
		this.authenticationManager = authenticationManager;
		this.userDao = userDao;
	}

	@Override
	public ApplicationData getApplicationData() {
		// load the current organization (based on the host used to access) into the session
		ServerContext.setCurrentOrg();

		User user = ServerContext.getCurrentUser();

		if (!ServerContext.isPhantomJsRequest()) {
			initLocation();
		}

		ApplicationData ap = new ApplicationData();
		ap.setCurrentOrg(ServerContext.getCurrentOrg());
		ap.setCurrentUser(user);
		ap.setAdultBirthYear(Calendar.getInstance().get(Calendar.YEAR) - 18);
		ap.setUserActivity(UserDaoImpl.getAllUserActivity());
		if (!Common.isNullOrBlank(ServerContext.getCurrentLocation())) {
			ap.setCurrentLocation(ServerContext.getCurrentLocation());
			ap.setCurrentLat(ServerContext.getCurrentLat());
			ap.setCurrentLng(ServerContext.getCurrentLng());
		}
		ap.setLive(ServerContext.isProduction());

		// UserDao userDao = ServerContext.getDaoImpl("user");
		// ap.setDynamicMenuItems(userDao.getMenuItems(new ArgMap<UserArg>(UserArg.ORGANIZATION_ID, ServerContext.getCurrentOrgId())));

		if (user != null) {
			UserDaoImpl.updateUserActivity(user.getId());

			TagDao tagDao = ServerContext.getDaoImpl("tag");
			ArgMap<TagArg> args = new ArgMap<TagArg>(TagArg.TYPE, TagType.USER.toString());
			args.put(TagArg.ENTITY_ID, user.getId());
			ap.setInterests(tagDao.list(args));

			PaymentDao paymentDao = ServerContext.getDaoImpl("payment");
			ap.setUnpaidBalance(paymentDao.getUnpaidBalance(user.getId()));
		}

		return ap;
	}

	@Override
	public boolean login(String username, String password) {
		boolean success = true;

		try {
			Authentication request = new UsernamePasswordAuthenticationToken(username, password);
			Authentication result = authenticationManager.authenticate(request);
			SecurityContextHolder.getContext().setAuthentication(result);
			// never time out
			ServerContext.getSession().setMaxInactiveInterval(-1);
			ServerContext.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

			ServerContext.setCurrentUser(username);
			User user = ServerContext.getCurrentUser();
			List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(result.getAuthorities());
			// populate the roles for use on the client side
			HashSet<AccessLevel> roles = new HashSet<AccessLevel>();
			for (AccessLevel role : AccessLevel.values()) {
				if (authorities.contains(new SimpleGrantedAuthority(role.toString()))) {
					roles.add(role);
				}
			}
			user.setAccessLevels(roles);

			if (!user.isActive()) {
				success = false;
			}

			if (!success) {
				logout();
			}
		} catch (Exception e) {
			if (!(e instanceof BadCredentialsException)) {
				e.printStackTrace();
			}

			success = false;
		}

		if (success) {
			userDao.recordLogin(username);
		}

		return success;
	}

	@Override
	public ApplicationData loginAndGetApplicationData(String username, String password) {
		login(username, password);
		return getApplicationData();
	}

	@Override
	public ApplicationData loginForPasswordReset(int id, String digest) {
		User u = userDao.setPasswordFromDigest(id, digest);

		if (u == null) {
			return getApplicationData();
		}

		return loginAndGetApplicationData(u.getUserName(), digest);
	}

	@Override
	public void logout() {
		ServerContext.getSession().setAttribute("userId", null);
		ServerContext.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, null);
		SecurityContextHolder.clearContext();
	}

	@Override
	public boolean sendPasswordResetEmail(String username) {
		return userDao.sendPasswordResetEmail(username);
	}

	private void initLocation() {
		// default location
		String location = ServerContext.getCurrentLocation();

		if (location != null) {
			// System.out.println("Using existing session location...");
			return;
		}

		location = "";
		try {
			// System.out.println("Looking up location...");
			// String stringData = ServerUtils.getUrlContents("http://ip-api.com/json/216.204.1.1");
			String stringData = ServerUtils.getUrlContents("http://ip-api.com/json/" + ServerContext.getRequest().getRemoteAddr());
			JsonObject locationData = new JsonParser().parse(stringData).getAsJsonObject();
			String status = ServerUtils.getStringFromJsonObject(locationData, "status");
			if (status != null && status.equals("success")) {
				location = ServerUtils.getStringFromJsonObject(locationData, "zip");
				String lat = ServerUtils.getStringFromJsonObject(locationData, "lat");
				String lng = ServerUtils.getStringFromJsonObject(locationData, "lon");
				if (lat != null) {
					ServerContext.setCurrentLat(Double.parseDouble(lat));
				}
				if (lng != null) {
					ServerContext.setCurrentLng(Double.parseDouble(lng));
				}
			}
		} catch (IOException e) {
			Logger logger = Logger.getLogger(this.getClass().toString());
			logger.log(Level.WARNING, e.getMessage());
		}

		ServerContext.setCurrentRadius(Constants.DEFAULT_SEARCH_RADIUS);
		ServerContext.setCurrentLocation(location);

	}
}
