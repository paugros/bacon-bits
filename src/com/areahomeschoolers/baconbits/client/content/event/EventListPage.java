package com.areahomeschoolers.baconbits.client.content.event;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.MonthPicker;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventListPage implements Page {
	private MonthPicker monthBox;
	private DefaultListBox ageBox;
	private EventCellTable table;

	public EventListPage(final VerticalPanel page) {
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		final String title = "Events";
		table = new EventCellTable(args);

		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		HorizontalPanel hp = new HorizontalPanel();
		hp.setWidth("100%");

		HTML message = new HTML();
		message.getElement().getStyle().setMarginBottom(10, Unit.PX);
		String text = "<b><font class=errorText>New this year:</font></b> all events must be paid for using PayPal or credit card (MC, Visa, Discover).<br>You can register for all your events then pay all at once.";
		message.setHTML(text);
		vp.add(message);

		PaddedPanel pp = new PaddedPanel();
		vp.add(pp);
		pp.addStyleName("mediumPadding");
		ageBox = new DefaultListBox();
		ageBox.addItem("all ages", 0);
		for (int age = 1; age < 19; age++) {
			ageBox.addItem(Integer.toString(age) + " year-olds", age);
		}

		ageBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				applyTableFilter();
			}
		});

		monthBox = new MonthPicker();
		monthBox.getListBox().setItemText(0, "any month");
		monthBox.getListBox().setSelectedIndex(0);
		monthBox.addValueChangeCommand(new Command() {
			@Override
			public void execute() {
				applyTableFilter();
			}
		});

		Label show = new Label("Show events for ");
		pp.add(show);
		pp.add(ageBox);

		Label in = new Label("in");
		pp.add(in);
		pp.add(monthBox);
		pp.getElement().getStyle().setBackgroundColor("#c5eabf");

		for (int i = 0; i < pp.getWidgetCount(); i++) {
			pp.setCellVerticalAlignment(pp.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
		}

		hp.add(vp);

		EventBalanceBox eb = new EventBalanceBox();
		eb.populate();
		hp.add(eb);
		hp.setCellHorizontalAlignment(eb, HasHorizontalAlignment.ALIGN_RIGHT);

		page.add(WidgetFactory.wrapForWidth(hp, ContentWidth.MAXWIDTH1300PX));

		table.setTitle(title);
		if (Application.isAuthenticated()) {
			Hyperlink addLink = new Hyperlink("Add", PageUrl.event(0));
			table.getTitleBar().addLink(addLink);
		}

		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1300PX));

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				Application.getLayout().setPage(title, page);
			}
		});

		table.populate();
	}

	private void applyTableFilter() {
		// first
		int month = monthBox.getMonth();
		int age = ageBox.getIntValue();

		if (month == 0 && age == 0) {
			table.showAllItems();
			return;
		}

		for (Event e : table.getFullList()) {
			boolean monthMatch = false;
			boolean ageMatch = false;

			// month
			monthMatch = month == 0 || (ClientDateUtils.getMonth(e.getStartDate()) == month);

			// age
			String rangeText = e.getAgeRanges();

			if (age == 0 || rangeText == null) {
				ageMatch = true;
			} else {
				String[] ranges = rangeText.split(",");

				for (int i = 0; i < ranges.length; i++) {
					String[] limits = ranges[i].split("-");

					int min = Integer.parseInt(limits[0]);
					int max = Integer.parseInt(limits[1]);

					ageMatch = ((min == 0 || (min <= age)) && (max == 0 || (max >= age)));
				}
			}

			if (monthMatch && ageMatch) {
				table.showItem(e, false, false, false);
			} else {
				table.hideItem(e, false, false);
			}
		}

		table.refresh(false);
		table.sort();
	}
}
