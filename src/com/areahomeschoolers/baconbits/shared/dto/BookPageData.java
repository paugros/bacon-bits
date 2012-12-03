package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class BookPageData implements IsSerializable {
	private ArrayList<Data> categories;
	private ArrayList<Data> ageLevels;

	public ArrayList<Data> getAgeLevels() {
		return ageLevels;
	}

	public ArrayList<Data> getCategories() {
		return categories;
	}

	public void setAgeLevels(ArrayList<Data> ageLevels) {
		this.ageLevels = ageLevels;
	}

	public void setCategories(ArrayList<Data> categories) {
		this.categories = categories;
	}

}
