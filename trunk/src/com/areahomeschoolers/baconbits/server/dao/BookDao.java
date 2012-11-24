package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;

public interface BookDao {
	public Book getById(int bookId);

	public ArrayList<Book> list(ArgMap<BookArg> args);

	public Book save(Book book);
}
