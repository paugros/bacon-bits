package com.areahomeschoolers.baconbits.client.util;


public class PageUrl {

	public static String home() {
		return "page=Home";
	}

	public static String user(int id) {
		String url = "page=User";
		return (id == 0) ? url : url + "&userId=" + id;
	}

	private PageUrl() {

	}
}
