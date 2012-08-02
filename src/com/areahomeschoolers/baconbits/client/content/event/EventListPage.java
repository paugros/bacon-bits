package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventListPage implements Page {
	public EventListPage(final VerticalPanel page) {
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		final String title = "Events";
		final EventCellTable table = new EventCellTable(args);

		PaddedPanel pp = new PaddedPanel();
		pp.addStyleName("mediumPadding");
		final DefaultListBox lb = new DefaultListBox();
		lb.addItem("All", 0);
		for (int age = 1; age < 19; age++) {
			lb.addItem(Integer.toString(age), age);
		}

		lb.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				int age = lb.getIntValue();
				if (age == 0) {
					table.showAllItems();
					return;
				}

				for (Event e : table.getFullList()) {
					String rangeText = e.getAgeRanges();

					if (rangeText == null) {
						table.showItem(e, false, false, false);
						continue;
					}

					String[] ranges = rangeText.split(",");

					for (int i = 0; i < ranges.length; i++) {
						String[] limits = ranges[i].split("-");

						int min = Integer.parseInt(limits[0]);
						int max = Integer.parseInt(limits[1]);

						if ((min == 0 || (min <= age)) && (max == 0 || (max >= age))) {
							table.showItem(e, false, false, false);
						} else {
							table.hideItem(e, false, false);
						}
					}
				}

				table.refresh(false);
				table.sort();
			}
		});

		Label l = new Label("Show events for age:");
		l.addStyleName("largeText");
		lb.addStyleName("mediumText");
		pp.add(l);
		pp.add(lb);
		page.add(pp);

		table.setTitle(title);
		if (Application.isAuthenticated()) {
			Hyperlink addLink = new Hyperlink("Add", PageUrl.event(0));
			table.getTitleBar().addLink(addLink);
		}

		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		page.add(WidgetFactory.newSection(table, "1150px"));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}
}
