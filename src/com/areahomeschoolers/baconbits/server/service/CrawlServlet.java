package com.areahomeschoolers.baconbits.server.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;

public final class CrawlServlet implements Filter {
	private static final String ESCAPED_FRAGMENT_FORMAT1 = "_escaped_fragment_=";
	private static final int ESCAPED_FRAGMENT_LENGTH1 = ESCAPED_FRAGMENT_FORMAT1.length();
	private static final String ESCAPED_FRAGMENT_FORMAT2 = "&" + ESCAPED_FRAGMENT_FORMAT1;
	private static final int ESCAPED_FRAGMENT_LENGTH2 = ESCAPED_FRAGMENT_FORMAT2.length();

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
		final HttpServletRequest httpRequest = (HttpServletRequest) request;
		String queryString = httpRequest.getQueryString();
		final HttpServletResponse httpResponse = (HttpServletResponse) response;

		if ((queryString != null) && (queryString.contains(ESCAPED_FRAGMENT_FORMAT1))) {
			String url = request.getScheme() + "://" + request.getServerName();
			int port = request.getServerPort();
			if (port != 80 && port != 443) {
				url += ":" + port;
			}
			url += "/";

			if (!ServerContext.isLive()) {
				url = "http://www.myhomeschoolgroups.com/";
			}

			url += rewriteQueryString(queryString);

			url = URLEncoder.encode(url, "UTF-8");

			String agent = URLEncoder.encode("Mozilla/5.0+(Windows+NT+6.1)+AppleWebKit/537.36+Chrome/28.0.1468.0+Safari/537.36+PhantomJsCloud/1.1", "UTF-8");
			String jsUrl = "http://api.phantomjscloud.com/single/browser/v1/997774cd49a439c431a67f9cf08d2b3025f3d5a6/?requestType=text&targetUrl=" + url;
			jsUrl += "&loadImages=false&outputAsJson=false&timeout=30000&abortOnJavascriptErrors=false&delayTime=1000&isDebug=false&postDomLoadedTimeout=10000";
			jsUrl += "&userAgent=" + agent + "&viewportSize=" + URLEncoder.encode("{+height:1280,+width:720+}", "UTF-8") + "&geolocation=us";

			String data = ServerUtils.getUrlContents(jsUrl);

			final PrintWriter out = httpResponse.getWriter();
			out.println(data);
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