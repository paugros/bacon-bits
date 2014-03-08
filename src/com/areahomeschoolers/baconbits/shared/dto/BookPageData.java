package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BookPageData implements IsSerializable {
	private Book book;
	private ArrayList<Data> categories;
	private ArrayList<Data> gradeLevels;
	private ArrayList<Data> conditions;
	private ArrayList<Data> statuses;

	public Book getBook() {
		return book;
	}

	public ArrayList<Data> getCategories() {
		return categories;
	}

	public ArrayList<Data> getConditions() {
		return conditions;
	}

	public ArrayList<Data> getGradeLevels() {
		return gradeLevels;
	}

	public ArrayList<Data> getStatuses() {
		return statuses;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public void setCategories(ArrayList<Data> categories) {
		this.categories = categories;
	}

	public void setConditions(ArrayList<Data> conditions) {
		this.conditions = conditions;
	}

	public void setGradeLevels(ArrayList<Data> gradeLevels) {
		this.gradeLevels = gradeLevels;
	}

	public void setStatuses(ArrayList<Data> statuses) {
		this.statuses = statuses;
	}

}
