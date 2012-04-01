package com.areahomeschoolers.baconbits.server.spring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DataSourceFactory {
	public DataSource createDataSource() {
		BasicDataSource ds = new BasicDataSource();
		try {
			Connection c = DriverManager.getConnection("jdbc:google:rdbms://baconbits-sql:areahomeschoolers/baconbits");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ds;
	}
}
