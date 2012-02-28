package com.areahomeschoolers.baconbits.shared.dto;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum EntityType implements IsSerializable, Serializable {
	// id corresponds to dbo.EntityType table
	NONE(0, false, ""), TT(1, true, "Trouble Ticket"), WT(2, true, "Work Ticket"), ORDER(3, true, "Order"), KB(4, true, "Knowledge Base"), HDT(5, true,
			"Help Desk Ticket"), ASSET(6, true, "Asset"), COLO(7, true, "Colocation"), CIRCUIT(8, true, "Circuit"), CUSTOMER(9, true, "Customer"), ACCOUNT(10,
			true, "Account"), SF(11, true, "Sales Forecast"), QUOTE(12, true, "Quote"), CONTACT(13, true, "Contact"), ADDRESS(14, true, "Address"), USER(15,
			false, "User"), USERGROUP(16, false, "User Group"), QAR(17, false, "Qar"), PROJECT(18, false, "Project"), DNS(19, false, "DNS"), REQUEST(20, false,
			"Request"), NETWORKSUMMARY(22, false, "Network Summary"), EMAILTEMPLATE(23, false, "Email Template"), VENDOR(24, false, "Vendor");

	private static final Map<Integer, EntityType> lookup = new HashMap<Integer, EntityType>();
	private static final Set<EntityType> flagEntityTypes = new HashSet<EntityType>();

	static {
		for (EntityType s : EnumSet.allOf(EntityType.class)) {
			lookup.put(s.getId(), s);

			if (s.isFlaggable()) {
				flagEntityTypes.add(s);
			}
		}
	}

	public static EntityType getById(int id) {
		return lookup.get(id);
	}

	public static Set<EntityType> getFlaggableTypes() {
		return new HashSet<EntityType>(flagEntityTypes);
	}

	private int id;

	private boolean flaggable = false;

	private final String displayName;

	private EntityType(int id, boolean canFlag, String displayName) {
		this.id = id;
		this.flaggable = canFlag;
		this.displayName = displayName;
	}

	public String getDbTableName() {
		switch (this) {
		case ACCOUNT:
			return "Accounts";
		case ADDRESS:
			return "Addresses";
		case ASSET:
			return "DeviceMain";
		case CIRCUIT:
			return "CircuitMain";
		case COLO:
			return "ColloMain";
		case CONTACT:
			return "Contacts";
		case CUSTOMER:
			return "Customers";
		case DNS:
			return "DNSRecords";
		case EMAILTEMPLATE:
			return "EmailTemplates";
		case HDT:
			return "HDMain";
		case KB:
			return "KBMain";
		case NETWORKSUMMARY:
			return "NetworkRecords";
		case ORDER:
			return "Orders";
		case PROJECT:
			return "OrderProjects";
		case QAR:
			return "QARMain";
		case QUOTE:
			return "Quotes";
		case REQUEST:
			return "Requests";
		case SF:
			return "SalesForecastMain";
		case USER:
			return "Users";
		case USERGROUP:
			return "UserGroups";
		case TT:
			return "TTMain";
		case WT:
			return "WTMain";
		case NONE:
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

	public boolean isFlaggable() {
		return flaggable;
	}
}
