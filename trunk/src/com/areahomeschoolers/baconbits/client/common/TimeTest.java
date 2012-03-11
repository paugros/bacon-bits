package com.areahomeschoolers.baconbits.client.common;

import java.io.PrintStream;
import java.util.Date;

public abstract class TimeTest {

	private static PrintStream printStream = System.out;
	// I added this is so we don't have to write System.out. + println because I search
	// for that often to remove test code and I don't want this class coming up.

	private static Date timeTestStart;

	public static void end() {
		Date now = new Date();
		String output = "";

		if (timeTestStart == null) {
			output = "Error, timeTestStart() wasn't called";
		} else {
			long ms = now.getTime() - timeTestStart.getTime();
			if (ms >= 1000 * 60) {
				output = ms / (1000 * 60) + "m ";
			}

			ms = ms % (1000 * 60);

			output += ms / 1000 + "." + Common.zeroPad((int) ms % 1000, 3) + "s";
		}

		printStream.println(output);

		timeTestStart = null;
	}

	public static void start() {
		timeTestStart = new Date();
	}
}
