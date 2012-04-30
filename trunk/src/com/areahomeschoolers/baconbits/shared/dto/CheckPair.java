package com.areahomeschoolers.baconbits.shared.dto;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CheckPair implements IsSerializable {
	private Boolean selected;
	private String text;
	private boolean specialAccess;

	public CheckPair() {
	}

	public CheckPair(Boolean selected, String text) {
		this.selected = selected;
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CheckPair)) {
			return false;
		}
		CheckPair other = (CheckPair) o;

		return text.equals(other.text);
	}

	public String getText() {
		return text;
	}

	@Override
	public int hashCode() {
		return selected.hashCode() * 13 + text.hashCode() * 7;
	}

	public Boolean isSelected() {
		return selected;
	}

	public boolean isSpecialAccess() {
		return specialAccess;
	}

	public void setSelected(Boolean left) {
		this.selected = left;
	}

	public void setSpecialAcess(boolean specialAccess) {
		this.specialAccess = specialAccess;
	}

	public void setText(String right) {
		this.text = right;
	}

	@Override
	public String toString() {
		return selected + ", " + text;
	}
}