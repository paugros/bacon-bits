package com.areahomeschoolers.baconbits.server.util;

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
		String url = tl.get().request.getScheme() + "://" + tl.get().request.getServerName();
		int port = tl.get().request.getServerPort();
		if (port != 80 && port != 443) {
			url += ":" + port;
		}
		url += "/";

		return url;
	}

	public static String getBaseUrlWithCodeServer() {
		String url = getBaseUrl();

		if (!isLive()) {
			url += "?gwt.codesvr=127.0.0.1:9997";
		}

		return url;
	}

	public static MemcacheService getCache() {
		return cache;
	}

	public static UserGroup getCurrentOrg() {
		HttpSession session = ServerContext.getSession();
		if (session == null) {
			return null;
		}

		Integer orgId = (Integer) session.getAttribute("orgId");
		if (orgId == null) {
			setCurrentOrg();
			orgId = getCurrentOrgId();
		}

		return (UserGroup) cache.get("group_" + orgId);
	}

	public static int getCurrentOrgId() {
		HttpSession session = ServerContext.getSession();
		if (session == null) {
			return 0;
		}

		Integer orgId = (Integer) session.getAttribute("orgId");
		if (orgId == null) {
			return 0;
		}

		return orgId;
	}

	public static User getCurrentUser() {
		HttpSession session = ServerContext.getSession();
		if (session == null) {
			return null;
		}

		Integer userId = (Integer) session.getAttribute("userId");
		User u = (User) cache.get("user_" + userId);
		if (u == null && userId != null) {
			UserDao userDao = getDaoImpl("user");
			u = userDao.getById(userId, false);
			setCurrentUser(u);
		}

		return u;
	}

	public static int getCurrentUserId() {
		HttpSession session = ServerContext.getSession();
		if (session == null) {
			return 0;
		}

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
		if (tl.get() == null) {
			return null;
		}
		return tl.get().request.getSession();
	}

	public static boolean isAuthenticated() {
		return getCurrentUser() != null;
	}

	public static boolean isCitrus() {
		if (getCurrentOrg() == null) {
			return true;
		}
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

	public static void setCurrentOrg() {
		if (getCurrentOrgId() > 0) {
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
		cache.put("user_" + u.getId(), u);
	}

	public static void setCurrentUser(User u) {
		getSession().setAttribute("userId", u.getId());
		updateUserCache(u);
	}

	public static void switchToUser(int userId) {
		User currentUser = getCurrentUser();
		if (currentUser.getId() == userId || currentUser.canSwitch() == false) {
			return;
		}
		UserDao userDao = (UserDao) ctx.getBean("userDaoImpl");
		User newCurrentUser = userDao.getById(userId);
		newCurrentUser.setCanSwitch(true);
		if (currentUser.isSwitched()) {
			newCurrentUser.setOriginalUserId(currentUser.getOriginalUserId());
			newCurrentUser.setOriginalEmail(currentUser.getOriginalEmail());
		} else {
			newCurrentUser.setOriginalUserId(currentUser.getId());
			newCurrentUser.setOriginalEmail(currentUser.getEmail());
		}

		getSession().setAttribute("user", newCurrentUser);
	}

	public static void unloadContext() {
		tl.remove();
	}

	public static void updateGroupCache(UserGroup group) {
		cache.put("group_" + group.getId(), group);
	}

	public static void updateUserCache(User user) {
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
