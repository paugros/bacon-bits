package com.areahomeschoolers.baconbits.client.content.minimodules;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.shared.dto.Arg.BookArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Book;

public class NewBooksMiniModule extends BookMiniModule {
	private String title = "NEW BOOKS";

	public NewBooksMiniModule() {
		ArgMap<BookArg> args = new ArgMap<BookArg>(Status.ACTIVE);
		args.put(BookArg.ONLINE_ONLY);
		args.put(BookArg.NEW_NUMBER, 5);
		populate(title, args);
	}

	public NewBooksMiniModule(ArrayList<Book> books) {
		populate(title, books);
	}

}
