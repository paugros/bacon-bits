package com.areahomeschoolers.baconbits.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.server.spring.GWTController;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.shared.dto.ApplicationData;
import com.areahomeschoolers.baconbits.shared.dto.User;

@Controller
@RequestMapping("/login")
public class LoginServiceImpl extends GWTController implements LoginService {
	private static final long serialVersionUID = 1L;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public LoginServiceImpl(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	public ApplicationData getApplicationData() {
		// if (ServerContext.getSession().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY) == null) {
		// return null;
		// }

		User user = ServerContext.getCurrentUser();

		// if (user == null) {
		// return null;
		// }

		ApplicationData ap = new ApplicationData();
		ap.setCurrentUser(user);

		return ap;
	}

	@Override
	public boolean login(String username, String password) {
		boolean success = true;
		// String remoteIp = ServerContext.getRequest().getRemoteAddr();
		// ApplicationContext ctx = ServerContext.getApplicationContext();

		try {
			Authentication request = new UsernamePasswordAuthenticationToken(username, password);
			Authentication result = authenticationManager.authenticate(request);
			SecurityContextHolder.getContext().setAuthentication(result);
			ServerContext.getSession().setMaxInactiveInterval((60 * 60) * 4);
			ServerContext.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

			ServerContext.setCurrentUser(username);
			User user = ServerContext.getCurrentUser();
			// List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(result.getAuthorities());

			if (!user.isActive()) {
				success = false;
			}

			if (!success) {
				logout();
			}
		} catch (Exception e) {
			if (!(e instanceof BadCredentialsException)) {
				e.printStackTrace();
			}

			e.printStackTrace();
			success = false;
		}

		// SystemDao systemDao = (SystemDao) ctx.getBean("systemDaoImpl");
		// systemDao.recordLoginAttempt(username, remoteIp, success);

		return success;
	}

	@Override
	public ApplicationData loginAndGetApplicationData(String username, String password) {
		if (login(username, password)) {
			return getApplicationData();
		}
		return null;
	}

	@Override
	public void logout() {
		ServerContext.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, null);
		SecurityContextHolder.clearContext();
	}
}
