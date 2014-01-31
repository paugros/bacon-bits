package com.areahomeschoolers.baconbits.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;

/**
 * A repository of useful static methods, constants, etc. for use only on the server side
 */
public abstract class ServerUtils {

	public static final String SQL_TOP_CLAUSE = "top " + (Constants.MAX_DATA_ROWS + 1);

	private final static RowMapper<Data> genericRowMapper = new RowMapper<Data>() {
		@Override
		public Data mapRow(ResultSet rs, int rowNum) throws SQLException {
			Data row = new Data();
			ResultSetMetaData meta = rs.getMetaData();
			for (int columnIndex = 1; columnIndex <= meta.getColumnCount(); columnIndex++) {
				int columnType = meta.getColumnType(columnIndex);
				if (columnType == Types.TIMESTAMP || columnType == Types.DATE || columnType == Types.TIME) {
					row.put(meta.getColumnName(columnIndex), rs.getTimestamp(columnIndex));
				} else {
					String value = (rs.getObject(columnIndex) == null) ? null : rs.getObject(columnIndex).toString();
					row.put(meta.getColumnName(columnIndex), value);
				}
			}

			return row;
		}
	};

	private final static RowMapper<String> stringRowMapper = new RowMapper<String>() {
		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString(1);
		}
	};

	private final static RowMapper<Integer> intRowMapper = new RowMapper<Integer>() {
		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt(1);
		}
	};

	private final static RowMapper<ServerSuggestion> suggestionMapper = new RowMapper<ServerSuggestion>() {
		@Override
		public ServerSuggestion mapRow(final ResultSet rs, int rowNum) throws SQLException {
			ServerSuggestion suggestion = new ServerSuggestion(rs.getString("Suggestion"), rs.getInt("ID"));
			suggestion.setEntityType(rs.getString("EntityType"));
			return suggestion;
		}
	};

	public static Document createDocumentFromUrl(URL url) {
		InputStream stream = null;
		try {
			stream = url.openStream();
		} catch (IOException e) {
		}
		try {
			Document document = null;
			DocumentBuilderFactory domFactory;
			DocumentBuilder builder;

			try {
				domFactory = DocumentBuilderFactory.newInstance();
				domFactory.setValidating(false);
				domFactory.setNamespaceAware(false);
				builder = domFactory.newDocumentBuilder();

				document = builder.parse(stream);
			} catch (Exception e) {
			}
			return document;
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

	public static MapSqlParameterSource createParamterMap(Data map) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		for (String key : map.getData().keySet()) {
			params.addValue(key, map.get(key));
		}

		return params;
	}

	public static String formatCurrency(double currency) {
		DecimalFormat df = new DecimalFormat("$#,##0.00;($#,##0.00)");
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);

		return df.format(currency);
	}

	public static String formatDate(Date date) {
		return new SimpleDateFormat("M/d/yy").format(date);
	}

	public static String formatDateForSql(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(date);
	}

	public static String formatDateTime(Date date) {
		return new SimpleDateFormat("M/d/yy h:mm a").format(date);
	}

	public static Boolean getBooleanFromRowSet(ResultSet rs, String name) throws SQLException {
		String strVal = rs.getString(name);

		Boolean boolVal = null;
		if ("0".equals(strVal)) {
			boolVal = false;
		} else if ("1".equals(strVal)) {
			boolVal = true;
		}

		return boolVal;
	}

	public static String getDistanceSql(String tableAlias, int withinMiles, String withinLat, String withinLng) {
		String sql = "(3959 * acos( cos( radians(" + withinLat + ") ) * ";
		sql += "cos( radians( " + tableAlias + ".lat ) ) * cos( radians( " + tableAlias + ".lng ) - radians(" + withinLng + ") ) ";
		sql += "+ sin( radians(" + withinLat + ") ) * sin( radians( " + tableAlias + ".lat ) ) ) ) as distance, ";

		return sql;
	}

	public static String getFirstNodeContentByName(Document doc, String name) {
		NodeList nodes = doc.getElementsByTagName(name);
		if (nodes.getLength() == 0) {
			return "";
		}

		return nodes.item(0).getTextContent();
	}

	public static RowMapper<Data> getGenericRowMapper() {
		return genericRowMapper;
	}

	public final static int getIdFromKeys(KeyHolder keys) {
		return Integer.parseInt(keys.getKeys().get(Constants.GENERATED_KEY_TOKEN).toString());
	}

	public static RowMapper<Integer> getIntRowMapper() {
		return intRowMapper;
	}

	public final static String getLocalNetworkIp() {
		try {
			Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
			if (nifs == null) {
				return null;
			}
			while (nifs.hasMoreElements()) {
				NetworkInterface nif = nifs.nextElement();

				if (!nif.isLoopback() && nif.isUp() && !nif.isVirtual()) {
					Enumeration<InetAddress> adrs = nif.getInetAddresses();
					while (adrs.hasMoreElements()) {
						InetAddress adr = adrs.nextElement();
						if (adr != null && !adr.isLoopbackAddress() && (nif.isPointToPoint() || !adr.isLinkLocalAddress())) {
							return adr.getHostAddress();
						}
					}
				}
			}

			return null;
		} catch (SocketException ex) {
			return null;
		}
	}

	public static Date getNthXdayInMonth(final Date input, final int weeks, final int targetWeekDay) {
		// strip all date fields below month
		final Date startOfMonth = DateUtils.truncate(input, Calendar.MONTH);
		final Calendar cal = Calendar.getInstance();
		cal.setTime(startOfMonth);
		final int weekDay = cal.get(Calendar.DAY_OF_WEEK);
		int modifier = (weeks - 1) * 7 + (targetWeekDay - weekDay);
		if (modifier > 0 && modifier < 7) {
			modifier += 7;
		}
		return modifier > 0 ? DateUtils.addDays(startOfMonth, modifier) : startOfMonth;
	}

	/**
	 * Creates a like clause that escapes wildcard characters. The wildcard characters are % and _
	 * 
	 * @param searchText
	 * @return
	 */
	public final static String getSafeLikeSearchClause(String searchText, List<Object> sqlArgs) {
		String clause = "like ?";

		if (searchText.contains("%") || searchText.contains("_")) {
			clause += " escape '!'";
			searchText = searchText.replaceAll("(!|_|%)", "!$1");
		}

		sqlArgs.add("%" + searchText + "%");

		return clause;
	}

	public static RowMapper<String> getStringRowMapper() {
		return stringRowMapper;
	}

	public static RowMapper<ServerSuggestion> getSuggestionMapper() {
		return suggestionMapper;
	}

	private ServerUtils() {

	}
}
