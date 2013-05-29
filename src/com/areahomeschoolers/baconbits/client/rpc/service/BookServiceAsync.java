package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;
import com.areahomeschoolers.baconbits.shared.dto.PaypalData;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BookServiceAsync {
	public void delete(Book book, AsyncCallback<Void> callback);

	public void getById(int bookId, AsyncCallback<Book> callback);

	public void getPageData(int bookId, AsyncCallback<BookPageData> callback);

	public void getSummaryData(ArgMap<BookArg> args, AsyncCallback<ArrayList<Data>> callback);

	public void list(ArgMap<BookArg> args, AsyncCallback<ArrayList<Book>> callback);

	public void save(Book book, AsyncCallback<Book> callback);

	public void sellBooks(ArrayList<Book> books, String email, AsyncCallback<Void> callback);

	public void signUpToSell(AsyncCallback<PaypalData> callback);
}
