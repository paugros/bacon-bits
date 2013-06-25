package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.ListBox;

public class DefaultListBox extends ListBox {

	public DefaultListBox() {
		super();
		getElement().getStyle().setBorderColor("#cccccc");
	}

	public DefaultListBox(boolean isMultipleSelect) {
		super(isMultipleSelect);
	}

	public void addItem(String text, int value) {
		addItem(text, Integer.toString(value));
	}

	public Boolean getBooleanValue() {
		String val = getValue();

		if ("1".equals(val) || "true".equals(val)) {
			return true;
		} else if ("0".equals(val) || "false".equals(val)) {
			return false;
		}

		return null;
	}

	public int getIntValue() {
		if (getValue() == null) {
			return 0;
		}
		return Integer.parseInt(getValue());
	}

	public int getIntValue(int index) {
		if (getValue(index) == null) {
			return 0;
		}
		return Integer.parseInt(getValue(index));
	}

	public String getSelectedText() {
		return getItemText(getSelectedIndex());
	}

	public String getValue() {
		if (getSelectedIndex() == -1) {
			return null;
		}
		return getValue(getSelectedIndex());
	}

	public void setItemDisabled(int index, boolean disabled) {
		OptionElement option = (OptionElement) getElement().getChild(index);
		option.setDisabled(disabled);
	}

	public void setItemHTML(int index, String html) {
		OptionElement option = (OptionElement) getElement().getChild(index);
		option.setInnerHTML(html);
	}

	public void setItemStyle(int index, String style) {
		OptionElement option = (OptionElement) getElement().getChild(index);
		option.setClassName(style);
	}

	/**
	 * Sets the row's style to a title style. Also disables the row.
	 * 
	 * @param index
	 */
	public void setItemTitled(int index) {
		setItemStyle(index, "listBoxTitleOption");
		setItemDisabled(index, true);
	}

	public void setItemVisibility(int index, boolean visible) {
		OptionElement option = (OptionElement) getElement().getChild(index);
		option.getStyle().setDisplay((visible ? Display.BLOCK : Display.NONE));
	}

	public boolean setValue(Integer value) {
		if (value == null) {
			value = 0;
		}
		return setValue(Integer.toString(value));
	}

	public boolean setValue(String value) {
		// first select the first item, which will select the blank option if it is a RequiredListBox and the value is 0 (like on an add form)
		setSelectedIndex(0);
		for (int i = 0; i < getItemCount(); i++) {
			if (getValue(i).equals(value)) {
				setSelectedIndex(i);
				return true;
			}
		}

		return false;
	}

	public boolean setValueByItemText(String value) {
		for (int i = 0; i < getItemCount(); i++) {
			if (getItemText(i).equals(value)) {
				setSelectedIndex(i);
				return true;
			}
		}
		return false;
	}
}
