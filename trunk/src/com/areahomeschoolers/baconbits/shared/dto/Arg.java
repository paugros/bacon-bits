package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface Arg {
	public enum ArticleArg implements Arg, IsSerializable {

	}

	public enum DocumentArg implements Arg, IsSerializable {

	}

	public enum EventArg implements Arg, IsSerializable {
		EVENT_ID, AGE_GROUP_ID, REGISTRATION_ID, REGISTRATION_PARTICIPANT_ID
	}

	public enum UserArg implements Arg, IsSerializable {
		USER_ID
	}

}