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
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;

@Repository
public class BookDaoImpl extends SpringWrapper implements BookDao {

	private final class BookMapper implements RowMapper<Book> {
		@Override
		public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
			Book book = new Book();
			book.setId(rs.getInt("id"));
			book.setAgeLevelId(rs.getInt("ageLevelId"));
			book.setAgeLevel(rs.getString("ageLevel"));
			book.setCategory(rs.getString("category"));
			book.setCategoryId(rs.getInt("categoryId"));
			book.setPrice(rs.getDouble("price"));
			book.setStatus(rs.getString("status"));
			book.setStatusId(rs.getInt("statusId"));
			book.setUserFirstName(rs.getString("firstName"));
			book.setUserLastName(rs.getString("lastName"));
			book.setUserId(rs.getInt("userId"));
			book.setTitle(rs.getString("title"));
			return book;
		}
	}

	@Autowired
	public BookDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	public String createSqlBase() {
		String sql = "select b.*, bs.status, ba.ageLevel, bc.category, u.firstName, u.lastName \n";
		sql += "from books b \n";
		sql += "join users u on u.id = b.userId \n";
		sql += "join bookStatus bs on bs.id = b.statusId \n";
		sql += "join bookCategories bc on bc.id = b.categoryId \n";
		sql += "join bookAgeLevels ba on ba.id = b.ageLevelId \n";
		sql += "where 1 = 1 \n";

		return sql;
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

		sql = "select * from bookAgeLevels order by ageLevel";
		pd.setAgeLevels(query(sql, ServerUtils.getGenericRowMapper()));

		return pd;
	}

	@Override
	public ArrayList<Book> list(ArgMap<BookArg> args) {
		List<Object> sqlArgs = new ArrayList<Object>();
		int userId = args.getInt(BookArg.USER_ID);

		String sql = createSqlBase();
		if (userId > 0) {
			sql += "and b.userId = ? ";
			sqlArgs.add(ServerContext.getCurrentUserId());
		}

		ArrayList<Book> data = query(sql, new BookMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public Book save(Book book) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(book);

		if (book.isSaved()) {
			String sql = "update books set title = :title, userId = :userId, categoryId = :categoryId, statusId = :statusId, price = :price, ageLevelId = :ageLevelId ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			book.setUserId(ServerContext.getCurrentUser().getId());

			String sql = "insert into books (userId, title, categoryId, ageLevelId, statusId, price) values ";
			sql += "(:userId, :title, :categoryId, :ageLevelId, :statusId, :price)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			book.setId(ServerUtils.getIdFromKeys(keys));
		}

		return getById(book.getId());
	}

}
