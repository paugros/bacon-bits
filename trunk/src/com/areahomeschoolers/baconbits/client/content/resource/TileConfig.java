package com.areahomeschoolers.baconbits.client.content.resource;

import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Tag.TagMappingType;

import com.google.gwt.user.client.ui.Image;

public class TileConfig {
	private String text;
	private String url;
	private Integer count;
	private String color;
	private TagMappingType tagType;
	private Image image;

	public TileConfig() {
	}

	public String getColor() {
		return color;
	}

	public Integer getCount() {
		return count;
	}

	public Image getImage() {
		return image;
	}

	public TagMappingType getTagType() {
		return tagType;
	}

	public String getText() {
		return text;
	}

	public String getUrl() {
		return url;
	}

	public TileConfig setColor(String color) {
		this.color = color;
		return this;
	}

	public TileConfig setCount(Integer count) {
		this.count = count;
		return this;
	}

	public TileConfig setImage(Image image) {
		this.image = image;
		return this;
	}

	public TileConfig setTagType(TagMappingType tagType) {
		this.color = tagType.getColor();
		this.text = tagType.getName();
		this.tagType = tagType;
		this.url = PageUrl.tagGroup(tagType.toString());
		return this;
	}

	public TileConfig setText(String text) {
		this.text = text;
		return this;
	}

	public TileConfig setUrl(String url) {
		this.url = url;
		return this;
	}

}
