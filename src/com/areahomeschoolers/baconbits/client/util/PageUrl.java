package com.areahomeschoolers.baconbits.client.util;

public class PageUrl {

	public static String article(int id) {
		String url = "page=Article";
		return (id == 0) ? url : url + "&articleId=" + id;
	}

	public static String articleGroup(String ids) {
		return "page=ArticleGroup&articleIds=" + ids;
	}

	public static String articleList() {
		return "page=ArticleList";
	}

	public static String book(int id) {
		String url = "page=Book";
		return (id == 0) ? url : url + "&bookId=" + id;
	}

	public static String bookManagement() {
		return "page=BookManagement";
	}

	public static String bookReceipt() {
		return "page=BookReceipt";
	}

	public static String bookSearch() {
		return "page=BookSearch";
	}

	public static String event(int id) {
		String url = "page=Event";
		return (id == 0) ? url : url + "&eventId=" + id;
	}

	public static String eventCalendar() {
		return "page=EventCalendar";
	}

	public static String eventList() {
		return "page=EventList";
	}

	public static String home() {
		return "page=Home";
	}

	public static String news(int id) {
		String url = "page=News";
		return (id == 0) ? url : url + "&newsId=" + id;
	}

	public static String payment() {
		return "page=Payment";
	}

	public static String registrationManagement() {
		return "page=RegistrationManagement";
	}

	public static String resource(int id) {
		String url = "page=Resource";
		return (id == 0) ? url : url + "&resourceId=" + id;
	}

	public static String resourceManagement() {
		return "page=ResourceManagement";
	}

	public static String tagManagement() {
		return "page=TagManagement";
	}

	public static String user(int id) {
		String url = "page=User";
		return (id == 0) ? url : url + "&userId=" + id;
	}

	public static String userGroup(int id) {
		String url = "page=UserGroup";
		return (id == 0) ? url : url + "&userGroupId=" + id;
	}

	public static String userGroupList() {
		return "page=UserGroupList";
	}

	public static String userList() {
		return "page=UserList";
	}

	public static String userTile() {
		return "page=UserTile";
	}

	private PageUrl() {

	}
}
