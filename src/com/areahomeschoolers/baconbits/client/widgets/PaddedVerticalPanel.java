package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PaddedVerticalPanel extends VerticalPanel {

	@Override
	public void add(Widget w) {
		w.getElement().getStyle().setMarginBottom(10, Unit.PX);
		super.add(w);
	}
}
