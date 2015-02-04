package com.areahomeschoolers.baconbits.server.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class CrawlServlet implements Filter {
	private static final String ESCAPED_FRAGMENT_FORMAT1 = "_escaped_fragment_=";
	private static final int ESCAPED_FRAGMENT_LENGTH1 = ESCAPED_FRAGMENT_FORMAT1.length();
	private static final String ESCAPED_FRAGMENT_FORMAT2 = "&" + ESCAPED_FRAGMENT_FORMAT1;
	private static final int ESCAPED_FRAGMENT_LENGTH2 = ESCAPED_FRAGMENT_FORMAT2.length();

	private static boolean done = false;

	private static String rewriteQueryString(String queryString) throws UnsupportedEncodingException {
		// Seek the escaped fragment.
		int index = queryString.indexOf(ESCAPED_FRAGMENT_FORMAT2);
		int length = ESCAPED_FRAGMENT_LENGTH2;
		if (index == -1) {
			index = queryString.indexOf(ESCAPED_FRAGMENT_FORMAT1);
			length = ESCAPED_FRAGMENT_LENGTH1;
		}
		if (index != -1) {
			// Found the escaped fragment, so build back the original decoded one.
			final StringBuilder queryStringSb = new StringBuilder();
			// Add url parameters if any.
			if (index > 0) {
				queryStringSb.append("?");
				queryStringSb.append(queryString.substring(0, index));
			}
			// Add the hash fragment as a replacement for the escaped fragment.
			queryStringSb.append("#!");
			// Add the decoded token.
			final String token2Decode = queryString.substring(index + length, queryString.length());
			final String tokenDecoded = URLDecoder.decode(token2Decode, "UTF-8");
			queryStringSb.append(tokenDecoded);
			return queryStringSb.toString();
		}
		return queryString;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		// Grab the request uri and query strings.
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		final String requestURI = httpRequest.getRequestURI();
		String queryString = httpRequest.getQueryString();
		final HttpServletResponse httpResponse = (HttpServletResponse) response;

		if (!done) {
			queryString = "_escaped_fragment_=page=EventList";
			done = true;
		}
		if ((queryString != null) && (queryString.contains(ESCAPED_FRAGMENT_FORMAT1))) {
			// Rewrite the URL back to the original #! version
			// -- basically remove _escaped_fragment_ from the query.
			// Unescape any %XX characters as need be.
			final String urlStringWithHashFragment = requestURI + rewriteQueryString(queryString);
			System.out.println(urlStringWithHashFragment);
			final PrintWriter out = httpResponse.getWriter();
			out.println("");
			out.flush();
			out.close();
		} else {
			filterChain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig args) throws ServletException {

	}
}