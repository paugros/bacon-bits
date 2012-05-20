package com.areahomeschoolers.baconbits.server.service;

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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.server.spring.GWTController;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.EventPageData;

@Controller
@RequestMapping("/event")
public class EventServiceImpl extends GWTController implements EventService {

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
			event.setAddedByFullName(rs.getString("firstName") + " " + rs.getString("lastName"));
			return event;
		}
	}

	private static final long serialVersionUID = 1L;
	private SpringWrapper wrapper;
	private static String SELECT;

	@Autowired
	public EventServiceImpl(DataSource ds) {
		wrapper = new SpringWrapper(ds);
		SELECT = "select e.*, g.groupName, c.category, u.firstName, u.lastName from events e \n";
		SELECT += "left join groups g on g.id = e.groupId \n";
		SELECT += "join eventCategories c on c.id = e.categoryId \n";
		SELECT += "join users u on u.id = e.addedById \n";
	}

	@Override
	public Event getById(int id) {
		String sql = SELECT + "where e.id = ?";

		return wrapper.queryForObject(sql, new EventMapper(), id);
	}

	@Override
	public EventPageData getPageData(int id) {
		EventPageData pd = new EventPageData();
		if (id > 0) {
			pd.setEvent(getById(id));

			if (pd.getEvent() == null) {
				return null;
			}
		} else {
			Event e = new Event();
			e.setRegistrationStartDate(new Date());
			e.setPublishDate(new Date());
			e.setNotificationEmail(ServerContext.getCurrentUser().getEmail());
			pd.setEvent(e);
		}

		String sql = "select * from eventCategories order by category";
		pd.setCategories(wrapper.query(sql, ServerUtils.getGenericRowMapper()));

		return pd;
	}

	@Override
	public ArrayList<Event> list(ArgMap<EventArg> args) {
		String sql = SELECT;
		ArrayList<Event> data = wrapper.query(sql, new EventMapper());

		return data;
	}

	@Override
	@PreAuthorize("hasRole('ROLE_BASIC_USER')")
	public Event save(Event event) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(event);

		if (event.isSaved()) {
			String sql = "update events set title = :title, description = :description, startDate = :startDate, endDate = :endDate, ";
			sql += "addedDate = :addedDate, groupId = :groupId, categoryId = :categoryId, cost = :cost, adultRequired = :adultRequired, ";
			sql += "registrationStartDate = :registrationStartDate, registrationEndDate = :registrationEndDate, sendSurvey = :sendSurvey, ";
			sql += "minimumParticipants = :minimumParticipants, maximumParticipants = :maximumParticipants, address = :address, ";
			sql += "notificationEmail = :notificationEmail, publishDate = :publishDate, active = :active ";
			sql += "where id = :id";
			wrapper.update(sql, namedParams);
		} else {
			event.setAddedById(ServerContext.getCurrentUser().getId());

			String sql = "insert into events (title, description, addedById, startDate, endDate, addedDate, groupId, categoryId, cost, adultRequired, ";
			sql += "registrationStartDate, registrationEndDate, sendSurvey, minimumParticipants, maximumParticipants, address, notificationEmail, ";
			sql += "publishDate, active) values ";
			sql += "(:title, :description, :addedById, :startDate, :endDate, now(), :groupId, :categoryId, :cost, :adultRequired, ";
			sql += ":registrationStartDate, :registrationEndDate, :sendSurvey, :minimumParticipants, :maximumParticipants, :address, :notificationEmail, ";
			sql += ":publishDate, :active)";

			KeyHolder keys = new GeneratedKeyHolder();
			wrapper.update(sql, namedParams, keys);

			event.setId(ServerUtils.getIdFromKeys(keys));
		}

		return getById(event.getId());
	}

}
