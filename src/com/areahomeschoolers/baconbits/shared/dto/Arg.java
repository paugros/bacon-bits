package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface Arg {
	public enum ArticleArg implements Arg, IsSerializable {
		MOST_RECENT
	}

	public enum DocumentArg implements Arg, IsSerializable {
		ARTICLE_ID, EVENT_ID
	}

	public enum EventArg implements Arg, IsSerializable {
		EVENT_ID, AGE_GROUP_ID, REGISTRATION_ID, REGISTRATION_PARTICIPANT_ID
	}

	public enum UserArg implements Arg, IsSerializable {
		USER_ID, PARENT_ID, NOT_ON_REGISTRATION_ID
	}

}
