package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface Arg {
	public enum ArticleArg implements Arg, IsSerializable {
		MOST_RECENT, IDS
	}

	public enum DocumentArg implements Arg, IsSerializable {
		ARTICLE_ID, EVENT_ID
	}

	public enum EventArg implements Arg, IsSerializable {
		EVENT_ID, AGE_GROUP_ID, REGISTRATION_ID, PARTICIPANT_ID, UPCOMING_NUMBER, INCLUDE_FIELDS, PARENT_ID, USER_ID
	}

	public enum UserArg implements Arg, IsSerializable {
		USER_ID, PARENT_ID, NOT_ON_REGISTRATION_ID
	}

}
