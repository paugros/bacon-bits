package com.areahomeschoolers.baconbits.client.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;
import com.areahomeschoolers.baconbits.shared.dto.GenericEntity;
import com.areahomeschoolers.baconbits.shared.dto.Pair;

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

	public static enum IpV6Style {
		NO_LEADING_ZEROS, ABBREV
	}

	private static final String[] PRIVATE_ALLOWED_UPLOAD_EXTENSIONS = { "jpeg", "jpg", "gif", "png", "csv", "xls", "xlsx", "doc", "docx", "pdf", "vsd", "tif",
			"pcap", "zip", "txt", "htm", "html", "pcf", "vpl", "ppt", "pptx", "mpp", "cap", "wav", "bmp", "mp3" };
	public static final List<String> ALLOWED_UPLOAD_EXTENSIONS = Collections.unmodifiableList(Arrays.asList(PRIVATE_ALLOWED_UPLOAD_EXTENSIONS));

	private final static String EMAIL_VALIDATION_REGEX = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

	public static final int MAX_DATA_ROWS = 2000;

	/**
	 * Ex. 5,500,000 = 5.5M 7,000 = 7K
	 * 
	 * @return
	 */
	public static String abbreviateNumber(double value) {
		String text;

		if (value >= 1000000) {
			long val = Math.round(value / 100000);
			text = Long.toString(val / 10);
			if (val % 10 != 0) {
				text += "." + val % 10;
			}
			text += "M";
		} else if (value >= 1000) {
			long val = Math.round(value / 100);
			text = Long.toString(val / 10);
			if (val % 10 != 0) {
				text += "." + val % 10;
			}
			text += "K";
		} else {
			text = Long.toString(Math.round(value));
		}

		return text;
	}

	/**
	 * Pads a list with nulls until the specified index.
	 * 
	 * @param list
	 * @param colIndex
	 */
	public static void addNullsUntil(List<?> list, int colIndex) {
		for (int nullIndex = list.size(); nullIndex <= colIndex; nullIndex++) {
			list.add(null);
		}
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

	// @SafeVarargs TODO Put these back for Java 7
	public final static <T> ArrayList<T> asArrayList(T... objects) {
		ArrayList<T> list = new ArrayList<T>();
		for (T object : objects) {
			list.add(object);
		}
		return list;
	}

	public final static <K, V> HashMap<K, V> asHashMap(Map<K, V> map) {
		if (map instanceof HashMap) {
			return (HashMap<K, V>) map;
		}

		return new HashMap<K, V>(map);
	}

	public final static <T> List<T> asList(T object) {
		List<T> list = new ArrayList<T>();
		list.add(object);
		return list;
	}

	// @SafeVarargs
	public final static <T> List<T> asList(T... objects) {
		List<T> list = new ArrayList<T>();
		for (T object : objects) {
			list.add(object);
		}
		return list;
	}

	public static String bytesToIpV6(byte[] bytes) {
		return bytesToIpV6(bytes, null);
	}

	public static String bytesToIpV6(byte[] bytes, Set<IpV6Style> set) {
		if (bytes.length != 16) {
			return null;
		}

		boolean noLeading = false;
		boolean abbrev = false;

		if (set != null) {
			if (set.contains(IpV6Style.NO_LEADING_ZEROS)) {
				noLeading = true;
			}

			if (set.contains(IpV6Style.ABBREV)) {
				abbrev = true;
			}
		}

		List<String> blocks = new ArrayList<String>();

		for (int i = 0; i < 16; i += 2) {
			blocks.add(byteToHex(bytes[i]) + byteToHex(bytes[i + 1]));
		}

		String text = join(blocks, ":");
		String zeroText = "0000";

		if (noLeading) {
			for (int i = 0; i < blocks.size(); i++) {
				String blockText = blocks.get(i);

				for (int j = 0; j < 3; j++) {
					if (blockText.startsWith("0")) {
						blockText = blockText.substring(1);
					}
				}

				blocks.set(i, blockText);
			}

			zeroText = "0";
			text = join(blocks, ":");
		}

		if (abbrev) {
			// This will abbreviate the biggest set of adjacent zero blocks,
			// favoring the right side

			int biggestBlockStartIndex = -1;
			int biggestBlockSize = 0;
			int currentBlockStartIndex = -1;
			int currentBlockSize = 0;
			boolean prevWasZero = false;

			for (int i = 0; i < 8; i++) {
				if (zeroText.equals(blocks.get(i))) {
					currentBlockSize++;
					if (!prevWasZero) {
						currentBlockStartIndex = i;
					}

					if (currentBlockSize >= biggestBlockSize) {
						biggestBlockSize = currentBlockSize;
						biggestBlockStartIndex = currentBlockStartIndex;
					}

					prevWasZero = true;
				} else {
					currentBlockSize = 0;

					prevWasZero = false;
				}
			}

			if (biggestBlockStartIndex != -1) {
				text = "";
				for (int i = 0; i < biggestBlockStartIndex; i++) {
					text += blocks.get(i) + ":";
				}

				if (biggestBlockStartIndex == 0) {
					text += "::";
				} else {
					text += ":";
				}

				for (int i = biggestBlockStartIndex; i < 8; i++) {
					if (blocks.get(i).equals(zeroText)) {
						continue;
					}

					text += blocks.get(i);

					if (i != 7) {
						text += ":";
					}
				}
			}
		}

		return text;
	}

	public static String byteToHex(byte b) {
		return Integer.toString((b & 0xff) + 0x100, 16).substring(1).toUpperCase();
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
		return value;
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

	public static <T extends EntityDto<?>> List<Integer> getIdList(List<T> list) {
		List<Integer> idList = new ArrayList<Integer>();

		for (T entity : list) {
			idList.add(entity.getId());
		}

		return idList;
	}

	public final static String[] getOutlookImportHeaders() {
		final String[] headers = { "Title", "First Name", "Middle Name", "Last Name", "Suffix", "Company", "Department", "Job Title", "Business Street",
				"Business Street 2", "Business Street 3", "Business City", "Business State", "Business Postal Code", "Business Country", "Home Street",
				"Home Street 2", "Home Street 3", "Home City", "Home State", "Home Postal Code", "Home Country", "Other Street", "Other Street 2",
				"Other Street 3", "Other City", "Other State", "Other Postal Code", "Other Country", "Assistant's Phone", "Business Fax", "Business Phone",
				"Business Phone 2", "Callback", "Car Phone", "Company Main Phone", "Home Fax", "Home Phone", "Home Phone 2", "ISDN", "Mobile Phone",
				"Other Fax", "Other Phone", "Pager", "Primary Phone", "Radio Phone", "TTY/TDD Phone", "Telex", "Account", "Anniversary", "Assistant's Name",
				"Billing Information", "Birthday", "Business Address PO Box", "Categories", "Children", "Directory Server", "E-mail Address", "E-mail Type",
				"E-mail Display Name", "E-mail 2 Address", "E-mail 2 Type", "E-mail 2 Display Name", "E-mail 3 Address", "E-mail 3 Type",
				"E-mail 3 Display Name", "Gender	Government ID Number", "Hobby", "Home Address PO Box", "Initials", "Internet Free Busy", "Keywords",
				"Language", "Location", "Manager's Name", "Mileage", "Notes", "Office Location", "Organizational ID Number", "Other Address PO Box",
				"Priority", "Private", ", Profession", "Referred By", "Sensitivity", "Spouse", "User 1", "User 2", "User 3", "User 4", "Web Page" };

		return headers;
	}

	public static String getSimpleClassName(Class<?> c) {
		return c.getName().substring(c.getName().lastIndexOf(".") + 1);
	}

	/**
	 * This gets a string between two identifier strings within a string. Returns null if any string isn't found or something is out of order.
	 * 
	 * Example: getTextBetween( "asdf PHONENUMBER(603-555-1234) asdf", "PHONENUMBER(", ")" ) = "603-555-1234"
	 * 
	 * @param fullText
	 * @param leadingText
	 * @param postText
	 * @return
	 */
	public static String getTextBetween(String full, String leading, String post) {
		if (full == null || leading == null || post == null) {
			return null;
		}

		int startPos = full.indexOf(leading) + leading.length();
		int endPos = full.indexOf(post, startPos);

		if (startPos < 0 || startPos > endPos) {
			return null;
		}

		return full.substring(startPos, endPos);
	}

	public static Pair<Long, Long> ipv4PrefixToRange(String addrPrefix) {
		Pair<Long, Long> range = new Pair<Long, Long>();
		String[] partArr = addrPrefix.split("\\.");

		for (int i = 0; i < partArr.length; i++) {
			partArr[i] = stripNonNumericChars(partArr[i]);
		}

		addrPrefix = join(partArr, ".");

		String lBound = addrPrefix;
		String uBound = addrPrefix;

		partArr = addrPrefix.split("\\.");
		for (int i = partArr.length; i < 4; i++) {
			lBound += ".0";
			uBound += ".255";
		}

		try {
			range.setA(ipv4ToLong(lBound));
			range.setB(ipv4ToLong(uBound));
		} catch (NumberFormatException e) {
			return null;
		}

		return range;
	}

	/**
	 * Converts an IP address to an integer.
	 * 
	 * @param addr
	 *            A dotted-decimal IPV4 address
	 * @return An IP address in Integer format
	 */
	public static long ipv4ToLong(String addr) throws NumberFormatException {
		String[] addrArray = addr.split("\\.");

		long num = 0;
		for (int i = 0; i < addrArray.length; i++) {
			int power = 3 - i;

			num += ((Long.parseLong(addrArray[i]) % 256 * Math.pow(256, power)));
		}

		return num;
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

	/**
	 * @param number
	 * @return Whether the string provided can be formatted as a Double
	 */
	public static boolean isDouble(String number) {
		try {
			Double.parseDouble(number);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	public static boolean isIn(Integer value, Integer... list) {
		for (Integer i : list) {
			if (value == i) {
				return true;
			}
		}

		return false;
	}

	public static boolean isIn(String value, String... list) {
		for (String compare : list) {
			if (value.equals(compare)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param number
	 * @return Whether the string provided can be formatted as an Integer
	 */
	public static boolean isInteger(String number) {
		try {
			Integer.parseInt(number);
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	// Non use means that the device is in inventory, not available, or spare.
	public static boolean isNonUseLocationTypeId(int locTypeId) {
		return locTypeId == 3 || (locTypeId > 4 && locTypeId != 9);
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
	 * @param ipAddress
	 * @return Whether the string provided is a valid dotted-decimal IPV4 address. Includes verification that each segment is less than 255 and greater than
	 *         zero. Allows zero-padded segments.
	 */
	public static boolean isValidIpAddress(String ipAddress) {
		String[] parts = ipAddress.split("\\.");
		if (parts.length != 4) {
			return false;
		}

		for (String part : parts) {
			int i = 0;
			try {
				i = Integer.parseInt(part);
			} catch (NumberFormatException e) {
				return false;
			}

			if ((i < 0) || (i > 255)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isValidRegex(String regex) {
		try {
			"".matches(regex);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static String join(List<GenericEntity> list, String key, String token) {
		List<String> strList = new ArrayList<String>();
		for (GenericEntity entity : list) {
			strList.add(entity.get(key));
		}
		return join(strList, token);
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
	 * This will return the numeric binary values in a boolean array.
	 * 
	 * For example: 3 in binary = 11 so this returns: {true, true}. 6 => 110 => {true, true, false}.
	 * 
	 * @param exValue
	 * @return
	 */
	public static List<Boolean> longToBooleanArray(long l) {
		if (l < 2) {
			List<Boolean> single = new ArrayList<Boolean>();
			single.add(l == 1 ? true : false);
			return single;
		}

		Boolean end = false;
		if (l % 2 == 1) {
			l--;
			end = true;
		}

		List<Boolean> rest = longToBooleanArray(l / 2);
		rest.add(end);

		return rest;
	}

	/**
	 * Converts a long int to an IP address.
	 * 
	 * @param i
	 *            An IP address in Integer format
	 * @return A dotted-decimal IPV4 address
	 */
	public static String longToIpV4(long i) {
		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}

	/**
	 * Mereges the two lists into one. If each list already contain the same GenericEnty, the GenericEntity's themselves are merged with a
	 * GenericEntity.putAll(). This assumes that neither lists contain duplicate entries.
	 * 
	 * @param list1
	 * @param list2
	 * @return
	 */
	public static List<GenericEntity> mergeLists(List<GenericEntity> list1, List<GenericEntity> list2) {
		List<GenericEntity> mergedList = new ArrayList<GenericEntity>(list1);

		for (GenericEntity l2Entity : list2) {
			boolean matchFound = false;
			for (GenericEntity l1Entity : list1) {
				if (l1Entity.equals(l2Entity)) {
					l1Entity.putAll(l2Entity);
					matchFound = true;
					break;
				}
			}

			if (!matchFound) {
				mergedList.add(l2Entity);
			}
		}

		return mergedList;
	}

	public static String nonEmptyStringOrNull(String in) {
		if (in == null || in.length() > 0) {
			return in;
		}
		return null;
	}

	public static int occuranceCount(String searchOn, String search) {
		int count = -1;
		int afterIndex = -1;

		while (afterIndex != -1 || count == -1) {
			count++;
			afterIndex = searchOn.indexOf(search, afterIndex + 1);
		}

		return count;
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

	public static <T> List<T> removeDuplicates(Collection<T> collection) {
		return new ArrayList<T>(new HashSet<T>(collection));
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
	public static String stripNonAlphaNumericChars(String str) {
		return str.replaceAll("[^0-9A-Za-z\\ ]+", "");
	}

	/**
	 * Removes all non-alphanumeric characters, except spaces
	 * 
	 * @param str
	 * @return
	 */
	public static String stripNonAlphaNumericChars(String str, String replace) {
		return str.replaceAll("[^0-9A-Za-z\\ ]+", replace);
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
	 * Throws a an exception if the provided regular expression is invalid.
	 * 
	 * @param regex
	 *            A regular expression
	 */
	public static void testRegex(String regex) {
		"".matches(regex);
	}

	/**
	 * Cuts a string down to the specified length (if neccessary) and appends "..." The length of the return string will never be longer than the length
	 * 
	 * @param string
	 * @param length
	 * @return
	 */
	public static String truncate(String string, int length) {
		if (string.length() <= length) {
			return string;
		}

		return string.substring(0, length - 3) + "...";
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
	 * The integer value of bytes are -127 to 127. This converts the value to 0 - 255.
	 * 
	 * @param byt
	 * @return
	 */
	public static int unsignedIntVal(byte byt) {
		if (byt < 0) {
			return 256 + byt;
		}

		return byt;
	}

	/**
	 * @param bool
	 * @return "Yes" if bool is true, otherwise "No"
	 */
	public static String yesNo(boolean bool) {
		return bool ? "Yes" : "No";
	}

	public static String zeroPad(double input) {
		return zeroPad(input, 15);
	}

	public static String zeroPad(double input, int size) {
		String ret = "";
		if (input < 0) {
			input = 1000000000000.0 + input;
			ret += "-";
		}

		return ret + zeroPad(Formatter.formatDoubleForSorting(input), size);
	}

	public static String zeroPad(int input) {
		return zeroPad(input, 10);
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
