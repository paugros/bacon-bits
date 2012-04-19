package com.areahomeschoolers.baconbits.client.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Pair;

import com.google.gwt.i18n.client.DateTimeFormat;

public abstract class ClientDateUtils {

	public static final long MILISECOND = 1;
	public static final long SECOND = MILISECOND * 1000;
	public static final long MINUTE = SECOND * 60;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	public static final long WEEK = DAY * 7;

	private static DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy:MM:dd HH:mm:ss");
	private static DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy:MM:dd");
	private static DateTimeFormat timeFormat = DateTimeFormat.getFormat("HH:mm:ss");
	private static List<Date> holidays;

	public static Date addDays(Date date, long amount) {
		return changeTime(date, amount, DAY);
	}

	public static Date addHours(Date date, long amount) {
		return changeTime(date, amount, HOUR);
	}

	public static Date addMiliSeconds(Date date, long amount) {
		return changeTime(date, amount, MILISECOND);
	}

	public static Date addMinutes(Date date, long amount) {
		return changeTime(date, amount, MINUTE);
	}

	/**
	 * This function will add a month to the date. It will not change the day or year, just the month. The day will only change if the new month does not have
	 * that day in it.
	 */
	public static Date addMonths(Date date, int amount) {
		int year = getYear(date);
		int month = getMonth(date) + amount;
		int day = getDay(date);

		if (month < 1) {
			year--;
			month += 12;
		} else if (month > 12) {
			year++;
			month = 1;
		}

		int lastDayOfMonth = Constants.DAYS_OF_MONTHS[month - 1];
		if (day > lastDayOfMonth) {
			day = lastDayOfMonth;
		}

		return createDate(year, month, day);
	}

	public static Date addSeconds(Date date, long amount) {
		return changeTime(date, amount, SECOND);
	}

	public static Date addWeeks(Date date, long amount) {
		return changeTime(date, amount, WEEK);
	}

	public static Date addYears(Date date, int amount) {
		return setYear(date, getYear(date) + amount);
	}

	public static boolean areEquals(Date date1, Date date2) {

		return getDayInMonth(date1) == getDayInMonth(date2) && getMonth(date1) == getMonth(date2) && getYear(date1) == getYear(date2);
	}

	public static Date changeTime(Date date, long amount, long timeFactor) {
		Date newDate = new Date();

		newDate.setTime(date.getTime() + (amount * timeFactor));

		return newDate;
	}

	public static long daysBetween(Date d1, Date d2) {
		return timeBetween(d1, d2, DAY);
	}

	public static List<Date> getAllHolidayDates() {
		if (holidays == null) {
			holidays = new ArrayList<Date>();
			holidays.add(dateFormat.parse("2010:01:01")); // New Year's Day
			holidays.add(dateFormat.parse("2010:01:18")); // Martin Luther King Day
			holidays.add(dateFormat.parse("2010:02:15")); // President's Day
			holidays.add(dateFormat.parse("2010:05:31")); // Memorial Day
			holidays.add(dateFormat.parse("2010:07:05")); // Independence Day
			holidays.add(dateFormat.parse("2010:09:06")); // Labor Day
			holidays.add(dateFormat.parse("2010:11:25")); // Thanksgiving
			holidays.add(dateFormat.parse("2010:11:26")); // Thanksgiving
			holidays.add(dateFormat.parse("2010:12:25")); // Christmas

			holidays.add(dateFormat.parse("2011:01:03")); // New Year's Day
			holidays.add(dateFormat.parse("2011:01:17")); // Martin Luther King Day
			holidays.add(dateFormat.parse("2011:02:21")); // President's Day
			holidays.add(dateFormat.parse("2011:05:30")); // Memorial Day
			holidays.add(dateFormat.parse("2011:07:04")); // Independence Day
			holidays.add(dateFormat.parse("2011:09:05")); // Labor Day
			holidays.add(dateFormat.parse("2011:11:24")); // Thanksgiving
			holidays.add(dateFormat.parse("2011:11:25")); // Thanksgiving
			holidays.add(dateFormat.parse("2011:12:26")); // Christmas

			holidays.add(dateFormat.parse("2012:01:02")); // New Year's Day
			holidays.add(dateFormat.parse("2012:01:16")); // Martin Luther King Day
			holidays.add(dateFormat.parse("2012:02:20")); // President's Day
			holidays.add(dateFormat.parse("2012:05:28")); // Memorial Day
			holidays.add(dateFormat.parse("2012:07:04")); // Independence Day
			holidays.add(dateFormat.parse("2012:09:03")); // Labor Day
			holidays.add(dateFormat.parse("2012:11:22")); // Thanksgiving
			holidays.add(dateFormat.parse("2012:11:23")); // Thanksgiving
			holidays.add(dateFormat.parse("2012:12:25")); // Christmas

			holidays.add(dateFormat.parse("2013:01:01")); // New Year's Day
			holidays.add(dateFormat.parse("2013:01:21")); // Martin Luther King Day
			holidays.add(dateFormat.parse("2013:02:18")); // President's Day
			holidays.add(dateFormat.parse("2013:05:27")); // Memorial Day
			holidays.add(dateFormat.parse("2013:07:04")); // Independence Day
			holidays.add(dateFormat.parse("2013:09:02")); // Labor Day
			holidays.add(dateFormat.parse("2013:11:28")); // Thanksgiving
			holidays.add(dateFormat.parse("2013:11:29")); // Thanksgiving
			holidays.add(dateFormat.parse("2013:12:25")); // Christmas
		}

		return holidays;
	}

	/**
	 * Returns a time at 9am on the day of the date parameter.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getBeginWorkDay(Date date) {
		return setDayTime(date, "09:00:00");
	}

	public static Date getDate(int year, int month, int day) {
		return DateTimeFormat.getFormat("yyyy-M-d").parse(year + "-" + month + "-" + day);
	}

	public static int getDay(Date day) {
		return Integer.parseInt(DateTimeFormat.getFormat("d").format(day));
	}

	public static int getDayInMonth(Date day) {
		return Integer.parseInt(DateTimeFormat.getFormat("d").format(day));
	}

	/**
	 * Returns the day of the week (1 - 7). 1 being Sunday.
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayInWeek(Date date) {
		DateTimeFormat format = DateTimeFormat.getFormat("EEEE");
		String day = format.format(date);

		for (int i = 0; i < 7; i++) {
			if (day.equals(Constants.DAYS_OF_WEEK[i])) {
				return i + 1;
			}
		}

		return -1;
	}

	// Returns day of the year, 1 being the first day.
	public static int getDayInYear(Date date) {
		int[] dom = Constants.DAYS_OF_MONTHS;
		int monthOfYear = getMonth(date);
		int dayOfYear = getDayInMonth(date);

		for (int i = 0; i < monthOfYear - 1; i++) {
			dayOfYear += dom[i];
		}

		return dayOfYear;
	}

	/**
	 * Returns a time at 5pm on the day of the date parameter.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getEndWorkDay(Date date) {
		return setDayTime(date, "17:00:00");
	}

	/**
	 * Returns the first day of the month, from the month of the specified date parameter.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		String month = Formatter.formatDate(date, "M");
		String year = Formatter.formatDate(date, "yyyy");
		DateTimeFormat format = DateTimeFormat.getFormat("d:M:yyyy");

		return format.parse("1:" + month + ":" + year);
	}

	public static Date getFirstDayOfWeek(Date date) {
		while (!Formatter.formatDate(date, "E").equals("Sun")) {
			date = addDays(date, -1);
		}
		return date;
	}

	/**
	 * Returns the first day of the year, from the year of the specified date parameter.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfYear(Date date) {
		String year = Formatter.formatDate(date, "yyyy");
		DateTimeFormat format = DateTimeFormat.getFormat("d:M:yyyy");

		return format.parse("1:1:" + year);
	}

	public static List<Date> getHolidayDates() {
		return getHolidayDates(new Date());
	}

	public static List<Date> getHolidayDates(Date date) {
		return getHolidayDates(getYear(date));
	}

	public static List<Date> getHolidayDates(int year) {
		List<Date> days = new ArrayList<Date>();
		for (Date d : getAllHolidayDates()) {
			if (getYear(d) == year) {
				days.add(d);
			}
		}

		return days;
	}

	public static int getHolidayHours(String year, String week) {
		List<Date> holidays = getHolidayDates(Integer.parseInt(year));
		int intWeek = Integer.parseInt(week);
		int ret = 0;

		for (int i = 0; i < holidays.size(); i++) {
			if (intWeek == getWeekInYear(holidays.get(i))) {
				ret += 8;
				if (getHolidayNames().get(i).equals("Christmas")) {
					// Extra 4 hours for Christmas eve
					ret += 4;
				}
			}
		}

		return ret;
	}

	public static List<String> getHolidayNames() {
		List<String> holidays = new ArrayList<String>();

		holidays.add("New year's day");
		holidays.add("Martin Luther King day");
		holidays.add("President's day");
		holidays.add("Memorial day");
		holidays.add("Independence day");
		holidays.add("Labor day");
		holidays.add("Thanksgiving");
		holidays.add("Thanksgiving");
		holidays.add("Christmas");

		return holidays;
	}

	/**
	 * Returns the last day of the month, from the month of the specified date parameter.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		// This is implemented by getting the first day of the next month and subtracting 1 day.

		DateTimeFormat format = DateTimeFormat.getFormat("d:M:yyyy");
		int month = Integer.parseInt(Formatter.formatDate(date, "M")) + 1;
		int year = Integer.parseInt(Formatter.formatDate(date, "yyyy"));
		if (month == 13) {
			year++;
			month = 1;
		}

		Date firstDayOfNextMonth = format.parse("1:" + month + ":" + year);
		return addDays(firstDayOfNextMonth, -1);
	}

	/**
	 * Returns a date at the last second of the last day of the month, from the month of the specified date parameter.
	 * 
	 * @param date
	 * @return
	 */
	public static Date getLastSecondOfMonth(Date date) {
		return setDayTime(getLastDayOfMonth(date), "23:59:59");
	}

	public static int getMonth(Date day) {
		return Integer.parseInt(DateTimeFormat.getFormat("M").format(day));
	}

	public static String getMonthNameAbbreviated(Date date) {
		return DateTimeFormat.getFormat("MMM").format(date);
	}

	public static String getMonthNameAbbreviated(int month) {
		return getMonthNameAbbreviated(dateFormat.parse("2000:" + month + ":01"));
	}

	public static int getWeekInYear(final Date date) {
		String year = DateTimeFormat.getFormat("yyyy").format(date);
		Date firstDayOfYear = DateTimeFormat.getFormat("y-M-d").parse(year + "-1-1");
		int offset = getDayInWeek(firstDayOfYear);

		return ((getDayInYear(date) + offset - 2) / 7) + 1;
	}

	public static int getYear(Date day) {
		return Integer.parseInt(DateTimeFormat.getFormat("y").format(day));
	}

	public static long hoursBetween(Date d1, Date d2) {
		return timeBetween(d1, d2, HOUR);
	}

	public static boolean isHoliday(Date date) {
		for (Date holiday : getHolidayDates()) {
			if (isSameDay(date, holiday)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isSameDay(Date d1, Date d2) {
		DateTimeFormat dayFormat = DateTimeFormat.getFormat("dd");

		return dayFormat.format(d1).equals(dayFormat.format(d2)) && isSameMonth(d1, d2);
	}

	public static boolean isSameHour(Date d1, Date d2) {
		DateTimeFormat hourFormat = DateTimeFormat.getFormat("HH");

		return hourFormat.format(d1).equals(hourFormat.format(d2)) && isSameDay(d1, d2);
	}

	public static boolean isSameMonth(Date d1, Date d2) {
		DateTimeFormat monthFormat = DateTimeFormat.getFormat("MM");

		return monthFormat.format(d1).equals(monthFormat.format(d2)) && isSameYear(d1, d2);
	}

	public static boolean isSameYear(Date d1, Date d2) {
		DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");

		return yearFormat.format(d1).equals(yearFormat.format(d2));
	}

	public static boolean isWeekend(Date date) {
		int dayOfWeek = getDayInWeek(date);
		return dayOfWeek == 1 || dayOfWeek == 7;
	}

	public static long minutesBetween(Date d1, Date d2) {
		return timeBetween(d1, d2, MINUTE);
	}

	public static Date setDayTime(Date date, String time) {
		String day = dateFormat.format(date);

		return dateTimeFormat.parse(day + " " + time);
	}

	public static Date setYear(Date date, int year) {
		return dateTimeFormat.parse(year + dateTimeFormat.format(date).substring(4));
	}

	/**
	 * This function will return a list of days t
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static List<Pair<Date, Date>> splitByDays(Date start, Date end) {
		List<Pair<Date, Date>> ranges = new ArrayList<Pair<Date, Date>>();
		if (isSameDay(start, end)) {
			ranges.add(new Pair<Date, Date>(start, end));
			return ranges;
		}

		long daysBetween = daysBetween(start, end);

		// Compare the times without the dates
		Date startTime = dateTimeFormat.parse("2000:01:01 " + timeFormat.format(start));
		Date endTime = dateTimeFormat.parse("2000:01:01 " + timeFormat.format(end));
		if (startTime.getTime() > endTime.getTime()) {
			daysBetween++;
		}

		for (long i = 0; i <= daysBetween; i++) {
			ranges.add(new Pair<Date, Date>());
		}

		ranges.get(0).setA(start);
		ranges.get(ranges.size() - 1).setB(end);

		for (long i = 0; i < daysBetween; i++) {
			Date tempStart = setDayTime(addDays(start, i + 1), "00:00:00");
			Date tempEnd = setDayTime(addDays(start, i + 1), "00:00:00");

			ranges.get((int) i).setB(tempEnd);
			ranges.get((int) i + 1).setA(tempStart);
		}

		return ranges;
	}

	public static Date subDays(Date date, long amount) {
		return changeTime(date, amount * -1, DAY);
	}

	public static Date subHours(Date date, long amount) {
		return changeTime(date, amount * -1, HOUR);
	}

	public static long timeBetween(Date d1, Date d2, long timeFactor) {
		long dif = d1.getTime() - d2.getTime();

		if (dif < 0) {
			dif *= -1;
		}

		return dif / timeFactor;
	}

	private static Date createDate(int year, int month, int day) {
		return dateFormat.parse(year + ":" + month + ":" + day);
	}

}
