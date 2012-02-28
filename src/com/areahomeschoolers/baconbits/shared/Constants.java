package com.areahomeschoolers.baconbits.shared;

public abstract class Constants {
	public final static String NOT_AUTHENTICATED_TOKEN = "<NOT AUTHENTICATED>";

	/** days in each month */
	public final static int[] DAYS_OF_MONTHS = { 31, 28, 31, /* jan, feb, mar */
	30, 31, 30, /* apr, may, jun */
	31, 31, 30, /* jul, aug, sep */
	31, 30, 31 /* oct, nov, dec */
	};

	public final static String[] MONTH_NAMES = { "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November",
			"December" };

	public static final String[] DAYS_OF_WEEK = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
}
