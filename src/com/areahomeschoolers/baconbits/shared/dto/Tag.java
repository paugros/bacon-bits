package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Date;

import com.areahomeschoolers.baconbits.shared.Common;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Tag extends EntityDto<Tag> {
	public enum TagType implements IsSerializable {
		USER("#ffff77"), ARTICLE("#d2d6ec"), EVENT("#a2d69e"), BOOK("#ffcc6f"), RESOURCE("#bae0f5");
		// USER("#e4ed81"), ARTICLE("#eca575"), EVENT("#80c87d"), BOOK("#f28e76"), RESOURCE("#92ceef");

		private String color;

		private TagType(String color) {
			this.color = color;
		}

		public String getColor() {
			return color;
		}

		public String getHelpText() {
			String h = "";

			switch (this) {
			case ARTICLE:
				h += "Read helpful information about homeschooling topics.";
				break;
			case BOOK:
				h += "Find used books for sale. <a href=\"mailto:info@citrusgroups.com\">Contact us</a> if you're interested in selling your used items.";
				break;
			case EVENT:
				h += "Find local and virtual events, classes, and workshops. You can also add your own events, or add community events to share with other homeschoolers.";
				break;
			case RESOURCE:
				h += "Browse local, statewide, and national resources such as: field trips, tutors, blogs, books, or other homeschooling-friendly services. ";
				h += "Share your own great resources by adding them to our directory!";
				break;
			case USER:
				h += "Find local homeschoolers near you!";
				break;
			default:
				break;
			}
			return h;
		}

		public String getName() {
			if (TagType.this.equals(USER)) {
				return "Community";
			}
			return Common.ucWords(this.toString()) + "s";
		}
	}

	private static final long serialVersionUID = 1L;
	private String name;
	private Date addedDate;
	private int addedById;
	private Integer imageId;
	private Integer smallImageId;
	private String imageExtension;

	// mapping
	private int entityId;
	private Date mappingAddedDate;
	private int mappingId;
	private TagType mappingType;

	// aux
	private int count;

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public int getCount() {
		return count;
	}

	public int getEntityId() {
		return entityId;
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

	public Date getMappingAddedDate() {
		return mappingAddedDate;
	}

	public String getMappingColumn() {
		if (getMappingType() == null) {
			return null;
		}
		return getMappingType().toString().toLowerCase() + "Id";
	}

	public int getMappingId() {
		return mappingId;
	}

	public String getMappingTable() {
		if (getMappingType() == null) {
			return null;
		}
		return "tag" + Common.ucWords(getMappingType().toString()) + "Mapping";
	}

	public TagType getMappingType() {
		return mappingType;
	}

	public String getName() {
		return name;
	}

	public Integer getSmallImageId() {
		if (smallImageId == null || smallImageId == 0) {
			return null;
		}

		return smallImageId;
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setEntityId(int entityId) {
		this.entityId = entityId;
	}

	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}

	public void setImageId(Integer imageId) {
		this.imageId = imageId;
	}

	public void setMappingAddedDate(Date mappingAddedDate) {
		this.mappingAddedDate = mappingAddedDate;
	}

	public void setMappingId(int mappingId) {
		this.mappingId = mappingId;
	}

	public void setMappingType(TagType t) {
		mappingType = t;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSmallImageId(Integer smallImageId) {
		this.smallImageId = smallImageId;
	}

}
