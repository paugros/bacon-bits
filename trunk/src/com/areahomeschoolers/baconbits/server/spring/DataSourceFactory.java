package com.areahomeschoolers.baconbits.server.spring;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.google.appengine.api.utils.SystemProperty;

public class DataSourceFactory {
	public DataSource createDataSource() {
		BasicDataSource ds = new BasicDataSource();

		String db = "production".equals(SystemProperty.version.get()) ? "areahomeschoolers" : "development";
		ds.setUrl("jdbc:google:rdbms://baconbits-sql:" + db + "/baconbits");
		ds.setValidationQuery("select max(id) from articles");
		ds.setTestWhileIdle(true);
		ds.setTestOnBorrow(true);
		ds.setInitialSize(1);
		ds.setMinIdle(1);
		ds.setMaxIdle(5);
		ds.setMaxActive(100);
		ds.setMaxWait(3 * 1000);
		ds.setLogAbandoned(true);
		ds.setRemoveAbandoned(true);
		ds.setRemoveAbandonedTimeout(60);

		return ds;
	}
}
