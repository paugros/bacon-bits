package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class HomePageData implements IsSerializable {
	private ArrayList<Event> upcomingEvents;
	private ArrayList<Event> communityEvents;
	private ArrayList<Event> newlyAddedEvents;
	private ArrayList<Event> myUpcomingEvents;
	private ArrayList<Data> groups;
	private ArrayList<Resource> ads;
	private Article partners;
	private Article intro;
	private int eventCount;
	private int userCount;
	private int bookCount;
	private int resourceCount;
	private int articleCount;

	public HomePageData() {

	}

	public ArrayList<Resource> getAds() {
		return ads;
	}

	public int getArticleCount() {
		return articleCount;
	}

	public int getBookCount() {
		return bookCount;
	}

	public ArrayList<Event> getCommunityEvents() {
		return communityEvents;
	}

	public int getEventCount() {
		return eventCount;
	}

	public ArrayList<Data> getGroups() {
		return groups;
	}

	public Article getIntro() {
		return intro;
	}

	public ArrayList<Event> getMyUpcomingEvents() {
		return myUpcomingEvents;
	}

	public ArrayList<Event> getNewlyAddedEvents() {
		return newlyAddedEvents;
	}

	public Article getPartners() {
		return partners;
	}

	public int getResourceCount() {
		return resourceCount;
	}

	public ArrayList<Event> getUpcomingEvents() {
		return upcomingEvents;
	}

	public int getUserCount() {
		return userCount;
	}

	public void setAds(ArrayList<Resource> ads) {
		this.ads = ads;
	}

	public void setArticleCount(int articleCount) {
		this.articleCount = articleCount;
	}

	public void setBookCount(int bookCount) {
		this.bookCount = bookCount;
	}

	public void setCommunityEvents(ArrayList<Event> communityEvents) {
		this.communityEvents = communityEvents;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}

	public void setGroups(ArrayList<Data> groups) {
		this.groups = groups;
	}

	public void setIntro(Article intro) {
		this.intro = intro;
	}

	public void setMyUpcomingEvents(ArrayList<Event> myUpcomingEvents) {
		this.myUpcomingEvents = myUpcomingEvents;
	}

	public void setNewlyAddedEvents(ArrayList<Event> newlyAddedEvents) {
		this.newlyAddedEvents = newlyAddedEvents;
	}

	public void setPartners(Article partners) {
		this.partners = partners;
	}

	public void setResourceCount(int resourceCount) {
		this.resourceCount = resourceCount;
	}

	public void setUpcomingEvents(ArrayList<Event> upcomingEvents) {
		this.upcomingEvents = upcomingEvents;
	}

	public void setUserCount(int userCount) {
		this.userCount = userCount;
	}

}
