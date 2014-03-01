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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragEndEvent;
import com.allen_sauer.gwt.dnd.client.DragHandler;
import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.DayViewDropController;
import com.allen_sauer.gwt.dnd.client.drop.DayViewPickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DayViewResizeController;
import com.areahomeschoolers.baconbits.client.HistoryToken;
import com.areahomeschoolers.baconbits.client.content.calendar.Appointment;
import com.areahomeschoolers.baconbits.client.content.calendar.CalendarSettings.Click;
import com.areahomeschoolers.baconbits.client.content.calendar.CalendarView;
import com.areahomeschoolers.baconbits.client.content.calendar.CalendarWidget;
import com.areahomeschoolers.baconbits.client.content.calendar.DateUtils;
import com.areahomeschoolers.baconbits.client.content.calendar.event.DaySelectionHandler;
import com.areahomeschoolers.baconbits.client.content.calendar.event.WeekSelectionHandler;
import com.areahomeschoolers.baconbits.client.content.calendar.theme.google.client.GoogleDayViewStyleManager;
import com.areahomeschoolers.baconbits.client.content.calendar.util.AppointmentUtil;
import com.areahomeschoolers.baconbits.client.util.PageUrl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("deprecation")
public class DayView extends CalendarView {

	private DayViewHeader dayViewHeader = null;
	private DayViewBody dayViewBody = null;
	private DayViewMultiDayBody multiViewBody = null;
	private DayViewLayoutStrategy layoutStrategy = null;

	private final List<AppointmentWidget> appointmentWidgets = new ArrayList<AppointmentWidget>();
	/**
	 * List of AppointmentAdapter objects that represent the currently selected appointment.
	 */
	private List<AppointmentWidget> selectedAppointmentWidgets = new ArrayList<AppointmentWidget>();

	private List<Widget> resizeControlledWidgets = new LinkedList<Widget>();
	private List<Widget> dragControlledWidgets = new LinkedList<Widget>();

	private final DayViewStyleManager styleManager = GWT.create(GoogleDayViewStyleManager.class);

	private DayViewResizeController resizeController = null;

	private DayViewDropController dropController = null;

	private PickupDragController dragController = null;

	private DayViewResizeController proxyResizeController = null;

	public DayView() {
		super();
	}

	@Override
	public HandlerRegistration addDaySelectionHandler(DaySelectionHandler<Date> handler) {
		return dayViewHeader.addDaySelectionHandler(handler);
	}

	@Override
	public HandlerRegistration addWeekSelectionHandler(WeekSelectionHandler<Date> handler) {
		return dayViewHeader.addWeekSelectionHandler(handler);
	}

	@Override
	public void attach(CalendarWidget widget) {
		super.attach(widget);

		if (dayViewBody == null) {
			dayViewBody = new DayViewBody(this);
			dayViewHeader = new DayViewHeader(this);
			layoutStrategy = new DayViewLayoutStrategy(this);
			if (getSettings().isMultidayVisible()) {
				multiViewBody = new DayViewMultiDayBody(this);
			}
		}

		calendarWidget.getRootPanel().add(dayViewHeader);
		if (getSettings().isMultidayVisible()) {
			calendarWidget.getRootPanel().add(multiViewBody);
		}
		calendarWidget.getRootPanel().add(dayViewBody);

		if (getSettings() != null) {
			scrollToHour(getSettings().getScrollToHour());
		}

		// Creates the different Controllers, if needed
		createDragController();
		createDropController();
		createResizeController();
	}

	@Override
	public void doLayout() {
		// PERFORM APPOINTMENT LAYOUT NOW
		final Date date = (Date) calendarWidget.getDate().clone();

		if (getSettings().isMultidayVisible()) {
			multiViewBody.setDays((Date) date.clone(), calendarWidget.getDays());
		}

		dayViewHeader.setDays((Date) date.clone(), calendarWidget.getDays());
		dayViewHeader.setYear((Date) date.clone());
		dayViewBody.setDays((Date) date.clone(), calendarWidget.getDays());
		dayViewBody.getTimeline().prepare();

		dropController.setColumns(calendarWidget.getDays());
		dropController.setIntervalsPerHour(calendarWidget.getSettings().getIntervalsPerHour());
		dropController.setDayStartsAt(getSettings().getDayStartsAt());
		dropController.setDate((Date) calendarWidget.getDate().clone());
		dropController.setSnapSize(calendarWidget.getSettings().getPixelsPerInterval());
		dropController.setMaxProxyHeight(getMaxProxyHeight());
		resizeController.setIntervalsPerHour(calendarWidget.getSettings().getIntervalsPerHour());
		resizeController.setDayStartsAt(getSettings().getDayStartsAt());
		resizeController.setSnapSize(calendarWidget.getSettings().getPixelsPerInterval());
		proxyResizeController.setSnapSize(calendarWidget.getSettings().getPixelsPerInterval());
		proxyResizeController.setIntervalsPerHour(calendarWidget.getSettings().getIntervalsPerHour());
		proxyResizeController.setDayStartsAt(getSettings().getDayStartsAt());

		for (Widget widget : resizeControlledWidgets) {
			resizeController.makeNotDraggable(widget);
		}
		resizeControlledWidgets.clear();
		for (Widget widget : dragControlledWidgets) {
			dragController.makeNotDraggable(widget);
		}
		dragControlledWidgets.clear();

		this.selectedAppointmentWidgets.clear();
		appointmentWidgets.clear();

		// HERE IS WHERE WE DO THE LAYOUT
		Date startDate = (Date) calendarWidget.getDate().clone();
		Date endDate = (Date) calendarWidget.getDate().clone();
		endDate.setDate(endDate.getDate() + 1);
		DateUtils.resetTime(startDate);
		DateUtils.resetTime(endDate);

		startDate.setHours(startDate.getHours());
		endDate.setHours(endDate.getHours());

		for (int i = 0; i < calendarWidget.getDays(); i++) {

			List<Appointment> filteredList = AppointmentUtil.filterListByDate(calendarWidget.getAppointments(), startDate, endDate);

			// perform layout
			List<AppointmentAdapter> appointmentAdapters = layoutStrategy.doLayout(filteredList, i, calendarWidget.getDays());

			// add all appointments back to the grid
			addAppointmentsToGrid(appointmentAdapters, false);

			startDate.setDate(startDate.getDate() + 1);
			endDate.setDate(endDate.getDate() + 1);
		}

		List<Appointment> filteredList = AppointmentUtil.filterListByDateRange(calendarWidget.getAppointments(), calendarWidget.getDate(),
				calendarWidget.getDays());

		ArrayList<AppointmentAdapter> adapterList = new ArrayList<AppointmentAdapter>();
		int desiredHeight = layoutStrategy.doMultiDayLayout(filteredList, adapterList, calendarWidget.getDate(), calendarWidget.getDays());

		if (getSettings().isMultidayVisible()) {
			if (getSettings().getMultiDayMaxPixelsHeight() > 0) {
				desiredHeight = Math.min(desiredHeight, getSettings().getMultiDayMaxPixelsHeight());
			}
			multiViewBody.setViewHeight(desiredHeight + "px");

			addAppointmentsToGrid(adapterList, true);
		}
	}

	@Override
	public void doSizing() {
		if (calendarWidget.getOffsetHeight() > 0) {
			int height = 0;
			if (getSettings().isMultidayVisible()) {
				height = calendarWidget.getOffsetHeight() - 2 - dayViewHeader.getOffsetHeight() - multiViewBody.getOffsetHeight();
			} else {
				height = calendarWidget.getOffsetHeight() - 2 - dayViewHeader.getOffsetHeight();
			}
			if (height < 0) {
				height = 0;
			}
			dayViewBody.setHeight(height + "px");
		}
	}

	@Override
	public String getStyleName() {
		return "gwt-cal";
	}

	@Override
	public void onAppointmentSelected(Appointment appointment) {

		List<AppointmentWidget> clickedAppointmentAdapters = findAppointmentWidget(appointment);

		if (!clickedAppointmentAdapters.isEmpty()) {
			for (AppointmentWidget adapter : selectedAppointmentWidgets) {
				styleManager.applyStyle(adapter, false);
			}

			for (AppointmentWidget adapter : clickedAppointmentAdapters) {
				styleManager.applyStyle(adapter, true);
			}

			selectedAppointmentWidgets.clear();
			selectedAppointmentWidgets = clickedAppointmentAdapters;

			float height = clickedAppointmentAdapters.get(0).getHeight();
			// scrollIntoView ONLY if the appointment fits in the viewable area
			if (dayViewBody.getScrollPanel().getOffsetHeight() > height) {
				DOM.scrollIntoView(clickedAppointmentAdapters.get(0).getElement());
			}
		}
	}

	@Override
	public void onDeleteKeyPressed() {
		if (calendarWidget.getSelectedAppointment() != null) {
			calendarWidget.fireDeleteEvent(calendarWidget.getSelectedAppointment());
		}
	}

	@Override
	public void onDoubleClick(Element element, Event event) {

		List<AppointmentWidget> list = findAppointmentWidgetsByElement(element);
		if (!list.isEmpty()) {
			Appointment appt = list.get(0).getAppointment();
			// if (appt.equals(calendarWidget.getSelectedAppointment()))
			calendarWidget.fireOpenEvent(appt);
			// exit out
		} else if (getSettings().getTimeBlockClickNumber() == Click.Double && element == dayViewBody.getGrid().gridOverlay.getElement()) {
			int x = DOM.eventGetClientX(event) + Window.getScrollLeft();
			int y = DOM.eventGetClientY(event) + Window.getScrollTop();
			timeBlockClick(x, y);
		}
	}

	@Override
	public void onDownArrowKeyPressed() {
		calendarWidget.selectNextAppointment();
	}

	@Override
	public void onLeftArrowKeyPressed() {
		calendarWidget.selectPreviousAppointment();
	}

	@Override
	public void onMouseOver(final Element element, final Event event) {
		Appointment appointment = findAppointmentByElement(element);
		calendarWidget.fireMouseOverEvent(appointment, element);
	}

	@Override
	public void onRightArrowKeyPressed() {
		calendarWidget.selectNextAppointment();
	}

	@Override
	public void onSingleClick(final Element element, final Event event) {

		// Ignore the scroll panel
		if (dayViewBody.getScrollPanel().getElement().equals(element)) {
			return;
		}

		Appointment appointment = findAppointmentByElement(element);

		HistoryToken.set(PageUrl.event(Integer.parseInt(appointment.getId())));

		// if (appointment != null) {
		// selectAppointment(appointment);
		// } else if ((getSettings().getTimeBlockClickNumber() == Click.Single || getSettings().getTimeBlockClickNumber() == Click.Drag)
		// && element == dayViewBody.getGrid().gridOverlay.getElement()) {
		// int x = DOM.eventGetClientX(event) + Window.getScrollLeft();
		// int y = DOM.eventGetClientY(event) + Window.getScrollTop();
		// timeBlockClick(x, y);
		// }
	}

	@Override
	public void onUpArrowKeyPressed() {
		calendarWidget.selectPreviousAppointment();
	}

	@Override
	public void scrollToHour(final int hour) {
		dayViewBody.getScrollPanel().setVerticalScrollPosition(
				(hour - getSettings().getDayStartsAt()) * getSettings().getIntervalsPerHour() * getSettings().getPixelsPerInterval());
	}

	/**
	 * Adds the Appointments to the view.
	 * 
	 * @param appointmentList
	 *            List of Appointments
	 * @param addToMultiView
	 *            <code>true</code> if is adding the appointments to the multiview section, <code>false</code> otherwise
	 */
	private void addAppointmentsToGrid(final List<AppointmentAdapter> appointmentList, final boolean addToMultiView) {
		for (AppointmentAdapter appt : appointmentList) {
			AppointmentWidget panel = new AppointmentWidget();
			panel.setWidth(appt.getWidth());
			panel.setHeight(appt.getHeight());
			panel.setTitle(appt.getAppointment().getTitle());
			panel.setTop(appt.getTop());
			panel.setLeft(appt.getLeft());
			panel.setAppointment(appt.getAppointment());

			boolean selected = calendarWidget.isTheSelectedAppointment(panel.getAppointment());
			if (selected) {
				selectedAppointmentWidgets.add(panel);
			}
			styleManager.applyStyle(panel, selected);
			appointmentWidgets.add(panel);

			if (addToMultiView) {
				panel.setMultiDay(true);
				this.multiViewBody.add(panel);
			} else {
				panel.setDescription(appt.getAppointment().getDescription());
				dayViewBody.getGrid().grid.add(panel);

				// make footer 'draggable'
				if (calendarWidget.getSettings().isEnableDragDrop() && !appt.getAppointment().isReadOnly()) {
					resizeController.makeDraggable(panel.getResizeHandle());
					resizeControlledWidgets.add(panel.getResizeHandle());
					dragController.makeDraggable(panel, panel.getMoveHandle());
					dragControlledWidgets.add(panel);
				}
			}
		}
	}

	private void createDragController() {
		if (dragController == null) {
			dragController = new DayViewPickupDragController(dayViewBody.getGrid().grid, false);
			dragController.setBehaviorDragProxy(true);
			dragController.setBehaviorDragStartSensitivity(1);
			dragController.setBehaviorConstrainedToBoundaryPanel(true); // do I need these?
			dragController.setConstrainWidgetToBoundaryPanel(true); // do I need these?
			dragController.setBehaviorMultipleSelection(false);
			dragController.addDragHandler(new DragHandler() {

				@Override
				public void onDragEnd(DragEndEvent event) {
					Appointment appt = ((AppointmentWidget) event.getContext().draggable).getAppointment();
					calendarWidget.setCommittedAppointment(appt);
					calendarWidget.fireUpdateEvent(appt);
				}

				@Override
				public void onDragStart(DragStartEvent event) {
					Appointment appt = ((AppointmentWidget) event.getContext().draggable).getAppointment();
					calendarWidget.setRollbackAppointment(appt.cloneAppointment());
					((DayViewPickupDragController) dragController).setMaxProxyHeight(getMaxProxyHeight());
				}

				@Override
				public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
				}

				@Override
				public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
				}
			});
		}
	}

	private void createDropController() {
		if (dropController == null) {
			dropController = new DayViewDropController(dayViewBody.getGrid().grid);
			dragController.registerDropController(dropController);
		}
	}

	private void createResizeController() {
		if (resizeController == null) {
			resizeController = new DayViewResizeController(dayViewBody.getGrid().grid);
			resizeController.addDragHandler(new DragHandler() {

				@Override
				public void onDragEnd(DragEndEvent event) {
					Appointment appt = ((AppointmentWidget) event.getContext().draggable.getParent()).getAppointment();
					calendarWidget.setCommittedAppointment(appt);
					calendarWidget.fireUpdateEvent(appt);
				}

				@Override
				public void onDragStart(DragStartEvent event) {
					calendarWidget.setRollbackAppointment(((AppointmentWidget) event.getContext().draggable.getParent()).getAppointment().cloneAppointment());
				}

				@Override
				public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
				}

				@Override
				public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
				}
			});
		}

		if (proxyResizeController == null) {
			proxyResizeController = new DayViewResizeController(dayViewBody.getGrid().grid);
			proxyResizeController.addDragHandler(new DragHandler() {
				long startTime = 0L;
				int initialX = 0;
				int initialY = 0;
				Date startDate;

				@Override
				public void onDragEnd(DragEndEvent event) {
					long clickTime = System.currentTimeMillis() - startTime;
					int y = event.getContext().mouseY;
					if (clickTime <= 500 && initialY == y) {
						calendarWidget.fireTimeBlockClickEvent(startDate);
					} else {
						Appointment appt = ((AppointmentWidget) event.getContext().draggable.getParent()).getAppointment();
						calendarWidget.setCommittedAppointment(appt);
						calendarWidget.fireCreateEvent(appt);
					}
				}

				@Override
				public void onDragStart(DragStartEvent event) {
					startTime = System.currentTimeMillis();
					initialX = event.getContext().mouseX;
					initialY = event.getContext().mouseY;
					startDate = getCoordinatesDate(initialX, initialY);
					calendarWidget.setRollbackAppointment(null);
				}

				@Override
				public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
				}

				@Override
				public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {
				}
			});
		}
	}

	/**
	 * Returns the {@link Appointment} indirectly associated to the passed <code>element</code>. Each Appointment drawn on the CalendarView maps to a Widget and
	 * therefore an Element. This method attempts to find an Appointment based on the provided Element. If no match is found a null value is returned.
	 * 
	 * @param element
	 *            Element to look up.
	 * @return Appointment matching the element.
	 */
	private Appointment findAppointmentByElement(Element element) {
		Appointment appointmentAtElement = null;
		for (AppointmentWidget widget : appointmentWidgets) {
			if (DOM.isOrHasChild(widget.getElement(), element)) {
				appointmentAtElement = widget.getAppointment();
				break;
			}
		}
		return appointmentAtElement;
	}

	/**
	 * Finds any related adapters that match the given Appointment.
	 * 
	 * @param appt
	 *            Appointment to match.
	 * @return List of related AppointmentWidget objects.
	 */
	private List<AppointmentWidget> findAppointmentWidget(Appointment appt) {
		ArrayList<AppointmentWidget> appointmentAdapters = new ArrayList<AppointmentWidget>();
		if (appt != null) {
			for (AppointmentWidget widget : appointmentWidgets) {
				if (widget.getAppointment().equals(appt)) {
					appointmentAdapters.add(widget);
				}
			}
		}
		return appointmentAdapters;
	}

	private List<AppointmentWidget> findAppointmentWidgetsByElement(Element element) {
		return findAppointmentWidget(findAppointmentByElement(element));
	}

	private Date getCoordinatesDate(int x, int y) {
		int left = dayViewBody.getGrid().gridOverlay.getAbsoluteLeft();
		int top = dayViewBody.getScrollPanel().getAbsoluteTop();
		int width = dayViewBody.getGrid().gridOverlay.getOffsetWidth();
		int scrollOffset = dayViewBody.getScrollPanel().getVerticalScrollPosition();

		// x & y are based on screen position,need to get x/y relative to
		// component
		int relativeY = y - top + scrollOffset;
		int relativeX = x - left;

		// find the interval clicked and day clicked
		double interval = Math.floor(relativeY / (double) getSettings().getPixelsPerInterval());
		double day = Math.floor(relativeX / ((double) width / (double) calendarWidget.getDays()));

		// create new appointment date based on click
		Date newStartDate = calendarWidget.getDate();
		newStartDate.setHours(getSettings().getDayStartsAt());
		newStartDate.setMinutes(0);
		newStartDate.setSeconds(0);
		newStartDate.setMinutes((int) interval * (60 / getSettings().getIntervalsPerHour()));
		newStartDate.setDate(newStartDate.getDate() + (int) day);

		return newStartDate;
	}

	private int getMaxProxyHeight() {
		// For a comfortable use, the Proxy should be, top 2/3 (66%) of the view
		return (2 * (dayViewBody.getScrollPanel().getOffsetHeight() / 3));
	}

	private void timeBlockClick(int x, int y) {
		int left = dayViewBody.getGrid().gridOverlay.getAbsoluteLeft();
		int top = dayViewBody.getScrollPanel().getAbsoluteTop();
		int width = dayViewBody.getGrid().gridOverlay.getOffsetWidth();
		int scrollOffset = dayViewBody.getScrollPanel().getVerticalScrollPosition();

		// x & y are based on screen position,need to get x/y relative to
		// component
		int relativeY = y - top + scrollOffset;
		int relativeX = x - left;

		// find the interval clicked and day clicked
		double day = Math.floor(relativeX / ((double) width / (double) calendarWidget.getDays()));

		Date newStartDate = getCoordinatesDate(x, y);

		if (getSettings().getTimeBlockClickNumber() != Click.Drag) {
			calendarWidget.fireTimeBlockClickEvent(newStartDate);
		} else {
			int snapSize = calendarWidget.getSettings().getPixelsPerInterval();
			// Create the proxy
			width = width / calendarWidget.getDays();
			left = (int) day * width;
			// Adjust the start to the closest interval
			top = (int) Math.floor((float) relativeY / snapSize) * snapSize;

			AppointmentWidget proxy = new AppointmentWidget();
			Appointment app = new Appointment();
			app.setStart(newStartDate);
			app.setEnd(newStartDate);
			proxy.setAppointment(app);
			proxy.setStart(newStartDate);
			proxy.setPixelSize(width, /* height */snapSize);
			dayViewBody.getGrid().grid.add(proxy, left, top);
			styleManager.applyStyle(proxy, false);
			proxyResizeController.makeDraggable(proxy.getResizeHandle());

			NativeEvent evt = Document.get().createMouseDownEvent(1, 0, 0, x, y, false, false, false, false, NativeEvent.BUTTON_LEFT);
			proxy.getResizeHandle().getElement().dispatchEvent(evt);
		}
	}

}