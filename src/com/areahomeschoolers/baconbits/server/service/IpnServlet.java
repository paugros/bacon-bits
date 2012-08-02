package com.areahomeschoolers.baconbits.server.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;

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
import com.areahomeschoolers.baconbits.shared.Common;

@Component
@RequestMapping("/ipn")
public class IpnServlet extends HttpServlet implements ServletContextAware, Controller {
	private static final long serialVersionUID = 1L;
	protected ServletContext servletContext;
	private final JdbcTemplate template;

	@Autowired
	public IpnServlet(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	// sample for testing in dev:
	// http://127.0.0.1:8888/baconbits/service/ipn?mc_gross=19.95&protection_eligibility=Eligible&address_status=confirmed&payer_id=LPLWNMTBWMFAY&tax=0.00&address_street=1+Main+St&payment_date=20%3A12%3A59+Jan+13%2C+2009+PST&payment_status=Completed&charset=windows-1252&address_zip=95131&first_name=Test&mc_fee=0.88&address_country_code=US&address_name=Test+User&notify_version=2.6&custom=&payer_status=verified&address_country=United+States&address_city=San+Jose&quantity=1&verify_sign=AtkOfCXbDm2hu0ZELryHFjY-Vb7PAUvS6nMXgysbElEn9v-1XcmSoGtf&payer_email=gpmac_1231902590_per%40paypal.com&txn_id=61E67681CH3238416&payment_type=instant&last_name=User&address_state=CA&receiver_email=gpmac_1231902686_biz%40paypal.com&payment_fee=0.88&receiver_id=S8XGHLYDW9T3S&txn_type=express_checkout&item_name=&mc_currency=USD&item_number=&residence_country=US&test_ipn=1&handling_amount=0.00&transaction_subject=&payment_gross=19.95&shipping=0.00&pay_key=AP-11S910134P318015J

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ServerContext.loadContext(request, response, servletContext);

		try {
			if (!ServerContext.isLive()) {
				processPayment(request);
				return null;
			}

			StringBuffer params = new StringBuffer();
			String encoding = "UTF-8";
			params.append("cmd=_notify-validate");
			for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
				String name = e.nextElement();
				String value = request.getParameter(name);
				params.append("&" + name + "=" + URLEncoder.encode(value, encoding));
			}

			String rc = getResponse("https://www.paypal.com/cgi-bin/webscr", params.toString()).trim();
			if ("VERIFIED".equals(rc)) {
				processPayment(request);
			} else if ("INVALID".equals(rc)) {
				throw new RuntimeException("Ukulele no good - Invalid IPN transaction! Pay key: " + request.getParameter("pay_key"));
			} else {
				throw new RuntimeException("Ukulele no good - IPN transaction totally failed - Pay key: " + request.getParameter("pay_key"));
			}
		} finally {
			ServerContext.unloadContext();
		}

		return null;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private String getResponse(String urlString, String params) throws MalformedURLException {
		URL url = new URL(urlString);
		URLConnection connection;
		StringBuffer response = new StringBuffer();

		try {
			connection = url.openConnection();
			connection.setDoOutput(true);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(params);
			writer.flush();
			writer.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return response.toString();
	}

	private void processPayment(HttpServletRequest request) {
		String status = request.getParameter("payment_status");
		String key = request.getParameter("pay_key");
		String fee = request.getParameter("payment_fee");
		String txnId = request.getParameter("txn_id");

		if (key != null) {
			String statusText = "statusId";
			int paymentStatusId = 0;
			try {
				paymentStatusId = template.queryForInt("select id from paymentStatus where status = ?", status);
				statusText = Integer.toString(paymentStatusId);
			} catch (Exception e) {
			}
			double paymentFee = 0;
			if (Common.isDouble(fee)) {
				paymentFee = Double.parseDouble(fee);
			}

			String sql = "update payments set paymentFee = ?, transactionId = ?, ipnDate = now(), statusId = " + statusText + " where payKey = ?";
			template.update(sql, paymentFee, txnId, key);

			int participantStatusId = 0;

			if (paymentStatusId > 0) {
				if (paymentStatusId == 2 || paymentStatusId == 11) {
					participantStatusId = 2;
				}

				if (participantStatusId > 0) {
					sql = "update eventRegistrationParticipants set statusId = 2 where paymentId = (select id from payments where payKey = ? limit 1)";
					template.update(sql, key);
				}
			}
		}
	}

}
