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
		// System.out.println(request.getSession().getCreationTime());
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
		if ("class org.mortbay.jetty.EofException".equals(e.getClass().toString())) {
			// We want to allow this. We know this will be thrown when pages are reloaded because an RPC call is made from
			// a CloseHandler event. This exception happens then because the client it not there to receive the HttpResponse.
			// We couldn't do an instanceof check because the server doesn't know about the EofException class.
			return;
		}

		e.printStackTrace();
		super.doUnexpectedFailure(e);
	}

}
