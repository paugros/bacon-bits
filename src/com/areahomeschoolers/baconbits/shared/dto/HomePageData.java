package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class HomePageData implements IsSerializable {
	private ArrayList<Event> upcomingEvents;
	private ArrayList<Event> communityEvents;
	private ArrayList<Event> newlyAddedEvents;
	private ArrayList<Event> myUpcomingEvents;
	private ArrayList<Data> groups;
	private Article intro;

	public HomePageData() {

	}

	public ArrayList<Event> getCommunityEvents() {
		return communityEvents;
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

	public ArrayList<Event> getUpcomingEvents() {
		return upcomingEvents;
	}

	public void setCommunityEvents(ArrayList<Event> communityEvents) {
		this.communityEvents = communityEvents;
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

	public void setUpcomingEvents(ArrayList<Event> upcomingEvents) {
		this.upcomingEvents = upcomingEvents;
	}

}
