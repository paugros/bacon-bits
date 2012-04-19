package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.Date;

import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;

public class MonthPicker extends Composite implements CustomFocusWidget {

	private final DefaultListBox listBox = new DefaultListBox();
	private final ArrayList<Command> changeCommands = new ArrayList<Command>();

	public MonthPicker() {
		setMonthRange(1, 12);
		initWidget(listBox);

		listBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				for (Command command : changeCommands) {
					command.execute();
				}
			}
		});
	}

	public void addValueChangeCommand(Command command) {
		changeCommands.add(command);
	}

	public int getMonth() {
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

	public void setMonth(Date date) {
		if (date != null) {
			setMonth(ClientDateUtils.getMonth(date));
		}
	}

	public void setMonth(int month) {
		listBox.setSelectedIndex(month - 1);
	}

	// Use 1 - 12, not 0 - 11
	public void setMonthRange(int startMonth, int endMonth) {
		listBox.clear();
		int currentMonth = ClientDateUtils.getMonth(new Date());

		int index = 1;
		for (String month : Constants.MONTH_NAMES) {
			if (index >= startMonth && index <= endMonth) {
				listBox.addItem(month, Integer.toString(index));

				if (currentMonth == index) {
					listBox.setSelectedIndex(index - startMonth);
				}
			}

			index++;
		}
	}
}
