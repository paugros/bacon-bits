package com.areahomeschoolers.baconbits.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

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
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventAgeGroup;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;
import com.areahomeschoolers.baconbits.shared.dto.EventVolunteerPosition;

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
	public Event getById(int id) {
		String sql = SELECT + "where e.id = ?";

		return queryForObject(sql, new EventMapper(), id);
	}

	@Override
	public EventPageData getPageData(int id) {
		EventPageData pd = new EventPageData();
		if (id > 0) {
			pd.setEvent(getById(id));

			if (pd.getEvent() == null) {
				return null;
			}

			String sql = "select * from eventAgeGroups where eventId = ?";
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

			sql = "select * from eventVolunteerPositions where eventId = ?";
			pd.setVolunteerPositions(query(sql, new RowMapper<EventVolunteerPosition>() {
				@Override
				public EventVolunteerPosition mapRow(ResultSet rs, int rowNum) throws SQLException {
					EventVolunteerPosition e = new EventVolunteerPosition();
					e.setId(rs.getInt("id"));
					e.setEventId(rs.getInt("eventId"));
					e.setDescription(rs.getString("description"));
					e.setDiscount(rs.getDouble("discount"));
					e.setJobTitle(rs.getString("jobTitle"));
					e.setPositionCount(rs.getInt("positionCount"));
					return e;
				}
			}, id));

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

}
