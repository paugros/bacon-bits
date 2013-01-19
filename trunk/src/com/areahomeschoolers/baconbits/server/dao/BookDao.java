package com.areahomeschoolers.baconbits.server.dao;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.Book;
import com.areahomeschoolers.baconbits.shared.dto.BookPageData;
import com.areahomeschoolers.baconbits.shared.dto.Data;

public interface BookDao {
	public void delete(Book book);

	public Book getById(int bookId);

	public BookPageData getPageData(int bookId);

	public ArrayList<Data> getSummaryData(ArgMap<BookArg> args);

	public ArrayList<Book> list(ArgMap<BookArg> args);

	public Book save(Book book);

	public void sellBooks(ArrayList<Book> books, String email);
}
