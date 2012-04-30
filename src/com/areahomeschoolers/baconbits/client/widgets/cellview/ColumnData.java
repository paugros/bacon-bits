package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

public class ColumnData<T extends EntityDto<T>> implements HasHorizontalAlignment {

	private HorizontalAlignmentConstant horizontalAlignment;
	private String header;
	private ValueGetter<?, T> valueGetter;
	private ValueGetter<?, T> sortGetter;
	private int index;

	public ColumnData(String header, int index) {
		this.header = header;
		this.setIndex(index);
	}

	public String getHeader() {
		return header;
	}

	@Override
	public HorizontalAlignmentConstant getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public int getIndex() {
		return index;
	}

	public ValueGetter<?, T> getSortGetter() {
		return sortGetter;
	}

	public ValueGetter<?, T> getValueGetter() {
		return valueGetter;
	}

	@Override
	public void setHorizontalAlignment(HorizontalAlignmentConstant align) {
		horizontalAlignment = align;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setSortGetter(ValueGetter<?, T> sortGetter) {
		this.sortGetter = sortGetter;
	}

	public void setValueGetter(ValueGetter<?, T> valueGetter) {
		this.valueGetter = valueGetter;
	}
}
