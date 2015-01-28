package com.areahomeschoolers.baconbits.client;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;

/**
 * Manages access to history token in URL.
 */
public class HistoryToken {
	private static Map<String, String> tokenMap;

	/**
	 * Appends a new parameter segment to the URL
	 * 
	 * @param extraToken
	 *            The additional parameter segment to be appended to the URL
	 */
	public static void append(String extraToken) {
		append(extraToken, true);
	}

	/**
	 * Appends a new parameter segment to the URL
	 * 
	 * @param extraToken
	 *            The additional parameter segment to be appended to the URL
	 * @param issueEvent
	 *            Whether to issue a history event (load the page)
	 */
	public static void append(String extraToken, boolean issueEvent) {
		createMapFromToken(History.getToken() + "&" + extraToken);
		updateHistoryFromMap(issueEvent, false);
	}

	/**
	 * Updates the underlying history token map by parsing the key/value pairs in a URL-style string.
	 * 
	 * @param token
	 */
	public static void createMapFromToken(String token) {
		if (token.startsWith("!")) {
			token = token.substring(1);
		}

		tokenMap = new TreeMap<String, String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1 == null || o2 == null) {
					return 1;
				}

				if (o1.equals(o2)) {
					return 0;
				}

				if ("page".equals(o1)) {
					return -1;
				} else if ("page".equals(o2)) {
					return 1;
				}

				return o1.compareTo(o2);
			}
		});

		if (token == null || token.isEmpty()) {
			return;
		}
		String[] stmts = token.split("&");

		for (final String s : stmts) {
			String[] kv = s.split("=");
			if (kv.length > 1) {
				tokenMap.put(kv[0], kv[1]);
			}
		}
	}

	/**
	 * Gets the value of a particular history token entry (URL parameter)
	 * 
	 * @param key
	 *            The parameter name whose value is to be fetched.
	 * @return The current value of the provided URL parameter name.
	 */
	public static String getElement(String key) {
		return tokenMap.get(key);
	}

	public static void removeToken(String token, boolean issueEvent) {
		tokenMap.remove(token);
		updateHistoryFromMap(issueEvent, false);
	}

	/**
	 * Replaces the entire current token map and URL with a new one.
	 * 
	 * @param newToken
	 *            The new URL
	 */
	public static void set(String newToken) {
		set(newToken, true);
	}

	/**
	 * Replaces the entire current token map and URL with a new one.
	 * 
	 * @param newToken
	 *            The new URL
	 * @param issueEvent
	 *            Whether to issue a history event (load the page)
	 */
	public static void set(String newToken, boolean issueEvent) {
		createMapFromToken(newToken);
		updateHistoryFromMap(issueEvent, false);
	}

	/**
	 * Sets one particular key/value URL parameter pair. This will update the value if the parameter already exists, or add it if it does not exist.
	 * 
	 * @param key
	 *            The parameter name
	 * @param value
	 *            The parameter value
	 */
	public static void setElement(String key, String value) {
		setElement(key, value, true);
	}

	/**
	 * Sets one particular key/value URL parameter pair. This will update the value if the parameter already exists, or add it if it does not exist.
	 * 
	 * @param key
	 *            The parameter key
	 * @param value
	 *            The parameter value
	 * @param issueEvent
	 *            Whether to issue a history event (load the page)
	 */
	public static void setElement(String key, String value, boolean issueEvent) {
		tokenMap.put(key, value);
		updateHistoryFromMap(issueEvent, false);
	}

	public static void setNewTab(String newToken) {
		createMapFromToken(newToken);
		updateHistoryFromMap(true, true);
	}

	/**
	 * Uses the current token map to set the URL.
	 * 
	 * @param issueEvent
	 *            Whether to issue a history event (load the page).
	 */
	private static void updateHistoryFromMap(boolean issueEvent, boolean newTab) {
		String historyToken = "";

		int i = 0;
		for (String key : tokenMap.keySet()) {
			historyToken += key + "=" + tokenMap.get(key);
			if (++i < tokenMap.size()) {
				historyToken += "&";
			}
		}

		if (newTab) {
			Window.open(Url.getGwtCodeServerAsQueryString() + Constants.URL_SEPARATOR + historyToken, "_blank", "");
		} else {
			History.newItem(Constants.URL_SPECIAL_CHAR + historyToken, issueEvent);
		}
	}

	private HistoryToken() {
	}
}
