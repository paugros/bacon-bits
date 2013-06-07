package com.areahomeschoolers.baconbits.client.content.calendar.theme.google.client;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.content.calendar.AppointmentStyle;

final public class GoogleAppointmentTheme {
	// border, background
	public static final GoogleAppointmentStyle BLUE = new GoogleAppointmentStyle("#2952A3", "#b8c9ed");
	public static final GoogleAppointmentStyle RED = new GoogleAppointmentStyle("#A32929", "#edb8b8");
	public static final GoogleAppointmentStyle PINK = new GoogleAppointmentStyle("#B1365F", "#f5cad8");
	public static final GoogleAppointmentStyle PURPLE = new GoogleAppointmentStyle("#7A367A", "#d7b5d7");
	public static final GoogleAppointmentStyle DARK_PURPLE = new GoogleAppointmentStyle("#5229A3", "#c9b8ed");
	public static final GoogleAppointmentStyle STEELE_BLUE = new GoogleAppointmentStyle("#29527A", "#4885c1");
	public static final GoogleAppointmentStyle LIGHT_BLUE = new GoogleAppointmentStyle("#1B887A", "#a3dbd5");
	public static final GoogleAppointmentStyle TEAL = new GoogleAppointmentStyle("#28754E", "#a8d0bc");
	public static final GoogleAppointmentStyle LIGHT_TEAL = new GoogleAppointmentStyle("#4A716C", "#c1d4d1");
	public static final GoogleAppointmentStyle GREEN = new GoogleAppointmentStyle("#0D7813", "#92d096");
	public static final GoogleAppointmentStyle LIGHT_GREEN = new GoogleAppointmentStyle("#528800", "#bad98c");
	public static final GoogleAppointmentStyle YELLOW_GREEN = new GoogleAppointmentStyle("#88880E", "#dada98");
	public static final GoogleAppointmentStyle YELLOW = new GoogleAppointmentStyle("#AB8B00", "#eede98");
	public static final GoogleAppointmentStyle ORANGE = new GoogleAppointmentStyle("#BE6D00", "#f8d2a0");
	public static final GoogleAppointmentStyle RED_ORANGE = new GoogleAppointmentStyle("#B1440E", "#f3c0a6");
	public static final GoogleAppointmentStyle LIGHT_BROWN = new GoogleAppointmentStyle("#865A5A", "#e5d3d3");
	public static final GoogleAppointmentStyle LIGHT_PURPLE = new GoogleAppointmentStyle("#705770", "#d6cbd6");
	public static final GoogleAppointmentStyle GREY = new GoogleAppointmentStyle("#4E5D6C", "#c3cad1");
	public static final GoogleAppointmentStyle BLUE_GREY = new GoogleAppointmentStyle("#5A6986", "#d3d9e5");
	public static final GoogleAppointmentStyle YELLOW_GREY = new GoogleAppointmentStyle("#6E6E41", "#d0d0ba");
	public static final GoogleAppointmentStyle BROWN = new GoogleAppointmentStyle("#8D6F47", "#e5d8c8");
	public static final GoogleAppointmentStyle DEFAULT = BLUE;
	public static final Map<AppointmentStyle, GoogleAppointmentStyle> STYLES = new HashMap<AppointmentStyle, GoogleAppointmentStyle>();

	static {
		STYLES.put(AppointmentStyle.BLUE, BLUE);
		STYLES.put(AppointmentStyle.BLUE_GREY, BLUE_GREY);
		STYLES.put(AppointmentStyle.BROWN, BROWN);
		STYLES.put(AppointmentStyle.DARK_PURPLE, DARK_PURPLE);
		STYLES.put(AppointmentStyle.GREEN, GREEN);
		STYLES.put(AppointmentStyle.GREY, GREY);
		STYLES.put(AppointmentStyle.LIGHT_BLUE, LIGHT_BLUE);
		STYLES.put(AppointmentStyle.LIGHT_BROWN, LIGHT_BROWN);
		STYLES.put(AppointmentStyle.LIGHT_GREEN, LIGHT_GREEN);
		STYLES.put(AppointmentStyle.LIGHT_PURPLE, LIGHT_PURPLE);
		STYLES.put(AppointmentStyle.LIGHT_TEAL, LIGHT_TEAL);
		STYLES.put(AppointmentStyle.ORANGE, ORANGE);
		STYLES.put(AppointmentStyle.PINK, PINK);
		STYLES.put(AppointmentStyle.PURPLE, PURPLE);
		STYLES.put(AppointmentStyle.RED, RED);
		STYLES.put(AppointmentStyle.RED_ORANGE, RED_ORANGE);
		STYLES.put(AppointmentStyle.STEELE_BLUE, STEELE_BLUE);
		STYLES.put(AppointmentStyle.TEAL, TEAL);
		STYLES.put(AppointmentStyle.YELLOW, YELLOW);
		STYLES.put(AppointmentStyle.YELLOW_GREEN, YELLOW_GREEN);
		STYLES.put(AppointmentStyle.YELLOW_GREY, YELLOW_GREY);
		STYLES.put(AppointmentStyle.DEFAULT, DEFAULT);
	}

	private GoogleAppointmentTheme() {
	}
}
