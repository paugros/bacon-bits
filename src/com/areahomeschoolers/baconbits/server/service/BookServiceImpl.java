package com.areahomeschoolers.baconbits.server.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.server.dao.BookDao;
import com.areahomeschoolers.baconbits.server.spring.GwtController;
import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

@Controller
@RequestMapping("/book")
public class BookServiceImpl extends GwtController implements BookService {
	private static final long serialVersionUID = 1L;

	private BookDao dao;

	@Autowired
	public BookServiceImpl(BookDao dao) {
		this.dao = dao;
	}

	@Override
	public Boolean addBookToCart(int bookId, int userId) {
		return dao.addBookToCart(bookId, userId);
	}

	@Override
	public void delete(Book book) {
		dao.delete(book);
	}

	@Override
	public Book fetchGoogleData(Book b) {
		return dao.fetchGoogleData(b);
	}

	@Override
	public Book getById(int bookId) {
		return dao.getById(bookId);
	}

	@Override
	public BookPageData getPageData(int bookId) {
		return dao.getPageData(bookId);
	}

	@Override
	public ArrayList<Data> getSummaryData(ArgMap<BookArg> args) {
		return dao.getSummaryData(args);
	}

	@Override
	public ArrayList<Book> list(ArgMap<BookArg> args) {
		return dao.list(args);
	}

	@Override
	public void removeBookFromCart(int bookId, int userId) {
		dao.removeBookFromCart(bookId, userId);
	}

	@Override
	public Book save(Book book) {
		return dao.save(book);
	}

	@Override
	public void sellBooks(ArrayList<Book> books, String email) {
		dao.sellBooks(books, email);
	}

	@Override
	public PaypalData signUpToSell(int groupOption) {
		return dao.signUpToSell(groupOption);
	}

}
