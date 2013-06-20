package com.areahomeschoolers.baconbits.shared;

import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class TimeTest {

	private static PrintStream printStream = System.out;
	// I added this is so we don't have to write System.out. + println because I search
	// for that often to remove test code and I don't want this class coming up.

	private static Map<Integer, Date> startMap = new HashMap<Integer, Date>();

	public static String end() {
		return end(0);
	}

	public static String end(int testNumber) {
		String output = getTime(testNumber);

		printStream.println(output);

		startMap.remove(testNumber);

		return output;
	}

	public static String getTime() {
		return getTime(0);
	}

	public static String getTime(int testNumber) {
		Date now = new Date();
		String output = "";

		Date timeTestStart = startMap.get(testNumber);
		if (timeTestStart == null) {
			output = "Error, TimeTest.start() wasn't called";
		} else {
			long ms = now.getTime() - timeTestStart.getTime();
			if (ms >= 1000 * 60) {
				output = ms / (1000 * 60) + "m ";
			}

			ms = ms % (1000 * 60);

			output += ms / 1000 + "." + Common.zeroPad((int) ms % 1000, 3) + "s";
		}

		return output;

	}

	public static void start() {
		start(0);
	}

	public static void start(int testNumber) {
		Date timeTestStart = new Date();
		startMap.put(testNumber, timeTestStart);
	}
}
