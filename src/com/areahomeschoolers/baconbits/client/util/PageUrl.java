package com.areahomeschoolers.baconbits.client.util;

public class PageUrl {

	public static String article(int id) {
		String url = "page=Article";
		return (id == 0) ? url : url + "&articleId=" + id;
	}

	public static String event(int id) {
		String url = "page=Event";
		return (id == 0) ? url : url + "&eventId=" + id;
	}

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