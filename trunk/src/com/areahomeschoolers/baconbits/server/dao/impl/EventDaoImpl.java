package com.areahomeschoolers.baconbits.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.server.dao.ArticleDao;
import com.areahomeschoolers.baconbits.server.dao.BookDao;
import com.areahomeschoolers.baconbits.server.dao.EventDao;
import com.areahomeschoolers.baconbits.server.dao.PaymentDao;
import com.areahomeschoolers.baconbits.server.dao.ResourceDao;
import com.areahomeschoolers.baconbits.server.dao.Suggestible;
import com.areahomeschoolers.baconbits.server.dao.TagDao;
import com.areahomeschoolers.baconbits.server.dao.UserDao;
import com.areahomeschoolers.baconbits.server.util.Mailer;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
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
import com.areahomeschoolers.baconbits.shared.dto.HomePageData;
import com.areahomeschoolers.baconbits.shared.dto.Pair;
import com.areahomeschoolers.baconbits.shared.dto.Payment;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreference;
import com.areahomeschoolers.baconbits.shared.dto.PrivacyPreferenceType;
import com.areahomeschoolers.baconbits.shared.dto.ServerResponseData;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestionData;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;
import com.areahomeschoolers.baconbits.shared.dto.User;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.VisibilityLevel;

@Repository
public class EventDaoImpl extends SpringWrapper implements EventDao, Suggestible {
	private final class EventMapper implements RowMapper<Event> {
		@Override
		public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
			Event event = new Event();
			event.setId(rs.getInt("id"));
			event.setActive(rs.getBoolean("active"));
			event.setAddedDate(rs.getTimestamp("addedDate"));
			event.setAddedById(rs.getInt("addedById"));
			event.setAdultRequired(rs.getBoolean("adultRequired"));
			event.setCategory(rs.getString("category"));
			event.setCategoryId(rs.getInt("categoryId"));
			event.setCost(rs.getDouble("cost"));
			event.setPrice(rs.getDouble("price"));
			event.setMarkup(rs.getDouble("markup"));
			event.setDescription(rs.getString("description"));
			event.setEndDate(rs.getTimestamp("endDate"));
			event.setGroupId(rs.getInt("groupId"));
			event.setMaximumParticipants(rs.getInt("maximumParticipants"));
			event.setMinimumParticipants(rs.getInt("minimumParticipants"));
			event.setNotificationEmail(rs.getString("notificationEmail"));
			event.setContactEmail(rs.getString("contactEmail"));
			event.setContactName(rs.getString("contactName"));
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
			event.setVisibilityLevel(rs.getString("visibilityLevel"));
			event.setVisibilityLevelId(rs.getInt("visibilityLevelId"));
			event.setCurrentUserParticipantCount(rs.getInt("currentUserParticipantCount"));
			event.setAgePrices(rs.getString("agePrices"));
			event.setAgeRanges(rs.getString("ageRanges"));
			event.setRegistrationInstructions(rs.getString("registrationInstructions"));
			event.setSeriesId(rs.getInt("seriesId"));
			event.setRequiredInSeries(rs.getBoolean("requiredInSeries"));
			event.setNewlyAdded(rs.getBoolean("newlyAdded"));
			event.setOwningOrgId(rs.getInt("owningOrgId"));
			event.setMarkupDollars(rs.getDouble("markupDollars"));
			event.setMarkupPercent(rs.getDouble("markupPercent"));
			event.setMarkupOverride(rs.getBoolean("markupOverride"));
			event.setGroupMarkupDollars(rs.getDouble("groupMarkupDollars"));
			event.setGroupMarkupPercent(rs.getDouble("groupMarkupPercent"));
			event.setGroupMarkupOverride(rs.getBoolean("groupMarkupOverride"));
			event.setFacilityName(rs.getString("facilityName"));
			event.setCity(rs.getString("city"));
			event.setZip(rs.getString("zip"));
			event.setState(rs.getString("state"));
			event.setAddress(rs.getString("address"));
			event.setStreet(rs.getString("street"));
			event.setLat(rs.getDouble("lat"));
			event.setLng(rs.getDouble("lng"));
			event.setImageId(rs.getInt("imageId"));
			event.setSmallImageId(rs.getInt("smallImageId"));
			event.setImageExtension(rs.getString("fileExtension"));
			event.setDirectoryPriority(rs.getBoolean("directoryPriority"));
			return event;
		}
	}

	@Autowired
	public EventDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public void createSeries(Event event) {
		int cloneFromId = event.getId();

		update("update events set seriesId = id, requiredInSeries = ? where id = ?", event.getRequiredInSeries(), event.getId());

		Date firstDate = event.getSeriesDates().get(0).getLeft();
		for (Pair<Date, Date> dates : event.getSeriesDates()) {
			if (dates.getLeft().before(firstDate)) {
				firstDate = dates.getLeft();
			}
		}

		for (Pair<Date, Date> dates : event.getSeriesDates()) {
			event.setId(0);
			event.setCloneFromId(cloneFromId);
			event.setSeriesId(cloneFromId);
			event.setStartDate(dates.getLeft());
			event.setEndDate(dates.getRight());

			event.setRegistrationStartDate(new Date());

			if (event.getRequiredInSeries()) {
				event.setRegistrationEndDate(DateUtils.addDays(firstDate, 14));
			} else {
				event.setRegistrationEndDate(DateUtils.addDays(event.getStartDate(), 14));
			}

			save(event);
		}
	}

	@Override
	public String createWhere() {
		int userId = ServerContext.getCurrentUserId();

		String sql = "left join userGroupMembers ugm on ugm.groupId = e.groupId and ugm.userId = " + userId + " \n";
		sql += "left join userGroupMembers org on org.groupId = e.owningOrgId and org.userId = " + userId + " \n";
		sql += "where 1 = 1 \n";

		int auth = ServerContext.isAuthenticated() ? 1 : 0;
		if (!ServerContext.isSystemAdministrator()) {
			sql += "and case e.visibilityLevelId ";
			sql += "when 1 then 1 ";
			sql += "when 2 then " + auth + " \n";
			sql += "when 4 then (ugm.id > 0 or org.isAdministrator) \n";
			sql += "else 0 end > 0 \n";
		}

		return sql;
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
	public HomePageData getHomePageData() {
		HomePageData pd = new HomePageData();

		// if (ServerContext.isCitrus()) {
		// String sql = "select g.id, g.groupName, g.description, g.orgDomain, g.orgSubDomain, g.logoId, \n";
		// sql += "(select count(ugm.id) from userGroupMembers ugm \n";
		// sql += "join users u on u.id = ugm.userId \n";
		// sql += "where groupId = g.id and isActive(u.startDate, u.endDate) = 1) as memberCount \n";
		// sql += "from groups g \n";
		// sql += "where g.isOrganization = 1 and g.id != " + Constants.CG_ORG_ID + "\n";
		// sql += "and isActive(g.startDate, g.endDate) = 1 \n";
		// sql += "order by g.groupName";
		// pd.setGroups(query(sql, ServerUtils.getGenericRowMapper()));
		// }
		//
		// ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		// args.put(EventArg.UPCOMING_NUMBER, 5);
		//
		// pd.setUpcomingEvents(list(args));
		//
		// args.put(EventArg.ONLY_COMMUNITY);
		// pd.setCommunityEvents(list(args));
		//
		// args.remove(EventArg.ONLY_COMMUNITY);
		// args.put(EventArg.NEWLY_ADDED);
		// pd.setNewlyAddedEvents(list(args));
		//
		// if (ServerContext.isAuthenticated()) {
		// args.remove(EventArg.NEWLY_ADDED);
		// args.put(EventArg.REGISTERED_BY_OR_ADDED_FOR_ID, ServerContext.getCurrentUserId());
		// pd.setMyUpcomingEvents(list(args));
		// }
		//
		// Integer articleId = 0;
		//
		// UserGroup org = ServerContext.getCurrentOrg();
		// if (org != null) {
		// articleId = ServerContext.isAuthenticated() ? org.getPrivateGreetingId() : org.getPublicGreetingId();
		// if (articleId == null) {
		// articleId = 0;
		// }
		// }
		// ArticleDao articleDao = ServerContext.getDaoImpl("article");
		// Article a = articleDao.getById(articleId);
		// if (a == null) {
		// a = new Article();
		// a.setTitle("Welcome!");
		// String text;
		// if (org != null) {
		// text = "Welcome to " + org.getGroupName() + ". ";
		// } else {
		// text = "Welcome. ";
		// }
		// text += "Our site is still being constructed, but you can have a look around anyway.";
		// a.setArticle(text);
		// }
		// pd.setIntro(a);
		//
		// pd.setPartners(articleDao.getById(Constants.PARTNER_LOGO_ARTICLE_ID));

		// various counts
		BookDao bd = ServerContext.getDaoImpl("book");
		pd.setBookCount(bd.getCount());

		UserDao ud = ServerContext.getDaoImpl("user");
		pd.setUserCount(ud.getCount());

		ResourceDao rd = ServerContext.getDaoImpl("resource");
		pd.setResourceCount(rd.getCount());

		ArgMap<ResourceArg> args = new ArgMap<>(ResourceArg.AD);
		args.put(ResourceArg.LIMIT, 3);
		args.setStatus(Status.ACTIVE);
		args.put(ResourceArg.RANDOM);
		pd.setAds(rd.list(args));

		ArticleDao ad = ServerContext.getDaoImpl("article");
		pd.setArticleCount(ad.getCount());

		String sql = "select count(*) from events e ";
		sql += TagDaoImpl.createWhere(TagMappingType.EVENT);
		pd.setEventCount(queryForInt(sql));

		return pd;
	}

	@Override
	public EventPageData getPageData(int eventId) {
		final EventPageData pd = new EventPageData();
		if (eventId > 0) {
			pd.setEvent(getById(eventId));

			if (pd.getEvent() == null) {
				return null;
			}
			// tags
			TagDao tagDao = ServerContext.getDaoImpl("tag");
			ArgMap<TagArg> tagArgs = new ArgMap<TagArg>(TagArg.ENTITY_ID, pd.getEvent().getId());
			tagArgs.put(TagArg.MAPPING_TYPE, TagMappingType.EVENT.toString());
			pd.setTags(tagDao.list(tagArgs));

			String sql = "";

			// other events in same series
			if (pd.getEvent().getSeriesId() != null) {
				ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.SERIES_ID, pd.getEvent().getSeriesId());
				pd.setEventsInSeries(list(args));
			}

			// age groups
			pd.setAgeGroups(getAgeGroups(eventId));

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
			UserGroup g = ServerContext.getCurrentOrg();
			e.setGroupMarkupOverride(g.getMarkupOverride());
			e.setGroupMarkupDollars(g.getMarkupDollars());
			e.setGroupMarkupPercent(g.getMarkupPercent());

			Date d = new Date();
			e.setRegistrationStartDate(DateUtils.setHours(d, 7));
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
		int registrationAddedById = args.getInt(EventArg.REGISTRATION_ADDED_BY_ID);
		int parentId = args.getInt(EventArg.PARENT_ID);
		int userId = args.getInt(EventArg.USER_ID);
		int statusId = args.getInt(EventArg.STATUS_ID);
		int notStatusId = args.getInt(EventArg.NOT_STATUS_ID);
		int registeredByOrAddedForId = args.getInt(EventArg.REGISTERED_BY_OR_ADDED_FOR_ID);
		final boolean includeFields = args.getBoolean(EventArg.INCLUDE_FIELDS);
		List<Integer> ids = args.getIntList(EventArg.PARTICIPANT_IDS);

		List<Object> sqlArgs = new ArrayList<Object>();
		String sql = "select r.eventId, e.title, e.startDate, e.endDate, p.*, u.firstName, u.lastName, u.birthDate, u.parentId, s.status, \n";
		sql += "up.firstName as addedByFirstName, up.lastName as addedByLastName, up.email as registrantEmailAddress, \n";
		sql += "r.addedById, e.groupId, e.owningOrgId, e.seriesId, e.requiredInSeries, py.statusId as paymentStatusId, \n";
		if (includeFields) {
			sql += "(select group_concat(concat(f.name, ' ', v.value) separator '\n') \n";
			sql += "from eventFieldValues v \n";
			sql += "join eventFields f on f.id = v.eventFieldId \n";
			sql += "where v.participantId = p.id) as fieldValues, \n";
		}
		sql += "case isnull(a.price) when true then e.price else a.price end as price, \n";
		sql += "case isnull(a.markup) when true then e.markup else a.markup end as markup \n";
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
				sql += "and e.endDate > now() \n";
			} else {
				sql += "and e.endDate < now() \n";
			}
		}

		if (!args.getBoolean(EventArg.SHOW_INACTIVE)) {
			sql += "and e.active = 1 ";
		}

		if (notStatusId > 0) {
			sql += "and p.statusId != ? \n";
			sqlArgs.add(notStatusId);
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

		if (registrationAddedById > 0) {
			sql += "and r.addedById = ? \n";
			sqlArgs.add(registrationAddedById);
		}

		if (registeredByOrAddedForId > 0) {
			sql += "and (r.addedById = ? or p.userId = ?) \n";
			sqlArgs.add(registeredByOrAddedForId);
			sqlArgs.add(registeredByOrAddedForId);
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
				p.setMarkup(rs.getDouble("markup"));
				p.setBirthDate(rs.getDate("birthDate"));
				p.setAddedByFirstName(rs.getString("addedByFirstName"));
				p.setAddedByLastName(rs.getString("addedByLastName"));
				p.setAddedById(rs.getInt("addedById"));
				p.setUserId(rs.getInt("userId"));
				p.setAddedDate(rs.getTimestamp("addedDate"));
				p.setEventId(rs.getInt("eventId"));
				p.setEventTitle(rs.getString("title"));
				p.setPaymentId(rs.getInt("paymentId"));
				p.setPaymentStatusId(rs.getInt("paymentStatusId"));
				p.setEventStartDate(rs.getTimestamp("startDate"));
				p.setEventEndDate(rs.getTimestamp("endDate"));
				p.setEventGroupId(rs.getInt("groupId"));
				p.setEventOrganizationId(rs.getInt("owningOrgId"));
				p.setEventSeriesId(rs.getInt("seriesId"));
				p.setRegistrantEmailAddress(rs.getString("registrantEmailAddress"));
				p.setRequiredInSeries(rs.getBoolean("requiredInSeries"));
				if (includeFields) {
					p.setFieldValues(rs.getString("fieldValues"));
				}
				return p;
			}
		}, sqlArgs.toArray());
	}

	@Override
	public ArrayList<Data> getParticipantStatusList() {
		String sql = "select * from eventParticipantStatus order by status";

		return query(sql, ServerUtils.getGenericRowMapper());
	}

	@Override
	public ArrayList<Data> getRegistrationSummary() {
		String sql = "select e.id, e.title, e.startDate, a.minimumAge, a.maximumAge, '' as ageText, '' as minMaxText, '' as countText, \n";
		sql += "case when a.minimumParticipants is null then e.minimumParticipants else a.minimumParticipants end as minimumParticipants, \n";
		sql += "case when a.maximumParticipants is null then e.maximumParticipants else a.maximumParticipants end as maximumParticipants, \n";
		sql += "sum(case when p.statusId in(1, 2) then 1 else 0 end) as participants, \n";
		sql += "sum(case when p.statusId = 3 then 1 else 0 end) as waiting \n";
		sql += "from events e \n";
		sql += "left join eventRegistrations r on r.eventId = e.id \n";
		sql += "left join eventAgeGroups a on a.eventId = e.id \n";
		sql += "left join eventRegistrationParticipants p on p.eventRegistrationId = r.id and (p.ageGroupId = a.id or p.ageGroupId is null) \n";
		sql += "where e.endDate > now() and e.active = 1 and e.requiresRegistration = 1 and p.statusId in(1, 2, 3) \n";
		sql += "group by e.id, e.title, e.startDate, a.minimumAge, a.maximumAge, a.minimumParticipants, a.maximumParticipants \n";
		sql += "order by e.id, a.minimumAge";

		List<Data> data = query(sql, ServerUtils.getGenericRowMapper());
		Map<Integer, Data> map = new HashMap<Integer, Data>();

		for (Data d : data) {
			Data event = map.get(d.getId());
			if (event == null) {
				map.put(d.getId(), d);
				event = d;
			}

			String ageText = d.get("minimumAge");
			if (d.getInt("maximumAge") == 0) {
				ageText += "+";
			} else {
				ageText += "-" + d.get("maximumAge");
			}

			if (d.get("minimumAge") == null) {
				ageText = "N/A";
			}
			event.put("ageText", event.get("ageText") + ageText + "\n");

			event.put("minMaxText", event.get("minMaxText") + d.get("minimumParticipants") + "/" + d.get("maximumParticipants") + "\n");
			event.put("countText", Common.getDefaultIfNull(event.get("countText"), "") + d.get("participants") + "\n");
			event.put("waitText", Common.getDefaultIfNull(event.get("waitText"), "") + d.get("waiting") + "\n");
		}

		return new ArrayList<Data>(map.values());
	}

	@Override
	public ServerSuggestionData getSuggestionData(String token, int limit, Data options) {
		ServerSuggestionData data = new ServerSuggestionData();
		String sql = "select e.id, concat(e.title, ' - ', date_format(e.startDate, '%b %e')) as Suggestion, 'Event' as entityType ";
		sql += "from events e ";
		sql += createWhere();
		sql += "and e.endDate > now() and e.active = 1 ";
		sql += "and e.title like ? ";
		sql += "order by e.title ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";
		data.setSuggestions(query(sql, ServerUtils.getSuggestionMapper(), search));
		return data;
	}

	@Override
	public ArrayList<Data> getVolunteers(ArgMap<EventArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int eventId = args.getInt(EventArg.EVENT_ID);
		int userId = args.getInt(EventArg.USER_ID);

		String sql = "select e.startDate, e.endDate, e.title, r.eventId, m.id, m.fulfilled, u.firstName, u.lastName, \n";
		sql += "p.jobTitle, r.addedById, case when a.id is null then 0 else 1 end as adjustmentApplied \n";
		sql += "from eventVolunteerMapping m \n";
		sql += "join eventVolunteerPositions p on p.id = m.eventVolunteerPositionId \n";
		sql += "join eventRegistrations r on r.id = m.eventRegistrationId \n";
		sql += "join events e on e.id = r.eventId \n";
		sql += "join users u on u.id = r.addedById \n";
		sql += "left join adjustments a on a.linkId = p.id and a.adjustmentTypeId = 2 and a.userId = u.id and a.statusId = 2 \n";
		sql += "where 1 = 1 ";
		if (eventId > 0) {
			sql += "and r.eventId = ? \n";
			sqlArgs.add(eventId);
		}

		if (userId > 0) {
			sql += "and r.addedById = ? \n";
			sqlArgs.add(userId);
		}

		if (args.getStatus() != Status.ALL) {
			if (args.getStatus() == Status.ACTIVE) {
				sql += "and e.endDate > date_add(now(), interval -2 day) \n";
			} else {
				sql += "and e.endDate < now() \n";
			}
		}

		sql += "order by p.jobTitle, u.lastName, u.firstName";

		return query(sql, ServerUtils.getGenericRowMapper(), sqlArgs.toArray());
	}

	@Override
	public ArrayList<Event> list(ArgMap<EventArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		List<Integer> tagIds = args.getIntList(EventArg.HAS_TAGS);
		int upcoming = args.getInt(EventArg.UPCOMING_NUMBER);
		// boolean showCommunity = args.getBoolean(EventArg.ONLY_COMMUNITY);
		// boolean includeCommunity = args.getBoolean(EventArg.INCLUDE_COMMUNITY);
		int seriesId = args.getInt(EventArg.SERIES_ID);
		boolean newlyAdded = args.getBoolean(EventArg.NEWLY_ADDED);
		int registeredByOrAddedForId = args.getInt(EventArg.REGISTERED_BY_OR_ADDED_FOR_ID);
		int withinMiles = args.getInt(EventArg.WITHIN_MILES);
		String withinLat = args.getString(EventArg.WITHIN_LAT);
		String withinLng = args.getString(EventArg.WITHIN_LNG);
		String distanceCols = "";

		if (withinMiles > 0 && !Common.isNullOrBlank(withinLat) && !Common.isNullOrBlank(withinLng)) {
			distanceCols = ServerUtils.getDistanceSql("e", withinMiles, withinLat, withinLng);
		}

		String sql = createSqlBase(distanceCols);

		if (args.getStatus() != Status.ALL) {
			if (args.getStatus() == Status.ACTIVE) {
				sql += "and e.endDate > now() \n";
			} else {
				sql += "and e.endDate < now() \n";
			}
		}

		if (!args.getBoolean(EventArg.SHOW_INACTIVE)) {
			sql += "and e.active = 1 \n";
		}

		if (!Common.isNullOrEmpty(tagIds)) {
			sql += "and e.id in(select tm.eventId from tagEventMapping tm ";
			sql += "join tags t on t.id = tm.tagId ";
			sql += "where t.id in(" + Common.join(tagIds, ", ") + ")) ";
		}

		if (!(seriesId > 0)) {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			ids.add(ServerContext.getCurrentOrgId());
			if (ServerContext.isAuthenticated()) {
				ids.addAll(ServerContext.getCurrentUser().getOrganizationIds());
			}

			// String idString = Common.join(ids, ", ");

			// if (showCommunity) {
			// sql += "and (e.categoryId = 6 or e.owningOrgId not in(" + idString + ")) ";
			// } else if (!includeCommunity) {
			// sql += "and (e.categoryId != 6 and e.owningOrgId in(" + idString + ")) ";
			// }
		}

		if (seriesId > 0) {
			sql += "and e.seriesId = ? ";
			sqlArgs.add(seriesId);
		}

		if (registeredByOrAddedForId > 0) {
			sql += "and (e.id in(select distinct r.eventId from eventRegistrations r ";
			sql += "join eventRegistrationParticipants p on p.eventRegistrationId = r.id and p.statusId in(1, 2) where r.addedById = ? or p.userId = ?)) \n";
			sqlArgs.add(registeredByOrAddedForId);
			sqlArgs.add(registeredByOrAddedForId);
		}

		if (newlyAdded) {
			sql += "and (e.addedDate >= date_add(now(), interval -2 week) and (e.seriesId = e.id or e.seriesId is null)) \n";
		}

		if (!Common.isNullOrBlank(distanceCols)) {
			sql += "having distance < " + withinMiles + " ";
		}

		sql += "order by e.directoryPriority desc, e.startDate ";
		if (upcoming > 0) {
			sql += "limit ? ";
			sqlArgs.add(upcoming);
		}

		ArrayList<Event> data = query(sql, new EventMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public void overrideParticipantStatus(EventParticipant participant) {
		String sql = "update eventRegistrationParticipants set statusId = ? where id = ?";
		update(sql, participant.getStatusId(), participant.getId());
	}

	@Override
	public PaypalData payForEvents(ArrayList<Integer> participantIds) {
		if (Common.isNullOrEmpty(participantIds)) {
			return null;
		}
		ArgMap<EventArg> args = new ArgMap<EventArg>(EventArg.PARTICIPANT_IDS, participantIds);
		// fetch the participants because client prices can't be trusted
		List<EventParticipant> participants = getParticipants(args);
		double principal = 0.00;
		double markup = 0.00;
		for (EventParticipant p : participants) {
			principal += p.getPrice();
			markup += p.getMarkup();
		}

		// add a payment record
		PaymentDao paymentDao = ServerContext.getDaoImpl("payment");
		Payment p = new Payment();
		p.setUserId(ServerContext.getCurrentUserId());
		p.setPaymentTypeId(1);
		p.setStatusId(1);
		p.setPrincipalAmount(principal);
		p.setMarkupAmount(markup);
		p.setReturnPage("User&tab=1&userId=" + ServerContext.getCurrentUserId());
		p.setMemo("Payment for events");
		p = paymentDao.save(p);

		String sql = "update eventRegistrationParticipants set paymentId = ?";
		// for zero or negative payments, update status now because there will be no ipn
		if (p.getTotalAmount() <= 0) {
			sql += ", statusId = 2 ";
		}
		sql += " where id in(" + Common.join(participantIds, ", ") + ")";
		update(sql, p.getId());

		return p.getPaypalData();
	}

	@Override
	public Event save(Event event) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(event);

		if (event.getPrice() > 0 && event.getCategoryId() != 6) {
			event.setMarkup(Common.getEventMarkup(event.getPrice(), event));
		}

		if (event.isSaved()) {
			String sql = "update events set title = :title, description = :description, startDate = :startDate, endDate = :endDate, visibilityLevelId = :visibilityLevelId, ";
			sql += "addedDate = :addedDate, groupId = :groupId, categoryId = :categoryId, cost = :cost, adultRequired = :adultRequired, markup = :markup, ";
			sql += "markupOverride = :markupOverride, markupPercent = :markupPercent, markupDollars = :markupDollars, facilityName = :facilityName, ";
			sql += "registrationStartDate = :registrationStartDate, registrationEndDate = :registrationEndDate, sendSurvey = :sendSurvey, ";
			sql += "minimumParticipants = :minimumParticipants, maximumParticipants = :maximumParticipants, requiresRegistration = :requiresRegistration, ";
			sql += "address = :address, street = :street, city = :city, state = :state, zip = :zip, lat = :lat, lng = :lng, ";
			sql += "registrationInstructions = :registrationInstructions, seriesId = :seriesId, requiredInSeries = :requiredInSeries, directoryPriority = :directoryPriority, ";
			sql += "contactName = :contactName, contactEmail = :contactEmail, ";
			sql += "notificationEmail = :notificationEmail, publishDate = :publishDate, active = :active, price = :price, phone = :phone, website = :website ";
			sql += "where id = :id";
			update(sql, namedParams);

			if (event.getMarkupChanged()) {
				ArrayList<EventAgeGroup> groups = getAgeGroups(event.getId());
				if (!Common.isNullOrEmpty(groups)) {
					for (EventAgeGroup group : groups) {
						saveAgeGroup(group, event);
					}
				}
			}
		} else {
			event.setRegistrationStartDate(new Date());
			event.setRegistrationEndDate(event.getStartDate());
			event.setAddedById(ServerContext.getCurrentUserId());
			event.setOwningOrgId(ServerContext.getCurrentOrgId());

			String sql = "insert into events (title, description, addedById, startDate, endDate, addedDate, groupId, categoryId, cost, adultRequired, markup, ";
			sql += "markupOverride, markupPercent, markupDollars, facilityName, directoryPriority, contactName, contactEmail, ";
			sql += "registrationStartDate, registrationEndDate, sendSurvey, minimumParticipants, maximumParticipants, notificationEmail, owningOrgId, ";
			sql += "address, street, city, state, zip, lat, lng, ";
			sql += "publishDate, active, price, requiresRegistration, phone, website, visibilityLevelId, registrationInstructions, seriesId, requiredInSeries) values ";
			sql += "(:title, :description, :addedById, :startDate, :endDate, now(), :groupId, :categoryId, :cost, :adultRequired, :markup, ";
			sql += ":markupOverride, :markupPercent, :markupDollars, :facilityName, :directoryPriority, :contactName, :contactEmail, ";
			sql += ":registrationStartDate, :registrationEndDate, :sendSurvey, :minimumParticipants, :maximumParticipants, :notificationEmail, :owningOrgId, ";
			sql += ":address, :street, :city, :state, :zip, :lat, :lng, ";
			sql += ":publishDate, :active, :price, :requiresRegistration, :phone, :website, :visibilityLevelId, :registrationInstructions, :seriesId, :requiredInSeries)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			event.setId(ServerUtils.getIdFromKeys(keys));

			if (event.getCloneFromId() > 0) {
				// clone age groups
				sql = "insert into eventAgeGroups (eventId, minimumAge, maximumAge, minimumParticipants, maximumParticipants, price, markup, clonedFromId) ";
				sql += "select ?, minimumAge, maximumAge, minimumParticipants, maximumParticipants, price, markup, id ";
				sql += "from eventAgeGroups where eventId = ? order by id";
				update(sql, event.getId(), event.getCloneFromId());

				// clone fields -- start by fetching all the age groups we just added, along with the ids of the age groups they were cloned from
				sql = "select id, clonedFromId from eventAgeGroups where eventId = ?";
				List<Data> ids = query(sql, ServerUtils.getGenericRowMapper(), event.getId());
				// loop through each age group cloning all of its fields
				for (Data d : ids) {
					sql = "insert into eventFields (eventAgeGroupId, name, eventFieldTypeId, required, options, eventId)";
					sql += "select ?, name, eventFieldTypeId, required, options, ? from eventFields where eventAgeGroupId = ?";
					update(sql, d.getId(), event.getId(), d.getInt("clonedFromId"));
				}

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
	public EventAgeGroup saveAgeGroup(EventAgeGroup ageGroup, Event event) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(ageGroup);

		if (ageGroup.getPrice() > 0) {
			ageGroup.setMarkup(Common.getEventMarkup(ageGroup.getPrice(), event));
		}

		if (ageGroup.isSaved()) {
			String sql = "update eventAgeGroups set minimumAge = :minimumAge, maximumAge = :maximumAge, minimumParticipants = :minimumParticipants, ";
			sql += "maximumParticipants = :maximumParticipants, price = :price, markup = :markup where id = :id";
			update(sql, namedParams);
		} else {
			String sql = "insert into eventAgeGroups (eventId, minimumAge, maximumAge, minimumParticipants, maximumParticipants, price, markup) ";
			sql += "values(:eventId, :minimumAge, :maximumAge, :minimumParticipants, :maximumParticipants, :price, :markup)";

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
	public synchronized ServerResponseData<ArrayList<EventParticipant>> saveParticipant(final EventParticipant participant) {
		return saveParticipant(participant, true);
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
				p.setUpdateAllInSeries(true);
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

			Event e = getById(registration.getEventId());
			if (!Common.isNullOrBlank(e.getNotificationEmail()) && !e.isSeriesChild()) {
				UserDao userDao = ServerContext.getDaoImpl("user");
				User u = userDao.getById(registration.getAddedById());
				Mailer mailer = new Mailer();
				if (e.getNotificationEmail().contains(",")) {
					String[] addrs = e.getNotificationEmail().split(", *");
					for (String a : addrs) {
						if (!a.trim().isEmpty()) {
							mailer.addTo(a.trim());
						}
					}
				} else {
					mailer.addTo(e.getNotificationEmail());
				}
				String subject = "Event registration notification: " + e.getTitle();

				String body = "Registrant: " + u.getFullName() + "\n";
				body += "Event: " + e.getTitle() + "\n";
				body += "Date: " + e.getStartDate() + " to " + e.getEndDate() + "\n";
				body += "Link: " + ServerContext.getBaseUrl() + "#" + PageUrl.event(e.getId()) + "\n";
				mailer.setSubject(subject);
				mailer.setBody(body);
				mailer.send();
			}
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

		sql = "select m.*, r.addedById from eventVolunteerMapping m ";
		sql += "join eventRegistrations r on r.id = m.eventRegistrationId ";
		sql += "where m.id = ?";
		Data mapping = queryForObject(sql, ServerUtils.getGenericRowMapper(), id);

		if (!fulfilled) {
			sql = "delete from adjustments where userId = ? and adjustmentTypeId = 2 and linkId = ?";
			update(sql, mapping.getInt("addedById"), mapping.getInt("eventVolunteerPositionId"));
		} else {
			sql = "select * from eventVolunteerPositions where id = ?";
			Data position = queryForObject(sql, ServerUtils.getGenericRowMapper(), mapping.getInt("eventVolunteerPositionId"));
			if (position.getDouble("discount") > 0) {
				sql = "insert into adjustments (adjustmentTypeId, userId, linkId, amount, statusId) values(2, ?, ?, ?, 1)";
				update(sql, mapping.getInt("addedById"), mapping.getInt("eventVolunteerPositionId"), position.getDouble("discount") * -1);
			}
		}
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
		return createSqlBase("");
	}

	private String createSqlBase(String specialCols) {
		int userId = ServerContext.getCurrentUserId();
		String sql = "select e.*, g.groupName, c.category, u.firstName, u.lastName, l.visibilityLevel, \n";
		sql += "gg.markupPercent as groupMarkupPercent, gg.markupDollars as groupMarkupDollars, gg.markupOverride as groupMarkupOverride, \n";
		if (!Common.isNullOrBlank(specialCols)) {
			sql += specialCols;
		}
		sql += "(select group_concat(price + markup) from eventAgeGroups where eventId = e.id) as agePrices, \n";
		sql += "(select group_concat(concat(minimumAge, '-', maximumAge)) from eventAgeGroups where eventId = e.id) as ageRanges, \n";
		if (ServerContext.isAuthenticated()) {
			sql += "(select count(p.id) from eventRegistrationParticipants p join eventRegistrations r on r.id = p.eventRegistrationId and r.addedById = "
					+ userId + " where r.eventId = e.id and p.statusId != 5) as currentUserParticipantCount, \n";
		} else {
			sql += "0 as currentUserParticipantCount, ";
		}
		sql += "(select count(id) from documentEventMapping where eventId = e.id) as documentCount, \n";
		sql += "(e.addedDate >= date_add(now(), interval -2 week) and (e.seriesId = e.id or e.seriesId is null)) as newlyAdded, \n";
		sql += "t.imageId, t.smallImageId, d.fileExtension, \n";
		sql += "(e.endDate < now()) as finished, isActive(e.registrationStartDate, e.registrationEndDate) as registrationOpen \n";
		sql += "from events e \n";
		sql += "left join groups g on g.id = e.groupId \n";
		sql += "left join tags t on t.id = e.firstTagId \n";
		sql += "left join documents d on d.id = t.imageId \n";
		sql += "join groups gg on gg.id = e.owningOrgId \n";
		sql += "join eventCategories c on c.id = e.categoryId \n";
		sql += "join users u on u.id = e.addedById \n";
		sql += "join itemVisibilityLevels l on l.id = e.visibilityLevelId \n";

		sql += createWhere();

		return sql;
	}

	private boolean eventIsFull(Data info) {
		if (info == null) {
			return false;
		}
		return info.getInt("maxParticipants") > 0 && (info.getInt("participants") >= info.getInt("maxParticipants"));
	}

	private ArrayList<EventAgeGroup> getAgeGroups(int eventId) {
		String sql = "select a.*, (select count(id) from eventRegistrationParticipants where ageGroupId = a.id) as registerCount, ";
		sql += "(select count(id) from eventFields where eventAgeGroupId = a.id) as fieldCount ";
		sql += "from eventAgeGroups a where a.eventId = ? order by a.minimumAge";
		return query(sql, new RowMapper<EventAgeGroup>() {
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
				g.setMarkup(rs.getDouble("markup"));
				g.setRegisterCount(rs.getInt("registerCount"));
				g.setFieldCount(rs.getInt("fieldCount"));
				return g;
			}
		}, eventId);
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
		sql += "case when p.ageGroupId is null then e.maximumParticipants else g.maximumParticipants end as maxParticipants, \n";
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
		sql += "date_format(e.endDate, '%W, %c/%e %l:%i %p') as endDate, e.notificationEmail ";
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
		mailer.addTo(notify.get("notificationEmail"));
		String subject = "Event wait list notification: " + notify.get("title");
		String body = "Hello,\n\n";
		body += "Due to a cancellation, " + notify.get("participantName") + " has been moved off the waiting list for the following event.\n\n";
		body += "Event: " + notify.get("title") + "\n";
		body += "Date: " + notify.get("startDate") + " to " + notify.get("endDate") + "\n";
		body += "Link: " + ServerContext.getBaseUrl() + "#" + PageUrl.event(notify.getInt("eventId")) + "\n\n";
		body += "Click the link above to view more event details and registration status.\n\n";
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

	private synchronized ServerResponseData<ArrayList<EventParticipant>> saveParticipant(final EventParticipant participant, boolean validateOverlaps) {
		ServerResponseData<ArrayList<EventParticipant>> data = new ServerResponseData<ArrayList<EventParticipant>>();

		String sql = "";
		if (participant.getStatusId() == 0) {
			participant.setStatusId(1);
		}

		ArgMap<EventArg> args = new ArgMap<EventArg>();
		int ageGroupId = participant.getAgeGroupId() == null ? 0 : participant.getAgeGroupId();

		if (ageGroupId == 0) {
			int eventId = queryForInt(0, "select eventId from eventRegistrations where id = ?", participant.getEventRegistrationId());
			args.put(EventArg.EVENT_ID, eventId);
		} else {
			args.put(EventArg.AGE_GROUP_ID, ageGroupId);
		}
		Data waitData = getWaitData(args);
		boolean eventIsFull = eventIsFull(waitData);

		if (participant.getStatusId() == 1 && eventIsFull) {
			participant.setStatusId(3);
		}

		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(participant);
		if (participant.isSaved()) {
			// get the old status
			sql = "select statusId from eventRegistrationParticipants where id = :id";
			int oldStatusId = queryForInt(0, sql, namedParams);

			// if we're un-canceling and we have already paid, go back to paid status
			if (oldStatusId == 5 && participant.getStatusId() != 5 && participant.getPaymentStatusId() == 2) {
				participant.setStatusId(2);
			}

			sql = "update eventRegistrationParticipants set statusId = :statusId where id = :id ";
			update(sql, namedParams);

			if (oldStatusId != 3 && participant.getStatusId() == 5 && eventIsFull && (waitData.getInt("participants") == waitData.getInt("maxParticipants"))) {
				registerNextWaitingParticipant(waitData);
			}

			if (participant.getStatusId() == 5 && oldStatusId != 5) {
				sql = "select notificationEmail from events where id = ?";
				String email = queryForObject(sql, ServerUtils.getStringRowMapper(), participant.getEventId());
				if (!Common.isNullOrBlank(email)) {
					Mailer mailer = new Mailer();
					if (email.contains(",")) {
						String[] addrs = email.split(", *");
						for (String a : addrs) {
							if (!a.trim().isEmpty()) {
								mailer.addTo(a.trim());
							}
						}
					} else {
						mailer.addTo(email);
					}
					String subject = "Event registration CANCELED: " + participant.getEventTitle();

					String body = "Registrant: " + participant.getFirstName() + " " + participant.getLastName() + "\n";
					body += "Event: " + participant.getEventTitle() + "\n";
					body += "Date: " + participant.getEventStartDate() + " to " + participant.getEventEndDate() + "\n";
					body += "Link: " + ServerContext.getBaseUrl() + "#" + PageUrl.event(participant.getEventId()) + "\n";
					mailer.setSubject(subject);
					mailer.setBody(body);
					mailer.send();
				}
			}
		} else {
			// validate for time overlaps
			// get the event date being registered first
			sql = "select e.startDate, e.endDate \n";
			sql += "from eventRegistrations r \n";
			sql += "join events e on e.id = r.eventId \n";
			sql += "where r.id = ? limit 1";
			Data event = queryForObject(sql, new RowMapper<Data>() {
				@Override
				public Data mapRow(ResultSet rs, int row) throws SQLException {
					Data d = new Data();
					d.put("startDate", rs.getTimestamp("startDate"));
					d.put("endDate", rs.getTimestamp("endDate"));
					return d;
				}
			}, participant.getEventRegistrationId());

			// now see if it overlaps
			sql = "select e.id, e.title \n";
			sql += "from eventRegistrationParticipants p \n";
			sql += "join eventRegistrations r on r.id = p.eventRegistrationId \n";
			sql += "join events e on e.id = r.eventId \n";
			sql += "where p.statusId != 5 and e.active = 1 \n";
			sql += "and ((? >= e.startDate and ? < e.endDate) \n";
			sql += "or (? > e.startDate and ? <= e.endDate)) \n";
			sql += "and userId = ? limit 1";
			Data existing = queryForObject(sql, ServerUtils.getGenericRowMapper(), event.getDate("startDate"), event.getDate("startDate"),
					event.getDate("endDate"), event.getDate("endDate"), participant.getUserId());

			if (existing != null) {
				String m = "This participant is already registered for an event that conflicts with this one: ";
				m += "<a href=\"" + ServerContext.getBaseUrl() + "#" + PageUrl.event(existing.getInt("id")) + "\">" + existing.get("title") + "</a>";
				if (validateOverlaps) {
					data.addError(m);
				} else {
					data.addWarning(m);
				}
				return data;
			}

			// create or update the associated user
			User u = participant.getUser();
			if (u == null) {
				u = new User();
			}
			u.setFirstName(participant.getFirstName());
			u.setLastName(participant.getLastName());
			u.setBirthDate(participant.getBirthDate());
			u.setSex(participant.getSex());
			if (u.getId() != ServerContext.getCurrentUserId()) {
				u.setParentId(ServerContext.getCurrentUserId());
				u.setHomePhone(ServerContext.getCurrentUser().getHomePhone());
				u.setAddress(ServerContext.getCurrentUser().getAddress());
				PrivacyPreference fam = ServerContext.getCurrentUser().getPrivacyPreference(PrivacyPreferenceType.FAMILY);
				u.setDirectoryOptOut(fam.getVisibilityLevel().equals(VisibilityLevel.PRIVATE));
			}

			UserDao userDao = ServerContext.getDaoImpl("user");
			u = userDao.save(u).getData();
			participant.setUserId(u.getId());
			participant.setUser(u);

			if (participant.getUserId() > 0 && participant.getUserId() == ServerContext.getCurrentUserId()) {
				ServerContext.getCurrentUser().setBirthDate(participant.getBirthDate());
			}

			sql = "insert into eventRegistrationParticipants(eventRegistrationId, userId, statusId, ageGroupId, addedDate) ";
			sql += "values(:eventRegistrationId, :userId, :statusId, :ageGroupId, now())";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			participant.setId(ServerUtils.getIdFromKeys(keys));

			if (participant.isWaiting()) {
				data.addWarning("This participant has been added to the waiting list because the event or age group is full.");
			}
		}

		// save dynamic fields if we have any
		if (!Common.isNullOrEmpty(participant.getEventFields())) {
			for (EventField f : participant.getEventFields()) {
				f.setParticipantId(participant.getId());
				saveFieldValue(f);
			}
		}

		ArrayList<EventParticipant> list = getParticipants(new ArgMap<EventArg>(EventArg.REGISTRATION_ID, participant.getEventRegistrationId()));

		// free events go straight to confirmed/paid
		if (participant.getStatusId() == 1) {
			for (EventParticipant p : list) {
				if (p.getId() == participant.getId() && p.getAdjustedPrice() == 0) {
					sql = "update eventRegistrationParticipants set statusId = 2 where id = ?";
					update(sql, p.getId());
					p.setStatusId(2);
					p.setStatus("Confirmed/Paid");
				}
			}
		}

		data.setData(list);

		if (!Common.isNullOrEmpty(participant.getSeriesEventIds())) {
			List<Integer> ids = new ArrayList<Integer>(participant.getSeriesEventIds());
			// TODO eventually we'll want to copy field values to the other events when registering
			// for now we just disable it
			participant.setEventFields(null);

			// this ensures we only enter this block once, avoiding infinite recursion
			participant.setSeriesEventIds(null);
			for (int eventId : ids) {
				int registrationId = queryForInt(0, "select id from eventRegistrations where eventId = ? and addedById = ?", eventId, ServerContext
						.getCurrentUser().getId());

				if (registrationId == 0) {
					EventRegistration r = new EventRegistration();
					r.setAddedById(ServerContext.getCurrentUser().getId());
					r.setEventId(eventId);
					r = saveRegistration(r);

					registrationId = r.getId();
				}

				participant.setEventRegistrationId(registrationId);

				if (participant.getAgeGroupId() != null) {
					// get current age group info
					Data d = queryForObject("select * from eventAgeGroups where id = ? limit 1", ServerUtils.getGenericRowMapper(), participant.getAgeGroupId());

					// use it to find the corresponding age group in series event we're on
					sql = "select id from eventAgeGroups where eventId = ? and minimumAge = ? and maximumAge = ? limit 1";
					int seriesAgeGroupId = queryForInt(0, sql, eventId, d.getInt("minimumAge"), d.getInt("maximumAge"));

					participant.setAgeGroupId(seriesAgeGroupId);
				}

				participant.setId(0);
				ServerResponseData<ArrayList<EventParticipant>> srd = saveParticipant(participant, false);

				if (srd.hasWarnings()) {
					data.addWarnings(srd.getWarnings());
				}
			}
		}

		// when canceling or restoring a required series registration
		if (participant.getUpdateAllInSeries()) {
			participant.setUpdateAllInSeries(false);

			sql = "select e.seriesId \n";
			sql += "from eventRegistrationParticipants p \n";
			sql += "join eventRegistrations r on r.id = p.eventRegistrationId \n";
			sql += "join events e on e.id = r.eventId \n";
			sql += "where e.seriesId is not null and e.requiredInSeries = 1 \n";
			sql += "and p.id = ? limit 1\n";
			int seriesId = queryForInt(0, sql, participant.getId());

			if (seriesId > 0) {
				sql = "select p.id \n";
				sql += "from eventRegistrationParticipants p \n";
				sql += "join eventRegistrations r on r.id = p.eventRegistrationId \n";
				sql += "join events e on e.id = r.eventId \n";
				sql += "where e.seriesId = ? \n";
				sql += "and p.userId = ? and p.id != ?";

				final List<Integer> ids = new ArrayList<Integer>();

				query(sql, new RowMapper<Void>() {
					@Override
					public Void mapRow(ResultSet rs, int row) throws SQLException {
						ids.add(rs.getInt("id"));
						return null;
					}
				}, seriesId, participant.getUserId(), participant.getId());

				if (!ids.isEmpty()) {
					for (int id : ids) {
						participant.setId(id);
						ServerResponseData<ArrayList<EventParticipant>> srd = saveParticipant(participant, false);

						if (srd.hasWarnings()) {
							data.addWarnings(srd.getWarnings());
						}
					}
				}
			}
		}

		return data;
	}

}
