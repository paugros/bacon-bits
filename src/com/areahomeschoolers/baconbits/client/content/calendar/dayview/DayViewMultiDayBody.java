/*
 * This file is part of gwt-cal
 * Copyright (C) 2010  Scottsdale Software LLC
 *
 * gwt-cal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/
 */
package com.areahomeschoolers.baconbits.client.content.calendar.dayview;

import java.util.Date;

import com.areahomeschoolers.baconbits.client.content.calendar.HasSettings;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A widget to render multi-day appointments in a day view.
 * 
 * @author Brad Rydzewski
 */
@SuppressWarnings("deprecation")
public class DayViewMultiDayBody extends Composite {

	private FlexTable header = new FlexTable();
	private AbsolutePanel grid = new AbsolutePanel();
	private AbsolutePanel splitter = new AbsolutePanel();
	private static final String TIMELINE_EMPTY_CELL_STYLE = "leftEmptyCell";
	// private static final String SCROLLBAR_EMPTY_CELL_STYLE = "rightEmptyCell";
	private static final String DAY_CONTAINER_CELL_STYLE = "centerDayContainerCell";
	private static final String SPLITTER_STYLE = "splitter";
	private SimplePanel gridOverlay = new SimplePanel();
	private ScrollPanel scrollPanel = new ScrollPanel();

	public DayViewMultiDayBody(HasSettings settings) {

		initWidget(header);

		this.header.setStyleName("multiDayBody");
		this.setWidth("100%");

		/*
		 * insert two rows ... first row holds multi-day appointments, second row is just a splitter
		 */
		header.insertRow(0);
		header.insertRow(0);
		// insert 3 cells
		// 1st cell is empty to align with the timeline
		// 2nd cell holds appointments
		// 3rd cell is empty, aligns with scrollbar
		header.insertCell(0, 0);
		header.insertCell(0, 0);
		// header.insertCell(0, 0);

		scrollPanel.setStylePrimaryName("scroll-area");
		// DOM.setStyleAttribute(scrollPanel.getElement(), "overflowX", "hidden");
		DOM.setStyleAttribute(scrollPanel.getElement(), "overflowY", "scroll");
		scrollPanel.add(grid);
		DOM.setStyleAttribute(grid.getElement(), "overflow", "visible");

		// add panel to hold appointments
		header.setWidget(0, 1, scrollPanel);

		// set cell styles
		header.getCellFormatter().setStyleName(0, 0, TIMELINE_EMPTY_CELL_STYLE);
		header.getCellFormatter().setStyleName(0, 1, DAY_CONTAINER_CELL_STYLE);
		// header.getCellFormatter().setStyleName(0, 2, SCROLLBAR_EMPTY_CELL_STYLE);
		// header.getCellFormatter().setWidth(0, 2, WindowUtils.getScrollBarWidth(true) + "px");

		// default grid to 50px height
		scrollPanel.setHeight("30px");

		header.getFlexCellFormatter().setColSpan(1, 0, 2);
		header.setCellPadding(0);
		header.setBorderWidth(0);
		header.setCellSpacing(0);

		splitter.setStylePrimaryName(SPLITTER_STYLE);
		header.setWidget(1, 0, splitter);
	}

	public void add(AppointmentWidget w) {
		grid.add(w);
	}

	public void setDays(Date date, int days) {

		grid.clear();
		float dayWidth = 100f / days;
		float dayLeft;

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

	public void setViewHeight(String height) {
		scrollPanel.setHeight(height);
	}
}