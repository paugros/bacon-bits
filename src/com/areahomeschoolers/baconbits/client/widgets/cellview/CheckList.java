package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.shared.dto.CheckPair;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CheckList extends Composite {

	private List<CheckBox> boxes = new ArrayList<CheckBox>();
	private VerticalPanel listPanel = new VerticalPanel();

	public CheckList() {
		initWidget(listPanel);
	}

	public List<String> getTextValues() {
		return getTextValues(null);
	}

	public List<String> getTextValues(Boolean only) {
		List<String> selected = new ArrayList<String>();

		for (CheckBox box : boxes) {
			if (only == null || box.getValue() == only) {
				selected.add(box.getText());
			}
		}

		return selected;
	}

	public void populate(List<CheckPair> values) {
		boxes.clear();
		listPanel.clear();

		for (CheckPair cp : values) {
			CheckBox box = new CheckBox(cp.getText());
			box.setValue(cp.isSelected());

			boxes.add(box);
			listPanel.add(box);
		}
	}

	public void setSelected(List<String> names) {
		for (CheckBox box : boxes) {
			box.setValue(names.contains(box.getText()));
		}
	}
}
