package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public interface Arg {
	public enum ArticleArg implements Arg, IsSerializable {
		ARTICLE_ID, MOST_RECENT, IDS, OWNING_ORG_ID, BLOG_ONLY, COMMENT_ID, BEFORE_ID, BEFORE_DATE, AFTER_DATE, SEARCH, HAS_TAGS, ONLY_TAGGED, INCLUDE_BLOG;

		private ArticleArg() {

		}
	}

	public enum BookArg implements Arg, IsSerializable {
		USER_ID, STATUS_ID, CATEGORY_ID, GRADE_LEVEL_ID, PRICE_BETWEEN, IDS, ONLINE_ONLY, SOLD_AT_BOOK_SALE, SOLD_ONLINE, NEW_NUMBER, IN_MY_CART, HAS_TAGS, LOCATION_FILTER;

		private BookArg() {

		}
	}

	public enum DocumentArg implements Arg, IsSerializable {
		ARTICLE_ID, EVENT_ID;

		private DocumentArg() {

		}
	}

	public enum EventArg implements Arg, IsSerializable {
		EVENT_ID, AGE_GROUP_ID, REGISTRATION_ID, PARTICIPANT_ID, UPCOMING_NUMBER, INCLUDE_FIELDS, REGISTRATION_ADDED_BY_ID, PARENT_ID, USER_ID, STATUS_ID, PARTICIPANT_IDS, NOT_STATUS_ID, ONLY_COMMUNITY, SERIES_ID, SHOW_INACTIVE, REGISTERED_BY_OR_ADDED_FOR_ID, NEWLY_ADDED, INCLUDE_COMMUNITY, HAS_TAGS, LOCATION_FILTER;
		private EventArg() {

		}
	}

	public enum PaymentArg implements Arg, IsSerializable {
		USER_ID, STATUS_ID, TYPE_ID, ADJUSTMENT_ID;

		private PaymentArg() {

		}
	}

	public enum ResourceArg implements Arg, IsSerializable {
		ID, LIMIT, LEAST_IMPRESSIONS, AD, HAS_TAGS, NAME_LIKE, LOCATION_FILTER;

		private ResourceArg() {

		}
	}

	public enum ReviewArg implements Arg, IsSerializable {
		ID, USER_ID, TYPE, ENTITY_ID;

		private ReviewArg() {

		}
	}

	public enum TagArg implements Arg, IsSerializable {
		ENTITY_ID, TYPE, MAPPING_ID, GET_COUNTS, GET_ALL_COUNTS, TAG_ID, LOCATION_FILTER;
		private TagArg() {

		}
	}

	public enum UserArg implements Arg, IsSerializable {
		USER_ID, PARENT_ID_PLUS_SELF, PARENT_ID, NOT_ON_REGISTRATION_ID, ORGANIZATION_ID, GROUP_ID, ADMIN_OF_GROUP_ID, PARENTS, PARENTS_OF_BOYS, PARENTS_OF_GIRLS, CHILDREN, AGES, ONLY_COMMON_INTERESTS, ADDRESS_SEARCH, SEX, ACTIVE_NUMBER, NEW_NUMBER, HAS_EMAIL, HAS_TAGS, LOCATION_FILTER;

		private UserArg() {

		}
	}

	public enum UserGroupArg implements Arg, IsSerializable {
		ID, USER_ID, USER_IS_ADMIN_OF, USER_NOT_MEMBER_OF, ORG_SUB_DOMAIN, ORG_DOMAIN;

		private UserGroupArg() {

		}
	}

}
