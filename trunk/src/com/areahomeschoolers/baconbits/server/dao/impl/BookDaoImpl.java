package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.areahomeschoolers.baconbits.server.dao.BookDao;
import com.areahomeschoolers.baconbits.server.dao.DocumentDao;
import com.areahomeschoolers.baconbits.server.dao.PaymentDao;
import com.areahomeschoolers.baconbits.server.dao.Suggestible;
import com.areahomeschoolers.baconbits.server.dao.TagDao;
import com.areahomeschoolers.baconbits.server.util.Mailer;
import com.areahomeschoolers.baconbits.server.util.ServerContext;
import com.areahomeschoolers.baconbits.server.util.ServerUtils;
import com.areahomeschoolers.baconbits.server.util.SpringWrapper;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.Arg.TagArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.Document;
import com.areahomeschoolers.baconbits.shared.dto.Document.DocumentLinkType;
import com.areahomeschoolers.baconbits.shared.dto.Payment;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestion;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Repository
public class BookDaoImpl extends SpringWrapper implements BookDao, Suggestible {
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
			book.setSoldAtBookSale(rs.getBoolean("soldAtBookSale"));
			book.setAuthor(rs.getString("author"));
			book.setSoldDate(rs.getTimestamp("soldDate"));
			book.setSubTitle(rs.getString("subTitle"));
			book.setPublisher(rs.getString("publisher"));
			book.setPublishDate(rs.getTimestamp("publishDate"));
			book.setDescription(rs.getString("description"));
			book.setPageCount(rs.getInt("pageCount"));
			book.setGoogleCategories(rs.getString("googleCategories"));
			return book;
		}
	}

	private final Logger logger = Logger.getLogger(this.getClass().toString());

	@Autowired
	public BookDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public void delete(Book book) {
		String sql = "delete from books where id = ?";
		update(sql, book.getId());

		sql = "delete from documents where id in(?, ?) and id > 34";
		update(sql, book.getSmallImageId(), book.getImageId());
	}

	@Override
	public Book fetchGoogleData(Book b) {
		return populateGoogleInfo(b);
	}

	@Override
	public Book getById(int bookId) {
		String sql = createSqlBase() + "and b.id = ?";

		return queryForObject(sql, new BookMapper(), bookId);
	}

	@Override
	public BookPageData getPageData(int bookId) {
		BookPageData pd = new BookPageData();

		if (bookId > 0) {
			pd.setBook(getById(bookId));

			// tags
			TagDao tagDao = ServerContext.getDaoImpl("tag");
			ArgMap<TagArg> tagArgs = new ArgMap<TagArg>(TagArg.ENTITY_ID, pd.getBook().getId());
			tagArgs.put(TagArg.MAPPING_TYPE, TagMappingType.BOOK.toString());
			pd.setTags(tagDao.list(tagArgs));
		}

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
	public ArrayList<ServerSuggestion> getSuggestions(String token, int limit, Data options) {
		String sql = "select b.id, b.title as Suggestion, 'Book' as entityType ";
		sql += "from books b ";
		sql += "where b.statusId = 1 and b.title like ? ";
		sql += "order by b.title ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";
		return query(sql, ServerUtils.getSuggestionMapper(), search);
	}

	@Override
	public ArrayList<Data> getSummaryData(ArgMap<BookArg> args) {
		int statusId = args.getInt(BookArg.STATUS_ID);
		boolean soldAtBookSale = args.getBoolean(BookArg.SOLD_AT_BOOK_SALE);
		boolean soldOnline = args.getBoolean(BookArg.SOLD_ONLINE);
		List<Object> sqlArgs = new ArrayList<Object>();

		String sql = "select bb.*, (select group_concat(groupName separator ', ') from groups g \n";
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

		if (soldAtBookSale) {
			sql += "and b.soldAtBookSale = 1 \n";
		}

		if (soldOnline) {
			sql += "and b.soldAtBookSale = 0 \n";
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
		int newNumber = args.getInt(BookArg.NEW_NUMBER);
		List<Integer> ids = args.getIntList(BookArg.IDS);
		int withinMiles = args.getInt(BookArg.WITHIN_MILES);
		String withinLat = args.getString(BookArg.WITHIN_LAT);
		String withinLng = args.getString(BookArg.WITHIN_LNG);
		String distanceCols = "";

		if (withinMiles > 0 && !Common.isNullOrBlank(withinLat) && !Common.isNullOrBlank(withinLng)) {
			distanceCols = ServerUtils.getDistanceSql("u", withinMiles, withinLat, withinLng);
		}

		String sql = createSqlBase(distanceCols);

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

		if (newNumber > 0) {
			sql += "and date_add(now(), interval -2 week) < b.addedDate ";
		}

		if (!Common.isNullOrBlank(distanceCols)) {
			sql += "having distance < " + withinMiles + " ";
		}

		if (newNumber > 0) {
			sql += "order by b.addedDate desc limit " + newNumber;
		}

		ArrayList<Book> data = query(sql, new BookMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public Book save(Book book) {
		SqlParameterSource namedParams = new BeanPropertySqlParameterSource(book);

		if (book.getStatusId() == 2 && queryForInt(0, "select statusId from books where id = ?", book.getId()) != 2) {
			book.setSoldDate(new Date());
		}

		if (book.isSaved()) {
			String sql = "update books set title = :title, userId = :userId, categoryId = :categoryId, statusId = :statusId, price = :price, gradeLevelId = :gradeLevelId, ";
			sql += "isbn = :isbn, notes = :notes, conditionId = :conditionId, imageId = :imageId, smallImageId = :smallImageId, author = :author, soldDate = :soldDate, ";
			sql += "subTitle = :subTitle, publisher = :publisher, publishDate = :publishDate, description = :description, pageCount = :pageCount, googleCategories = :googleCategories ";
			sql += "where id = :id";
			update(sql, namedParams);
		} else {
			book.setUserId(ServerContext.getCurrentUser().getId());

			book.setImageId(Constants.BLANK_BOOK_IMAGE);
			book.setSmallImageId(Constants.BLANK_BOOK_IMAGE_SMALL);
			String sql = "insert into books (userId, title, categoryId, gradeLevelId, statusId, price, isbn, notes, conditionId, imageId, smallImageId, author, ";
			sql += "subTitle, publisher, publishDate, description, pageCount, googleCategories) values ";
			sql += "(:userId, :title, :categoryId, :gradeLevelId, :statusId, :price, :isbn, :notes, :conditionId, :imageId, :smallImageId, :author, ";
			sql += ":subTitle, :publisher, :publishDate, :description, :pageCount, :googleCategories)";

			KeyHolder keys = new GeneratedKeyHolder();
			update(sql, namedParams, keys);

			book.setId(ServerUtils.getIdFromKeys(keys));
		}

		if (!Common.isNullOrBlank(book.getImageUrl())) {
			String text = book.getImageUrl().trim();
			if (!text.matches("^https?://.*")) {
				text = "http://" + text;
			}

			try {
				URL url = new URL(text);
				InputStream in = new BufferedInputStream(url.openStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int n = 0;
				while (-1 != (n = in.read(buf))) {
					out.write(buf, 0, n);
				}
				out.close();
				in.close();

				byte[] data = out.toByteArray();

				Image image = ImagesServiceFactory.makeImage(data);
				String ext = image.getFormat().toString().toLowerCase();

				Document doc = new Document();
				doc.setFileExtension(ext);
				doc.setFileType("image/" + ext);
				doc.setFileName("BookImage" + book.getId());
				doc.setLinkType(DocumentLinkType.BOOK);
				doc.setLinkId(book.getId());
				doc.setData(data);
				doc.setAddedById(ServerContext.getCurrentUserId());
				doc.setAddedDate(new Date());
				DocumentDao dao = ServerContext.getDaoImpl("document");
				dao.save(doc);
			} catch (Exception e) {
				e.printStackTrace();
			}

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

		sql = "update books set statusId = 2, boughtById = ?, soldAtBookSale = 1, soldDate = now() where id in(" + Common.join(ids, ",") + ")";
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

	@Override
	public PaypalData signUpToSell() {
		PaymentDao paymentDao = ServerContext.getDaoImpl("payment");
		Payment p = new Payment();
		p.setUserId(ServerContext.getCurrentUserId());
		p.setPaymentTypeId(2);
		p.setStatusId(1);
		p.setPrincipalAmount(5);
		p.setReturnPage("Home");
		p.setMemo("Payment for book selling registration");
		p = paymentDao.save(p);

		// if negative payment, don't wait for ipn
		if (p.getPrincipalAmount() <= 0 && !ServerContext.getCurrentUser().memberOf(Constants.BOOK_SELLERS_GROUP_ID)) {
			String sql = "insert into userGroupMembers (userId, groupId, isAdministrator) values(?, 16, 0)";
			update(sql, ServerContext.getCurrentUserId());
		}

		return p.getPaypalData();
	}

	private String createSqlBase() {
		return createSqlBase("");
	}

	private String createSqlBase(String specialCols) {
		String sql = "select b.*, bs.status, ba.gradeLevel, bc.category, ";
		if (!Common.isNullOrBlank(specialCols)) {
			sql += specialCols;
		}
		sql += "u.firstName, u.lastName, u.email, bo.bookCondition \n";
		sql += "from books b \n";
		sql += "join users u on u.id = b.userId \n";
		sql += "join bookStatus bs on bs.id = b.statusId \n";
		sql += "join bookCategories bc on bc.id = b.categoryId \n";
		sql += "join bookGradeLevels ba on ba.id = b.gradeLevelId \n";
		sql += "left join bookConditions bo on bo.id = b.conditionId \n";
		sql += "where 1 = 1 \n";

		return sql;
	}

	private Integer getIntegerFromJsonObject(JsonObject obj, String key) {
		JsonElement item = obj.get(key);
		if (item != null) {
			return item.getAsInt();
		}

		return null;
	}

	private String getStringFromJsonObject(JsonObject obj, String key, int maxLength) {
		JsonElement item = obj.get(key);
		if (item != null) {
			String text = item.getAsString();
			if (maxLength > 0 && text.length() > maxLength) {
				text = text.substring(0, maxLength);
			}
			return text;
		}

		return null;
	}

	private Book populateGoogleInfo(Book b) {
		logger.info("Attempting to fetch Google Book data.");
		try {
			URL url = new URL("https://www.googleapis.com/books/v1/volumes?q=isbn:" + b.getIsbn());
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;

			StringBuffer buffer = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			reader.close();

			JsonElement jelement = new JsonParser().parse(buffer.toString());
			if (jelement == null) {
				logger.info("Root element was null when looking up Google Book info.");
				return b;
			}

			JsonArray items = jelement.getAsJsonObject().getAsJsonArray("items");

			if (items == null) {
				logger.info("Items element was null when looking up Google Book info.");
				return b;
			}

			JsonObject book = items.get(0).getAsJsonObject();
			JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");
			if (volumeInfo == null) {
				logger.info("Volume Information was null when looking up Google Book info.");
				return b;
			}

			b.setTitle(getStringFromJsonObject(volumeInfo, "title", 200));
			b.setSubTitle(getStringFromJsonObject(volumeInfo, "subtitle", 200));
			b.setPublisher(getStringFromJsonObject(volumeInfo, "publisher", 200));
			b.setDescription(getStringFromJsonObject(volumeInfo, "description", 1000));
			b.setPageCount(getIntegerFromJsonObject(volumeInfo, "pageCount"));

			String dateText = getStringFromJsonObject(volumeInfo, "publishedDate", 0);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			try {
				b.setPublishDate(df.parse(dateText));
			} catch (ParseException e) {
			}

			JsonArray authors = volumeInfo.getAsJsonArray("authors");

			if (authors != null) {
				String authorText = "";
				for (int i = 0; i < authors.size(); i++) {
					authorText += authors.get(i).getAsString() + "\n";
				}
				b.setAuthor(authorText);
			}

			JsonArray categories = volumeInfo.getAsJsonArray("categories");

			if (categories != null) {
				String categoryText = "";
				for (int i = 0; i < categories.size(); i++) {
					categoryText += categories.get(i).getAsString() + "\n";
				}
				b.setGoogleCategories(categoryText);
			}

			JsonObject images = volumeInfo.getAsJsonObject("imageLinks");

			if (images != null) {
				String imageLink = getStringFromJsonObject(images, "thumbnail", 0);
				b.setImageUrl(imageLink);
			}

		} catch (MalformedURLException e) {
			logger.warning("MalformedURLException:" + e.getMessage());
		} catch (IOException e) {
			logger.warning("IOException:" + e.getMessage());
		}

		return b;
	}

}
