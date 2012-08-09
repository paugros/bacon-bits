package com.areahomeschoolers.baconbits.client.util;

public class PageUrl {

	public static String article(int id) {
		String url = "page=Article";
		return (id == 0) ? url : url + "&articleId=" + id;
	}

	public static String articleGroup(String ids) {
		return "page=ArticleGroup&articleIds=" + ids;
	}

	public static String event(int id) {
		String url = "page=Event";
		return (id == 0) ? url : url + "&eventId=" + id;
	}

	public static String eventList() {
		return "page=EventList";
	}

	public static String eventParticipantList() {
		return "page=EventParticipantList";
	}

	public static String eventPayment() {
		return "page=EventPayment";
	}

	public static String home() {
		return "page=Home";
	}

	public static String registrationManagement() {
		return "page=RegistrationManagement";
	}

	public static String user(int id) {
		String url = "page=User";
		return (id == 0) ? url : url + "&userId=" + id;
	}

	public static String userGroupList() {
		return "page=UserGroupList";
	}

	public static String userList() {
		return "page=UserList";
	}

	private PageUrl() {

	}
}
