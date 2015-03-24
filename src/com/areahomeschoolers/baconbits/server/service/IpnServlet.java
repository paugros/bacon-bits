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
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;

@Component
@RequestMapping("/ipn")
public class IpnServlet extends HttpServlet implements ServletContextAware, Controller {
	private static final long serialVersionUID = 1L;
	protected ServletContext servletContext;
	private final JdbcTemplate template;
	private final Logger logger = Logger.getLogger(this.getClass().toString());

	@Autowired
	public IpnServlet(DataSource dataSource) {
		template = new JdbcTemplate(dataSource);
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	// sample for testing in dev. all you should need to change is the pay_key at the end:
	// http://127.0.0.1:8888/baconbits/service/ipn?mc_gross=19.95&protection_eligibility=Eligible&address_status=confirmed&payer_id=LPLWNMTBWMFAY&tax=0.00&address_street=1+Main+St&payment_date=20%3A12%3A59+Jan+13%2C+2009+PST&transaction[0].status=Completed&charset=windows-1252&address_zip=95131&first_name=Test&mc_fee=0.88&address_country_code=US&address_name=Test+User&notify_version=2.6&custom=&payer_status=verified&address_country=United+States&address_city=San+Jose&quantity=1&verify_sign=AtkOfCXbDm2hu0ZELryHFjY-Vb7PAUvS6nMXgysbElEn9v-1XcmSoGtf&payer_email=gpmac_1231902590_per%40paypal.com&transaction[0].id=9YD72353HT136740A&payment_type=instant&last_name=User&address_state=CA&receiver_email=gpmac_1231902686_biz%40paypal.com&receiver_id=S8XGHLYDW9T3S&txn_type=express_checkout&item_name=&mc_currency=USD&item_number=&residence_country=US&test_ipn=1&handling_amount=0.00&transaction_subject=&payment_gross=19.95&shipping=0.00&pay_key=AP-4NB21378MC347284B

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ServerContext.loadContext(request, response, servletContext);

		try {
			StringBuffer params = new StringBuffer();
			String encoding = "UTF-8";
			params.append("cmd=_notify-validate");
			for (Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
				String name = e.nextElement();
				String value = request.getParameter(name);
				params.append("&" + name + "=" + URLEncoder.encode(value, encoding));
			}

			if (!ServerContext.isProduction()) {
				processPayment(request, params.toString());
				return null;
			}

			String rc = getResponse("https://www.paypal.com/cgi-bin/webscr", params.toString()).trim();
			if ("VERIFIED".equals(rc)) {
				processPayment(request, params.toString());
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

	private void processPayment(HttpServletRequest request, String rawData) {
		String status = request.getParameter("transaction[0].status");
		String key = request.getParameter("pay_key");
		String fee = request.getParameter("payment_fee");
		String txnId = request.getParameter("transaction[0].id");

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

			String sql = "update payments set paymentFee = ?, transactionId = ?, ipnDate = now(), ";
			sql += "rawData = ?, statusId = " + statusText + " where payKey = ?";
			template.update(sql, paymentFee, txnId, rawData, key);

			int paymentTypeId = 0;

			try {
				paymentTypeId = template.queryForInt("select paymentTypeId from payments where payKey = ? limit 1", key);
			} catch (EmptyResultDataAccessException e) {
				logger.warning("Payment not found for key: " + key);
				return;
			}
			int paymentId = template.queryForInt("select id from payments where payKey = ? limit 1", key);

			// set adjustments as being applied
			sql = "update adjustments set statusId = 2 where id in(select adjustmentId from paymentAdjustmentMapping where paymentId = ?)";
			template.update(sql, paymentId);

			switch (paymentTypeId) {

			case 1:
				int participantStatusId = 0;

				if (paymentStatusId > 0) {
					if (paymentStatusId == 2 || paymentStatusId == 11) {
						participantStatusId = 2;
					}

					if (participantStatusId > 0) {
						sql = "update eventRegistrationParticipants set statusId = 2 where paymentId = ?";
						template.update(sql, paymentId);
					}
				}
				break;
			case 2:
				int userId = template.queryForInt("select userId from payments where payKey = ? limit 1", key);

				Integer groupOption = (Integer) ServerContext.getCache().get(Constants.BOOK_GROUP_OPTION_CACHE_KEY + userId);
				if (groupOption == null) {
					groupOption = 1;
				}

				if (groupOption == 1 || groupOption == 2) {
					sql = "select count(id) from userGroupMembers where userId = ? and groupId = " + Constants.ONLINE_BOOK_SELLERS_GROUP_ID;
					int count = template.queryForInt(sql, userId);

					if (count > 0) {
						break;
					}

					sql = "insert into userGroupMembers (userId, groupId, isAdministrator, userApproved, groupApproved) ";
					sql += "values(?, " + Constants.ONLINE_BOOK_SELLERS_GROUP_ID + ", 0, 1, 1)";
					template.update(sql, userId);
				}

				if (groupOption == 1 || groupOption == 3) {
					sql = "select count(id) from userGroupMembers where userId = ? and groupId = " + Constants.PHYSICAL_BOOK_SELLERS_GROUP_ID;
					int count = template.queryForInt(sql, userId);

					if (count > 0) {
						break;
					}

					sql = "insert into userGroupMembers (userId, groupId, isAdministrator, userApproved, groupApproved) ";
					sql += "values(?, " + Constants.PHYSICAL_BOOK_SELLERS_GROUP_ID + ", 0, 1, 1)";
					template.update(sql, userId);
				}

				ServerContext.getCache().delete("user_" + userId);
				ServerContext.getCache().delete(Constants.BOOK_GROUP_OPTION_CACHE_KEY + userId);

				break;
			default:
				break;
			}
		}
	}
}
