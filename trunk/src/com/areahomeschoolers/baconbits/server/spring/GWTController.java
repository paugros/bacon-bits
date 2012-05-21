package com.areahomeschoolers.baconbits.server.spring;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.areahomeschoolers.baconbits.server.util.ServerContext;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Spring controller class that handles all requests and passes them on to GWT. Also initializes server context.
 */
public class GwtController extends RemoteServiceServlet implements ServletConfigAware, ServletContextAware, Controller, RemoteService {

	private static final long serialVersionUID = 1L;

	protected ServletContext servletContext;

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	// Call GWT's RemoteService doPost() method and return null.
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// load our ServerContext with current request, response, session, user, appContext, etc.
		ServerContext.loadContext(request, response, servletContext);
		try {
			doPost(request, response);
		} finally {
			ServerContext.unloadContext();
		}
		return null; // response handled by GWT RPC over XmlHttpRequest
	}

	@Override
	public void setServletConfig(ServletConfig conf) {
		try {
			super.init(conf);
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	protected void checkPermutationStrongName() throws SecurityException {
		return;
	}

	@Override
	protected void doUnexpectedFailure(Throwable e) {
		e.printStackTrace();
		super.doUnexpectedFailure(e);
	}

}
