package com.areahomeschoolers.baconbits.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.areahomeschoolers.baconbits.shared.dto.User;

/**
 * This class serves as an accessor class for thread specific data (request, response, url), session data (session, user), and the application context
 */
@Component
public class ServerContext implements ApplicationContextAware {

	// since the we use ctx as a read-only singleton, we can share it with all threads
	private static ApplicationContext ctx = null;
	// tl holds all thread-specific data
	private static ThreadLocal<ServerContext> tl = new ThreadLocal<ServerContext>();

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
		url += "/" + getGwtCodeServerAsQueryString();

		return url;
	}

	public static Date getBuildDate() {
		InputStream is = tl.get().servletContext.getResourceAsStream("/WEB-INF/ribeye.properties");
		Properties props = new Properties();

		try {
			props.load(is);

			return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(props.getProperty("build.date"));
		} catch (IOException e) {
			return null;
		} catch (ParseException e) {
			return null;
		}
	}

	public static int getBuildNumber() {
		InputStream is = tl.get().servletContext.getResourceAsStream("/WEB-INF/ribeye.properties");
		Properties props = new Properties();
		try {
			props.load(is);
			return Integer.parseInt(props.getProperty("build.number"));
		} catch (IOException e) {
			return 0;
		}
	}

	public static User getCurrentUser() {
		HttpSession session = ServerContext.getSession();
		if (session == null) {
			return null;
		}

		return (User) session.getAttribute("user");
	}

	/**
	 * Returns a URL query string containing the value of the "gwt.codesvr" parameter.
	 * 
	 * @return A string of form "?gwt.codesvr=[host:port]" or the empty string if the parameter is not set
	 */
	public static String getGwtCodeServerAsQueryString() {
		// Note: this is empty for non-page requests (ie, an RPC to lock a quote)
		String ret = "";
		String svr = tl.get().request.getParameter("gwt.codesvr");
		if (svr != null) {
			ret = "?gwt.codesvr=" + svr;
		}
		return ret;
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

	public static void loadContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
		ServerContext sc = new ServerContext();
		sc.request = request;
		sc.response = response;
		sc.servletContext = servletContext;
		tl.set(sc);
	}

	/**
	 * Called after successful authentication, pulls current user from db and puts it in session
	 * 
	 * @param username
	 *            Authenticated user's username
	 */
	public static void setCurrentUser(String username) {
		// UserDao userDao = (UserDao) ctx.getBean("userDaoImpl");
		// User u = userDao.getUserByUsername(username);
		//
		// u.setCanSwitch(u.memberOf(Group.APPLICATIONS_DEVELOPMENT));
		// u.setOriginalUserId(u.getId());
		// u.setOriginalEmail(u.getEmail());
		//
		// tl.get().request.getSession().setAttribute("user", u);
	}

	public static void unloadContext() {
		tl.remove();
	}

	private ServletContext servletContext;
	private HttpServletRequest request;
	private HttpServletResponse response;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		ServerContext.ctx = ctx;
	}
}
