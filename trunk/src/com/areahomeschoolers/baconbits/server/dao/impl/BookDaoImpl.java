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

import com.areahomeschoolers.baconbits.server.dao.BookDao;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;

@Repository
public class BookDaoImpl extends SpringWrapper implements BookDao {

	private final class BookMapper implements RowMapper<Book> {
		@Override
		public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
			Book book = new Book();
			book.setId(rs.getInt("id"));
			book.setGradeLevelId(rs.getInt("gradeLevelId"));
			book.setGradeLevel(rs.getString("gradeLevel"));
			book.setCategory(rs.getString("category"));
			book.setCategoryId(rs.getInt("categoryId"));
			book.setPrice(rs.getDouble("price"));
			book.setStatus(rs.getString("status"));
			book.setStatusId(rs.getInt("statusId"));
			book.setUserFirstName(rs.getString("firstName"));
			book.setUserLastName(rs.getString("lastName"));
			book.setUserId(rs.getInt("userId"));
			book.setTitle(rs.getString("title"));
			book.setNotes(rs.getString("notes"));
			book.setIsbn(rs.getString("isbn"));
			book.setConditionId(rs.getInt("conditionId"));
			book.setCondition(rs.getString("bookCondition"));
			book.setImageId(rs.getInt("imageId"));
			book.setSmallImageId(rs.getInt("smallImageId"));
			return book;
		}
	}

	@Autowired
	public BookDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	public String createSqlBase() {
		String sql = "select b.*, bs.status, ba.gradeLevel, bc.category, u.firstName, u.lastName, bo.bookCondition \n";
		sql += "from books b \n";
		sql += "join users u on u.id = b.userId \n";
		sql += "join bookStatus bs on bs.id = b.statusId \n";
		sql += "join bookCategories bc on bc.id = b.categoryId \n";
		sql += "join bookGradeLevels ba on ba.id = b.gradeLevelId \n";
		sql += "left join bookConditions bo on bo.id = b.conditionId \n";
		sql += "where 1 = 1 \n";

		return sql;
	}

	@Override
	public void delete(int bookId) {
		String sql = "delete from books where id = ?";
		update(sql, bookId);
	}

	@Override
	public Book getById(int bookId) {
		String sql = createSqlBase() + "and b.id = ?";

		return queryForObject(sql, new BookMapper(), bookId);
	}

	@Override
	public BookPageData getPageData(int bookId) {
		BookPageData pd = new BookPageData();

		String sql = "select * from bookCategories order by category";
		pd.setCategories(query(sql, ServerUtils.getGenericRowMapper()));

		sql = "select * from bookGradeLevels order by id";
		pd.setGradeLevels(query(sql, ServerUtils.getGenericRowMapper()));

		sql = "select * from bookConditions order by id";
		pd.setConditions(query(sql, ServerUtils.getGenericRowMapper()));

		sql = "select * from bookStatus order by id";
		pd.setStatuses(query(sql, ServerUtils.getGenericRowMapper()));

		return pd;
	}

	@Override
	public ArrayList<Data> getSummaryData(ArgMap<BookArg> args) {
		int statusId = args.getInt(BookArg.STATUS_ID);
		List<Object> sqlArgs = new ArrayList<Object>();

		String sql = "select count(b.id) as total, sum(b.price) as totalPrice, b.userId, u.firstName, u.lastName \n";
		sql += "from books b \n";
		sql += "join users u on u.id = b.userId \n";
		sql += "where 1 = 1 \n";
		if (statusId > 0) {
			sql += "and b.statusId = ? ";
			sqlArgs.add(statusId);
		}
		sql += "group by u.firstName, u.lastName, b.userId ";
		sql += "order by u.firstName, u.lastName";

		return query(sql, ServerUtils.getGenericRowMapper(), sqlArgs.toArray());
	}

	@Override
	public ArrayList<Book> list(ArgMap<BookArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int statusId = args.getInt(BookArg.STATUS_ID);
		int userId = args.getInt(BookArg.USER_ID);
		int categoryId = args.getInt(BookArg.CATEGORY_ID);
		int gradeLevelId = args.getInt(BookArg.GRADE_LEVEL_ID);
		String priceBetween = args.getString(BookArg.PRICE_BETWEEN);

		String sql = createSqlBase();
		if (userId > 0) {
			sql += "and b.userId = ? ";
			sqlArgs.add(userId);
		}

		if (priceBetween != null) {
			String[] range = priceBetween.split("-");
			if (range.length == 2 && Common.isDouble(range[0]) && Common.isDouble(range[1])) {
				sql += "and b.price between ? and ? ";
				sqlArgs.add(Double.parseDouble(range[0]));
				sqlArgs.add(Double.parseDouble(range[1]));
			}
		}

		if (statusId > 0) {
			sql += "and b.statusId = ? ";
			sqlArgs.add(statusId);
		}

		if (categoryId > 0) {
			sql += "and b.categoryId = ? ";
			sqlArgs.add(categoryId);
		}

		if (gradeLevelId > 0) {
			sql += "and b.gradeLevelId = ? ";
			sqlArgs.add(gradeLevelId);
		}

		ArrayList<Book> data = query(sql, new BookMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public Book save(Book book) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(book);

		if (book.isSaved()) {
			String sql = "update books set title = :title, userId = :userId, categoryId = :categoryId, statusId = :statusId, price = :price, gradeLevelId = :gradeLevelId, ";
			sql += "isbn = :isbn, notes = :notes, conditionId = :conditionId, imageId = :imageId, smallImageId = :smallImageId ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			book.setUserId(ServerContext.getCurrentUser().getId());

			String sql = "insert into books (userId, title, categoryId, gradeLevelId, statusId, price, isbn, notes, conditionId, imageId, smallImageId) values ";
			sql += "(:userId, :title, :categoryId, :gradeLevelId, :statusId, :price, :isbn, :notes, :conditionId, 32, 34)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			book.setId(ServerUtils.getIdFromKeys(keys));
		}

		return getById(book.getId());
	}

}
