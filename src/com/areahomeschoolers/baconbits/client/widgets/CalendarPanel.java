package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.areahomeschoolers.baconbits.client.content.calendar.Calendar;
import com.areahomeschoolers.baconbits.client.content.calendar.CalendarSettings;
import com.areahomeschoolers.baconbits.client.content.calendar.CalendarViews;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ShowRangeEvent;
import com.google.gwt.event.logical.shared.ShowRangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DecoratedTabBar;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class CalendarPanel extends FlowPanel {
	private Calendar calendar = null;
	private DatePicker datePicker = new DatePicker();
	private FlexTable layoutTable = new FlexTable();
	private VerticalPanel leftPanel = new VerticalPanel();
	private AbsolutePanel topPanel = new AbsolutePanel();
	private DecoratorPanel dayViewDecorator = new DecoratorPanel();
	private DecoratorPanel datePickerDecorator = new DecoratorPanel();
	private DecoratedTabBar calendarViewsTabBar = new DecoratedTabBar();
	private List<Date> selectedDates = new ArrayList<Date>();
	private SimplePanel legendPanel = new SimplePanel();

	private CalendarSettings settings = new CalendarSettings();

	public CalendarPanel() {
		configureCalendar();
		configureViewsTabBar();

		datePicker.setValue(new Date());
		datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				setDatePickerSelection(event.getValue());
			}
		});
		datePicker.addShowRangeHandler(new ShowRangeHandler<Date>() {
			@Override
			public void onShowRange(ShowRangeEvent<Date> event) {
				setDatePickerSelection(ClientDateUtils.addDays(event.getStart(), 10));
			}
		});

		topPanel.add(calendarViewsTabBar);
		topPanel.setStyleName("daysTabBar");
		leftPanel.setStyleName("leftPanel");
		leftPanel.add(datePickerDecorator);

		legendPanel.getElement().getStyle().setPadding(15, Unit.PX);
		leftPanel.add(legendPanel);

		datePickerDecorator.add(datePicker);
		dayViewDecorator.add(calendar);

		layoutTable.setWidth("99%");
		layoutTable.setCellPadding(0);
		layoutTable.setCellSpacing(0);
		layoutTable.setText(0, 0, "");
		layoutTable.setWidget(0, 1, topPanel);
		layoutTable.setWidget(1, 1, dayViewDecorator);
		layoutTable.setWidget(1, 0, leftPanel);
		layoutTable.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
		layoutTable.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);
		layoutTable.getCellFormatter().setWidth(1, 0, "50px");
		add(layoutTable);

		getElement().getStyle().setPadding(10, Unit.PX);

		addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						int totalHeight = Window.getClientHeight();
						int height = (totalHeight - calendar.getElement().getAbsoluteTop()) - 25;
						if (height > 500) {
							calendar.setHeight(height + "px");
							calendar.doSizing();
							calendar.doLayout();
						}
					}
				});
			}
		});

	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setLegend(Widget legend) {
		legendPanel.setWidget(legend);
	}

	private void configureCalendar() {
		settings.setOffsetHourLabels(false);

		calendar = new Calendar();
		calendar.setSettings(settings);
		calendar.setWidth("100%");
	}

	/**
	 * Configures the tab bar that allows users to switch views in the calendar.
	 */
	private void configureViewsTabBar() {
		calendarViewsTabBar.addTab("Today");
		calendarViewsTabBar.addTab("1 Day");
		calendarViewsTabBar.addTab("School Week");
		calendarViewsTabBar.addTab("Week");
		calendarViewsTabBar.addTab("Agenda");
		calendarViewsTabBar.addTab("Month");

		calendarViewsTabBar.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int tabIndex = event.getSelectedItem();
				if (tabIndex == 0) {
					datePicker.setCurrentMonth(new Date());
					calendar.setDate(new Date());
					calendar.setView(CalendarViews.DAY, 1);
					calendar.scrollToHour(8);
				} else if (tabIndex == 1) {
					calendar.setView(CalendarViews.DAY, 1);
					calendar.scrollToHour(8);
				} else if (tabIndex == 2) {
					calendar.setView(CalendarViews.DAY, 5);
					calendar.scrollToHour(8);
				} else if (tabIndex == 3) {
					calendar.setView(CalendarViews.DAY, 7);
					calendar.scrollToHour(8);
				} else if (tabIndex == 4) {
					calendar.setView(CalendarViews.AGENDA);
					calendar.setDays(7);
				} else if (tabIndex == 5) {
					calendar.setView(CalendarViews.MONTH);
				}

				setDatePickerSelection(calendar.getDate());

				datePickerDecorator.setVisible(tabIndex > 0);
				legendPanel.setVisible(tabIndex > 0);
			}
		});

		calendarViewsTabBar.selectTab(5, true);
	}

	private void setDatePickerSelection(Date date) {
		int days = calendar.getDays();
		// clear styles
		datePicker.removeStyleFromDates("calendarSelection", selectedDates);
		selectedDates.clear();

		if (days == 5 || days == 7) {
			int dayInWeek = ClientDateUtils.getDayInWeek(date);
			int offset = 1;
			if (days == 5) {
				offset = 2;
			}
			date = ClientDateUtils.addDays(date, ((dayInWeek) * -1) + offset);
			List<Date> dates = new ArrayList<Date>();
			dates.add(date);
			for (int i = 1; i < days; i++) {
				dates.add(ClientDateUtils.addDays(date, i));
			}

			datePicker.addStyleToDates("calendarSelection", dates);
			selectedDates = dates;
		}
		calendar.setDate(date);
	}

}
