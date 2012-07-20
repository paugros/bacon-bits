package com.areahomeschoolers.baconbits.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.EventDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventField;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistration;
import com.areahomeschoolers.baconbits.shared.dto.EventRegistrationParticipant;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;

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
			event.setPublicEvent(rs.getBoolean("isPublic"));
			event.setAddedByFullName(rs.getString("firstName") + " " + rs.getString("lastName"));
			return event;
		}
	}

	private static String SELECT;

	@Autowired
	public EventDaoImpl(DataSource dataSource) {
		super(dataSource);
		SELECT = "select e.*, g.groupName, c.category, u.firstName, u.lastName from events e \n";
		SELECT += "left join groups g on g.id = e.groupId \n";
		SELECT += "join eventCategories c on c.id = e.categoryId \n";
		SELECT += "join users u on u.id = e.addedById \n";
	}

	@Override
	public void deleteEventField(int fieldId) {
		String sql = "delete from eventFieldValues where eventFieldId = ?";
		update(sql, fieldId);

		sql = "delete from eventFields where id = ?";
		update(sql, fieldId);
	}

	@Override
	public Event getById(int id) {
		String sql = SELECT + "where e.id = ?";

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
		final int participantId = args.getInt(EventArg.REGISTRATION_PARTICIPANT_ID);

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
	public EventPageData getPageData(int id) {
		EventPageData pd = new EventPageData();
		if (id > 0) {
			pd.setEvent(getById(id));

			if (pd.getEvent() == null) {
				return null;
			}

			String sql = "select * from eventAgeGroups where eventId = ? order by minimumAge";
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
					return g;
				}
			}, id));

			pd.setVolunteerPositions(getVolunteerPositions(id, 0));

			sql = "select * from eventRegistrations where eventId = ? and addedById = ?";

			if (ServerContext.isAuthenticated()) {
				EventRegistration r = queryForObject(sql, new RowMapper<EventRegistration>() {
					@Override
					public EventRegistration mapRow(ResultSet rs, int row) throws SQLException {
						EventRegistration r = new EventRegistration();
						r.setId(rs.getInt("id"));
						r.setEventId(rs.getInt("eventId"));
						r.setAddedById(rs.getInt("addedById"));
						r.setAddedDate(rs.getTimestamp("addedDate"));
						r.setAttended(rs.getBoolean("attended"));
						r.setCanceled(rs.getBoolean("canceled"));
						r.setWaiting(rs.getBoolean("waiting"));
						return r;
					}
				}, id, ServerContext.getCurrentUser().getId());

				if (r != null) {
					pd.setRegistration(r);

					sql = "select * from eventRegistrationParticipants where eventRegistrationId = ? order by firstName";

					r.setParticipants(query(sql, new RowMapper<EventRegistrationParticipant>() {

						@Override
						public EventRegistrationParticipant mapRow(ResultSet rs, int row) throws SQLException {
							EventRegistrationParticipant p = new EventRegistrationParticipant();
							p.setId(rs.getInt("id"));
							p.setAgeGroupId(rs.getInt("ageGroupId"));
							p.setEventRegistrationId(rs.getInt("eventRegistrationId"));
							p.setFirstName(rs.getString("firstName"));
							p.setLastName(rs.getString("lastName"));
							return p;
						}
					}, r.getId()));

					r.setVolunteerPositions(getVolunteerPositions(id, r.getId()));
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
	public ArrayList<Event> list(ArgMap<EventArg> args) {
		String sql = SELECT;
		ArrayList<Event> data = query(sql, new EventMapper());

		return data;
	}

	@Override
	public Event save(Event event) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(event);

		if (event.isSaved()) {
			String sql = "update events set title = :title, description = :description, startDate = :startDate, endDate = :endDate, ";
			sql += "addedDate = :addedDate, groupId = :groupId, categoryId = :categoryId, cost = :cost, adultRequired = :adultRequired, ";
			sql += "registrationStartDate = :registrationStartDate, registrationEndDate = :registrationEndDate, sendSurvey = :sendSurvey, ";
			sql += "minimumParticipants = :minimumParticipants, maximumParticipants = :maximumParticipants, address = :address, ";
			sql += "notificationEmail = :notificationEmail, publishDate = :publishDate, active = :active, isPublic = :publicEvent ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			event.setAddedById(ServerContext.getCurrentUser().getId());

			String sql = "insert into events (title, description, addedById, startDate, endDate, addedDate, groupId, categoryId, cost, adultRequired, ";
			sql += "registrationStartDate, registrationEndDate, sendSurvey, minimumParticipants, maximumParticipants, address, notificationEmail, ";
			sql += "publishDate, active, isPublic) values ";
			sql += "(:title, :description, :addedById, :startDate, :endDate, now(), :groupId, :categoryId, :cost, :adultRequired, ";
			sql += ":registrationStartDate, :registrationEndDate, :sendSurvey, :minimumParticipants, :maximumParticipants, :address, :notificationEmail, ";
			sql += ":publishDate, :active, :publicEvent)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			event.setId(ServerUtils.getIdFromKeys(keys));
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
	public EventRegistrationParticipant saveParticipant(EventRegistrationParticipant participant) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(participant);

		if (participant.isSaved()) {
			String sql = "update eventRegistrationParticipants set firstName = :firstName, lastName = :lastName, ageGroupId = :ageGroupId, age = :age where id = :id ";
			update(sql, namedParams);
		} else {
			String sql = "insert into eventRegistrationParticipants(eventRegistrationId, firstName, lastName, ageGroupId, age) ";
			sql += "values(:eventRegistrationId, :firstName, :lastName, :ageGroupId, :age)";

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

		return participant;
	}

	@Override
	public ServerResponseData<EventRegistration> saveRegistration(EventRegistration registration) {
		ServerResponseData<EventRegistration> rd = new ServerResponseData<EventRegistration>();

		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(registration);
		if (registration.isSaved()) {
			String sql = "update eventRegistrations set waiting = :waiting, canceled = :canceled, attended = :attended where id = :id ";
			update(sql, namedParams);
		} else {
			registration.setAddedById(ServerContext.getCurrentUser().getId());
			String sql = "insert into eventRegistrations(eventId, waiting, addedDate, canceled, attended, addedById) ";
			sql += "values(:eventId, :waiting, now(), :canceled, :attended, :addedById)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			registration.setId(ServerUtils.getIdFromKeys(keys));
		}

		rd.setData(registration);

		return rd;
	}

	@Override
	public EventVolunteerPosition saveVolunteerPosition(EventVolunteerPosition position) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(position);

		if (position.isSaved()) {
			String sql = "update eventVolunteerPositions set jobTitle = :jobTitle, description = :description, discount = :discount, positionCount = :positionCount where id = :id ";
			update(sql, namedParams);
		} else {
			String sql = "insert into eventVolunteerPositions(eventId, jobTitle, description, discount, positionCount) ";
			sql += "values(:eventId, :jobTitle, :description, :discount, :positionCount)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			position.setId(ServerUtils.getIdFromKeys(keys));
		}

		return position;
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

	private ArrayList<EventVolunteerPosition> getVolunteerPositions(int eventId, final int registrationId) {
		List<Object> sqlArgs = new ArrayList<Object>();
		sqlArgs.add(eventId);
		String sql = "select p.*, p.positionCount - (select count(id) from eventVolunteerMapping where eventVolunteerPositionId = p.id) as openPositionCount ";
		if (registrationId > 0) {
			sql += ", m.volunteerCount ";
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
				}
				return e;
			}
		}, sqlArgs.toArray());
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
