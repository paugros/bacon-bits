package com.areahomeschoolers.baconbits.client.content.calendar.dayview;

import com.areahomeschoolers.baconbits.client.content.calendar.HasSettings;
import com.areahomeschoolers.baconbits.client.content.calendar.util.FormattingUtil;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The DayGrid draws the grid that displays days / time intervals in the body of the calendar.
 * 
 * @author Brad Rydzewski
 */
@SuppressWarnings("deprecation")
public class DayViewGrid /* Impl */extends Composite {

	class Div extends ComplexPanel {

		public Div() {
			setElement(DOM.createDiv());
		}

		@Override
		public void add(Widget w) {
			super.add(w, getElement());
		}

		@Override
		public boolean remove(Widget w) {
			return super.remove(w);
		}
	}

	protected AbsolutePanel grid = new AbsolutePanel();
	protected SimplePanel gridOverlay = new SimplePanel();

	private HasSettings settings = null;

	private static final int HOURS_PER_DAY = 24;

	public DayViewGrid(HasSettings settings) { // was DayViewGridImpl
		initWidget(grid);
		this.settings = settings;
	}

	public void build(int workingHourStart, int workingHourStop, int days) {

		grid.clear();

		int intervalsPerHour = settings.getSettings().getIntervalsPerHour(); // 2; //30 minute intervals
		float intervalSize = settings.getSettings().getPixelsPerInterval();

		this.setHeight((intervalsPerHour * (intervalSize) * 24) + "px");

		float dayWidth = 100f / days;
		float dayLeft = 0f;

		int dayStartsAt = settings.getSettings().getDayStartsAt();

		for (int i = 0; i < HOURS_PER_DAY; i++) {
			boolean isWorkingHours = ((i + dayStartsAt) >= workingHourStart && (i + dayStartsAt) <= workingHourStop);
			// create major interval
			SimplePanel sp1 = new SimplePanel();
			sp1.setStyleName("major-time-interval");
			sp1.setHeight(intervalSize + FormattingUtil.getBorderOffset() + "px");

			// if working hours set
			if (isWorkingHours) {
				sp1.addStyleName("working-hours");
			}

			// add to body
			grid.add(sp1);

			for (int x = 0; x < intervalsPerHour - 1; x++) {
				SimplePanel sp2 = new SimplePanel();
				sp2.setStyleName("minor-time-interval");

				sp2.setHeight(intervalSize + FormattingUtil.getBorderOffset() + "px");
				if (isWorkingHours) {
					sp2.addStyleName("working-hours");
				}
				grid.add(sp2);
			}
		}

		for (int day = 0; day < days; day++) {
			dayLeft = dayWidth * day;
			SimplePanel dayPanel = new SimplePanel();
			dayPanel.setStyleName("day-separator");
			grid.add(dayPanel);
			DOM.setStyleAttribute(dayPanel.getElement(), "left", dayLeft + "%");
		}

		gridOverlay.setHeight("100%");
		gridOverlay.setWidth("100%");
		DOM.setStyleAttribute(gridOverlay.getElement(), "position", "absolute");
		DOM.setStyleAttribute(gridOverlay.getElement(), "left", "0px");
		DOM.setStyleAttribute(gridOverlay.getElement(), "top", "0px");
		grid.add(gridOverlay);
	}
}