package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.server.dao.EventDao;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.paypal.PayPalCredentials;
import com.areahomeschoolers.baconbits.server.util.Mailer;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventParticipant;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;
import com.areahomeschoolers.baconbits.shared.dto.User;
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
public class EventDaoImpl extends SpringWrapper implements EventDao {
	private final class EventMapper implements RowMapper<Event> {
		@Override
		public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
			Event event = new Event();
			event.setId(rs.getInt("id"));
			event.setActive(rs.getBoolean("active"));
			event.setAddedDate(rs.getTimestamp("addedDate"));
			event.setAddedById(rs.getInt("addedById"));
			event.setAddress(rs.getString("address"));
			event.setAdultRequired(rs.getBoolean("adultRequired"));
			event.setCategory(rs.getString("category"));
			event.setCategoryId(rs.getInt("categoryId"));
			event.setCost(rs.getDouble("cost"));
			event.setPrice(rs.getDouble("price"));
			event.setDescription(rs.getString("description"));
			event.setEndDate(rs.getTimestamp("endDate"));
			event.setGroupId(rs.getInt("groupId"));
			event.setMaximumParticipants(rs.getInt("maximumParticipants"));
			event.setMinimumParticipants(rs.getInt("minimumParticipants"));
			event.setNotificationEmail(rs.getString("notificationEmail"));
			event.setPublishDate(rs.getTimestamp("publishDate"));
			event.setRegistrationEndDate(rs.getTimestamp("registrationEndDate"));
			event.setRegistrationStartDate(rs.getTimestamp("registrationStartDate"));
			event.setSendSurvey(rs.getBoolean("sendSurvey"));
			event.setStartDate(rs.getTimestamp("startDate"));
			event.setTitle(rs.getString("title"));
			event.setGroupName(rs.getString("groupName"));
			event.setFinished(rs.getBoolean("finished"));
			event.setRegistrationOpen(rs.getBoolean("registrationOpen"));
			event.setAddedByFullName(rs.getString("firstName") + " " + rs.getString("lastName"));
			event.setRequiresRegistration(rs.getBoolean("requiresRegistration"));
			event.setPhone(rs.getString("phone"));
			event.setWebsite(rs.getString("website"));
			event.setDocumentCount(rs.getInt("documentCount"));
			event.setAccessLevel(rs.getString("accessLevel"));
			event.setAccessLevelId(rs.getInt("accessLevelId"));
			event.setCurrentUserParticipantCount(rs.getInt("currentUserParticipantCount"));
			event.setAgePrices(rs.getString("agePrices"));
			event.setAgeRanges(rs.getString("ageRanges"));
			event.setRegistrationInstructions(rs.getString("registrationInstructions"));
			return event;
		}
	}

	private PayPalCredentials paypal;

	@Autowired
	public EventDaoImpl(DataSource dataSource, PayPalCredentials pp) {
		super(dataSource);
		this.paypal = pp;
	}

	@Override
	public void deleteAgeGroup(EventAgeGroup ageGroup) {
		String sql = "delete from eventAgeGroups where id = ?";
		update(sql, ageGroup.getId());
	}

	@Override
	public void deleteEventField(int fieldId) {
		String sql = "delete from eventFieldValues where eventFieldId = ?";
		update(sql, fieldId);

		sql = "delete from eventFields where id = ?";
		update(sql, fieldId);
	}

	@Override
	public void deleteEventParticipant(EventParticipant participant) {
		String sql = "delete from eventRegistrationParticipants where id = ?";
		update(sql, participant.getId());
	}

	@Override
	public void deleteVolunteerPosition(EventVolunteerPosition position) {
		String sql = "delete from eventVolunteerPositions where id = ?";
		update(sql, position.getId());
	}

	@Override
	public void deleteVolunteerPositionMapping(int id) {
		String sql = "delete from eventVolunteerMapping where id = ?";
		update(sql, id);
	}

	@Override
	public Event getById(int id) {
		String sql = createSqlBase() + "and e.id = ?";
		return queryForObject(sql, new EventMapper(), id);
	}

	@Override
	public ArrayList<Data> getEventFieldTypes() {
		String sql = "select * from eventFieldTypes order by type";

		return query(sql, ServerUtils.getGenericRowMapper());
	}

	@Override
	public ArrayList<EventField> getFields(ArgMap<EventArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int ageGroupId = args.getInt(EventArg.AGE_GROUP_ID);
		int eventId = args.getInt(EventArg.EVENT_ID);
		final int participantId = args.getInt(EventArg.PARTICIPANT_ID);

		String sql = "select ef.*, et.type ";
		if (participantId > 0) {
			sql += ", ev.Value, ev.id as valueId ";
		}
		sql += "from eventFields ef ";
		sql += "join eventFieldTypes et on et.id = ef.eventFieldTypeId ";
		if (participantId > 0) {
			sql += "join eventFieldValues ev on ev.eventFieldId = ef.id and ev.participantId = ? ";
			sqlArgs.add(participantId);
		}
		sql += "where 1 = 1 ";
		if (ageGroupId > 0) {
			sql += "and ef.eventAgeGroupId = ? ";
			sqlArgs.add(ageGroupId);
		} else if (eventId > 0) {
			sql += "and ef.eventId = ? and ef.eventAgeGroupId is null ";
			sqlArgs.add(eventId);
		}
		sql += "order by ef.id";

		return query(sql, new RowMapper<EventField>() {
			@Override
			public EventField mapRow(ResultSet rs, int rowNum) throws SQLException {
				EventField f = createBaseEventField(rs);
				if (participantId > 0) {
					f.setParticipantId(participantId);
					f.setValue(rs.getString("value"));
					f.setValueId(rs.getInt("valueId"));
				}
				return f;
			}
		}, sqlArgs.toArray());
	}

	@Override
	public EventPageData getPageData(int eventId) {
		EventPageData pd = new EventPageData();
		if (eventId > 0) {
			pd.setEvent(getById(eventId));

			if (pd.getEvent() == null) {
				return null;
			}

			// age groups
			String sql = "select a.*, (select count(id) from eventRegistrationParticipants where ageGroupId = a.id) as registerCount, ";
			sql += "(select count(id) from eventFields where eventAgeGroupId = a.id) as fieldCount ";
			sql += "from eventAgeGroups a where a.eventId = ? order by a.minimumAge";
			pd.setAgeGroups(query(sql, new RowMapper<EventAgeGroup>() {
				@Override
				public EventAgeGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
					EventAgeGroup g = new EventAgeGroup();
					g.setId(rs.getInt("id"));
					g.setEventId(rs.getInt("eventId"));
					g.setMaximumAge(rs.getInt("maximumAge"));
					g.setMinimumAge(rs.getInt("minimumAge"));
					g.setMaximumParticipants(rs.getInt("maximumParticipants"));
					g.setMinimumParticipants(rs.getInt("minimumParticipants"));
					g.setPrice(rs.getDouble("price"));
					g.setRegisterCount(rs.getInt("registerCount"));
					g.setFieldCount(rs.getInt("fieldCount"));
					return g;
				}
			}, eventId));

			// volunteer positions
			pd.setVolunteerPositions(getVolunteerPositions(eventId, 0));

			if (ServerContext.isAuthenticated()) {
				// registration
				sql = "select * from eventRegistrations where eventId = ? and addedById = ? limit 1";
				EventRegistration r = queryForObject(sql, new RowMapper<EventRegistration>() {
					@Override
					public EventRegistration mapRow(ResultSet rs, int row) throws SQLException {
						EventRegistration r = new EventRegistration();
						r.setId(rs.getInt("id"));
						r.setEventId(rs.getInt("eventId"));
						r.setAddedById(rs.getInt("addedById"));
						r.setAddedDate(rs.getTimestamp("addedDate"));
						return r;
					}
				}, eventId, ServerContext.getCurrentUser().getId());

				if (r != null) {
					pd.setRegistration(r);

					r.setParticipants(getParticipants(new ArgMap<EventArg>(EventArg.REGISTRATION_ID, r.getId())));

					if (!Common.isNullOrEmpty(r.getParticipants())) {
						int canceledCount = 0;
						for (EventParticipant p : r.getParticipants()) {
							if (p.isCanceled()) {
								canceledCount++;
							}
						}

						if (canceledCount == r.getParticipants().size()) {
							r.setCanceled(true);
						}
					}

					r.setVolunteerPositions(getVolunteerPositions(eventId, r.getId()));
				}
			}

			if (pd.getRegistration() == null) {
				pd.setRegistration(new EventRegistration());
			}

		} else {
			Event e = new Event();
			e.setRegistrationStartDate(new Date());
			e.setPublishDate(new Date());
			e.setNotificationEmail(ServerContext.getCurrentUser().getEmail());
			pd.setEvent(e);
		}

		String sql = "select * from eventCategories order by category";
		pd.setCategories(query(sql, ServerUtils.getGenericRowMapper()));

		return pd;
	}

	@Override
	public ArrayList<EventParticipant> getParticipants(ArgMap<EventArg> args) {
		int registrationId = args.getInt(EventArg.REGISTRATION_ID);
		int participantId = args.getInt(EventArg.PARTICIPANT_ID);
		int eventId = args.getInt(EventArg.EVENT_ID);
		int parentIdPlusSelf = args.getInt(EventArg.PARENT_ID_PLUS_SELF);
		int parentId = args.getInt(EventArg.PARENT_ID);
		int userId = args.getInt(EventArg.USER_ID);
		int statusId = args.getInt(EventArg.STATUS_ID);
		final boolean includeFields = args.getBoolean(EventArg.INCLUDE_FIELDS);
		List<Integer> ids = args.getIntList(EventArg.PARTICIPANT_IDS);

		List<Object> sqlArgs = new ArrayList<Object>();
		String sql = "select r.eventId, e.title, e.startDate, p.*, u.firstName, u.lastName, u.birthDate, u.parentId, s.status, \n";
		sql += "up.firstName as parentFirstName, up.lastName as parentLastName, \n";
		if (includeFields) {
			sql += "(select group_concat(concat(f.name, ' ', v.value) separator '\n') \n";
			sql += "from eventFieldValues v \n";
			sql += "join eventFields f on f.id = v.eventFieldId \n";
			sql += "where v.participantId = p.id) as fieldValues, \n";
		}
		sql += "case isnull(a.price) when true then e.price else a.price end as price \n";
		sql += "from eventRegistrationParticipants p \n";
		sql += "join users u on u.id = p.userId \n";
		sql += "join eventParticipantStatus s on s.id = p.statusId \n";
		sql += "join eventRegistrations r on r.id = p.eventRegistrationId \n";
		sql += "join events e on e.id = r.eventId \n";
		sql += "join users up on up.id = r.addedById \n";
		sql += "left join payments py on py.id = p.paymentId \n";
		sql += "left join eventAgeGroups a on a.id = p.ageGroupId \n";
		sql += "where 1 = 1 \n";

		if (args.getStatus() != Status.ALL) {
			if (args.getStatus() == Status.ACTIVE) {
				sql += "and e.endDate > now() and active = 1 \n";
			} else {
				sql += "(and e.endDate < now() or active = 0) \n";
			}

		}

		if (!Common.isNullOrEmpty(ids)) {
			sql += "and p.id in(" + Common.join(ids, ", ") + ")";
		}

		if (registrationId > 0) {
			sql += "and p.eventRegistrationId = ? \n";
			sqlArgs.add(registrationId);
		}

		if (statusId > 0) {
			sql += "and p.statusId = ? \n";
			sqlArgs.add(statusId);
		}

		if (participantId > 0) {
			sql += "and p.id = ? \n";
			sqlArgs.add(participantId);
		}

		if (userId > 0) {
			sql += "and p.userId = ? \n";
			sqlArgs.add(userId);
		}

		if (parentIdPlusSelf > 0) {
			sql += "and (u.parentId = ? or u.id = ?) \n";
			sqlArgs.add(parentIdPlusSelf);
			sqlArgs.add(parentIdPlusSelf);
		}

		if (parentId > 0) {
			sql += "and u.parentId = ? ";
			sqlArgs.add(parentId);
		}

		if (eventId > 0) {
			sql += "and r.eventId = ? \n";
			sqlArgs.add(eventId);
		}
		sql += "order by u.lastName, u.firstName \n";

		return query(sql, new RowMapper<EventParticipant>() {
			@Override
			public EventParticipant mapRow(ResultSet rs, int row) throws SQLException {
				EventParticipant p = new EventParticipant();
				p.setId(rs.getInt("id"));
				p.setAgeGroupId(rs.getInt("ageGroupId"));
				p.setEventRegistrationId(rs.getInt("eventRegistrationId"));
				p.setFirstName(rs.getString("firstName"));
				p.setLastName(rs.getString("lastName"));
				p.setStatusId(rs.getInt("statusId"));
				p.setStatus(rs.getString("status"));
				p.setPrice(rs.getDouble("price"));
				p.setBirthDate(rs.getDate("birthDate"));
				p.setParentFirstName(rs.getString("parentFirstName"));
				p.setParentLastName(rs.getString("parentLastName"));
				p.setParentId(rs.getInt("parentId"));
				p.setUserId(rs.getInt("userId"));
				p.setAddedDate(rs.getTimestamp("addedDate"));
				p.setEventId(rs.getInt("eventId"));
				p.setEventTitle(rs.getString("title"));
				p.setPaymentId(rs.getInt("paymentId"));
				p.setEventDate(rs.getTimestamp("startDate"));
				if (includeFields) {
					p.setFieldValues(rs.getString("fieldValues"));
				}
				return p;
			}
		}, sqlArgs.toArray());
	}

	@Override
	public ArrayList<Data> getVolunteers(int eventId) {
		String sql = "select m.id, m.fulfilled, u.firstName, u.lastName, p.jobTitle ";
		sql += "from eventVolunteerMapping m ";
		sql += "join eventVolunteerPositions p on p.id = m.eventVolunteerPositionId ";
		sql += "join eventRegistrations r on r.id = m.eventRegistrationId ";
		sql += "join users u on u.id = r.addedById ";
		sql += "where r.eventId = ? ";
		sql += "order by p.jobTitle, u.lastName, u.firstName";

		return query(sql, ServerUtils.getGenericRowMapper(), eventId);
	}

	@Override
	public ArrayList<Event> list(ArgMap<EventArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int upcoming = args.getInt(EventArg.UPCOMING_NUMBER);

		String sql = createSqlBase();

		if (args.getStatus() != Status.ALL) {
			if (args.getStatus() == Status.ACTIVE) {
				sql += "and e.endDate > now() and active = 1 \n";
			} else {
				sql += "(and e.endDate < now() or active = 0) \n";
			}

		}

		if (upcoming > 0) {
			sql += "order by e.startDate limit ? ";
			sqlArgs.add(upcoming);
		}

		ArrayList<Event> data = query(sql, new EventMapper(), sqlArgs.toArray());

		return data;
	}

	public PaypalData makePayment(int paymentId, double amount) {
		PaypalData data = new PaypalData();

		ServiceEnvironment environment = ServerContext.isLive() ? ServiceEnvironment.PRODUCTION : ServiceEnvironment.SANDBOX;

		try {
			StringBuilder url = new StringBuilder();
			url.append(ServerContext.getBaseUrl() + "#page=EventParticipantList");
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
			payment.setMemo("Payment for events");

			Receiver receiver = new Receiver();
			receiver.setAmount(amount);
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

	@Override
	public PaypalData payForEvents(ArrayList<Integer> participantIds) {
		if (Common.isNullOrEmpty(participantIds)) {
			return null;
		}
		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.PARTICIPANT_IDS, participantIds);
		// scrub the participants because client prices can't be trusted
		List<EventParticipant> participants = getParticipants(args);
		double total = 0.00;
		for (EventParticipant p : participants) {
			total += p.getPrice();
		}

		// add a payment record
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", ServerContext.getCurrentUserId());
		params.addValue("total", total);
		String sql = "insert into payments (userId, paymentDate, amount, statusId) ";
		sql += "values(:userId, now(), :total, 1)";
		KeyHolder keys = new GeneratedKeyHolder();
		update(sql, params, keys);

		int paymentId = ServerUtils.getIdFromKeys(keys);

		sql = "update eventRegistrationParticipants set paymentId = ? where id in(" + Common.join(participantIds, ", ") + ")";
		update(sql, paymentId);

		PaypalData pd = makePayment(paymentId, total);
		if (pd.getPayKey() != null) {
			sql = "update payments set payKey = ? where id = ?";
			update(sql, pd.getPayKey(), paymentId);
		}

		return pd;
	}

	@Override
	public Event save(Event event) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(event);

		if (event.isSaved()) {
			String sql = "update events set title = :title, description = :description, startDate = :startDate, endDate = :endDate, accessLevelId = :accessLevelId, ";
			sql += "addedDate = :addedDate, groupId = :groupId, categoryId = :categoryId, cost = :cost, adultRequired = :adultRequired, ";
			sql += "registrationStartDate = :registrationStartDate, registrationEndDate = :registrationEndDate, sendSurvey = :sendSurvey, ";
			sql += "minimumParticipants = :minimumParticipants, maximumParticipants = :maximumParticipants, address = :address, requiresRegistration = :requiresRegistration, ";
			sql += "registrationInstructions = :registrationInstructions, ";
			sql += "notificationEmail = :notificationEmail, publishDate = :publishDate, active = :active, price = :price, phone = :phone, website = :website ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			event.setAddedById(ServerContext.getCurrentUser().getId());

			String sql = "insert into events (title, description, addedById, startDate, endDate, addedDate, groupId, categoryId, cost, adultRequired, ";
			sql += "registrationStartDate, registrationEndDate, sendSurvey, minimumParticipants, maximumParticipants, address, notificationEmail, ";
			sql += "publishDate, active, price, requiresRegistration, phone, website, accessLevelId, registrationInstructions) values ";
			sql += "(:title, :description, :addedById, :startDate, :endDate, now(), :groupId, :categoryId, :cost, :adultRequired, ";
			sql += ":registrationStartDate, :registrationEndDate, :sendSurvey, :minimumParticipants, :maximumParticipants, :address, :notificationEmail, ";
			sql += ":publishDate, :active, :price, :requiresRegistration, :phone, :website, :accessLevelId, :registrationInstructions)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			event.setId(ServerUtils.getIdFromKeys(keys));

			if (event.getCloneFromId() > 0) {
				// clone age groups
				sql = "insert into eventAgeGroups (eventId, minimumAge, maximumAge, minimumParticipants, maximumParticipants, price) ";
				sql += "select ?, minimumAge, maximumAge, minimumParticipants, maximumParticipants, price ";
				sql += "from eventAgeGroups where eventId = ?";
				update(sql, event.getId(), event.getCloneFromId());

				// clone volunteer positions
				sql = "insert into eventVolunteerPositions (eventId, jobTitle, description, discount, positionCount) ";
				sql += "select ?, jobTitle, description, discount, positionCount ";
				sql += "from eventVolunteerPositions where eventId = ?";
				update(sql, event.getId(), event.getCloneFromId());
			}
		}

		return getById(event.getId());
	}

	@Override
	public EventAgeGroup saveAgeGroup(EventAgeGroup ageGroup) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(ageGroup);

		if (ageGroup.isSaved()) {
			String sql = "update eventAgeGroups set minimumAge = :minimumAge, maximumAge = :maximumAge, minimumParticipants = :minimumParticipants, ";
			sql += "maximumParticipants = :maximumParticipants, price = :price where id = :id";
			update(sql, namedParams);
		} else {
			String sql = "insert into eventAgeGroups (eventId, minimumAge, maximumAge, minimumParticipants, maximumParticipants, price) ";
			sql += "values(:eventId, :minimumAge, :maximumAge, :minimumParticipants, :maximumParticipants, :price)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			ageGroup.setId(ServerUtils.getIdFromKeys(keys));
		}

		return ageGroup;
	}

	@Override
	public EventField saveField(EventField field) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(field);

		if (field.isSaved()) {
			String sql = "update eventFields set name = :name, required = :required, options = :options, eventFieldTypeId = :typeId where id = :id";
			update(sql, namedParams);
		} else {
			String sql = "insert into eventFields (eventAgeGroupId, name, eventFieldTypeId, required, options, eventId) ";
			sql += "values(:eventAgeGroupId, :name, :typeId, :required, :options, :eventId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			field.setId(ServerUtils.getIdFromKeys(keys));
		}

		return field;
	}

	@Override
	public synchronized ArrayList<EventParticipant> saveParticipant(final EventParticipant participant) {

		if (participant.getStatusId() == 0) {
			participant.setStatusId(1);
		}

		ArgMap<EventArg> args = new ArgMap<EventArg>();
		int ageGroupId = participant.getAgeGroupId() == null ? 0 : participant.getAgeGroupId();
		args.put(EventArg.AGE_GROUP_ID, ageGroupId);
		Data waitData = getWaitData(args);
		boolean eventIsFull = eventIsFull(waitData);
		if (participant.getStatusId() == 1 && eventIsFull) {
			participant.setStatusId(3);
		}

		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(participant);
		if (participant.isSaved()) {
			String sql = "update eventRegistrationParticipants set statusId = :statusId where id = :id ";
			update(sql, namedParams);

			if (participant.getStatusId() == 5 && eventIsFull) {
				registerNextWaitingParticipant(waitData);
			}
		} else {
			// create or update the associated user
			User u = participant.getUser();
			if (u == null) {
				u = new User();
			}
			u.setFirstName(participant.getFirstName());
			u.setLastName(participant.getLastName());
			u.setBirthDate(participant.getBirthDate());
			if (u.getId() != ServerContext.getCurrentUserId()) {
				u.setParentId(ServerContext.getCurrentUserId());
			}

			UserDao userDao = ServerContext.getDaoImpl("user");
			u = userDao.save(u).getData();
			participant.setUserId(u.getId());

			String sql = "insert into eventRegistrationParticipants(eventRegistrationId, userId, statusId, ageGroupId, addedDate) ";
			sql += "values(:eventRegistrationId, :userId, :statusId, :ageGroupId, now())";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			participant.setId(ServerUtils.getIdFromKeys(keys));
		}

		if (!Common.isNullOrEmpty(participant.getEventFields())) {
			for (EventField f : participant.getEventFields()) {
				f.setParticipantId(participant.getId());
				saveFieldValue(f);
			}
		}

		ArrayList<EventParticipant> list = getParticipants(new ArgMap<EventArg>(EventArg.REGISTRATION_ID, participant.getEventRegistrationId()));

		if (participant.getStatusId() == 1) {
			for (EventParticipant p : list) {
				if (p.getId() == participant.getId() && p.getPrice() == 0) {
					String sql = "update eventRegistrationParticipants set statusId = 2 where id = ?";
					update(sql, p.getId());
					p.setStatusId(2);
					p.setStatus("Confirmed/Paid");
				}
			}
		}

		return list;
	}

	@Override
	public synchronized EventRegistration saveRegistration(EventRegistration registration) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(registration);
		if (registration.isSaved()) {
			if (registration.getCanceled()) {
				String sql = "delete from eventVolunteerMapping where eventRegistrationId = :id";
				update(sql, namedParams);
			}

			ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.REGISTRATION_ID, registration.getId());
			ArrayList<EventParticipant> participants = getParticipants(args);
			for (EventParticipant p : participants) {
				p.setStatusId(registration.getCanceled() ? 5 : 1);
				saveParticipant(p);
			}

			registration.setParticipants(getParticipants(args));
		} else {
			registration.setAddedById(ServerContext.getCurrentUser().getId());
			String sql = "insert into eventRegistrations(eventId, addedDate, addedById) ";
			sql += "values(:eventId, now(), :addedById)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			registration.setId(ServerUtils.getIdFromKeys(keys));
		}

		return registration;
	}

	@Override
	public EventVolunteerPosition saveVolunteerPosition(EventVolunteerPosition position) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(position);

		if (position.isSaved() && position.getEventRegistrationId() == 0) {
			String sql = "update eventVolunteerPositions set jobTitle = :jobTitle, description = :description, discount = :discount, positionCount = :positionCount where id = :id ";
			update(sql, namedParams);
		} else {
			String sql = "";
			if (position.getEventRegistrationId() > 0) {
				sql = "insert into eventVolunteerMapping (eventVolunteerPositionId, eventRegistrationId, volunteerCount) ";
				sql += "values(:id, :eventRegistrationId, :registerPositionCount)";
			} else {
				sql = "insert into eventVolunteerPositions(eventId, jobTitle, description, discount, positionCount) ";
				sql += "values(:eventId, :jobTitle, :description, :discount, :positionCount)";
			}

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			if (position.getEventRegistrationId() > 0) {
				position.setMappingId(ServerUtils.getIdFromKeys(keys));
			} else {
				position.setId(ServerUtils.getIdFromKeys(keys));
			}
		}

		return position;
	}

	@Override
	public void setVolunteerFulFilled(int id, boolean fulfilled) {
		String sql = "update eventVolunteerMapping set fulfilled = ? where id = ?";
		update(sql, fulfilled, id);
	}

	private EventField createBaseEventField(ResultSet rs) throws SQLException {
		EventField f = new EventField();
		f.setId(rs.getInt("id"));
		f.setEventId(rs.getInt("eventId"));
		f.setEventAgeGroupId(rs.getInt("eventAgeGroupId"));
		f.setName(rs.getString("name"));
		f.setTypeId(rs.getInt("eventFieldTypeId"));
		f.setType(rs.getString("type"));
		f.setOptions(rs.getString("options"));
		f.setRequired(rs.getBoolean("required"));
		return f;
	}

	private String createSqlBase() {
		int userId = ServerContext.getCurrentUserId();
		String sql = "select e.*, g.groupName, c.category, u.firstName, u.lastName, l.accessLevel, \n";
		sql += "(select group_concat(price) from eventAgeGroups where eventId = e.id) as agePrices, \n";
		sql += "(select group_concat(concat(minimumAge, '-', maximumAge)) from eventAgeGroups where eventId = e.id) as ageRanges, \n";
		if (ServerContext.isAuthenticated()) {
			sql += "(select count(p.id) from eventRegistrationParticipants p join eventRegistrations r on r.id = p.eventRegistrationId and r.addedById = "
					+ userId + " where r.eventId = e.id) as currentUserParticipantCount, \n";
		} else {
			sql += "0 as currentUserParticipantCount, ";
		}
		sql += "(select count(id) from documentEventMapping where eventId = e.id) as documentCount, \n";
		sql += "(e.endDate < now()) as finished, isActive(e.registrationStartDate, e.registrationEndDate) as registrationOpen \n";
		sql += "from events e \n";
		sql += "left join groups g on g.id = e.groupId \n";
		sql += "join eventCategories c on c.id = e.categoryId \n";
		sql += "join users u on u.id = e.addedById \n";
		sql += "join userAccessLevels l on l.id = e.accessLevelId \n";

		sql += "left join userGroupMembers ugm on ugm.groupId = e.groupId and ugm.userId = " + userId + " \n";
		sql += "where 1 = 1 \n";

		if (!ServerContext.isSystemAdministrator()) {
			int auth = ServerContext.isAuthenticated() ? 1 : 0;
			sql += "and case e.accessLevelId when 1 then 1 when 2 then " + auth + " when 3 then ugm.id when 4 then ugm.isAdministrator else 0 end > 0 \n";
		}

		return sql;
	}

	private boolean eventIsFull(Data info) {
		if (info == null) {
			return false;
		}
		return info.getInt("maxParticipants") > 0 && (info.getInt("participants") >= info.getInt("maxParticipants"));
	}

	private ArrayList<EventVolunteerPosition> getVolunteerPositions(int eventId, final int registrationId) {
		List<Object> sqlArgs = new ArrayList<Object>();
		sqlArgs.add(eventId);
		String sql = "select p.*, p.positionCount - (select count(id) from eventVolunteerMapping where eventVolunteerPositionId = p.id) as openPositionCount ";
		if (registrationId > 0) {
			sql += ", m.volunteerCount, m.id as mappingId ";
			sqlArgs.add(registrationId);
		}
		sql += "from eventVolunteerPositions p ";
		if (registrationId > 0) {
			sql += "join eventVolunteerMapping m on m.eventVolunteerPositionId = p.id ";
		}
		sql += "where p.eventId = ? ";
		if (registrationId > 0) {
			sql += "and m.eventRegistrationId = ? ";
		}
		sql += "order by jobTitle";
		return query(sql, new RowMapper<EventVolunteerPosition>() {
			@Override
			public EventVolunteerPosition mapRow(ResultSet rs, int rowNum) throws SQLException {
				EventVolunteerPosition e = new EventVolunteerPosition();
				e.setId(rs.getInt("id"));
				e.setEventId(rs.getInt("eventId"));
				e.setDescription(rs.getString("description"));
				e.setDiscount(rs.getDouble("discount"));
				e.setJobTitle(rs.getString("jobTitle"));
				e.setPositionCount(rs.getInt("positionCount"));
				e.setOpenPositionCount(rs.getInt("openPositionCount"));
				if (registrationId > 0) {
					e.setEventRegistrationId(registrationId);
					e.setRegisterPositionCount(rs.getInt("volunteerCount"));
					e.setMappingId(rs.getInt("mappingId"));
				}
				return e;
			}
		}, sqlArgs.toArray());
	}

	private Data getWaitData(ArgMap<EventArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int eventId = args.getInt(EventArg.EVENT_ID);
		int ageGroupId = args.getInt(EventArg.AGE_GROUP_ID);
		int registrationId = args.getInt(EventArg.REGISTRATION_ID);

		String sql = "select r.eventId, p.ageGroupId, \n";
		sql += "case p.ageGroupId when 0 then e.maximumParticipants else g.maximumParticipants end as maxParticipants, \n";
		sql += "sum(case when  p.statusId in(1, 2) then 1 else 0 end) as participants \n";
		sql += "from eventRegistrationParticipants p \n";
		sql += "left join eventAgeGroups g on g.id = p.ageGroupId \n";
		sql += "join eventRegistrations r on p.eventRegistrationId = r.id \n";
		sql += "join events e on e.id = r.eventId \n";
		sql += "where 1 = 1 ";
		if (eventId > 0) {
			sql += "and e.id = ? \n";
			sqlArgs.add(eventId);
		}
		if (registrationId > 0) {
			sql += "and r.id = ? \n";
			sqlArgs.add(registrationId);
		}
		if (ageGroupId > 0) {
			sql += "and p.ageGroupId = ? \n";
			sqlArgs.add(ageGroupId);
		}
		sql += "group by r.eventId, p.ageGroupId limit 1";

		return queryForObject(sql, ServerUtils.getGenericRowMapper(), sqlArgs.toArray());
	}

	private void registerNextWaitingParticipant(Data info) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int eventId = info.getInt("eventId");
		int ageGroupId = info.getInt("ageGroupId");

		if (eventId == 0 && ageGroupId == 0) {
			return;
		}

		String sql = "select p.id, r.eventId, e.title, concat(up.firstName, ' ', up.lastName) as participantName, \n";
		sql += "ur.firstName, ur.email, date_format(e.startDate, '%W, %c/%e %l:%i %p') as startDate, \n";
		sql += "date_format(e.endDate, '%W, %c/%e %l:%i %p') as endDate ";
		sql += "from eventRegistrationParticipants p \n";
		sql += "join eventRegistrations r on r.id = p.eventRegistrationId \n";
		sql += "join users ur on ur.id = r.addedById \n";
		sql += "join users up on up.id = p.userId \n";
		sql += "join events e on e.id = r.eventId \n";
		sql += "where p.statusId = 3 \n";
		if (ageGroupId > 0) {
			sql += "and p.ageGroupId = ? \n";
			sqlArgs.add(ageGroupId);
		}
		if (eventId > 0) {
			sql += "and e.id = ? \n";
			sqlArgs.add(eventId);
		}
		sql += "order by p.addedDate limit 1";

		Data notify = queryForObject(sql, ServerUtils.getGenericRowMapper(), sqlArgs.toArray());

		if (notify == null) {
			return;
		}

		sql = "update eventRegistrationParticipants set statusId = 1 where id = ?";
		update(sql, notify.getId());

		Mailer mailer = new Mailer();
		mailer.addTo(notify.get("email"));
		String subject = "A spot has opened up for " + notify.get("participantName");
		String body = "Hello " + notify.get("firstName") + ",\n\n";
		body += "Due to a cancellation, " + notify.get("participantName") + " has been moved off the waiting list for the following event.\n\n";
		body += "Event: " + notify.get("title") + "\n";
		body += "Date: " + notify.get("startDate") + " to " + notify.get("endDate") + "\n";
		body += "Link: http://www.areahomeschoolers.com/#" + PageUrl.event(notify.getInt("eventId")) + "\n\n";
		body += "Click the link above to view more event details and your registration status.\n\n";
		body += "Thank you.";
		mailer.setSubject(subject);
		mailer.setBody(body);
		mailer.send();
	}

	private void saveFieldValue(EventField field) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(field);

		if (field.hasValue()) {
			String sql = "update eventFieldValues set value = :value where id = :valueId ";
			update(sql, namedParams);
		} else {
			String sql = "insert into eventFieldValues(eventFieldId, value, participantId) ";
			sql += "values(:id, :value, :participantId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			field.setId(ServerUtils.getIdFromKeys(keys));
		}
	}

}
