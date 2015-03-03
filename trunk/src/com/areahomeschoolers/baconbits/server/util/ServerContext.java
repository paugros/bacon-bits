package com.areahomeschoolers.baconbits.server.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.utils.SystemProperty;

/**
 * This class serves as an accessor class for thread specific data (request, response, url), session data (session, user), and the application context
 */
@Component
public class ServerContext implements ApplicationContextAware {

	// since the we use ctx as a read-only singleton, we can share it with all threads
	private static ApplicationContext ctx = null;
	// tl holds all thread-specific data
	private static ThreadLocal<ServerContext> tl = new ThreadLocal<ServerContext>();
	private static boolean isLiveIsSet;
	private static boolean isLive;
	private static MemcacheService cache = MemcacheServiceFactory.getMemcacheService();

	static {
		cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
	}

	public static ApplicationContext getApplicationContext() {
		return ctx;
	}

	/**
	 * Returns server-side equivalent of GWT.getHostPageBaseURL() with the gwt.codesrv parameter, if present. <b>NOTE:</b> because the return value may contain
	 * the code server parameter, it is not possible to use this method in links that contain paths.
	 * 
	 * @return String in the form of "[scheme]://[host][:protocol]/[?gwt.codesvr=[host:port]]"
	 * 
	 */
	public static String getBaseUrl() {
		return getBaseUrlWithoutSeparator() + Constants.URL_SEPARATOR;
	}

	public static String getBaseUrlWithCodeServer() {
		String url = tl.get().request.getScheme() + "://" + tl.get().request.getServerName();
		int port = tl.get().request.getServerPort();
		if (port != 80 && port != 443) {
			url += ":" + port;
		}

		if (!isLive()) {
			url += "?gwt.codesvr=127.0.0.1:9997";
		}
		url += "/" + Constants.URL_SEPARATOR;

		return url;
	}

	public static String getBaseUrlWithoutSeparator() {
		String url = tl.get().request.getScheme() + "://" + tl.get().request.getServerName();
		int port = tl.get().request.getServerPort();
		if (port != 80 && port != 443) {
			url += ":" + port;
		}
		url += "/";

		return url;
	}

	public static MemcacheService getCache() {
		return cache;
	}

	public static Double getCurrentLat() {
		if (getSession().getAttribute("lat") == null) {
			return 0.0;
		}
		return (Double) getSession().getAttribute("lat");
	}

	public static Double getCurrentLng() {
		if (getSession().getAttribute("lng") == null) {
			return 0.0;
		}

		return (Double) getSession().getAttribute("lng");
	}

	public static String getCurrentLocation() {
		if (getSession().getAttribute("location") == null) {
			return null;
		}
		return (String) getSession().getAttribute("location");
	}

	public static UserGroup getCurrentOrg() {
		HttpSession session = ServerContext.getSession();

		Integer orgId = (Integer) session.getAttribute("orgId");

		if (orgId == null || !cache.contains("group_" + orgId)) {
			setCurrentOrg();
			orgId = getCurrentOrgId();
		}

		return (UserGroup) cache.get("group_" + orgId);
	}

	public static int getCurrentOrgId() {
		HttpSession session = ServerContext.getSession();

		Integer orgId = (Integer) session.getAttribute("orgId");
		if (orgId == null) {
			return 0;
		}

		return orgId;
	}

	public static int getCurrentRadius() {
		if (getSession().getAttribute("radius") == null) {
			return 0;
		}
		return (int) getSession().getAttribute("radius");
	}

	public static User getCurrentUser() {
		HttpSession session = ServerContext.getSession();

		Integer userId = (Integer) session.getAttribute("userId");
		User u = (User) cache.get("user_" + userId);
		if (u == null && userId != null) {
			UserDao userDao = getDaoImpl("user");
			u = userDao.getById(userId, false);
			setCurrentUser(u);
		}

		if (u != null) {
			Integer originalUserId = (Integer) session.getAttribute("originalUserId");
			u.setSwitched(originalUserId != null && originalUserId != u.getId());
		}

		return u;
	}

	public static int getCurrentUserId() {
		HttpSession session = ServerContext.getSession();

		Integer userId = (Integer) session.getAttribute("userId");
		if (userId == null) {
			return 0;
		}

		return userId;
	}

	public static <T> T getDaoImpl(String dao) {
		return (T) getApplicationContext().getBean(dao + "DaoImpl");
	}

	public static HttpServletRequest getRequest() {
		return tl.get().request;
	}

	public static HttpServletResponse getResponse() {
		return tl.get().response;
	}

	public static ServletContext getServletContext() {
		return tl.get().servletContext;
	}

	public static HttpSession getSession() {
		return tl.get().request.getSession();
	}

	public static String getUrlContents(String urlText) throws IOException {
		URL url = new URL(urlText);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;

		StringBuffer buffer = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		reader.close();

		return buffer.toString();
	}

	public static boolean hasLocation() {
		return !Common.isNullOrBlank(getCurrentLocation());
	}

	public static boolean isAuthenticated() {
		HttpSession session = ServerContext.getSession();

		return session.getAttribute("userId") != null;
	}

	public static boolean isCitrus() {
		return getCurrentOrg().isCitrus();
	}

	public static boolean isLive() {
		if (isLiveIsSet) {
			return isLive;
		}

		isLive = SystemProperty.environment.value() == SystemProperty.Environment.Value.Production;

		String version = SystemProperty.applicationVersion.get();
		if (version == null) {
			version = Constants.PRODUCTION_VERSION;
		}

		if (!version.contains(Constants.PRODUCTION_VERSION)) {
			isLive = false;
		}

		isLiveIsSet = true;

		return isLive;
	}

	public static boolean isPhantomJsRequest() {
		String agent = tl.get().request.getHeader("User-Agent");
		return (agent != null && agent.contains("PhantomJsCloud"));
	}

	public static boolean isSystemAdministrator() {
		return isAuthenticated() && getCurrentUser().getSystemAdministrator();
	}

	public static void loadContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
		ServerContext sc = new ServerContext();
		sc.request = request;
		sc.response = response;
		sc.servletContext = servletContext;
		tl.set(sc);

		// an active session for a disabled user -- kill it
		User u = getCurrentUser();
		if (u != null && !u.isActive()) {
			LoginService loginService = (LoginService) getApplicationContext().getBean("LoginServiceImpl");
			loginService.logout();
		}
	}

	public static void setCurrentLat(double lat) {
		getSession().setAttribute("lat", lat);
	}

	public static void setCurrentLng(double lng) {
		getSession().setAttribute("lng", lng);
	}

	public static void setCurrentLocation(String location) {
		getSession().setAttribute("location", location);
	}

	public static void setCurrentOrg() {
		int orgId = getCurrentOrgId();
		if (orgId > 0 && cache.contains("group_" + orgId)) {
			return;
		}

		UserDao userDao = getDaoImpl("user");
		UserGroup org = userDao.getOrgForCurrentRequest();
		getSession().setAttribute("orgId", org.getId());
		cache.put("group_" + org.getId(), org);
	}

	public static void setCurrentOrg(UserGroup org) {
		getSession().setAttribute("orgId", org.getId());
	}

	public static void setCurrentRadius(int radius) {
		getSession().setAttribute("radius", radius);
	}

	/**
	 * Called after successful authentication, pulls current user from db and puts it in session
	 * 
	 * @param username
	 *            Authenticated user's username
	 */
	public static void setCurrentUser(String username) {
		User u = null;
		if (username != null) {
			UserService userService = (UserService) ctx.getBean("userServiceImpl");
			u = userService.getUserByUsername(username);
		}

		getSession().setAttribute("userId", u.getId());
		updateUserCache(u);
	}

	public static void setCurrentUser(User u) {
		Integer userId = u == null ? null : u.getId();
		getSession().setAttribute("userId", userId);
		updateUserCache(u);
	}

	public static void switchToUser(int userId) {
		User currentUser = getCurrentUser();
		if (!currentUser.canSwitch()) {
			return;
		}

		Integer origId = (Integer) getSession().getAttribute("originalUserId");
		getSession().setAttribute("userId", userId);

		// switching back
		if (origId != null && origId == userId) {
			getSession().removeAttribute("originalUserId");
		} else if (origId == null && currentUser.getId() != userId) {
			getSession().setAttribute("originalUserId", currentUser.getId());
		}
	}

	public static void unloadContext() {
		tl.remove();
	}

	public static void updateGroupCache(UserGroup group) {
		cache.put("group_" + group.getId(), group);
	}

	public static void updateUserCache(User user) {
		user.setSwitched(false);
		cache.put("user_" + user.getId(), user);
	}

	private ServletContext servletContext;
	private HttpServletRequest request;

	private HttpServletResponse response;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		ServerContext.ctx = ctx;
	}
}
