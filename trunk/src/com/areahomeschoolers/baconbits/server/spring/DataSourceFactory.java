package com.areahomeschoolers.baconbits.server.spring;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DataSourceFactory {
	public DataSource createDataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setUrl("jdbc:google:rdbms://baconbits-sql:areahomeschoolers/baconbits");
		ds.setValidationQuery("select max(id) from articles");
		ds.setTestWhileIdle(true);
		ds.setTestOnBorrow(true);
		ds.setInitialSize(1);

		return ds;
	}
}
