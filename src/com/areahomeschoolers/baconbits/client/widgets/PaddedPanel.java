package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class PaddedPanel extends HorizontalPanel implements HasText {
	private int defaultPadding = 6;

	public PaddedPanel() {
	}

	public PaddedPanel(int defaultPadding) {
		this.defaultPadding = defaultPadding;
	}

	public PaddedPanel(Widget... widgets) {
		this();

		for (Widget widget : widgets) {
			add(widget);
		}
	}

	@Override
	public void add(Widget w) {
		this.add(w, defaultPadding);
	}

	public void add(Widget w, int customPadding) {
		super.add(w);
		w.getElement().getParentElement().getStyle().setPaddingRight(customPadding, Unit.PX);
	}

	@Override
	public String getText() {
		return new HTML(this.toString()).getText();
	}

	@Override
	public void insert(IsWidget w, int beforeIndex) {
		super.insert(w, beforeIndex);
		((UIObject) w).getElement().getParentElement().getStyle().setPaddingRight(defaultPadding, Unit.PX);
	}

	@Override
	public void insert(Widget w, int beforeIndex) {
		super.insert(w, beforeIndex);
		w.getElement().getParentElement().getStyle().setPaddingRight(defaultPadding, Unit.PX);
	}

	public void setPadding(int newPadding) {
		defaultPadding = newPadding;
	}

	@Override
	public void setText(String text) {
	}

}
