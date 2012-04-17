package com.areahomeschoolers.baconbits.client.util;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;

public class Url {

	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-M-d");

	public static int accountId() {
		return getIntegerParameter("accountId");
	}

	public static int addressId() {
		return getIntegerParameter("addressId");
	}

	public static int customerId() {
		return getIntegerParameter("customerId");
	}

	public static String decode(String encodedText) {
		if (encodedText == null) {
			return null;
		}

		return URL.decodePathSegment(encodedText);
	}

	public static String encode(String decodedText) {
		if (decodedText == null) {
			return null;
		}

		return URL.encodePathSegment(decodedText);
	}

	public static String formatDateForUrl(Date date) {
		return dateFormat.format(date);
	}

	/**
	 * Returns equivalent of GWT.getHostPageBaseURL() with the gwt.codesrv parameter, if present
	 * 
	 * @return String in the form of "[scheme]://[host][:protocol]/[?gwt.codesvr=[host:port]]"
	 */
	public static String getBaseUrl() {
		return GWT.getHostPageBaseURL() + getGwtCodeServerAsQueryString();
	}

	public static boolean getBooleanParameter(String token) {
		// return Boolean.parseBoolean(HistoryToken.getElement(token));
		return false;
	}

	public static String getCustomerUrlSegment() {
		int accountId = accountId();
		int customerId = customerId();

		if (accountId > 0) {
			// return "&accountId=" + HistoryToken.getElement("accountId");
		} else if (customerId > 0) {
			// return "&customerId=" + HistoryToken.getElement("customerId");
		} else {
			return "";
		}
		return "";
	}

	public static DateTimeFormat getDateFormat() {
		return dateFormat;
	}

	public static Date getDateParameter(String token) {
		// String value = HistoryToken.getElement(token);
		// if (Common.isNullOrBlank(value)) {
		// return null;
		// }

		// try {
		// return dateFormat.parse(value);
		// } catch (IllegalArgumentException e) {
		// return null;
		// }
		return null;
	}

	/**
	 * Returns a URL query string containing the value of the "gwt.codesvr" parameter
	 * 
	 * @return A string of form "?gwt.codesvr=[host:port]" or the empty string if the parameter is not set
	 */
	public static String getGwtCodeServerAsQueryString() {
		String ret = "";
		String svr = Window.Location.getParameter("gwt.codesvr");
		if (svr != null && !svr.equals("")) {
			ret = "?gwt.codesvr=" + svr;
		}
		return ret;
	}

	public static int getIntegerParameter(String token) {
		// String value = HistoryToken.getElement(token);
		// if (Common.isInteger(value)) {
		// return Integer.parseInt(value);
		// }
		return -1;
	}

	public static String getParam(String token) {
		// return HistoryToken.getElement(token);
		return "";
	}

	public static boolean isParamValidId(String token) {
		return getIntegerParameter(token) > 0;
	}

	public static boolean isValidDate(String dateString) {
		return dateString.matches("[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}");
	}

	public static int vendorId() {
		return getIntegerParameter("vendorId");
	}

	private Url() {

	}
}