package com.areahomeschoolers.baconbits.client.content.event;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.content.Sidebar;
import com.areahomeschoolers.baconbits.client.content.Sidebar.MiniModule;
import com.areahomeschoolers.baconbits.client.content.event.EventTable.EventColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.GeocoderTextBox;
import com.areahomeschoolers.baconbits.client.widgets.MonthPicker;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public final class EventListPage implements Page {
	private MonthPicker monthBox;
	private DefaultListBox ageBox;
	private DefaultListBox categoryBox;
	private EventTable table;
	private static final String NEWLY_ADDED_TOKEN = "newlyAdded";
	private boolean showCommunity = Url.getBooleanParameter("showCommunity");
	private boolean newlyAdded = Url.getBooleanParameter(NEWLY_ADDED_TOKEN);

	public EventListPage(final VerticalPanel page) {
		final ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		if (showCommunity) {
			args.put(EventArg.ONLY_COMMUNITY);
		}
		if (newlyAdded) {
			args.put(EventArg.NEWLY_ADDED);
		}
		final String title = showCommunity ? "Community Events" : "Events";
		table = new EventTable(args);

		if (!showCommunity) {
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth("100%");
			HorizontalPanel hp = new HorizontalPanel();
			hp.setWidth("100%");

			PaddedPanel pp = new PaddedPanel(10);
			VerticalPanel vpp = new VerticalPanel();
			vpp.setSpacing(8);
			vpp.add(pp);
			vp.add(vpp);
			categoryBox = new DefaultListBox();
			categoryBox.addItem("all", 0);
			categoryBox.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					applyTableFilter();
				}
			});

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

			pp.add(new Label("Show "));
			pp.add(categoryBox);
			pp.add(new Label(" events for "));
			pp.add(ageBox);

			Label in = new Label("in");
			pp.add(in);
			pp.add(monthBox);

			for (int i = 0; i < pp.getWidgetCount(); i++) {
				pp.setCellVerticalAlignment(pp.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
			}

			PaddedPanel bottom = new PaddedPanel(15);
			// within miles
			final DefaultListBox milesInput = new DefaultListBox();
			final GeocoderTextBox locationInput = new GeocoderTextBox();
			milesInput.addItem("5", 5);
			milesInput.addItem("10", 10);
			milesInput.addItem("25", 25);
			milesInput.addItem("50", 50);
			milesInput.addChangeHandler(new ChangeHandler() {
				@Override
				public void onChange(ChangeEvent event) {
					args.put(EventArg.WITHIN_MILES, milesInput.getIntValue());
					if (!locationInput.getText().isEmpty()) {
						table.populate();
					}
				}
			});

			locationInput.setClearCommand(new Command() {
				@Override
				public void execute() {
					args.remove(EventArg.WITHIN_LAT);
					args.remove(EventArg.WITHIN_LNG);
					table.populate();
				}
			});

			locationInput.setChangeCommand(new Command() {
				@Override
				public void execute() {
					args.put(EventArg.WITHIN_LAT, Double.toString(locationInput.getLat()));
					args.put(EventArg.WITHIN_LNG, Double.toString(locationInput.getLng()));
					args.put(EventArg.WITHIN_MILES, milesInput.getIntValue());
					table.populate();
				}
			});

			bottom.add(new Label("within"));
			bottom.add(milesInput);
			bottom.add(new Label("miles of"));
			bottom.add(locationInput);

			for (int i = 0; i < bottom.getWidgetCount(); i++) {
				bottom.setCellVerticalAlignment(bottom.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
			}
			vpp.add(bottom);

			CheckBox cb = new CheckBox("Only show recently added events");
			cb.setValue(newlyAdded, false);

			cb.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if (event.getValue()) {
						args.put(EventArg.NEWLY_ADDED);
						HistoryToken.append(NEWLY_ADDED_TOKEN + "=true", false);
					} else {
						args.remove(EventArg.NEWLY_ADDED);
						HistoryToken.removeToken(NEWLY_ADDED_TOKEN, false);
					}
					table.populate();
				}
			});
			vpp.add(cb);

			vpp.addStyleName("boxedBlurb");

			hp.add(vp);

			page.add(hp);
			// page.add(WidgetFactory.wrapForWidth(hp, ContentWidth.MAXWIDTH1000PX));

			table.addDataReturnHandler(new DataReturnHandler() {
				@Override
				public void onDataReturn() {
					if (table.returnHandlersHaveRun()) {
						return;
					}
					// add in categories
					Map<Integer, String> categories = new HashMap<Integer, String>();
					for (Event item : table.getFullList()) {
						categories.put(item.getCategoryId(), item.getCategory());
					}

					for (int id : categories.keySet()) {
						categoryBox.addItem(categories.get(id), id);
					}
				}
			});
		}

		table.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				if (table.returnHandlersHaveRun()) {
					return;
				}

				Sidebar sb = Sidebar.create(MiniModule.CITRUS, MiniModule.LINKS, MiniModule.MY_EVENTS, MiniModule.COMMUNITY_EVENTS, MiniModule.SELL_BOOKS);
				Application.getLayout().setPage(title, sb, page);
			}
		});

		if (showCommunity) {
			table.removeColumn(EventColumn.CATEGORY);
			table.removeColumn(EventColumn.REGISTER);
			table.removeColumn(EventColumn.REGISTERED);
		}
		table.setTitle(title);
		if (Application.hasRole(AccessLevel.GROUP_ADMINISTRATORS)) {
			Hyperlink addLink = new Hyperlink("Add", PageUrl.event(0));
			table.getTitleBar().addLink(addLink);
		}

		table.addStatusFilterBox();
		table.getTitleBar().addSearchControl();
		table.getTitleBar().addExcelControl();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH900PX));

		table.populate();
	}

	private void applyTableFilter() {
		// first
		int month = monthBox.getMonth();
		int age = ageBox.getIntValue();
		int categoryId = categoryBox.getIntValue();

		if (month == 0 && age == 0 && categoryId == 0) {
			table.showAllItems();
			return;
		}

		for (Event e : table.getFullList()) {
			boolean monthMatch = false;
			boolean ageMatch = false;
			boolean categoryMatch = false;

			// month
			monthMatch = month == 0 || (ClientDateUtils.getMonth(e.getStartDate()) == month);

			// category
			categoryMatch = categoryId == 0 || (categoryId == e.getCategoryId());

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
					if (ageMatch) {
						break;
					}
				}
			}

			if (monthMatch && ageMatch && categoryMatch) {
				table.showItem(e, false, false, false);
			} else {
				table.hideItem(e, false, false);
			}
		}

		table.refresh(false);
		table.sort();
	}
}
