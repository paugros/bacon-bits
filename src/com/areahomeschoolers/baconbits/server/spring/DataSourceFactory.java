package com.areahomeschoolers.baconbits.server.spring;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DataSourceFactory {
	public DataSource createDataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl("jdbc:google:rdbms://baconbits-sql:areahomeschoolers/baconbits");
		ds.setTestOnBorrow(true);

		return ds;
	}
}
