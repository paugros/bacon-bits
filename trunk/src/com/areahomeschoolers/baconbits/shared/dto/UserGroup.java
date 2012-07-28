package com.areahomeschoolers.baconbits.shared.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserGroup extends EntityDto<UserGroup> {

	public enum AccessLevel implements IsSerializable, Serializable {
		PUBLIC(1, "Public"), SITE_MEMBERS(2, "Site members"), GROUP_MEMBERS(3, "Group members"), GROUP_ADMINISTRATORS(4, "Group administrators"), SYSTEM_ADMINISTRATORS(
				5, "System administrators");

		private static final Map<Integer, AccessLevel> lookup = new HashMap<Integer, AccessLevel>();

		static {
			for (AccessLevel s : EnumSet.allOf(AccessLevel.class)) {
				lookup.put(s.getId(), s);
			}
		}

		public static AccessLevel getById(int id) {
			return lookup.get(id);
		}

		private int id;

		private final String displayName;

		private AccessLevel(int id, String displayName) {
			this.id = id;
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return displayName;
		}

		public int getId() {
			return id;
		}
	}

	private static final long serialVersionUID = 1L;

	private String groupName;
	private String description;
	private Date startDate, endDate;

	// for membership records
	private boolean isAdministrator;

	public UserGroup() {

	}

	public boolean getAdministrator() {
		return isAdministrator;
	}

	public String getDescription() {
		return description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public String getGroupName() {
		return groupName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setAdministrator(boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
