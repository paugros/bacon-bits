package com.areahomeschoolers.baconbits.server.service;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.areahomeschoolers.baconbits.server.util.ServerContext;

@Component
@RequestMapping("/cron/cleanup")
public class CleanupServlet extends HttpServlet implements ServletContextAware, Controller {
	private static final long serialVersionUID = 1L;
	protected ServletContext servletContext;
	private final JdbcTemplate template;

	@Autowired
	public CleanupServlet(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ServerContext.loadContext(request, response, servletContext);

		try {
			doCleanup();
		} finally {
			ServerContext.unloadContext();
		}

		return null;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private void doCleanup() {
		// auto-delete pending payments after 24 hours
		String sql = "update eventRegistrationParticipants set paymentId = null where paymentId in(select id from payments where statusId = 1 and paymentDate < date_add(now(), interval -24 hour))";
		template.update(sql);

		sql = "delete from payments where statusId = 1 and paymentDate < date_add(now(), interval -24 hour)";
		template.update(sql);

		// auto-cancel pending registrations older than 24 hours
		// sql = "update eventRegistrationParticipants  set statusId = 5 where statusId = 1 and addedDate < date_add(now(), interval -24 hour)";
		// TODO need to: 1) update the wait list 2) email the user
		// template.update(sql);
	}

}
