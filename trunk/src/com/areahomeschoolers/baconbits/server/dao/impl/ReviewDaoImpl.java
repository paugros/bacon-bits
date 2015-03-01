package com.areahomeschoolers.baconbits.server.dao.impl;

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

import com.areahomeschoolers.baconbits.server.dao.ReviewDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.ReviewArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Review;
import com.areahomeschoolers.baconbits.shared.dto.Review.ReviewType;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

@Repository
public class ReviewDaoImpl extends SpringWrapper implements ReviewDao {
	private final class ReviewMapper implements RowMapper<Review> {
		@Override
		public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
			Review resource = new Review();
			resource.setId(rs.getInt("id"));
			resource.setUserId(rs.getInt("userId"));
			resource.setEndDate(rs.getTimestamp("endDate"));
			resource.setAddedDate(rs.getTimestamp("addedDate"));
			resource.setAddedByFullName(rs.getString("addedByFullName"));
			resource.setRating(rs.getInt("rating"));
			resource.setAnonymous(rs.getBoolean("anonymous"));
			String safeHtml = new SafeHtmlBuilder().appendEscaped(rs.getString("review")).toSafeHtml().asString();
			resource.setReview(Common.makeUrlsClickable(safeHtml).replaceAll("\\\n", "<br/>"));

			return resource;
		}
	}

	@Autowired
	public ReviewDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public ArrayList<Review> list(ArgMap<ReviewArg> args) {
		int userId = args.getInt(ReviewArg.USER_ID);
		int id = args.getInt(ReviewArg.ID);
		ReviewType mappingType = Common.isNullOrBlank(args.getString(ReviewArg.TYPE)) ? null : ReviewType.valueOf(args.getString(ReviewArg.TYPE));
		int entityId = args.getInt(ReviewArg.ENTITY_ID);

		List<Object> sqlArgs = new ArrayList<>();

		String sql = "select r.*, concat(u.firstName, ' ', u.lastName) as addedByFullName \n";
		sql += "from reviews r \n";
		sql += "join users u on u.id = r.userId \n";
		if (mappingType != null) {
			sql += "join review" + Common.ucWords(mappingType.toString()) + "Mapping rm on rm.reviewId = r.id \n";
			if (entityId > 0) {
				sql += "and rm." + mappingType.toString().toLowerCase() + "Id = ? \n";
				sqlArgs.add(entityId);
			}
		}
		sql += "where 1 = 1 ";

		if (id > 0) {
			sql += "and r.id = ? \n";
			sqlArgs.add(id);
		}

		if (userId > 0) {
			sql += "and r.addedById = ? ";
			sqlArgs.add(userId);
		}

		if (args.getStatus() != Status.ALL) {
			sql += "and r.endDate " + (args.getStatus() == Status.ACTIVE ? "is" : "is not") + " null \n";
		}

		sql += "order by r.addedDate desc";

		return query(sql, new ReviewMapper(), sqlArgs.toArray());
	}

	@Override
	public Review save(Review review) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(review);

		if (review.isSaved()) {
			String sql = "update reviews set review = :review, rating = :rating, endDate = :endDate, hiddenById = :hiddenById, anonymous = :anonymous ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			review.setUserId(ServerContext.getCurrentUserId());

			String sql = "insert into reviews (review, rating, userId, anonymous) values ";
			sql += "(:review, :rating, :userId, :anonymous)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			review.setId(ServerUtils.getIdFromKeys(keys));

			// add mapping
			sql = "insert into review" + Common.ucWords(review.getType().toString()) + "Mapping ";
			sql += "(reviewId, " + review.getType().toString().toLowerCase() + "Id) ";
			sql += "values(?, ?)";
			update(sql, review.getId(), review.getEntityId());
		}

		return list(new ArgMap<ReviewArg>(ReviewArg.ID, review.getId())).get(0);
	}

}
