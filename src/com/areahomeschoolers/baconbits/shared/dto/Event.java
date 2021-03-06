package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;

import com.areahomeschoolers.baconbits.client.content.document.HasDocuments;
import com.areahomeschoolers.baconbits.shared.HasAddress;
import com.areahomeschoolers.baconbits.shared.HasMarkup;

import com.google.gwt.event.shared.HandlerRegistration;

public class Event extends EntityDto<Event> implements HasDocuments, HasGroupOwnership, HasMarkup, HasAddress {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String title;
	private String description;
	private int addedById;
	private Date startDate, endDate, addedDate, registrationStartDate, registrationEndDate, publishDate;
	private Integer groupId;
	private int categoryId;
	private double cost;
	private double price;
	private double highPrice;
	private double markup;
	private boolean adultRequired = false;
	private boolean active = true;
	private boolean finished, registrationOpen;
	private boolean sendSurvey;
	private boolean requiresRegistration = false;
	private int minimumParticipants, maximumParticipants;
	private String notificationEmail;
	private String website;
	private String phone;
	private String registrationInstructions;
	private int visibilityLevelId;
	private Integer seriesId;
	private boolean requiredInSeries;
	private int owningOrgId;
	private double markupPercent;
	private double markupDollars;
	private boolean markupOverride;
	private boolean directoryPriority;
	private String contactName;
	private String contactEmail;
	private int viewCount;
	private String payPalEmail;
	private String refundPolicy;
	private int minimumAge;
	private int maximumAge;
	private boolean priceNotApplicable = true;
	private Integer resourceId;

	// address
	private String address;
	private String street;
	private String city;
	private String state;
	private String zip;
	private double lat;
	private double lng;
	private boolean addressChanged;
	private String facilityName;

	// auxilliary
	private String resourceName;
	private String tags;
	private Integer imageId;
	private Integer smallImageId;
	private String imageExtension;
	private double groupMarkupPercent;
	private double groupMarkupDollars;
	private boolean groupMarkupOverride;
	private boolean newlyAdded;
	private boolean saveAllInSeries;
	private boolean markupChanged;

	private int cloneFromId;
	private int currentUserParticipantCount;
	private String agePrices;
	private String ageRanges;
	private int documentCount;
	private String category;
	private String groupName;

	private String visibilityLevel;
	private String addedByFullName;
	// used to create a series
	private ArrayList<Pair<Date, Date>> createSeriesDates;

	public Event() {

	}

	public HandlerRegistration addSeriesDate(Date startDate, Date endDate) {
		if (createSeriesDates == null) {
			createSeriesDates = new ArrayList<Pair<Date, Date>>();
		}

		final Pair<Date, Date> p = new Pair<Date, Date>(startDate, endDate);
		createSeriesDates.add(p);

		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				createSeriesDates.remove(p);
			}
		};
	}

	public boolean allowRegistrations() {
		return active && !finished && registrationOpen;
	}

	public boolean getActive() {
		return active;
	}

	public String getAddedByFullName() {
		return addedByFullName;
	}

	@Override
	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public boolean getAddressChanged() {
		return addressChanged;
	}

	public double getAdjustedPrice() {
		if (!requiresRegistration) {
			return price;
		}
		return price + markup;
	}

	public boolean getAdultRequired() {
		return adultRequired;
	}

	public String getAgePrices() {
		return agePrices;
	}

	public String getAgeRanges() {
		return ageRanges;
	}

	public String getCategory() {
		return category;
	}

	public int getCategoryId() {
		if (categoryId == 0) {
			return 1;
		}
		return categoryId;
	}

	@Override
	public String getCity() {
		return city;
	}

	public int getCloneFromId() {
		return cloneFromId;
	}

	public String getContactEmail() {
		return contactEmail;
	}

	public String getContactName() {
		return contactName;
	}

	public double getCost() {
		return cost;
	}

	public int getCurrentUserParticipantCount() {
		return currentUserParticipantCount;
	}

	public String getDescription() {
		return description;
	}

	public boolean getDirectoryPriority() {
		return directoryPriority;
	}

	@Override
	public int getDocumentCount() {
		return documentCount;
	}

	public Date getEndDate() {
		return endDate;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.EVENT;
	}

	public String getFacilityName() {
		return facilityName;
	}

	public boolean getFinished() {
		return finished;
	}

	@Override
	public Integer getGroupId() {
		if (groupId == null || groupId == 0) {
			return null;
		}
		return groupId;
	}

	public double getGroupMarkupDollars() {
		return groupMarkupDollars;
	}

	public boolean getGroupMarkupOverride() {
		return groupMarkupOverride;
	}

	public double getGroupMarkupPercent() {
		return groupMarkupPercent;
	}

	public String getGroupName() {
		return groupName;
	}

	public double getHighPrice() {
		return highPrice;
	}

	public String getImageExtension() {
		return imageExtension;
	}

	public Integer getImageId() {
		if (imageId == null || imageId == 0) {
			return null;
		}
		return imageId;
	}

	@Override
	public double getLat() {
		return lat;
	}

	@Override
	public double getLng() {
		return lng;
	}

	public double getMarkup() {
		return markup;
	}

	public boolean getMarkupChanged() {
		return markupChanged;
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

	public int getMaximumAge() {
		return maximumAge;
	}

	public int getMaximumParticipants() {
		return maximumParticipants;
	}

	public int getMinimumAge() {
		return minimumAge;
	}

	public int getMinimumParticipants() {
		return minimumParticipants;
	}

	public String getNotificationEmail() {
		return notificationEmail;
	}

	@Override
	public int getOwningOrgId() {
		return owningOrgId;
	}

	public String getPayPalEmail() {
		return payPalEmail;
	}

	public String getPhone() {
		return phone;
	}

	public double getPrice() {
		return price;
	}

	public boolean getPriceNotApplicable() {
		return priceNotApplicable;
	}

	public Date getPublishDate() {
		return publishDate;
	}

	public String getRefundPolicy() {
		return refundPolicy;
	}

	public Date getRegistrationEndDate() {
		return registrationEndDate;
	}

	public String getRegistrationInstructions() {
		return registrationInstructions;
	}

	public boolean getRegistrationOpen() {
		return registrationOpen;
	}

	public Date getRegistrationStartDate() {
		return registrationStartDate;
	}

	public boolean getRequiredInSeries() {
		return requiredInSeries;
	}

	public boolean getRequiresRegistration() {
		return requiresRegistration;
	}

	public Integer getResourceId() {
		if (resourceId == null || resourceId == 0) {
			return null;
		}
		return resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public boolean getSaveAllInSeries() {
		return saveAllInSeries;
	}

	public boolean getSendSurvey() {
		return sendSurvey;
	}

	public ArrayList<Pair<Date, Date>> getSeriesDates() {
		return createSeriesDates;
	}

	public Integer getSeriesId() {
		if (seriesId == null || seriesId == 0) {
			return null;
		}
		return seriesId;
	}

	public Integer getSmallImageId() {
		if (smallImageId == null || smallImageId == 0) {
			return null;
		}
		return smallImageId;
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

	public String getTags() {
		return tags;
	}

	public String getTitle() {
		return title;
	}

	public int getViewCount() {
		return viewCount;
	}

	public String getVisibilityLevel() {
		return visibilityLevel;
	}

	public int getVisibilityLevelId() {
		return visibilityLevelId;
	}

	public String getWebsite() {
		return website;
	}

	@Override
	public String getZip() {
		return zip;
	}

	@Override
	public boolean hasDocuments() {
		return documentCount > 0;
	}

	public boolean isNewlyAdded() {
		return newlyAdded;
	}

	public boolean isSeriesChild() {
		return getSeriesId() != null && getSeriesId() != getId();
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAddedByFullName(String addedByFullName) {
		this.addedByFullName = addedByFullName;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	@Override
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public void setAddressChanged(boolean changed) {
		this.addressChanged = changed;
	}

	public void setAdultRequired(boolean adultRequired) {
		this.adultRequired = adultRequired;
	}

	public void setAgePrices(String agePrices) {
		this.agePrices = agePrices;
	}

	public void setAgeRanges(String ageRanges) {
		this.ageRanges = ageRanges;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public void setCity(String city) {
		this.city = city;
	}

	public void setCloneFromId(int cloneFromId) {
		this.cloneFromId = cloneFromId;
	}

	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public void setCurrentUserParticipantCount(int currentUserParticipantCount) {
		this.currentUserParticipantCount = currentUserParticipantCount;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDirectoryPriority(boolean directoryPriority) {
		this.directoryPriority = directoryPriority;
	}

	public void setDocumentCount(int documentCount) {
		this.documentCount = documentCount;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setFacilityName(String facilityName) {
		this.facilityName = facilityName;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	@Override
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public void setGroupMarkupDollars(double groupMarkupDollars) {
		this.groupMarkupDollars = groupMarkupDollars;
	}

	public void setGroupMarkupOverride(boolean groupMarkupOverride) {
		this.groupMarkupOverride = groupMarkupOverride;
	}

	public void setGroupMarkupPercent(double groupMarkupPercent) {
		this.groupMarkupPercent = groupMarkupPercent;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setHighPrice(double highPrice) {
		this.highPrice = highPrice;
	}

	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	@Override
	public void setLat(double lat) {
		this.lat = lat;
	}

	@Override
	public void setLng(double lng) {
		this.lng = lng;
	}

	public void setMarkup(double markup) {
		this.markup = markup;
	}

	public void setMarkupChanged(boolean markupChanged) {
		this.markupChanged = markupChanged;
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

	public void setMaximumAge(int maximumAge) {
		this.maximumAge = maximumAge;
	}

	public void setMaximumParticipants(int maximumParticipants) {
		this.maximumParticipants = maximumParticipants;
	}

	public void setMinimumAge(int minimumAge) {
		this.minimumAge = minimumAge;
	}

	public void setMinimumParticipants(int minimumParticipants) {
		this.minimumParticipants = minimumParticipants;
	}

	public void setNewlyAdded(boolean newlyAdded) {
		this.newlyAdded = newlyAdded;
	}

	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}

	@Override
	public void setOwningOrgId(int organizationId) {
		this.owningOrgId = organizationId;
	}

	public void setPayPalEmail(String payPalEmail) {
		this.payPalEmail = payPalEmail;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public void setPriceNotApplicable(boolean priceNotApplicable) {
		this.priceNotApplicable = priceNotApplicable;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public void setRefundPolicy(String refundPolicy) {
		this.refundPolicy = refundPolicy;
	}

	public void setRegistrationEndDate(Date registrationEndDate) {
		this.registrationEndDate = registrationEndDate;
	}

	public void setRegistrationInstructions(String registrationInstructions) {
		this.registrationInstructions = registrationInstructions;
	}

	public void setRegistrationOpen(boolean registrationFinished) {
		this.registrationOpen = registrationFinished;
	}

	public void setRegistrationStartDate(Date registrationStartDate) {
		this.registrationStartDate = registrationStartDate;
	}

	public void setRequiredInSeries(boolean requiredInSeries) {
		this.requiredInSeries = requiredInSeries;
	}

	public void setRequiresRegistration(boolean requiresRegistration) {
		this.requiresRegistration = requiresRegistration;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public void setSaveAllInSeries(boolean saveAllInSeries) {
		this.saveAllInSeries = saveAllInSeries;
	}

	public void setSendSurvey(boolean sendSurvey) {
		this.sendSurvey = sendSurvey;
	}

	public void setSeriesId(Integer seriesId) {
		this.seriesId = seriesId;
	}

	public void setSmallImageId(Integer smallImageId) {
		this.smallImageId = smallImageId;
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

	public void setTags(String tags) {
		this.tags = tags;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}

	public void setVisibilityLevel(String visibilityLevel) {
		this.visibilityLevel = visibilityLevel;
	}

	public void setVisibilityLevelId(int visibilityLevelId) {
		this.visibilityLevelId = visibilityLevelId;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Override
	public void setZip(String zip) {
		this.zip = zip;
	}

}
