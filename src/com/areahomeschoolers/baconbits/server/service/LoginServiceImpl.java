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
}
