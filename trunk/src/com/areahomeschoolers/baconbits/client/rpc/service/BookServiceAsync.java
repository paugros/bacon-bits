package com.areahomeschoolers.baconbits.client.rpc.service;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BookServiceAsync {

	void getById(int bookId, AsyncCallback<Book> callback);

	void list(ArgMap<BookArg> args, AsyncCallback<ArrayList<Book>> callback);

	void save(Book book, AsyncCallback<Book> callback);
}
