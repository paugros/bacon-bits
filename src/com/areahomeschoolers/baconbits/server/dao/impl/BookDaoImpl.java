package com.areahomeschoolers.baconbits.server.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
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
import com.areahomeschoolers.baconbits.server.util.Mailer;
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
			book.setUserEmail(rs.getString("email"));
			return book;
		}
	}

	@Autowired
	public BookDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	public String createSqlBase() {
		String sql = "select b.*, bs.status, ba.gradeLevel, bc.category, u.firstName, u.lastName, u.email, bo.bookCondition \n";
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
	public void delete(Book book) {
		String sql = "delete from books where id = ?";
		update(sql, book.getId());

		sql = "delete from documents where id in(?, ?) and id > 34";
		update(sql, book.getSmallImageId(), book.getImageId());
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

		String sql = "select bb.*, (select group_concat(groupName) from groups g \n";
		sql += "join userGroupMembers ugm on ugm.groupId = g.id \n";
		sql += "where bb.userId = ugm.userId and g.id in(16, 17)) as groups  from \n";
		sql += "(select count(b.id) as total, sum(b.price) as totalPrice, b.userId, u.firstName, u.lastName \n";
		sql += "from books b \n";
		sql += "join users u on u.id = b.userId \n";
		sql += "where 1 = 1 \n";
		if (statusId > 0) {
			sql += "and b.statusId = ? \n";
			sqlArgs.add(statusId);
		}
		sql += "group by u.firstName, u.lastName, b.userId \n";
		sql += "order by u.firstName, u.lastName \n";
		sql += ") as bb";

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
		boolean hideOffline = args.getBoolean(BookArg.ONLINE_ONLY);
		List<Integer> ids = args.getIntList(BookArg.IDS);

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

		if (ids != null && !ids.isEmpty()) {
			sql += "and b.id in(" + Common.join(ids, ",") + ") ";
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

		if (hideOffline) {
			sql += "and b.userId in(select userId from userGroupMembers where groupId = 16) ";
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

	@Override
	public void sellBooks(ArrayList<Book> books, String email) {
		String sql = "";
		// check if email belongs to a registered user
		Data boughtBy = null;
		if (!Common.isNullOrBlank(email)) {
			sql = "select id, firstName from users where email = ?";
			boughtBy = queryForObject(sql, ServerUtils.getGenericRowMapper(), email.toLowerCase());
		}

		List<Integer> ids = new ArrayList<Integer>();
		String html = "<html><body>\n";
		if (boughtBy != null) {
			html += "Hello " + boughtBy.get("firstName") + ",<br><br>\n";
		}
		html += "Below is your receipt for items purchased at the book sale:<br><br>\n";
		html += "<table width=700><tr><td><b>ID</b></td><td><b>Title</b></td><td><b>Category</b></td><td><b>Price</b></td></tr>\n";
		double total = 0.00;
		for (Book b : books) {
			ids.add(b.getId());
			total += b.getPrice();
			html += "<tr>\n";
			html += "<td>" + b.getId() + "</td>\n";
			html += "<td>" + b.getTitle() + "</td>\n";
			html += "<td>" + b.getCategory() + "</td>\n";
			html += "<td>" + NumberFormat.getCurrencyInstance().format(b.getPrice()) + "</td>\n";
			html += "</tr>\n";
		}
		html += "<tr><td></td><td></td><td></td><td><b>";
		html += NumberFormat.getCurrencyInstance().format(total);
		html += "</b></td></tr>";
		html += "</table></body></html>\n";

		sql = "update books set statusId = 2, boughtById = ? where id in(" + Common.join(ids, ",") + ")";
		Integer bid = (boughtBy == null) ? null : boughtBy.getId();
		update(sql, bid);

		if (!Common.isNullOrBlank(email)) {
			Mailer m = new Mailer();
			m.setHtmlMail(true);

			m.setSubject("Book Sale Receipt");
			m.setBody(html);
			m.addTo(email);
			m.send();
		}
	}

}
