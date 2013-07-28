package com.areahomeschoolers.baconbits.server.dao.impl;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.PaymentDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Adjustment;
import com.areahomeschoolers.baconbits.shared.dto.Arg.PaymentArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Payment;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;
import com.paypal.svcs.services.AdaptivePaymentsService;
import com.paypal.svcs.types.ap.PayRequest;
import com.paypal.svcs.types.ap.PayResponse;
import com.paypal.svcs.types.ap.Receiver;
import com.paypal.svcs.types.ap.ReceiverList;
import com.paypal.svcs.types.common.ClientDetailsType;
import com.paypal.svcs.types.common.RequestEnvelope;

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
			a.setDescription(rs.getString("description"));
			a.setAddedById(rs.getInt("addedById"));

			return a;
		}
	}

	private final class PaymentMapper implements RowMapper<Payment> {
		@Override
		public Payment mapRow(ResultSet rs, int rowNum) throws SQLException {
			Payment payment = new Payment();
			payment.setId(rs.getInt("id"));
			payment.setPrincipalAmount(rs.getDouble("amount"));
			payment.setMarkupAmount(rs.getDouble("markupAmount"));
			payment.setIpnDate(rs.getTimestamp("ipnDate"));
			payment.setPayKey(rs.getString("payKey"));
			payment.setPaymentDate(rs.getTimestamp("paymentDate"));
			payment.setPaymentFee(rs.getDouble("paymentFee"));
			payment.setRawData(rs.getString("rawData"));
			payment.setStatusId(rs.getInt("statusId"));
			payment.setTransactionId(rs.getString("transactionId"));
			payment.setUserId(rs.getInt("userId"));
			payment.setUserFullName(rs.getString("userFullName"));
			payment.setStatus(rs.getString("status"));
			payment.setPaymentTypeId(rs.getInt("paymentTypeId"));
			payment.setPaymentType(rs.getString("paymentType"));
			return payment;
		}
	}

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	private Properties payPalProperties;

	@Autowired
	public PaymentDaoImpl(DataSource dataSource) {
		super(dataSource);
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
	@PreAuthorize("hasRole('GROUP_ADMINISTRATORS')")
	public void deleteAdjustment(int adjustmentId) {
		String sql = "delete from adjustments where id = ?";
		update(sql, adjustmentId);
	}

	@Override
	public ArrayList<Adjustment> getAdjustments(ArgMap<PaymentArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int userId = args.getInt(PaymentArg.USER_ID);
		int statusId = args.getInt(PaymentArg.STATUS_ID);
		int typeId = args.getInt(PaymentArg.TYPE_ID);
		int adjustmentId = args.getInt(PaymentArg.ADJUSTMENT_ID);

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

		if (typeId > 0) {
			sql += "and a.adjustmentTypeId = ? \n";
			sqlArgs.add(typeId);
		}

		if (adjustmentId > 0) {
			sql += "and a.id = ? \n";
			sqlArgs.add(adjustmentId);
		}

		return query(sql, new AdjustmentMapper(), sqlArgs.toArray());
	}

	@Override
	public Payment getById(int paymentId) {
		String sql = createSqlBase() + "and p.id = ?";

		return queryForObject(sql, new PaymentMapper(), paymentId);
	}

	@Override
	public Data getUnpaidBalance(int userId) {
		String sql = "select count(p.id) as itemCount, sum(case isnull(a.price) when true then (e.price + e.markup) else (a.price + a.markup) end) as balance \n";
		sql += "from eventRegistrationParticipants p \n";
		sql += "join eventParticipantStatus s on s.id = p.statusId \n";
		sql += "join eventRegistrations r on r.id = p.eventRegistrationId \n";
		sql += "join events e on e.id = r.eventId \n";
		sql += "left join eventAgeGroups a on a.id = p.ageGroupId \n";
		sql += "where e.active = 1 and r.addedById = ? and p.statusId = 1";

		return queryForObject(sql, ServerUtils.getGenericRowMapper(), userId);
	}

	@Override
	public ArrayList<Payment> list(ArgMap<PaymentArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int userId = args.getInt(PaymentArg.USER_ID);
		int statusId = args.getInt(PaymentArg.STATUS_ID);
		int typeId = args.getInt(PaymentArg.TYPE_ID);

		String sql = createSqlBase();
		if (userId > 0) {
			sql += "and p.userId = ? \n";
			sqlArgs.add(userId);
		}

		if (statusId > 0) {
			sql += "and p.statusId = ? \n";
			sqlArgs.add(statusId);
		}

		if (typeId > 0) {
			sql += "and p.paymentTypeId = ? \n";
			sqlArgs.add(typeId);
		}

		ArrayList<Payment> data = query(sql, new PaymentMapper(), sqlArgs.toArray());

		return data;
	}

	public PaypalData makePayment(Payment p) {
		PaypalData data = new PaypalData();

		RequestEnvelope requestEnvelope = new RequestEnvelope();
		requestEnvelope.setErrorLanguage("en_US");

		List<Receiver> receiverLst = new ArrayList<Receiver>();

		// Our cut
		Receiver siteReceiver = new Receiver(round(p.getTotalAmount(), 2, BigDecimal.ROUND_HALF_UP));
		siteReceiver.setPaymentType("SERVICE");

		// A receiver's email address
		if (ServerContext.isLive()) {
			siteReceiver.setEmail("paul.augros@gmail.com");
		} else {
			siteReceiver.setEmail("citrusgroups@citrusgroups.com"); // password same as email
		}

		// The organization's cut
		// TODO re-enable when ready
		// if (p.getPrincipalAmount() > 0) {
		// siteReceiver.setPrimary(Boolean.TRUE);
		// Receiver orgReceiver = new Receiver(round(p.getPrincipalAmount(), 2, BigDecimal.ROUND_HALF_UP));
		// orgReceiver.setPaymentType("SERVICE");
		// orgReceiver.setPrimary(Boolean.FALSE);
		//
		// // org's email address
		// if (ServerContext.isLive()) {
		// orgReceiver.setEmail(ServerContext.getCurrentOrg().getPayPalEmail());
		// } else {
		// orgReceiver.setEmail("organization@fake.com"); // password same as email
		// }
		// receiverLst.add(orgReceiver);
		// }

		receiverLst.add(siteReceiver);

		ReceiverList receiverList = new ReceiverList(receiverLst);

		StringBuilder url = new StringBuilder();
		url.append(ServerContext.getBaseUrl() + "#page=" + p.getReturnPage());
		String returnUrl = url.toString() + "&ps=return&payKey=${payKey}";
		String cancelUrl = url.toString() + "&ps=cancel";
		String ipnUrl = ServerContext.getBaseUrl() + "baconbits/service/ipn";

		PayRequest payRequest = new PayRequest(requestEnvelope, "PAY", cancelUrl, "USD", receiverList, returnUrl);
		payRequest.setMemo(p.getMemo());
		payRequest.setIpnNotificationUrl(ipnUrl);
		ClientDetailsType cd = new ClientDetailsType();
		cd.setIpAddress(ServerContext.getRequest().getRemoteAddr());
		cd.setApplicationId("weare.home.educators");

		payRequest.setClientDetails(cd);
		if (!ServerContext.isLive()) {
			payRequest.setSenderEmail("someone@enduser.com"); // password same as email
		}

		PayResponse payResponse = makePayPalApiCall(payRequest);

		data.setPayKey(payResponse.getPayKey());
		data.setPaymentExecStatus(payResponse.getPaymentExecStatus());
		data.setAuthorizationUrl(payPalProperties.getProperty("service.RedirectURL") + "_ap-payment&paykey=" + payResponse.getPayKey());

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
			// apply any pending adjustments
			String sql = "select 1 as id, sum(amount) as total from adjustments where userId = ? and statusId = 1";
			Data sum = queryForObject(sql, ServerUtils.getGenericRowMapper(), ServerContext.getCurrentUserId());
			boolean adjustmentWasUsed = false;
			double discount = sum.getDouble("total");
			if (discount < 0 && payment.getTotalAmount() > 0) {
				adjustmentWasUsed = true;
				// apply discounts to principal first
				payment.setPrincipalAmount(payment.getPrincipalAmount() + discount);

				// roll over to markup if necessary
				// we only do this so that we can completely hide our markup from the end user
				if (payment.getPrincipalAmount() < 0) {
					payment.setMarkupAmount(payment.getMarkupAmount() + payment.getPrincipalAmount());
					payment.setPrincipalAmount(0);
				} else {
					discount = 0;
				}

				// if there's any left over, put it back in the original discount variable
				if (payment.getMarkupAmount() < 0) {
					discount = payment.getMarkupAmount();
					payment.setMarkupAmount(0);
				} else {
					discount = 0;
				}

			}

			// no need for pending status if payment amount is zero or less
			if (payment.getTotalAmount() <= 0) {
				payment.setStatusId(2);
			}
			payment.setUserId(ServerContext.getCurrentUser().getId());

			sql = "insert into payments (userId, paymentTypeId, paymentDate, amount, markupAmount, statusId) values ";
			sql += "(:userId, :paymentTypeId, now(), :principalAmount, :markupAmount, :statusId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			payment.setId(ServerUtils.getIdFromKeys(keys));

			// link payment to applied adjustments
			if (adjustmentWasUsed) {
				sql = "insert into paymentAdjustmentMapping (paymentId, adjustmentId) \n";
				sql += "select ?, a.id from adjustments a where a.userId = ? and a.statusId = 1";
				update(sql, payment.getId(), ServerContext.getCurrentUserId());

				// mark all adjustments as applied
				sql = "update adjustments set statusId = 2 where id in(select adjustmentId from paymentAdjustmentMapping where paymentId = ?)";
				update(sql, payment.getId());

				// then add a new adjustment for the left-over difference, if any
				if (discount < 0) {
					Adjustment adj = new Adjustment();
					adj.setAmount(discount);
					adj.setUserId(payment.getUserId());
					adj.setAdjustmentTypeId(3);
					adj.setStatusId(1);
					saveAdjustment(adj);
				}
			}

			if (payment.getTotalAmount() > 0) {
				// when adding a new payment, we need to create a transaction with PayPal
				data = makePayment(payment);
				if (data.getPayKey() != null) {
					// and then link it to our payment via a payKey
					sql = "update payments set payKey = ? where id = ?";
					update(sql, data.getPayKey(), payment.getId());
				}
			} else {
				// if a negative value, we don't need PayPal
				data = new PaypalData();
			}
		}

		Payment p = getById(payment.getId());
		// send the PayPal payment information back to the client, so we can redirect them to PayPal to make the payment
		p.setPaypalData(data);

		return p;
	}

	@Override
	public Adjustment saveAdjustment(Adjustment adjustment) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(adjustment);

		if (adjustment.isSaved()) {
			String sql = "update adjustments set statusId = :statusId, amount = :amount, description = :description ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			adjustment.setAddedById(ServerContext.getCurrentUser().getId());

			String sql = "insert into adjustments (userId, addedById, adjustmentTypeId, amount, statusId, description) values ";
			sql += "(:userId, :addedById, :adjustmentTypeId, :amount, :statusId, :description)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			adjustment.setId(ServerUtils.getIdFromKeys(keys));
		}

		Adjustment a = getAdjustments(new ArgMap<PaymentArg>(PaymentArg.ADJUSTMENT_ID, adjustment.getId())).get(0);

		return a;
	}

	private Properties getPayPalProperties() {
		if (payPalProperties == null) {
			Properties p = new Properties();
			if (ServerContext.isLive()) {
				// account email: weare.home.educators@gmail.com
				p.setProperty("acct1.UserName", "weare.home.educators_api1.gmail.com");
				p.setProperty("acct1.Password", "BZFADA4CXJSCHYW8");
				p.setProperty("acct1.Signature", "AFcWxV21C7fd0v3bYYYRCpSSRl31AIiJDk-qOGV.J6VfFcbztTiAOVoS");
				p.setProperty("acct1.AppId", "APP-4NF95723S77525228");
				p.setProperty("mode", "live");
				p.setProperty("service.RedirectURL", "https://www.paypal.com/webscr&cmd=");
			} else {
				// account email: paul.a_1343673136_biz@gmail.com
				p.setProperty("acct1.UserName", "paul.a_1343673136_biz_api1.gmail.com");
				p.setProperty("acct1.Password", "1343673159");
				p.setProperty("acct1.Signature", "AirQtU1053N9JvGp9xtKZtxvbZojAv39XGRp8tQ8KFG.CaL5sQWMC8F6");
				p.setProperty("acct1.AppId", "APP-80W284485P519543T");
				p.setProperty("mode", "sandbox");
				p.setProperty("service.RedirectURL", "https://www.sandbox.paypal.com/webscr&cmd=");
			}

			p.setProperty("http.ConnectionTimeOut", "5000");
			p.setProperty("http.Retry", "2");
			p.setProperty("http.ReadTimeOut", "30000");
			p.setProperty("http.MaxConnection", "100");
			p.setProperty("http.IPAddress", "127.0.0.1");
			p.setProperty("http.UseProxy", "false");
			p.setProperty("http.ProxyPort", "8080");
			p.setProperty("http.ProxyHost", "127.0.0.1");
			p.setProperty("http.ProxyUserName", "null");
			p.setProperty("http.ProxyPassword", "null");
			p.setProperty("http.GoogleAppEngine", "true");

			p.setProperty("service.DevCentralURL", "https://developer.paypal.com");
			payPalProperties = p;
		}

		return payPalProperties;
	}

	private PayResponse makePayPalApiCall(PayRequest payRequest) {
		AdaptivePaymentsService service = new AdaptivePaymentsService(getPayPalProperties());
		PayResponse payResponse = null;
		try {
			payResponse = service.pay(payRequest);
		} catch (Exception e) {
			logger.severe("Error Message: " + e.getMessage());
		}

		if (!payResponse.getResponseEnvelope().getAck().getValue().equalsIgnoreCase("Success")) {
			logger.severe("API Error Message: " + payResponse.getError().get(0).getMessage());
		}
		return payResponse;
	}

	private double round(double unrounded, int precision, int roundingMode) {
		BigDecimal bd = new BigDecimal(unrounded);
		BigDecimal rounded = bd.setScale(precision, roundingMode);
		return rounded.doubleValue();
	}

}
