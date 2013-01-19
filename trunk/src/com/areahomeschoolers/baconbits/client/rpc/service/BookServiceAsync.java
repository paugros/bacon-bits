package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BookServiceAsync {

	void delete(Book book, AsyncCallback<Void> callback);

	void getById(int bookId, AsyncCallback<Book> callback);

	void getPageData(int bookId, AsyncCallback<BookPageData> callback);

	void getSummaryData(ArgMap<BookArg> args, AsyncCallback<ArrayList<Data>> callback);

	void list(ArgMap<BookArg> args, AsyncCallback<ArrayList<Book>> callback);

	void save(Book book, AsyncCallback<Book> callback);

	void sellBooks(ArrayList<Book> books, String email, AsyncCallback<Void> callback);
}
