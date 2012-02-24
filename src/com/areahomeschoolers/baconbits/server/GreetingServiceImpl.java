package com.areahomeschoolers.baconbits.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.areahomeschoolers.baconbits.client.GreetingService;

import com.google.appengine.api.rdbms.AppEngineDriver;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	@Override
	public String greetServer(String input) throws IllegalArgumentException {
		try {
			return runIt();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	private String runIt() throws SQLException {
		DriverManager.registerDriver(new AppEngineDriver());

		Connection c = DriverManager.getConnection("jdbc:google:rdbms://baconbits-sql:areahomeschoolers/baconbits");
		ResultSet rs = c.createStatement().executeQuery("select * from articles");
		rs.next();
		return rs.getString("title") + ": " + rs.getString("article");
	}
}
