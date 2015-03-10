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

	public static final String DOCUMENT_URL_PREFIX = "/baconbits/service/file?id=";

	public static final String URL_SPECIAL_CHAR = "!";
	public static final String URL_SEPARATOR = "#" + URL_SPECIAL_CHAR;
	public static final int CG_ORG_ID = 21;
	public static final String CG_PAYPAL_EMAIL = "payments@citrusgroups.com";
	public static final String BOOK_GROUP_OPTION_CACHE_KEY = "bookSellerGroupOption_";
	public static final String SYSTEM_FROM_EMAIL = "kaugros@citrusgroups.com";
	public static final String SUPPORT_EMAIL = "kaugros@citrusgroups.com";
	public static final int DEFAULT_LOGO_ID = 1241;
	public static final int ACCOUNT_CREATION_INSTRUCTIONS_ID = 77;
	public static final int ONLINE_BOOK_SELLERS_GROUP_ID = 16;
	public static final int PHYSICAL_BOOK_SELLERS_GROUP_ID = 17;
	public static final int BOOK_TC_ARTICLE_ID = 103;
	public static final String CG_DOMAIN = "myhomeschoolgroups.com";
	public static final String CG_URL = "http://www." + CG_DOMAIN;
	public static final String TOS_URL = CG_URL + "/" + URL_SEPARATOR + "page=Article&articleId=73";
	public static final String PRIVACY_POLICY_URL = CG_URL + "/" + URL_SEPARATOR + "page=Article&articleId=72";
	public static final String PRODUCTION_VERSION = "production";
	// 2.9% and 30 to PayPal
	public static final double EVENT_PERCENT_MARKUP = 2.9;
	public static final double EVENT_DOLLARS_MARKUP = 0.30;
	public static final int DEFAULT_SEARCH_RADIUS = 25;
	public static final int MAX_DATA_ROWS = 2000;

	public static final String GCS_PREFIX = "http://storage.googleapis.com/baconbits-production/documents/";

	public final static String[] STATE_NAMES = { "AK", "AL", "AR", "AZ", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "IA", "ID", "IL", "IN", "KS", "KY", "LA",
			"MA", "MD", "ME", "MI", "MN", "MO", "MS", "MT", "NC", "ND", "NE", "NH", "NJ", "NM", "NJ", "NY", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN",
			"TX", "UT", "VA", "VT", "WA", "WI", "WV", "WY" };
}
