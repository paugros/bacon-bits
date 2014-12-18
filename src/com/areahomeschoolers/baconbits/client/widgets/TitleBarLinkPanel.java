package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;

public class TitleBarLinkPanel extends Composite {
	private PaddedPanel pp = new PaddedPanel();

	public TitleBarLinkPanel() {
		pp.getElement().getStyle().setPadding(2, Unit.PX);
		initWidget(pp);
	}

	public void add() {

	}

	public void add(Widget link) {
		if (link instanceof Anchor || link instanceof Hyperlink || link instanceof ClickLabel) {
			link.addStyleName("linkPanelItem");
		}
		pp.add(link);
	}

}
