package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class FixedWidthLabel extends Label {
	public static void setFixedWidth(Widget w, int width) {
		Style s = w.getElement().getStyle();
		s.setWidth(width, Unit.PX);
		s.setOverflow(Overflow.HIDDEN);
		w.addStyleName("nowrap");
	}

	private int width;

	public FixedWidthLabel(int width) {
		this("", width);
	}

	public FixedWidthLabel(String text, int width) {
		super(text);
		setWidth(width);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
		setFixedWidth(this, width);
	}
}
