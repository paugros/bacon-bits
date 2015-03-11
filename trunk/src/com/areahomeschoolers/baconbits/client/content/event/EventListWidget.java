package com.areahomeschoolers.baconbits.client.content.event;

import java.util.ArrayList;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.shared.dto.Data;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EventListWidget extends Composite {
	private VerticalPanel outerPanel = new VerticalPanel();

	public EventListWidget(String title, ArrayList<Data> events) {
		VerticalPanel ep = new VerticalPanel();
		ep.setSpacing(10);

		for (Data e : events) {
			Hyperlink el = new Hyperlink(e.get("title"), PageUrl.event(e.getId()));
			String txt = el.toString() + "<span style=\"color: #555555;\">&nbsp;&nbsp;-&nbsp;&nbsp;";
			txt += Formatter.formatDate(e.getDate("startDate"), "MMM d") + "</span>";
			HTML eep = new HTML(txt);

			ep.add(eep);
		}

		Label upcoming = new Label(title);
		upcoming.getElement().getStyle().setMarginTop(8, Unit.PX);
		upcoming.addStyleName("largeText mediumPadding");
		outerPanel.add(upcoming);
		outerPanel.add(ep);

		initWidget(outerPanel);
	}

}
