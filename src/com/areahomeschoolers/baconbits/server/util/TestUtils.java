package com.areahomeschoolers.baconbits.server.util;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.areahomeschoolers.baconbits.shared.Common;

public abstract class TestUtils {

	private static PrintStream printStream = System.out;

	public static void printSql(String sql, List<Object> sqlArgs) {
		printStream.println(getSql(sql, sqlArgs));
	}

	public static void printSql(String sql, Object... sqlArgs) {
		printSql(sql, Arrays.asList(sqlArgs));
	}

	public static void printSql(String sql, SqlParameterSource source) {
		if (source instanceof MapSqlParameterSource) {
			printStream.println(getSql(sql, ((MapSqlParameterSource) source)));
			return;
		} else if (source instanceof BeanPropertySqlParameterSource) {
			printStream.println(getSql(sql, ((BeanPropertySqlParameterSource) source)));
			return;
		}

		printStream.println("Error.  SqlParameterSource type '" + source.getClass() + "' not supported.");
	}

	private static List<String> getParamParts(String str) {
		List<String> ret = new ArrayList<String>();
		String regex = ":[A-Za-z0-9]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);

		while (matcher.find()) {
			ret.add(matcher.group().substring(1));
		}

		return ret;
	}

	private static String getSql(String sql, BeanPropertySqlParameterSource source) {
		String lastPropName = "";
		List<Object> sqlArgs = new ArrayList<Object>();

		try {
			for (String propName : getParamParts(sql)) {
				sql = sql.replaceFirst(":" + propName, "?");

				lastPropName = propName;
				sqlArgs.add(source.getValue(propName));
			}
		} catch (org.springframework.beans.InvalidPropertyException e) {
			return "Error.  Property " + lastPropName + " cannot be invoked on the server side.";
		} catch (java.lang.IllegalArgumentException e) {
			return "Error.  Property " + lastPropName + " cannot be invoked on the server side.";
		}

		return getSql(sql, sqlArgs);
	}

	private static String getSql(String sql, List<Object> sqlArgs) {
		String newSql = sql;
		int argCount = occuranceCount(newSql, "?");
		if (argCount != sqlArgs.size()) {
			return "Invalid arg count.  SQL expected " + argCount + ", received " + sqlArgs.size() + ".";
		}

		for (Object arg : sqlArgs) {
			newSql = newSql.replaceFirst("\\?", getStringValue(arg));
		}

		return newSql;
	}

	private static String getSql(String sql, MapSqlParameterSource source) {
		for (Map.Entry<String, Object> entry : source.getValues().entrySet()) {
			sql = sql.replaceAll(":" + entry.getKey(), getStringValue(entry.getValue()));
		}

		return sql;
	}

	private static String getStringValue(Object o) {
		if (o == null) {
			return "null";
		}

		if (o instanceof Date) {
			return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(o) + "'";
		} else if (o instanceof Boolean) {
			return (Boolean) o ? "1" : "0";
		} else if (o instanceof List<?>) {
			return Common.join((List<?>) o, ", ");
		} else {
			String str = o.toString();
			if (!Common.isNumeric(str)) {
				str = "'" + str + "'";
			}
			return str;
		}
	}

	private static int occuranceCount(String searchOn, String search) {
		int count = -1;
		int afterIndex = -1;

		while (afterIndex != -1 || count == -1) {
			count++;
			afterIndex = searchOn.indexOf(search, afterIndex + 1);
		}

		return count;
	}
}
