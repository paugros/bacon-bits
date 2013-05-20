package com.areahomeschoolers.baconbits.client.content.event;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.event.EventCellTable.EventColumn;
import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.generated.Page;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.util.Url;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory.ContentWidth;
import com.areahomeschoolers.baconbits.client.widgets.DefaultListBox;
import com.areahomeschoolers.baconbits.client.widgets.MonthPicker;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;
import com.areahomeschoolers.baconbits.shared.dto.Arg.EventArg;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap;
import com.areahomeschoolers.baconbits.shared.dto.ArgMap.Status;
import com.areahomeschoolers.baconbits.shared.dto.Event;
import com.areahomeschoolers.baconbits.shared.dto.UserGroup.AccessLevel;

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
	private DefaultListBox categoryBox;
	private EventCellTable table;
	private boolean showCommunity = Url.getBooleanParameter("showCommunity");
	private boolean newlyAdded = Url.getBooleanParameter("newlyAdded");

	public EventListPage(final VerticalPanel page) {
		ArgMap<EventArg> args = new ArgMap<EventArg>(Status.ACTIVE);
		if (showCommunity) {
			args.put(EventArg.SHOW_COMMUNITY);
		}
		if (newlyAdded) {
			args.put(EventArg.NEWLY_ADDED);
		}
		final String title = showCommunity ? "Community Events" : "Events";
		table = new EventCellTable(args);

		if (!showCommunity) {
			VerticalPanel vp = new VerticalPanel();
			vp.setWidth("100%");
			HorizontalPanel hp = new HorizontalPanel();
			hp.setWidth("100%");

			PaddedPanel pp = new PaddedPanel(10);
			pp.setHeight("51px");
			vp.add(pp);
			pp.addStyleName("mediumPadding");
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
			pp.addStyleName("boxedBlurb");

			for (int i = 0; i < pp.getWidgetCount(); i++) {
				pp.setCellVerticalAlignment(pp.getWidget(i), HasVerticalAlignment.ALIGN_MIDDLE);
			}

			HTML message = new HTML();
			message.getElement().getStyle().setMarginBottom(10, Unit.PX);
			String text = "<ul><li>Events must be paid for using PayPal or credit card (MC, Visa, Discover)</li>";
			text += "<li>Register for all your events and then pay for all at once</li>";
			text += "<li>If payment is not received within 24 hours of registering, your registration will be canceled</li>";
			text += "<li>No refunds</li></ul>";
			message.setHTML(text);
			message.addStyleName("smallText errorText");
			vp.add(message);

			hp.add(vp);

			BalanceBox eb = new BalanceBox();
			eb.populate();
			hp.add(eb);
			hp.setCellHorizontalAlignment(eb, HasHorizontalAlignment.ALIGN_RIGHT);

			page.add(WidgetFactory.wrapForWidth(hp, ContentWidth.MAXWIDTH1300PX));

			table.addDataReturnHandler(new DataReturnHandler() {
				@Override
				public void onDataReturn() {
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
				Application.getLayout().setPage(title, page);
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
		table.getTitleBar().addExcelControl();
		table.getTitleBar().addSearchControl();
		page.add(WidgetFactory.newSection(table, ContentWidth.MAXWIDTH1300PX));

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
