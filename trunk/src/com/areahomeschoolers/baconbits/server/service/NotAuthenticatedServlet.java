package com.areahomeschoolers.baconbits.server.service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * This servlet is invoked by Spring Security whenever an unauthenticated request for a protected resource is received
 */
@Component
@RequestMapping("/notAuthenticated")
public class NotAuthenticatedServlet extends RemoteServiceServlet implements Controller {

	private static final long serialVersionUID = 1L;

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String method = request.getMethod();

		if ("GET".equals(method)) {
			response.reset();
			ServletOutputStream out = response.getOutputStream();
			out.write(Constants.NOT_AUTHENTICATED_TOKEN.getBytes());
			out.flush();
			out.close();
		}
		return null;
	}

	@Override
	protected void checkPermutationStrongName() throws SecurityException {
		return;
	}

}