package com.areahomeschoolers.baconbits.client.widgets;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;

public class YearPicker extends Composite implements CustomFocusWidget {

	private static final int DEFAULT_START_YEAR = 2008;
	private final DefaultListBox listBox = new DefaultListBox();
	private final int currentYear = ClientDateUtils.getYear(new Date());

	public YearPicker() {
		this(DEFAULT_START_YEAR);
	}

	public YearPicker(int startYear) {
		setStartYear(startYear);
		initWidget(listBox);
	}

	public ListBox getListBox() {
		return listBox;
	}

	public int getYear() {
		return listBox.getIntValue();
	}

	@Override
	public void setEnabled(boolean enabled) {
		listBox.setEnabled(enabled);
	}

	@Override
	public void setFocus(boolean focus) {
		listBox.setFocus(focus);
	}

	public void setStartYear(int startYear) {
		if (startYear > currentYear) {
			startYear = DEFAULT_START_YEAR;
		}

		listBox.clear();
		listBox.addItem("", 0);
		for (int i = currentYear; i >= startYear; i--) {
			listBox.addItem(Integer.toString(i));
		}
		listBox.setSelectedIndex(1);
	}

	public void setYear(Date date) {
		if (date != null) {
			setYear(ClientDateUtils.getYear(date));
		} else {
			listBox.setSelectedIndex(0);
		}
	}

	public void setYear(int year) {
		listBox.setValue(year);
	}
}
