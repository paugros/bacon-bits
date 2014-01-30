package com.areahomeschoolers.baconbits.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * A repository of useful static methods, constants, etc. for use on both the client and server side
 */
public abstract class Common {

	/**
	 * For use inside compareTo overrides.
	 */
	public static class ComparisonType {
		public static final int BEFORE = -1;
		public static final int EQUAL = 0;
		public static final int AFTER = 1;
	}

	private final static String EMAIL_VALIDATION_REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

	public static double getEventMarkup(double price, Event event) {
		double percent = Constants.EVENT_PERCENT_MARKUP;
		double dollars = Constants.EVENT_DOLLARS_MARKUP;

		if (event.getGroupMarkupOverride()) {
			percent = event.getGroupMarkupPercent();
			dollars = event.getGroupMarkupDollars();
		}

		if (event.getMarkupOverride()) {
			percent = event.getMarkupPercent();
			dollars = event.getMarkupDollars();
		}

		return (price * (percent / 100)) + dollars;
	}

	public final static <T> ArrayList<T> asArrayList(List<T> list) {
		if (list instanceof ArrayList) {
			return (ArrayList<T>) list;
		}
		return new ArrayList<T>(list);
	}

	public final static <T> ArrayList<T> asArrayList(T object) {
		ArrayList<T> list = new ArrayList<T>();
		list.add(object);
		return list;
	}

	@SafeVarargs
	public final static <T> ArrayList<T> asArrayList(T... objects) {
		ArrayList<T> list = new ArrayList<T>();
		for (T object : objects) {
			list.add(object);
		}
		return list;
	}

	public final static <T> List<T> asList(T object) {
		List<T> list = new ArrayList<T>();
		list.add(object);
		return list;
	}

	@SafeVarargs
	public final static <T> List<T> asList(T... objects) {
		List<T> list = new ArrayList<T>();
		for (T object : objects) {
			list.add(object);
		}
		return list;
	}

	/**
	 * Converts degrees to radians.
	 * 
	 * @param deg
	 * @return
	 */
	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/**
	 * A generic logical equals method.
	 * 
	 * @param object1
	 * @param object2
	 * @return True if the two objects refer to the same object, or are logically equal according to their equals method. This method is null safe (won't
	 *         generate exceptions if one or both objects is null).
	 */
	public static boolean equals(Object object1, Object object2) {
		if (object1 == object2) {
			return true;
		}

		if ((object1 == null) || (object2 == null)) {
			return false;
		}

		return object1.equals(object2);
	}

	/**
	 * @param value
	 * @return The string "None" if value is null, otherwise value
	 */
	public static String getDefaultIfNull(String value) {
		return getDefaultIfNull(value, "None");
	}

	/**
	 * @param value
	 * @param defaultValue
	 * @return The specified string if value is null, otherwise value
	 */
	public static String getDefaultIfNull(String value, String defaultValue) {
		if (value == null || value.isEmpty()) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return The distance in miles between the two points specified.
	 */
	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		return (dist);
	}

	/**
	 * Places quotes around strings that contain commas, for CSV formatting. Also converts nulls to empty strings.
	 * 
	 * @param value
	 * @return
	 */
	public static String getExcelValue(String value) {
		if (value == null) {
			value = "";
		}
		if (value.contains(",")) {
			value = "\"" + value + "\"";
		}
		return value.replaceAll("\n", " ");
	}

	/**
	 * Gets the file extension off of a file name. Returns an empty string if there is no extension.
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileExtension(String fileName) {
		if (fileName == null) {
			return null;
		}

		fileName = fileName.trim();
		int dotPos = fileName.lastIndexOf('.');
		if (dotPos == -1) {
			return "";
		}

		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

	public static String getSimpleClassName(Class<?> c) {
		return c.getName().substring(c.getName().lastIndexOf(".") + 1);
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return True if the current date falls between the dates provided. null dates are interpreted as max and min date values for end and start dates
	 *         respectively.
	 */
	public static boolean isActive(Date startDate, Date endDate) {
		return isActive(startDate, endDate, new Date());
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @param referenceDate
	 * @return True if the reference date falls between the start and end dates. null dates are interpreted as max and min date values for end and start dates
	 *         respectively.
	 */
	public static boolean isActive(Date startDate, Date endDate, Date referenceDate) {
		long start, end, now;

		if (startDate == null) {
			start = Long.MIN_VALUE;
		} else {
			start = startDate.getTime();
		}

		if (endDate == null) {
			end = Long.MAX_VALUE;
		} else {
			end = endDate.getTime();
		}

		if (referenceDate == null) {
			now = new Date().getTime();
		} else {
			now = referenceDate.getTime();
		}

		return (start <= now && end >= now);
	}

	public static boolean isAllLowerCase(String test) {
		if (isNullOrBlank(test)) {
			return false;
		}

		return test.matches("^[a-z]+$");
	}

	public static boolean isAllUpperCase(String test) {
		if (isNullOrBlank(test)) {
			return false;
		}

		return test.matches("^[A-Z]+$");
	}

	/**
	 * @param number
	 * @return Whether the string provided can be formatted as a Double
	 */
	public static boolean isDouble(String number) {
		if (number == null) {
			return false;
		}

		try {
			Double.parseDouble(number);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	/**
	 * @param number
	 * @return Whether the string provided can be formatted as an Integer
	 */
	public static boolean isInteger(String number) {
		if (number == null) {
			return false;
		}

		try {
			Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	/**
	 * Returns true if the string is null, is empty, or contains only white-space.
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNullOrBlank(String str) {
		if (str == null) {
			return true;
		}

		return str.trim().isEmpty();
	}

	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * @param number
	 * @return Whether the string provided can be formatted as an Integer or a Double
	 */
	public static boolean isNumeric(String number) {
		return (isInteger(number) || isDouble(number));
	}

	/**
	 * @param email
	 * @return Whether the string provided matches the email validation regular expression
	 */
	public static boolean isValidEmail(String email) {
		if (email == null) {
			return false;
		}

		if (email.toLowerCase().matches(EMAIL_VALIDATION_REGEX)) {
			return true;
		}
		return false;
	}

	/**
	 * @param <T>
	 * @param list
	 * @param token
	 * @return The objects in the provided list converted to Strings, joined in a string delimited by token.
	 */
	public static <T> String join(List<T> list, String token) {
		if (list == null || list.size() < 1) {
			return "";
		}

		StringBuffer output = new StringBuffer();
		for (int i = 0; i < list.size(); i++) {
			T item = list.get(i);
			if (item == null) {
				continue;
			}
			output.append(item.toString());
			if (i < (list.size() - 1)) {
				output.append(token);
			}
		}
		return output.toString();
	}

	/**
	 * @param set
	 * @param token
	 * @return The strings in the provided set, joined in a string delimited by token.
	 */
	public static <T> String join(Set<T> set, String token) {
		List<T> s = new ArrayList<T>();
		s.addAll(set);
		return join(s, token);
	}

	public static <T> String join(String[] list, String token) {
		return join(Arrays.asList(list), token);
	}

	/**
	 * @param array
	 * @param token
	 * @return The items in array, joined in a string delimited by token.
	 */
	public static <T> String join(T[] array, String token) {
		return join(Arrays.asList(array), token);
	}

	/**
	 * This wraps URLs in plain text with anchor tags so that they can be clicked when shown as HTML.
	 * 
	 * @param text
	 * @return
	 */
	public static String makeUrlsClickable(String text) {
		if (text == null) {
			return null;
		}

		String clickableText = text;
		List<String> replacedUrls = new ArrayList<String>();

		String urlRegex = "\\b(https?://|www\\.|dash\\.)+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*\\b";
		RegExp regExp = RegExp.compile(urlRegex, "gi");

		for (MatchResult matcher = regExp.exec(text); matcher != null; matcher = regExp.exec(text)) {
			String rawUrl = matcher.getGroup(0);
			if (replacedUrls.contains(rawUrl)) {
				// If the same URL is in the text twice, we've already made the replacement in the replaceAll below.
				continue;
			}

			String url = rawUrl;
			if (!url.toLowerCase().startsWith("http")) {
				url = "http://" + rawUrl;
			}

			clickableText = clickableText.replace(rawUrl, "<a href=\"" + url + "\">" + rawUrl + "</a>");
			replacedUrls.add(rawUrl);
		}

		return clickableText;
	}

	/**
	 * Converts radians to degrees.
	 * 
	 * @param rad
	 * @return
	 */
	public static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	/**
	 * This works exactly like String.split, only it doesn't skip empty strings. For example:
	 * 
	 * "'asdf;;asdf".split(";").length = 2;
	 * 
	 * Common.split(";asdf;;asdf", ";").length() = 4
	 * 
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static List<String> split(String str, String pattern) {
		List<String> list = new ArrayList<String>();

		int pos;
		while ((pos = str.indexOf(pattern)) >= 0) {
			list.add(str.substring(0, pos));
			str = str.substring(pos + 1);
		}

		list.add(str);

		return list;
	}

	/**
	 * Removes all non-alphanumeric characters, except spaces
	 * 
	 * @param str
	 * @return
	 */
	public static String stripNonAlphaChars(String str) {
		return str.replaceAll("[^A-Za-z]+", "");
	}

	public static String stripNonNumericChars(String str) {
		return str.replaceAll("[^0-9]+", "");
	}

	/**
	 * Removes all white space characters.
	 * 
	 * @param str
	 * @return
	 */
	public static String stripWhiteSpace(String str) {
		return str.replaceAll("\\s", "");
	}

	/**
	 * Uppercases the first letter of each word in a string.
	 * 
	 * @param str
	 * @return The string provided with the first letter of each of its words in uppercase
	 */
	public static String ucWords(String str) {
		if (str == null) {
			return null;
		}

		char ch; // One of the characters in str.
		char prevCh; // The character that comes before ch in the string.
		prevCh = '.'; // Prime the loop with any non-letter character.
		String newString = "";
		for (int i = 0; i < str.length(); i++) {
			ch = str.charAt(i);
			if (Character.isLetter(ch) && !Character.isLetter(prevCh)) {
				newString += Character.toUpperCase(ch);
			} else {
				newString += Character.toLowerCase(ch);
			}
			prevCh = ch;
		}

		return newString;
	}

	/**
	 * @param bool
	 * @return "Yes" if bool is true, otherwise "No"
	 */
	public static String yesNo(boolean bool) {
		return bool ? "Yes" : "No";
	}

	public static String zeroPad(int input, int size) {
		if (input < 0) {
			input = Integer.MAX_VALUE - input;
		}

		return zeroPad(Integer.toString(input), size);
	}

	/**
	 * @param input
	 * @return The provide string, left-padded with zeros until it is 10 characters in length
	 */
	public static String zeroPad(String input) {
		return zeroPad(input, 10);
	}

	/**
	 * @param input
	 * @param size
	 *            The length of the desired zero-padded string
	 * @return The provide string, left-padded with zeros until it has as many characters as are specified in size
	 */
	public static String zeroPad(String s, int n) {
		if (s == null) {
			return s;
		}

		int add = n - s.length();

		int decPos = s.indexOf(".");
		if (decPos > 0) {
			add = n - decPos;
		}

		if (add <= 0) {
			return s;
		}
		StringBuffer str = new StringBuffer(s);
		char[] ch = new char[add];
		Arrays.fill(ch, '0');
		str.insert(0, ch);
		return str.toString();
	}

	private Common() {

	}

}
