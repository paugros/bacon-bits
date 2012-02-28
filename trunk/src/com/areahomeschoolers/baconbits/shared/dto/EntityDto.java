package com.areahomeschoolers.baconbits.shared.dto;

import java.io.Serializable;

import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Common.ComparisonType;

import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class EntityDto<T extends EntityDto<T>> implements HasId, IsSerializable, HasDescriptor, Comparable<T>, Serializable {
	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_NEW_DESCRIPTOR = "New";
	private int id;
	private static int nextDefaultId = 1;

	@Override
	public int compareTo(T that) {
		if (that == null) {
			return ComparisonType.BEFORE;
		}

		if (this == that || this.equals(that)) {
			return ComparisonType.EQUAL;
		}

		int comparison;

		String descriptor = getDescriptor();
		String thatDescriptor = that.getDescriptor();
		if (Common.isInteger(descriptor) && Common.isInteger(thatDescriptor)) {
			descriptor = Common.zeroPad(descriptor);
			thatDescriptor = Common.zeroPad(thatDescriptor);
		}

		comparison = descriptor.compareTo(thatDescriptor);

		if (comparison != ComparisonType.EQUAL) {
			return comparison;
		}

		return ComparisonType.EQUAL;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HasId)) {
			return false;
		}

		// if we have a non-saved object that doesn't have have reference equality, it is not equal to anything
		if (id == 0) {
			return false;
		}

		HasId idObject = (HasId) object;
		if (id == idObject.getId()) {
			return true;
		}

		return false;
	}

	@Override
	public String getDescriptor() {
		if (isSaved()) {
			String text = Integer.toString(id);
			return text;
		}
		return DEFAULT_NEW_DESCRIPTOR;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return getId();
	}

	public boolean isEditable() {
		return true;
	}

	public boolean isSaved() {
		return getId() > 0;
	}

	public void setDefaultId() {
		this.id = ++nextDefaultId;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return getDescriptor();
	}
}