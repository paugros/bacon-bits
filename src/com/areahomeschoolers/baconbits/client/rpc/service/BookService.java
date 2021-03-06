package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("service/book")
public interface BookService extends RemoteService {
	public Boolean addBookToCart(int bookId, int userId);

	public void delete(Book book);

	public Book fetchGoogleData(Book b);

	public Book getById(int bookId);

	public BookPageData getPageData(int bookId);

	public ArrayList<Data> getSummaryData(ArgMap<BookArg> args);

	public ArrayList<Book> list(ArgMap<BookArg> args);

	public void removeBookFromCart(int bookId, int userId);

	public Book save(Book book);

	public void sellBooks(ArrayList<Book> books, String email);

	public PaypalData signUpToSell(int groupOption);
}
