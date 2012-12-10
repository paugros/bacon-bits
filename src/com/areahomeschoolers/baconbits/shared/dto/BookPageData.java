package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BookPageData implements IsSerializable {
	private ArrayList<Data> categories;
	private ArrayList<Data> gradeLevels;

	public ArrayList<Data> getGradeLevels() {
		return gradeLevels;
	}

	public ArrayList<Data> getCategories() {
		return categories;
	}

	public void setGradeLevels(ArrayList<Data> gradeLevels) {
		this.gradeLevels = gradeLevels;
	}

	public void setCategories(ArrayList<Data> categories) {
		this.categories = categories;
	}

}
