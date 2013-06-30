package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Date;

public class MainMenuItem extends EntityDto<MainMenuItem> implements HasGroupOwnership {

	private static final long serialVersionUID = 1L;
	private String name;
	private String articleIds;
	private String url;
	private Integer parentNodeId;
	private int owningOrgId = 11;
	private int visibilityLevelId;
	private Integer groupId;
	private int addedById;
	private Date addedDate;

	// aux
	private ArrayList<MainMenuItem> subItems = new ArrayList<MainMenuItem>();

	public void addItem(MainMenuItem item) {
		subItems.add(item);
	}

	public int getAddedById() {
		return addedById;
	}

	public Date getAddedDate() {
		return addedDate;
	}

	public String getArticleIds() {
		return articleIds;
	}

	@Override
	public Integer getGroupId() {
		if (groupId == null || groupId == 0) {
			return null;
		}
		return groupId;
	}

	public ArrayList<MainMenuItem> getItems() {
		return subItems;
	}

	public String getName() {
		return name;
	}

	@Override
	public int getOwningOrgId() {
		return owningOrgId;
	}

	public Integer getParentNodeId() {
		if (parentNodeId == null || parentNodeId == 0) {
			return null;
		}
		return parentNodeId;
	}

	public String getUrl() {
		return url;
	}

	public int getVisibilityLevelId() {
		return visibilityLevelId;
	}

	public boolean hasChildren() {
		return subItems != null && !subItems.isEmpty();
	}

	public void setAddedById(int addedById) {
		this.addedById = addedById;
	}

	public void setAddedDate(Date addedDate) {
		this.addedDate = addedDate;
	}

	public void setArticleIds(String articleIds) {
		this.articleIds = articleIds;
	}

	@Override
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setOwningOrgId(int organizationId) {
		this.owningOrgId = organizationId;
	}

	public void setParentNodeId(Integer parentNodeId) {
		this.parentNodeId = parentNodeId;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setVisibilityLevelId(int visibilityLevelId) {
		this.visibilityLevelId = visibilityLevelId;
	}

}
