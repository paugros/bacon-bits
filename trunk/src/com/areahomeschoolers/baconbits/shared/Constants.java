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

	public static final String GENERATED_KEY_TOKEN = "GENERATED_KEY";

	public static final int MAXIMUM_TAG_COUNT = 50;

	public static final String DOCUMENT_URL_PREFIX = "/baconbits/service/file?id=";

	public static final int BLANK_PROFILE_MALE_LARGE = 961;
	public static final int BLANK_PROFILE_MALE_SMALL = 963;
	public static final int BLANK_PROFILE_FEMALE_LARGE = 960;
	public static final int BLANK_PROFILE_FEMALE_SMALL = 962;
	public static final int BLANK_BOOK_IMAGE_SMALL = 34;
	public static final int BLANK_BOOK_IMAGE = 32;

	public static final int CG_ORG_ID = 21;
	public static final String CG_DOMAIN = "myhomeschoolgroups.com";
	public static final String CG_URL = "http://www." + CG_DOMAIN;
	public static final String TOS_URL = CG_URL + "/#page=Article&articleId=73";
	public static final String PRIVACY_POLICY_URL = CG_URL + "/#page=Article&articleId=72";

}
