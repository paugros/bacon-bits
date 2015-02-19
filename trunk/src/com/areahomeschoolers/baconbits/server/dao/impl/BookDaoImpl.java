package com.areahomeschoolers.baconbits.server.dao.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
import com.areahomeschoolers.baconbits.shared.dto.ServerSuggestionData;
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
			book.setImageExtension(rs.getString("fileExtension"));
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
			book.setInMyShoppingCart(rs.getBoolean("inMyShoppingCart"));
			book.setShippingFrom(rs.getString("shippingFrom"));
			book.setAddedDate(rs.getTimestamp("addedDate"));
			book.setTags(rs.getString("tags"));
			book.setViewCount(rs.getInt("viewCount"));
			if (book.getImageId() == null) {
				book.setImageId(rs.getInt("tagImageId"));
				book.setSmallImageId(rs.getInt("tagSmallImageId"));
				book.setImageExtension(rs.getString("tagFileExtension"));
			}
			return book;
		}
	}

	private final Logger logger = Logger.getLogger(this.getClass().toString());
	private static final String GOOGLE_QUERY_URL = "https://www.googleapis.com/books/v1/volumes?country=US&printType=books&maxResults=12&key=AIzaSyBPxbeFCFBNAUxprA4_FSRcJ6AOVAQJr9A&q=";

	@Autowired
	public BookDaoImpl(DataSource dataSource) {
		super(dataSource);
	}

	@Override
	public Boolean addBookToCart(int bookId, int userId) {
		String sql = "select count(*) from books where statusId = 1 and id = ?";
		if (queryForInt(0, sql, bookId) == 0) {
			return false;
		}

		sql = "insert into bookShoppingCart(bookId, userId) values(?, ?)";
		try {
			update(sql, bookId, userId);
		} catch (DataIntegrityViolationException e) {
		}

		return true;
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
	public int getCount() {
		String sql = "select count(*) from books b ";
		// Double latD = ServerContext.getCurrentLat();
		// String lat = latD == null ? null : Double.toString(latD);
		// Double lngD = ServerContext.getCurrentLng();
		// String lng = lngD == null ? null : Double.toString(lngD);
		sql += TagDaoImpl.createWhere(TagMappingType.BOOK, 0, null, null, null);
		return queryForInt(0, sql);
	}

	@Override
	public BookPageData getPageData(int bookId) {
		BookPageData pd = new BookPageData();

		if (bookId > 0) {
			if (!ServerContext.isSystemAdministrator()) {
				update("update books set viewCount = viewCount + 1 where id = ?", bookId);
			}

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
	public ServerSuggestionData getSuggestionData(String token, int limit, Data options) {
		ServerSuggestionData data = new ServerSuggestionData();
		boolean googleLookup = options.getBoolean("googleLookup");

		if (googleLookup) {
			return getGoogleBookSuggestions(token);
		}

		String sql = "select b.id, b.title as Suggestion, 'Book' as entityType ";
		sql += "from books b ";
		sql += "join users u on u.id = b.userId ";
		sql += "where b.statusId = 1 and b.title like ? ";
		sql += "and b.userId in(select userId from userGroupMembers where groupId = " + Constants.ONLINE_BOOK_SELLERS_GROUP_ID + ") ";
		sql += "and isActive(u.startDate, u.endDate) = 1 ";
		sql += "order by b.title ";
		sql += "limit " + Integer.toString(limit + 1);

		String search = "%" + token + "%";
		data.setSuggestions(query(sql, ServerUtils.getSuggestionMapper(), search));
		return data;
	}

	@Override
	public ArrayList<Data> getSummaryData(ArgMap<BookArg> args) {
		int statusId = args.getInt(BookArg.STATUS_ID);
		boolean soldAtBookSale = args.getBoolean(BookArg.SOLD_AT_BOOK_SALE);
		boolean soldOnline = args.getBoolean(BookArg.SOLD_ONLINE);
		List<Object> sqlArgs = new ArrayList<Object>();

		String sql = "select bb.*, (select group_concat(groupName separator ', ') from groups g \n";
		sql += "join userGroupMembers ugm on ugm.groupId = g.id \n";
		sql += "where bb.userId = ugm.userId and g.id in(" + Constants.ONLINE_BOOK_SELLERS_GROUP_ID + ", " + Constants.PHYSICAL_BOOK_SELLERS_GROUP_ID
				+ ", 31)) as groups  from \n";
		sql += "(select count(b.id) as total, sum(b.price) as totalPrice, b.userId, u.firstName, u.lastName \n";
		sql += "from books b \n";
		sql += "join users u on u.id = b.userId \n";
		if (!ServerContext.isSystemAdministrator()) {
			sql += "join userGroupMembers ugm on ugm.userId = u.id and ugm.groupId = " + ServerContext.getCurrentOrgId() + " ";
		}
		sql += "where 1 = 1 \n";
		if (statusId > 0) {
			sql += "and b.statusId = ? \n";
			sqlArgs.add(statusId);
		}

		if (soldAtBookSale) {
			sql += "and b.soldAtBookSale = 1 and soldDate > date_add(now(), interval -6 month) \n";
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
		List<Integer> tagIds = args.getIntList(BookArg.HAS_TAGS);
		int statusId = args.getInt(BookArg.STATUS_ID);
		int userId = args.getInt(BookArg.USER_ID);
		int categoryId = args.getInt(BookArg.CATEGORY_ID);
		int gradeLevelId = args.getInt(BookArg.GRADE_LEVEL_ID);
		String priceBetween = args.getString(BookArg.PRICE_BETWEEN);
		boolean hideOffline = args.getBoolean(BookArg.ONLINE_ONLY);
		boolean inMyCart = args.getBoolean(BookArg.IN_MY_CART);
		int newNumber = args.getInt(BookArg.NEW_NUMBER);
		List<Integer> ids = args.getIntList(BookArg.IDS);
		boolean locationFilter = args.getBoolean(BookArg.LOCATION_FILTER);
		int withinMiles = ServerContext.getCurrentRadius();
		String withinLat = Double.toString(ServerContext.getCurrentLat());
		String withinLng = Double.toString(ServerContext.getCurrentLng());
		String loc = ServerContext.getCurrentLocation();
		String state = null;
		if (ServerContext.getCurrentLocation() != null && loc.length() == 2 && ServerContext.getCurrentLat() == 0) {
			state = loc;
		}
		String sql = createSqlBase();

		if (userId > 0) {
			sql += "and b.userId = ? ";
			sqlArgs.add(userId);
		}

		if (inMyCart) {
			sql += "and bsc.id is not null ";
		}

		if (locationFilter && !Common.isNullOrBlank(state) && state.matches("^[A-Z]{2}$")) {
			sql += "and u.state = '" + state + "' \n";
		}

		if (priceBetween != null) {
			String[] range = priceBetween.split("-");
			if (range.length == 2 && Common.isDouble(range[0]) && Common.isDouble(range[1])) {
				sql += "and b.price between ? and ? ";
				sqlArgs.add(Double.parseDouble(range[0]));
				sqlArgs.add(Double.parseDouble(range[1]));
			}
		}

		if (locationFilter && withinMiles > 0) {
			sql += "and " + ServerUtils.getDistanceSql("u", withinLat, withinLng) + " < " + withinMiles + " ";
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

		if (!Common.isNullOrEmpty(tagIds)) {
			sql += "and b.id in(select tm.bookId from tagBookMapping tm ";
			sql += "join tags t on t.id = tm.tagId ";
			sql += "where t.id in(" + Common.join(tagIds, ", ") + ")) ";
		}

		if (hideOffline) {
			sql += "and b.userId in(select userId from userGroupMembers where groupId = " + Constants.ONLINE_BOOK_SELLERS_GROUP_ID + ") ";
			sql += "and isActive(u.startDate, u.endDate) = 1 ";
		}

		if (newNumber > 0) {
			sql += "and date_add(now(), interval -2 week) < b.addedDate and statusId = 1 ";
		}

		if (ids != null && !ids.isEmpty()) {
			sql += "order by field(b.id, " + Common.join(ids, ", ") + ")";
		} else if (newNumber > 0) {
			sql += "order by b.addedDate desc limit " + newNumber;
		} else if (inMyCart) {
			sql += "order by b.userId ";
		} else {
			sql += "order by b.title ";
		}

		ArrayList<Book> data = query(sql, new BookMapper(), sqlArgs.toArray());

		return data;
	}

	@Override
	public void removeBookFromCart(int bookId, int userId) {
		update("delete from bookShoppingCart where userId = ? and bookId = ?", userId, bookId);
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
	public PaypalData signUpToSell(int groupOption) {
		PaymentDao paymentDao = ServerContext.getDaoImpl("payment");
		Payment p = new Payment();
		p.setUserId(ServerContext.getCurrentUserId());
		p.setPaymentTypeId(2);
		p.setStatusId(1);
		p.setPrincipalAmount(5);
		p.setReturnPage("Home");
		p.setMemo("Payment for book selling registration");
		p = paymentDao.save(p);

		ServerContext.getCache().put(Constants.BOOK_GROUP_OPTION_CACHE_KEY + ServerContext.getCurrentUserId(), groupOption);

		return p.getPaypalData();
	}

	private String createSqlBase() {
		String sql = "select b.*, bs.status, ba.gradeLevel, bc.category, case when bsc.id is null then 0 else 1 end as inMyShoppingCart, d.fileExtension, \n";
		sql += "t.imageId as tagImageId, t.smallImageId as tagSmallImageId, dd.fileExtension as tagFileExtension, \n";
		sql += "(select group_concat(t.name separator ', ') ";
		sql += "from tags t join tagBookMapping tm on tm.tagId = t.id where tm.bookId = b.id) as tags, \n";
		sql += "u.firstName, u.lastName, u.email, concat(u.city, ', ', u.state) as shippingFrom, bo.bookCondition \n";
		sql += "from books b \n";
		sql += "join users u on u.id = b.userId \n";
		sql += "join bookStatus bs on bs.id = b.statusId \n";
		sql += "join bookCategories bc on bc.id = b.categoryId \n";
		sql += "left join documents d on d.id = b.imageId \n";
		sql += "left join tags t on t.id = b.firstTagId \n";
		sql += "left join documents dd on dd.id = t.imageId \n";
		sql += "left join bookGradeLevels ba on ba.id = b.gradeLevelId \n";
		sql += "left join bookConditions bo on bo.id = b.conditionId \n";
		sql += "left join bookShoppingCart bsc on bsc.bookId = b.id and bsc.userId = " + ServerContext.getCurrentUserId() + " \n";
		sql += "where 1 = 1 \n";

		return sql;
	}

	private ServerSuggestionData getGoogleBookSuggestions(String token) {
		ServerSuggestionData sdata = new ServerSuggestionData();
		ArrayList<ServerSuggestion> results = new ArrayList<ServerSuggestion>();
		sdata.setSuggestions(results);

		try {
			String data = ServerUtils.getUrlContents(GOOGLE_QUERY_URL + "intitle:" + URLEncoder.encode(token, "UTF-8"));
			JsonElement jelement = new JsonParser().parse(data);

			if (jelement == null) {
				return sdata;
			}

			JsonArray items = jelement.getAsJsonObject().getAsJsonArray("items");

			Integer totalItems = getIntegerFromJsonObject(jelement.getAsJsonObject(), "totalItems");
			sdata.setTotalMatches(totalItems);

			if (items == null) {
				return sdata;
			}

			for (int i = 0; i < items.size(); i++) {
				JsonObject book = items.get(i).getAsJsonObject();
				JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");
				if (volumeInfo == null) {
					continue;
				}

				String title = ServerUtils.getStringFromJsonObject(volumeInfo, "title", 60);
				String date = ServerUtils.getStringFromJsonObject(volumeInfo, "publishedDate", 0);
				String publisher = ServerUtils.getStringFromJsonObject(volumeInfo, "publisher", 40);
				JsonArray authors = volumeInfo.getAsJsonArray("authors");

				String extra = "";

				if (date != null) {
					extra += date.substring(0, 4);
				}

				if (authors != null) {
					String authorText = " - by " + authors.get(0).getAsString();
					if (authorText.length() > 30) {
						authorText = authorText.substring(0, 30);
					}
					extra += authorText;
				}

				if (publisher != null) {
					if (!extra.isEmpty()) {
						extra += " - ";
					}
					extra += publisher;
				}

				if (!extra.isEmpty()) {
					title += "\n" + extra;
				}

				JsonArray isbns = volumeInfo.getAsJsonArray("industryIdentifiers");

				if (isbns == null) {
					continue;
				}

				String isbn = null;

				for (int j = 0; j < isbns.size(); j++) {
					JsonObject item = isbns.get(j).getAsJsonObject();
					String type = ServerUtils.getStringFromJsonObject(item, "type", 0);
					if (type == null) {
						continue;
					}

					if (type.startsWith("ISBN")) {
						isbn = ServerUtils.getStringFromJsonObject(item, "identifier", 0);
					}
				}

				if (isbn == null) {
					continue;
				}

				ServerSuggestion ss = new ServerSuggestion(title, i);
				ss.setStringId(isbn);

				results.add(ss);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		sdata.setSuggestions(results);
		return sdata;
	}

	private Integer getIntegerFromJsonObject(JsonObject obj, String key) {
		JsonElement item = obj.get(key);
		if (item != null) {
			return item.getAsInt();
		}

		return null;
	}

	private void populateAmazonPrices(Book b) throws Exception {
		String amazonUrl = "http://www.amazon.com/gp/search/?search-alias=stripbooks&field-isbn=" + b.getIsbn();
		b.setAmazonUrl(amazonUrl);

		URL url = new URL(amazonUrl);
		BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;

		StringBuffer buffer = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
		}
		reader.close();

		String newPrice = "";
		String usedPrice = "";

		Pattern p = Pattern.compile("<td class=\"toeOurPrice\">(.*?)</td>", Pattern.DOTALL);
		Matcher m = p.matcher(buffer.toString());
		if (m.find()) {
			newPrice = m.group(1);
		}

		if (newPrice.isEmpty() || !newPrice.contains("$")) {
			m.usePattern(Pattern.compile("<td class=\"toeNewPrice\">(.*?)</td>", Pattern.DOTALL));
			if (m.find()) {
				newPrice = m.group(1);
			}
		}

		m.usePattern(Pattern.compile("<td class=\"toeUsedPrice\">(.*?)</td>", Pattern.DOTALL));

		if (m.find()) {
			usedPrice = m.group(1);
		}

		if (!newPrice.isEmpty() && newPrice.contains("$")) {
			b.setAmazonNewPrice(newPrice);
		}

		if (!usedPrice.isEmpty() && usedPrice.contains("$")) {
			b.setAmazonUsedPrice(usedPrice);
		}
	}

	private Book populateGoogleInfo(Book b) {
		try {
			Book deadBook = new Book();
			deadBook.setIsbn(b.getIsbn());

			if (b.getIsbn() == null || b.getIsbn().isEmpty()) {
				return deadBook;
			}

			JsonElement jelement = new JsonParser().parse(ServerContext.getUrlContents(GOOGLE_QUERY_URL + "isbn:" + b.getIsbn()));
			if (jelement == null) {
				logger.warning("Root element was null when looking up Google Book info.");
				return deadBook;
			}

			JsonArray items = jelement.getAsJsonObject().getAsJsonArray("items");

			if (items == null) {
				logger.warning("Items element was null when looking up Google Book info.");
				return deadBook;
			}

			JsonObject book = items.get(0).getAsJsonObject();
			JsonObject volumeInfo = book.getAsJsonObject("volumeInfo");
			if (volumeInfo == null) {
				logger.warning("Volume Information was null when looking up Google Book info.");
				return deadBook;
			}

			b.setTitle(ServerUtils.getStringFromJsonObject(volumeInfo, "title", 200));
			b.setSubTitle(ServerUtils.getStringFromJsonObject(volumeInfo, "subtitle", 200));
			b.setPublisher(ServerUtils.getStringFromJsonObject(volumeInfo, "publisher", 200));
			b.setDescription(ServerUtils.getStringFromJsonObject(volumeInfo, "description", 10000));
			Integer pageCount = getIntegerFromJsonObject(volumeInfo, "pageCount");
			if (pageCount != null) {
				b.setPageCount(pageCount);
			}

			String dateText = ServerUtils.getStringFromJsonObject(volumeInfo, "publishedDate", 0);
			if (dateText != null) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				try {
					b.setPublishDate(df.parse(dateText));
				} catch (ParseException e) {
					b.setPublishDate(null);
				}
			} else {
				b.setPublishDate(null);
			}

			JsonArray authors = volumeInfo.getAsJsonArray("authors");

			if (authors != null) {
				String authorText = "";
				for (int i = 0; i < authors.size(); i++) {
					authorText += authors.get(i).getAsString() + "\n";
				}
				b.setAuthor(authorText);
			} else {
				b.setAuthor(null);
			}

			JsonArray categories = volumeInfo.getAsJsonArray("categories");

			if (categories != null) {
				String categoryText = "";
				for (int i = 0; i < categories.size(); i++) {
					categoryText += categories.get(i).getAsString() + "\n";
				}
				b.setGoogleCategories(categoryText);
			} else {
				b.setGoogleCategories(null);
			}

			JsonObject images = volumeInfo.getAsJsonObject("imageLinks");

			if (images != null) {
				String imageLink = ServerUtils.getStringFromJsonObject(images, "thumbnail", 0);
				b.setImageUrl(imageLink);
			} else {
				b.setImageUrl(null);
			}

		} catch (MalformedURLException e) {
			logger.warning("MalformedURLException:" + e.getMessage());
		} catch (IOException e) {
			logger.warning("IOException:" + e.getMessage());
		}

		try {
			populateAmazonPrices(b);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return b;
	}

}
