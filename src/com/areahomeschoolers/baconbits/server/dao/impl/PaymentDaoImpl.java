package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.PaymentDao;
import com.areahomeschoolers.baconbits.server.paypal.PayPalCredentials;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Payment;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;
import com.paypal.adaptive.api.requests.fnapi.SimplePay;
import com.paypal.adaptive.api.responses.PayResponse;
import com.paypal.adaptive.core.AckCode;
import com.paypal.adaptive.core.CurrencyCodes;
import com.paypal.adaptive.core.PayError;
import com.paypal.adaptive.core.PaymentType;
import com.paypal.adaptive.core.Receiver;
import com.paypal.adaptive.core.ServiceEnvironment;
import com.paypal.adaptive.exceptions.AuthorizationRequiredException;
import com.paypal.adaptive.exceptions.InvalidAPICredentialsException;
import com.paypal.adaptive.exceptions.InvalidResponseDataException;
import com.paypal.adaptive.exceptions.MissingAPICredentialsException;
import com.paypal.adaptive.exceptions.MissingParameterException;
import com.paypal.adaptive.exceptions.PayPalErrorException;
import com.paypal.adaptive.exceptions.PaymentExecException;
import com.paypal.adaptive.exceptions.PaymentInCompleteException;
import com.paypal.adaptive.exceptions.RequestAlreadyMadeException;
import com.paypal.adaptive.exceptions.RequestFailureException;

@Repository
public class PaymentDaoImpl extends SpringWrapper implements PaymentDao {

	private final class AdjustmentMapper implements RowMapper<Adjustment> {
		@Override
		public Adjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Adjustment a = new Adjustment();
			a.setId(rs.getInt("id"));
			a.setAddedDate(rs.getTimestamp("addedDate"));
			a.setAdjustmentTypeId(rs.getInt("adjustmentTypeId"));
			a.setAmount(rs.getDouble("amount"));
			a.setAdjustmentType(rs.getString("adjustmentType"));
			a.setLinkId(rs.getInt("linkId"));
			a.setStatus(rs.getString("status"));
			a.setStatusId(rs.getInt("statusId"));
			a.setUserFullName(rs.getString("userFullName"));
			a.setUserId(rs.getInt("userId"));

			return a;
		}
	}

	private final class PaymentMapper implements RowMapper<Payment> {
		@Override
		public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Payment payment = new Payment();
			payment.setId(rs.getInt("id"));
			payment.setAmount(rs.getDouble("amount"));
			payment.setIpnDate(rs.getTimestamp("ipnDate"));
			payment.setPayKey(rs.getString("payKey"));
			payment.setPaymentDate(rs.getTimestamp("paymentDate"));
			payment.setPaymentFee(rs.getDouble("paymentFee"));
			payment.setRawData(rs.getString("rawData"));
			payment.setStatusId(rs.getInt("rawData"));
			payment.setTransactionId(rs.getString("transactionId"));
			payment.setUserId(rs.getInt("userId"));
			payment.setUserFullName(rs.getString("userFullName"));
			payment.setStatus(rs.getString("status"));
			payment.setPaymentTypeId(rs.getInt("paymentTypeId"));
			payment.setPaymentType(rs.getString("paymentType"));
			return payment;
		}
	}

	private PayPalCredentials paypal;

	@Autowired
	public PaymentDaoImpl(DataSource dataSource, PayPalCredentials pp) {
		super(dataSource);
		this.paypal = pp;
	}

	public String createSqlBase() {
		String sql = "select p.*, concat(u.firstName, ' ', u.lastName) as userFullName, ps.status, pt.paymentType \n";
		sql += "from payments p \n";
		sql += "join users u on u.id = p.userId \n";
		sql += "join paymentStatus ps on ps.id = p.statusId \n";
		sql += "join paymentTypes pt on pt.id = p.paymentTypeId \n";
		sql += "where 1 = 1 \n";

		return sql;
	}

	@Override
	public ArrayList<Adjustment> getAdjustments(ArgMap<PaymentArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int userId = args.getInt(PaymentArg.USER_ID);
		int statusId = args.getInt(PaymentArg.ADJUSTMENT_STATUS_ID);

		String sql = "select a.*, s.status, t.adjustmentType, \n";
		sql += "concat(u.firstName, ' ', u.lastName) as userFullName from adjustments a \n";
		sql += "join adjustmentStatus s on s.id = a.statusId \n";
		sql += "join adjustmentTypes t on t.id = a.adjustmentTypeId \n";
		sql += "join users u on u.id = a.userId \n";
		sql += "where 1 = 1 \n";
		if (userId > 0) {
			sql += "and a.userId = ? \n";
			sqlArgs.add(userId);
		}

		if (statusId > 0) {
			sql += "and a.statusId = ? \n";
			sqlArgs.add(statusId);
		}

		return query(sql, new AdjustmentMapper(), sqlArgs.toArray());
	}

	@Override
	public Payment getById(int paymentId) {
		String sql = createSqlBase() + "and p.id = ?";

		return queryForObject(sql, new PaymentMapper(), paymentId);
	}

	@Override
	public ArrayList<Payment> list(ArgMap<PaymentArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();

		String sql = createSqlBase();

		ArrayList<Payment> data = query(sql, new PaymentMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public Payment save(Payment payment) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(payment);
		PaypalData data = null;

		if (payment.isSaved()) {
			String sql = "update payments set statusId = :statusId, ipnDate = :ipnDate, paymentFee = :paymentFee, transactionId = :transactionId, rawData = :rawData ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			payment.setUserId(ServerContext.getCurrentUser().getId());

			String sql = "insert into payments (userId, paymentTypeId, paymentDate, amount, statusId) values ";
			sql += "(:userId, :paymentTypeId, now(), :amount, :statusId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			payment.setId(ServerUtils.getIdFromKeys(keys));

			// when adding a new payment, we need to create a transaction with PayPal
			data = makePayment(payment);
			if (data.getPayKey() != null) {
				// and then link it to our payment via a payKey
				sql = "update payments set payKey = ? where id = ?";
				update(sql, data.getPayKey(), payment.getId());
			}
		}

		Payment p = getById(payment.getId());
		// send the PayPal payment information back to the client, so we can redirect them to PayPal to make the payment
		p.setPaypalData(data);

		return p;
	}

	private PaypalData makePayment(Payment p) {
		PaypalData data = new PaypalData();

		ServiceEnvironment environment = ServerContext.isLive() ? ServiceEnvironment.PRODUCTION : ServiceEnvironment.SANDBOX;

		try {
			StringBuilder url = new StringBuilder();
			url.append(ServerContext.getBaseUrl() + "#page=" + p.getReturnPage());
			String returnURL = url.toString() + "&ps=return&payKey=${payKey}";
			String cancelURL = url.toString() + "&ps=cancel";
			String ipnURL = ServerContext.getBaseUrl() + "baconbits/service/ipn";

			SimplePay payment = new SimplePay();
			// always the same
			payment.setCredentialObj(paypal);
			payment.setUserIp(ServerContext.getRequest().getRemoteAddr());
			payment.setApplicationName("weare.home.educators");
			payment.setCurrencyCode(CurrencyCodes.USD);
			payment.setLanguage("en_US");
			payment.setEnv(environment);
			if (!ServerContext.isLive()) {
				payment.setSenderEmail("paul.a_1343673034_per@gmail.com"); // password: 343833982
			}

			payment.setCancelUrl(cancelURL);
			payment.setReturnUrl(returnURL);
			payment.setIpnURL(ipnURL);
			payment.setMemo(p.getMemo());

			Receiver receiver = new Receiver();
			receiver.setAmount(p.getAmount());
			if (ServerContext.isLive()) {
				receiver.setEmail("weare.home.educators@gmail.com");
			} else {
				receiver.setEmail("paul.a_1343673136_biz@gmail.com");
			}
			receiver.setPaymentType(PaymentType.SERVICE);
			payment.setReceiver(receiver);

			PayResponse payResponse = payment.makeRequest();
			data.setPayKey(payResponse.getPayKey());
			data.setPaymentExecStatus(payResponse.getPaymentExecStatus().toString());
			return data;
			// System.out.println("PaymentExecStatus:" + payResponse.getPaymentExecStatus().toString());
		} catch (IOException e) {
			System.out.println("Payment Failed w/ IOException");
		} catch (MissingAPICredentialsException e) {
			// No API Credential Object provided - log error
			// e.printStackTrace();
			throw new RuntimeException("No APICredential object provided");
		} catch (InvalidAPICredentialsException e) {
			// invalid API Credentials provided - application error - log error
			// e.printStackTrace();
			System.out.println("Invalid API Credentials " + e.getMissingCredentials());
		} catch (MissingParameterException e) {
			// missing parameter - log error
			// e.printStackTrace();
			throw new RuntimeException("Missing Parameter error: " + e.getParameterName());
		} catch (RequestFailureException e) {
			// HTTP Error - some connection issues ?
			// e.printStackTrace();
			throw new RuntimeException("Request HTTP Error: " + e.getHTTP_RESPONSE_CODE());
		} catch (InvalidResponseDataException e) {
			// PayPal service error
			// log error
			// e.printStackTrace();
			throw new RuntimeException("Invalid Response Data from PayPal: \"" + e.getResponseData() + "\"");
		} catch (PayPalErrorException e) {
			// Request failed due to a Service/Application error
			// e.printStackTrace();
			if (e.getResponseEnvelope().getAck() == AckCode.Failure) {
				// log the error
				String text = "Received Failure from PayPal (ack)\n";
				text += "ErrorData provided:";
				text += e.getPayErrorList().toString();
				for (PayError error : e.getPayErrorList()) {
					text += error.getError().getMessage();
				}
				if (e.getPaymentExecStatus() != null) {
					text += "PaymentExecStatus: " + e.getPaymentExecStatus();
				}
				throw new RuntimeException(text);
			} else if (e.getResponseEnvelope().getAck() == AckCode.FailureWithWarning) {
				// there is a warning - log it!
				String text = "Received Failure with Warning from PayPal (ack)";
				text += "ErrorData provided:";
				text += e.getPayErrorList().toString();
				throw new RuntimeException(text);
			}
		} catch (RequestAlreadyMadeException e) {
			// shouldn't occur - log the error
			// e.printStackTrace();
			throw new RuntimeException("Request to send a request that has already been sent!");
		} catch (PaymentExecException e) {
			String text = "Failed Payment Request w/ PaymentExecStatus: " + e.getPaymentExecStatus().toString();
			text += "ErrorData provided:";

			text += e.getPayErrorList().toString();

			throw new RuntimeException(text);
		} catch (PaymentInCompleteException e) {
			String text = "Incomplete Payment w/ PaymentExecStatus: " + e.getPaymentExecStatus().toString();
			text += "ErrorData provided:";

			text += e.getPayErrorList().toString();
			throw new RuntimeException(text);
		} catch (AuthorizationRequiredException e) {
			// redirect the user to PayPal for Authorization
			// resp.sendRedirect(e.getAuthorizationUrl(ServiceEnvironment.SANDBOX));

			try {
				data.setAuthorizationUrl(e.getAuthorizationUrl(environment));
				return data;
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
		}

		return data;
	}

}
