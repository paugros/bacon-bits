package com.areahomeschoolers.baconbits.client.content.resource;

public class TileConfig {
	private String text;
	private String url;
	private int count;
	private long color;
	private int imageId;

	public TileConfig() {
	}

	public long getColor() {
		return color;
	}

	public int getCount() {
		return count;
	}

	public int getImageId() {
		return imageId;
	}

	public String getText() {
		return text;
	}

	public String getUrl() {
		return url;
	}

	public TileConfig setColor(long color) {
		this.color = color;
		return this;
	}

	public TileConfig setCount(int count) {
		this.count = count;
		return this;
	}

	public TileConfig setImageId(int imageId) {
		this.imageId = imageId;
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
