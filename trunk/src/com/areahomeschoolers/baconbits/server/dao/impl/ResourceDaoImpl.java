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

import com.areahomeschoolers.baconbits.server.dao.ResourceDao;
import com.areahomeschoolers.baconbits.server.dao.Suggestible;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ResourceArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Resource;
import com.areahomeschoolers.baconbits.shared.dto.ResourcePageData;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestionData;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

@Repository
public class ResourceDaoImpl extends SpringWrapper implements ResourceDao, Suggestible {

	private final class ResourceMapper implements RowMapper<Resource> {
		@Override
		public Resource mapRow(ResultSet rs, int rowNum) throws SQLException {
			Resource ad = new Resource();
			ad.setId(rs.getInt("id"));
			ad.setUrlDisplay(rs.getString("urlDisplay"));
			ad.setAddedById(rs.getInt("addedById"));
			ad.setStartDate(rs.getTimestamp("startDate"));
			ad.setEndDate(rs.getTimestamp("endDate"));
			ad.setAddedDate(rs.getTimestamp("addedDate"));
			ad.setName(rs.getString("name"));
			ad.setClickCount(rs.getInt("clickCount"));
			ad.setAddressScopeId(rs.getInt("addressScopeId"));
			ad.setLastClickDate(rs.getTimestamp("lastClickDate"));
			ad.setUrl(rs.getString("url"));
			ad.setImageId(rs.getInt("imageId"));
			ad.setSmallImageId(rs.getInt("smallImageId"));
			ad.setDescription(rs.getString("description"));
			ad.setPhone(rs.getString("phone"));
			ad.setCity(rs.getString("city"));
			ad.setZip(rs.getString("zip"));
			ad.setState(rs.getString("state"));
			ad.setAddress(rs.getString("address"));
			ad.setStreet(rs.getString("street"));
			ad.setLat(rs.getDouble("lat"));
			ad.setLng(rs.getDouble("lng"));
			ad.setShowInAds(rs.getBoolean("showInAds"));
			ad.setTagCount(rs.getInt("tagCount"));
			ad.setAddressScope(rs.getString("addressScope"));
			ad.setEmail(rs.getString("email"));
			ad.setImageExtension(rs.getString("fileExtension"));

			if (ad.getImageId() == null) {
				ad.setImageExtension(rs.getString("tagFileExtension"));
				ad.setImageId(rs.getInt("tagImageId"));
				ad.setSmallImageId(rs.getInt("tagSmallImageId"));
			}

			return ad;
		}
	}

	@Autowired
	public ResourceDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public void clickResource(int adId) {
		String sql = "update resources set clickCount = clickCount + 1, lastClickDate = now() where id = ?";
		update(sql, adId);

		sql = "insert into adClicks (resourceId, ipAddress, clickDate) values(?, ?, now())";
		update(sql, adId, ServerContext.getRequest().getRemoteAddr());
	}

	public String createSqlBase() {
		String sql = "select r.*, u.firstName, u.lastName, s.scope as addressScope, d.fileExtension, ";
		sql += "t.imageId as tagImageId, t.smallImageId as tagSmallImageId, dd.fileExtension as tagFileExtension, \n";
		sql += "(select count(id) from tagResourceMapping where resourceId = r.id) as tagCount \n";
		sql += "from resources r \n";
		sql += "join users u on u.id = r.addedById \n";
		sql += "left join tags t on t.id = r.firstTagId \n";
		sql += "left join documents dd on dd.id = t.imageId \n";
		sql += "left join addressScope s on s.id = r.addressScopeId \n";
		sql += "left join documents d on d.id = r.imageId \n";
		return sql;
	}

	@Override
	public Resource getById(int resourceId) {
		String sql = createSqlBase() + "where r.id = ?";

		return queryForObject(sql, new ResourceMapper(), resourceId);
	}

	@Override
	public int getCount() {
		String sql = "select count(*) from resources r " + TagDaoImpl.createWhere(TagMappingType.RESOURCE);

		return queryForInt(0, sql);
	}

	@Override
	public ResourcePageData getPageData(int resourceId) {
		ResourcePageData pd = new ResourcePageData();
		if (resourceId > 0) {
			pd.setResource(getById(resourceId));
		} else {
			pd.setResource(new Resource());
		}

		String sql = "select * from addressScope";
		pd.setAddressScopes(query(sql, ServerUtils.getGenericRowMapper()));

		return pd;
	}

	@Override
	public ServerSuggestionData getSuggestionData(String token, int limit, Data options) {
		ServerSuggestionData data = new ServerSuggestionData();

		String sql = "select r.id, r.name as Suggestion, 'Resource' as entityType ";
		sql += "from resources r ";
		sql += "where isActive(r.startDate, r.endDate) = 1 ";
		sql += "and r.name like ? ";
		sql += "order by r.name ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";
		data.setSuggestions(query(sql, ServerUtils.getSuggestionMapper(), search));
		return data;
	}

	@Override
	public ArrayList<Resource> list(ArgMap<ResourceArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		List<Integer> tagIds = args.getIntList(ResourceArg.HAS_TAGS);
		int id = args.getInt(ResourceArg.ID);
		int limit = args.getInt(ResourceArg.LIMIT);
		boolean random = args.getBoolean(ResourceArg.RANDOM);
		boolean ad = args.getBoolean(ResourceArg.AD);

		String sql = createSqlBase();
		sql += "where 1 = 1 ";

		if (args.getStatus() != Status.ALL) {
			sql += "and isActive(r.startDate, r.endDate) = " + (args.getStatus() == Status.ACTIVE ? "1" : "0") + " \n";
		}

		if (ad) {
			sql += "and r.showInAds = 1 ";
		}

		if (!Common.isNullOrEmpty(tagIds)) {
			sql += "and r.id in(select tm.resourceId from tagResourceMapping tm ";
			sql += "join tags t on t.id = tm.tagId ";
			sql += "where t.id in(" + Common.join(tagIds, ", ") + ")) ";
		}

		if (id > 0) {
			sql += "and r.id = ? ";
			sqlArgs.add(id);
		}

		if (random) {
			sql += "order by rand() ";
		}

		if (limit > 0) {
			sql += "limit " + limit;
		}

		ArrayList<Resource> data = query(sql, new ResourceMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public Resource save(Resource r) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(r);

		if (r.isSaved()) {
			String sql = "update resources set name = :name, startDate = :startDate, endDate = :endDate, url = :url, description = :description, showInAds = :showInAds, ";
			sql += "address = :address, street = :street, city = :city, state = :state, zip = :zip, lat = :lat, lng = :lng, phone = :phone, email = :email, ";
			sql += "urlDisplay = :urlDisplay, addressScopeId = :addressScopeId ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			if (r.getStartDate() == null) {
				r.setStartDate(new Date());
			}
			r.setAddedById(ServerContext.getCurrentUserId());

			String sql = "insert into resources (addedById, startDate, endDate, addedDate, name, url, description, ";
			sql += "address, street, city, state, zip, lat, lng, phone, showInAds, email, urlDisplay, addressScopeId) ";
			sql += "values(:addedById, :startDate, :endDate, now(), :name, :url, :description, ";
			sql += ":address, :street, :city, :state, :zip, :lat, :lng, :phone, :showInAds, :email, :urlDisplay, :addressScopeId)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			r.setId(ServerUtils.getIdFromKeys(keys));
		}

		return getById(r.getId());
	}

}
