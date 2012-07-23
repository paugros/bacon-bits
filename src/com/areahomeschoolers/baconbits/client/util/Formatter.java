package com.areahomeschoolers.baconbits.client.util;

import java.util.Date;

import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Label;

/**
 * A repository of useful format related static methods, constants, etc. for use only on the client side
 */
public abstract class Formatter {
	public final static NumberFormat DEFAULT_CURRENCY_FORMAT = NumberFormat.getFormat("$#,##0.00;($#,##0.00)");
	public final static NumberFormat EXTENDED_CURRENCY_FORMAT = NumberFormat.getFormat("$#,##0.0000;($#,##0.0000)");
	public final static NumberFormat DOUBLE_SORT_FORMAT = NumberFormat.getFormat("0.0000");
	public final static DateTimeFormat DATE_SORT_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd-HH:mm:ss");
	public final static DateTimeFormat DEFAULT_SQL_DATE_VARCHAR_FORMAT = DateTimeFormat.getFormat("MMM dd yyyy h:mma");
	public final static DateTimeFormat DEFAULT_DATE_FORMAT = DateTimeFormat.getFormat("M/d/yy");
	public final static DateTimeFormat DEFAULT_TIME_FORMAT = DateTimeFormat.getFormat("h:mm a");
	public final static DateTimeFormat DEFAULT_DATE_TIME_FORMAT = DateTimeFormat.getFormat(DEFAULT_DATE_FORMAT.getPattern() + " "
			+ DEFAULT_TIME_FORMAT.getPattern());
	// 80 years out
	public final static Date invalidAfter = new Date(new Date().getTime() + 4160L * 604800000);

	/**
	 * Formats a double as currency.
	 * 
	 * @param amount
	 * @return The formatted currency string.
	 */
	public static String formatCurrency(Double amount) {
		if (amount == null) {
			return "";
		}
		return DEFAULT_CURRENCY_FORMAT.format(amount);
	}

	public static String formatCurrency(Integer amount) {
		if (amount == null) {
			return "";
		}
		return formatCurrency(amount.doubleValue());
	}

	/**
	 * Formats a string as currency.
	 * 
	 * @param amount
	 * @return The formatted currency string.
	 */
	public static String formatCurrency(String amount) {
		if (amount == null) {
			return "";
		}
		return formatCurrency(Double.parseDouble(amount));
	}

	/**
	 * Formats a date using the default date format.
	 * 
	 * @param date
	 * @return The formatted date string
	 */
	public static String formatDate(Date date) {
		if (date == null) {
			return "";
		}

		if (date.after(invalidAfter)) {
			return "";
		}
		return DEFAULT_DATE_FORMAT.format(date);
	}

	/**
	 * Formats a date using the specified date format.
	 * 
	 * @param date
	 * @param format
	 * @return The formatted date string
	 */
	public static String formatDate(Date date, String format) {
		if (date == null) {
			return "";
		}

		if (date.after(invalidAfter)) {
			return "";
		}
		return DateTimeFormat.getFormat(format).format(date);
	}

	/**
	 * Formats a date for proper sorting.
	 * 
	 * @param date
	 * @return The sort-formatted date string.
	 */
	public static String formatDateForSorting(Date date) {
		if (date == null) {
			return "";
		}

		if (date.after(invalidAfter)) {
			return "";
		}
		return DATE_SORT_FORMAT.format(date);
	}

	/**
	 * Formats a date using the default date and time format.
	 * 
	 * @param date
	 * @return The formatted date/time string
	 */
	public static String formatDateTime(Date date) {
		if (date == null) {
			return "";
		}

		if (date.after(invalidAfter)) {
			return "";
		}

		return DEFAULT_DATE_TIME_FORMAT.format(date);
	}

	/**
	 * Formats a date using the default date and time format, but trunctates the time if it is midnight.
	 * 
	 * @param date
	 * @return The formatted date/time string
	 */
	public static String formatDateTimeWithoutMidnight(Date date) {
		if (date == null) {
			return "";
		}

		if (date.after(invalidAfter)) {
			return "";
		}

		String value = DEFAULT_DATE_TIME_FORMAT.format(date);

		return value.replaceFirst(" 12:00 AM", "");
	}

	public static String formatDoubleForSorting(double value) {
		return DOUBLE_SORT_FORMAT.format(value);
	}

	/**
	 * Formats a double as currency with 4 decimal places.
	 * 
	 * @param amount
	 * @return The formatted currency string.
	 */
	public static String formatExtendedCurrency(double amount) {
		return EXTENDED_CURRENCY_FORMAT.format(amount);
	}

	public static String formatExtendedCurrency(String amount) {
		if (amount == null) {
			return "";
		}
		return formatExtendedCurrency(Double.parseDouble(amount));
	}

	/**
	 * Formats a number of bytes in a human-friendly way.
	 * 
	 * @param bytes
	 * @return The file size string
	 */
	public static String formatFileSize(int bytes) {
		int count = 0;
		float byteFloat = bytes;

		String[] format = { "B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };

		while ((byteFloat / 1024) > 1 && count < 8) {
			byteFloat = byteFloat / 1024;
			count++;
		}

		return Integer.toString(Math.round(byteFloat)) + " " + format[count];
	}

	/**
	 * Converts line breaks to HTML breaks in the specified string.
	 * 
	 * @param noteText
	 * @return The formatted note text
	 */
	public static String formatNoteText(String noteText) {
		if (noteText == null) {
			return "";
		}
		noteText = SafeHtmlUtils.fromString(Common.getDefaultIfNull(noteText, "")).asString();

		noteText = noteText.replaceAll("\\\n", "<br/>");
		return noteText;
	}

	/**
	 * Formats a double according to the specified format.
	 * 
	 * @param number
	 * @param format
	 * @return The formatted number string.
	 */
	public static String formatNumber(Number number, String format) {
		return NumberFormat.getFormat(format).format(number);
	}

	/**
	 * Formats a string containing a number according to the specified format. Will generate an exception if the string is not a valid number.
	 * 
	 * @param number
	 * @param format
	 * @return The formatted number string.
	 */
	public static String formatNumber(String number, String format) {
		return NumberFormat.getFormat(format).format(Double.parseDouble(number));
	}

	public static String formatNumberRange(int min, int max) {
		String range = Integer.toString(min);
		if (max == 0) {
			range += "+";
		} else {
			range += "-" + max;
		}

		return range;
	}

	/**
	 * Formats a string of numbers according to the default telephone number format.
	 * 
	 * @param number
	 * @return The formatted telephone number string
	 */
	public static String formatPhone(String number) {
		if (number == null) {
			return "";
		}

		String[] parts = number.split(" x");

		String newNumber = Common.stripNonNumericChars(parts[0]);
		String tmpNumber = "";

		if (newNumber.length() == 11) {
			tmpNumber = newNumber.substring(0, 1) + " (" + newNumber.substring(1, 4) + ") " + newNumber.substring(4, 7) + "-" + newNumber.substring(7);
		} else if (newNumber.length() == 10) {
			tmpNumber = "(" + newNumber.substring(0, 3) + ") " + newNumber.substring(3, 6) + "-" + newNumber.substring(6);
		} else if (newNumber.length() == 7) {
			tmpNumber = newNumber.substring(0, 3) + "-" + newNumber.substring(3);
		} else {
			tmpNumber = parts[0];
		}

		if (parts.length > 1) {
			tmpNumber += " x" + parts[1];
		}

		return tmpNumber;
	}

	/**
	 * Ex. 1 = "1st", 2 = "2nd", 3 = "3rd", 4 = "4th", 3815 = "3815th"
	 * 
	 * Only works for positive
	 */
	public static String formatRank(int rankNumber) {
		if (rankNumber < 1) {
			return Integer.toString(rankNumber);
		}

		String suffix;
		int mod100 = rankNumber % 100;
		switch (rankNumber % 10) {
		case 1:
			if (mod100 == 11) {
				suffix = "th";
			} else {
				suffix = "st";
			}
			break;
		case 2:
			if (mod100 == 12) {
				suffix = "th";
			} else {
				suffix = "nd";
			}
			break;
		case 3:
			suffix = "rd";
			break;
		default:
			suffix = "th";
			break;
		}

		return rankNumber + suffix;
	}

	public static String formatTime(Date date) {
		if (date == null) {
			return "";
		}
		return DEFAULT_TIME_FORMAT.format(date);
	}

	/**
	 * Creates a label with the number currency formatted. It changes the text color to red if the value is negative.
	 * 
	 * @return
	 */
	public static Label getCurrencyLabel(double value) {
		Label label = new Label(formatCurrency(value));

		if (value < 0) {
			label.setStyleName("inactiveText");
		}

		return label;
	}

	private Formatter() {
	}

}
