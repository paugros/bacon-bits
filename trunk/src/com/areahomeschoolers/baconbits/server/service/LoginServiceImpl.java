package com.areahomeschoolers.baconbits.server.service;

import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	private static final long serialVersionUID = 1L;

	@Override
	public ApplicationData getApplicationData() {
		return new ApplicationData();
	}

	@Override
	public boolean login(String username, String password) {
		return true;
	}

	@Override
	public ApplicationData loginAndGetApplicationData(String username, String password) {
		return new ApplicationData();
	}

	@Override
	public void logout() {

	}

	// private String runIt() throws SQLException {
	// DriverManager.registerDriver(new AppEngineDriver());
	//
	// Connection c = DriverManager.getConnection("jdbc:google:rdbms://baconbits-sql:areahomeschoolers/baconbits");
	// ResultSet rs = c.createStatement().executeQuery("select * from articles limit 1");
	// rs.next();
	// return rs.getString("title") + ": " + rs.getString("article");
	// }
}
