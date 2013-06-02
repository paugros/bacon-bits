package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface Arg {
	public enum ArticleArg implements Arg, IsSerializable {
		MOST_RECENT, IDS;
		private ArticleArg() {

		}
	}

	public enum BookArg implements Arg, IsSerializable {
		USER_ID, STATUS_ID, CATEGORY_ID, GRADE_LEVEL_ID, PRICE_BETWEEN, IDS, ONLINE_ONLY, SOLD_AT_BOOK_SALE, SOLD_ONLINE;

		private BookArg() {

		}
	}

	public enum DocumentArg implements Arg, IsSerializable {
		ARTICLE_ID, EVENT_ID;

		private DocumentArg() {

		}
	}

	public enum EventArg implements Arg, IsSerializable {
		EVENT_ID, AGE_GROUP_ID, REGISTRATION_ID, PARTICIPANT_ID, UPCOMING_NUMBER, INCLUDE_FIELDS, REGISTRATION_ADDED_BY_ID, PARENT_ID, USER_ID, STATUS_ID, PARTICIPANT_IDS, NOT_STATUS_ID, SHOW_COMMUNITY, SERIES_ID, SHOW_INACTIVE, REGISTERED_BY_OR_ADDED_FOR_ID, NEWLY_ADDED;
		private EventArg() {

		}
	}

	public enum PaymentArg implements Arg, IsSerializable {
		USER_ID, STATUS_ID, TYPE_ID, ADJUSTMENT_ID;

		private PaymentArg() {

		}
	}

	public enum TagArg implements Arg, IsSerializable {
		ENTITY_ID, MAPPING_TYPE, MAPPING_ID;
		private TagArg() {

		}
	}

	public enum UserArg implements Arg, IsSerializable {
		USER_ID, PARENT_ID_PLUS_SELF, PARENT_ID, NOT_ON_REGISTRATION_ID, GROUP_ID, PARENTS, PARENTS_OF_BOYS, PARENTS_OF_GIRLS, CHILDREN, PARENTS_OF_AGES, ONLY_COMMON_INTERESTS, ADDRESS_SEARCH, WITHIN_MILES, WITHIN_LAT, WITHIN_LNG;

		private UserArg() {

		}
	}

	public enum UserGroupArg implements Arg, IsSerializable {
		USER_ID, USER_IS_ADMIN_OF, USER_NOT_MEMBER_OF;

		private UserGroupArg() {

		}
	}

}
