package com.areahomeschoolers.baconbits.server.service;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.server.spring.GWTController;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;

@Controller
@RequestMapping("/login")
public class LoginServiceImpl extends GWTController implements LoginService {

	private static final long serialVersionUID = 1L;

	public LoginServiceImpl() {

	}

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
	// Connection c = DriverManager.getConnection("jdbc:google:rdbms://baconbits-sql:areahomeschoolers/baconbits");
	// ResultSet rs = c.createStatement().executeQuery("select * from articles limit 1");
	// rs.next();
	// return rs.getString("title") + ": " + rs.getString("article");
	// }
}
