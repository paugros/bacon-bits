package com.areahomeschoolers.baconbits.shared.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.HasAddress;
import com.areahomeschoolers.baconbits.shared.HasMarkup;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserGroup extends EntityDto<UserGroup> implements HasGroupOwnership, HasAddress, HasMarkup {

	public enum AccessLevel implements IsSerializable, Serializable {
		PUBLIC(1, "Public"), SITE_MEMBERS(2, "Site members"), GROUP_MEMBERS(3, "Group members"), GROUP_ADMINISTRATORS(4, "Group administrators"), ORGANIZATION_ADMINISTRATORS(
				6, "Organization administrators"), SYSTEM_ADMINISTRATORS(7, "System administrators"), BLOG_CONTRIBUTORS(33, "Blog contributors");

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

		private AccessLevel() {
			this.displayName = null;
		}

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

	public enum GroupPolicy implements IsSerializable, Serializable {
		PUBLIC_GREETING("Public Greeting", "publicGreetingId"), PRIVATE_GREETING("Private Greeting", "privateGreetingId"), GENERAL_POLICY("General Policy",
				"generalPolicyId"), EVENT_POLICY("Event Policy", "eventPolicyId"), COOP_POLICY("Co-op Policy", "coopPolicyId"), BOOKS_POLICY(
				"Book Sale Policy", "booksPolicyId");

		private String column;
		private String title;

		private GroupPolicy(String title, String column) {
			this.column = column;
			this.title = title;
		}

		public String getColumn() {
			return column;
		}

		public String getTitle() {
			return title;
		}
	}

	public enum VisibilityLevel implements IsSerializable, Serializable {
		PUBLIC(1, "Public"), SITE_MEMBERS(2, "Site members"), MY_GROUPS(3, "All my groups"), GROUP_MEMBERS(4, "Group members"), PRIVATE(5, "Private");

		private static final Map<Integer, VisibilityLevel> lookup = new HashMap<Integer, VisibilityLevel>();

		static {
			for (VisibilityLevel s : EnumSet.allOf(VisibilityLevel.class)) {
				lookup.put(s.getId(), s);
			}
		}

		public static VisibilityLevel getById(int id) {
			return lookup.get(id);
		}

		private int id;

		private final String displayName;

		private VisibilityLevel() {
			this.displayName = null;
		}

		private VisibilityLevel(int id, String displayName) {
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
	private boolean isOrganization;
	private int owningOrgId;
	private String shortName;
	private String orgDomain;
	private String orgSubDomain;
	private Integer privateGreetingId;
	private Integer publicGreetingId;
	private Integer generalPolicyId;
	private Integer eventPolicyId;
	private Integer coopPolicyId;
	private Integer booksPolicyId;
	private String payPalEmail;
	private Integer logoId;
	private Integer faviconId;
	private boolean religious;
	private Integer contactId;
	private double membershipFee;
	private String facebookUrl;
	private double markupPercent;
	private double markupDollars;
	private boolean markupOverride;

	// address
	private String address;
	private String street;
	private String city;
	private String state;
	private String zip;
	private double lat;
	private double lng;
	private boolean addressChanged;

	// aux
	private String organizationName;
	private String contact;

	// for membership records
	private boolean isAdministrator;
	private boolean groupApproved;
	private boolean userApproved;

	public UserGroup() {

	}

	@Override
	public int getAddedById() {
		return -1;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public boolean getAddressChanged() {
		return addressChanged;
	}

	public boolean getAdministrator() {
		return isAdministrator;
	}

	public Integer getBooksPolicyId() {
		return booksPolicyId;
	}

	@Override
	public String getCity() {
		return city;
	}

	public String getContact() {
		return contact;
	}

	public Integer getContactId() {
		if (contactId == null || contactId == 0) {
			return null;
		}
		return contactId;
	}

	public Integer getCoopPolicyId() {
		if (coopPolicyId == null || coopPolicyId == 0) {
			return null;
		}
		return coopPolicyId;
	}

	public String getDescription() {
		return description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Integer getEventPolicyId() {
		if (eventPolicyId == null || eventPolicyId == 0) {
			return null;
		}
		return eventPolicyId;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public Integer getFaviconId() {
		if (faviconId == null || faviconId == 0) {
			return null;
		}
		return faviconId;
	}

	public Integer getGeneralPolicyId() {
		if (generalPolicyId == null || generalPolicyId == 0) {
			return null;
		}
		return generalPolicyId;
	}

	public boolean getGroupApproved() {
		return groupApproved;
	}

	@Override
	public Integer getGroupId() {
		return getId();
	}

	public String getGroupName() {
		return groupName;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLng() {
		return lng;
	}

	public Integer getLogoId() {
		if (logoId == null || logoId == 0) {
			return null;
		}
		return logoId;
	}

	@Override
	public double getMarkupDollars() {
		return markupDollars;
	}

	@Override
	public boolean getMarkupOverride() {
		return markupOverride;
	}

	@Override
	public double getMarkupPercent() {
		return markupPercent;
	}

	public double getMembershipFee() {
		return membershipFee;
	}

	public boolean getOrganization() {
		return isOrganization;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public String getOrgDomain() {
		return orgDomain;
	}

	public String getOrgSubDomain() {
		return orgSubDomain;
	}

	@Override
	public int getOwningOrgId() {
		return owningOrgId;
	}

	public String getPayPalEmail() {
		return payPalEmail;
	}

	public int getPolicyId(GroupPolicy policy) {
		switch (policy) {
		case COOP_POLICY:
			return coopPolicyId;
		case EVENT_POLICY:
			return eventPolicyId;
		case GENERAL_POLICY:
			return generalPolicyId;
		case PRIVATE_GREETING:
			return privateGreetingId;
		case PUBLIC_GREETING:
			return publicGreetingId;
		case BOOKS_POLICY:
			return booksPolicyId;
		default:
			return 0;
		}
	}

	public Integer getPrivateGreetingId() {
		if (privateGreetingId == null || privateGreetingId == 0) {
			return null;
		}
		return privateGreetingId;
	}

	public Integer getPublicGreetingId() {
		if (publicGreetingId == null || publicGreetingId == 0) {
			return null;
		}
		return publicGreetingId;
	}

	public boolean getReligious() {
		return religious;
	}

	public String getShortName() {
		return shortName;
	}

	public Date getStartDate() {
		return startDate;
	}

	@Override
	public String getState() {
		return state;
	}

	@Override
	public String getStreet() {
		return street;
	}

	public boolean getUserApproved() {
		return userApproved;
	}

	@Override
	public String getZip() {
		return zip;
	}

	public boolean isActive() {
		return Common.isActive(new Date(), endDate);
	}

	public boolean isCitrus() {
		return getId() == Constants.CG_ORG_ID;
	}

	public boolean isOrganization() {
		return isOrganization;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void setAddressChanged(boolean changed) {
		this.addressChanged = changed;
	}

	public void setAdministrator(boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}

	public void setBooksPolicyId(Integer booksPolicyId) {
		this.booksPolicyId = booksPolicyId;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public void setCoopPolicyId(Integer coopPolicyId) {
		this.coopPolicyId = coopPolicyId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setEventPolicyId(Integer eventPolicyId) {
		this.eventPolicyId = eventPolicyId;
	}

	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}

	public void setFaviconId(Integer faviconId) {
		this.faviconId = faviconId;
	}

	public void setGeneralPolicyId(Integer generalPolicyId) {
		this.generalPolicyId = generalPolicyId;
	}

	public void setGroupApproved(boolean groupApproved) {
		this.groupApproved = groupApproved;
	}

	@Override
	public void setGroupId(Integer groupId) {
		setId(groupId);
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setLogoId(Integer logoId) {
		this.logoId = logoId;
	}

	@Override
	public void setMarkupDollars(double markupDollars) {
		this.markupDollars = markupDollars;
	}

	@Override
	public void setMarkupOverride(boolean override) {
		markupOverride = override;
	}

	@Override
	public void setMarkupPercent(double markupPercent) {
		this.markupPercent = markupPercent;
	}

	public void setMembershipFee(double membershipFee) {
		this.membershipFee = membershipFee;
	}

	public void setOrganization(boolean isOrganization) {
		this.isOrganization = isOrganization;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public void setOrgDomain(String orgDomain) {
		this.orgDomain = orgDomain;
	}

	public void setOrgSubDomain(String orgSubDomain) {
		this.orgSubDomain = orgSubDomain;
	}

	@Override
	public void setOwningOrgId(int organizationId) {
		this.owningOrgId = organizationId;
	}

	public void setPayPalEmail(String payPalEmail) {
		this.payPalEmail = payPalEmail;
	}

	public void setPolicyId(GroupPolicy policy, int id) {
		switch (policy) {
		case COOP_POLICY:
			coopPolicyId = id;
		case EVENT_POLICY:
			eventPolicyId = id;
		case GENERAL_POLICY:
			generalPolicyId = id;
		case PRIVATE_GREETING:
			privateGreetingId = id;
		case PUBLIC_GREETING:
			publicGreetingId = id;
		default:
		}
	}

	public void setPrivateGreetingId(Integer privateGreetingId) {
		this.privateGreetingId = privateGreetingId;
	}

	public void setPublicGreetingId(Integer publicGreetingId) {
		this.publicGreetingId = publicGreetingId;
	}

	public void setReligious(boolean religious) {
		this.religious = religious;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setState(String state) {
		this.state = state;
	}

	@Override
	public void setStreet(String street) {
		this.street = street;
	}

	public void setUserApproved(boolean userApproved) {
		this.userApproved = userApproved;
	}

	@Override
	public void setZip(String zip) {
		this.zip = zip;
	}

}
