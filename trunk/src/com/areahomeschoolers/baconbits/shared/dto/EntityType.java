package com.areahomeschoolers.baconbits.shared.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum EntityType implements IsSerializable, Serializable {
	// id corresponds to dbo.EntityType table
	NONE(0, ""), ARTICLE(1, "Article"), EVENT(2, "Event");

	private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>();

	static {
		for (EntityType s : EnumSet.allOf(EntityType.class)) {
			lookup.put(s.getId(), s);
		}
	}

	public static EntityType getById(int id) {
		return lookup.get(id);
	}

	private int id;

	private final String displayName;

	private EntityType(int id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}

	public String getDbTableName() {
		switch (this) {
		case NONE:
		case ARTICLE:
			return "articles";
		case EVENT:
			return "events";
		default:
			return null;
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getId() {
		return id;
	}

}
