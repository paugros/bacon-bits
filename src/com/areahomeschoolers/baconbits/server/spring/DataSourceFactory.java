package com.areahomeschoolers.baconbits.server.spring;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DataSourceFactory {
	public DataSource createDataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl("jdbc:google:rdbms://baconbits-sql:areahomeschoolers/baconbits");
		ds.setValidationQuery("select max(id) from articles");
		ds.setTestWhileIdle(true);
		ds.setTimeBetweenEvictionRunsMillis(1000 * 60);
		ds.setTestOnBorrow(true);
		ds.setInitialSize(1);
		ds.setMinIdle(1);

		return ds;
	}
}
