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

import com.areahomeschoolers.baconbits.client.content.calendar.Appointment;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AppointmentWidget extends FlowPanel {

   class Div extends ComplexPanel implements HasAllMouseHandlers, HasAllTouchHandlers {

      public Div() {
         setElement(DOM.createDiv());
      }

      @Override
	public void add(Widget w) {
         super.add(w, getElement());
      }

      @Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
         return addDomHandler(handler, MouseDownEvent.getType());
      }

      @Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
         return addDomHandler(handler, MouseUpEvent.getType());
      }

      @Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
         return addDomHandler(handler, MouseOutEvent.getType());
      }

      @Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
         return addDomHandler(handler, MouseOverEvent.getType());
      }

      @Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
         return addDomHandler(handler, MouseMoveEvent.getType());
      }

      @Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
         return addDomHandler(handler, MouseWheelEvent.getType());
      }

		@Override
		public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
			return addDomHandler(handler, TouchStartEvent.getType());
		}

		@Override
		public HandlerRegistration addTouchMoveHandler(TouchMoveHandler handler) {
			return addDomHandler(handler, TouchMoveEvent.getType());
		}

		@Override
		public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
			return addDomHandler(handler, TouchEndEvent.getType());
		}

		@Override
		public HandlerRegistration addTouchCancelHandler(TouchCancelHandler handler) {
			return addDomHandler(handler, TouchCancelEvent.getType());
		}
   }

   private String title;
   private String description;
   private Date start;
   private Date end;
   private boolean selected;
   private float top;
   private float left;
   private float width;
   private float height;
   private Widget headerPanel = new Div();
   private Panel bodyPanel = new SimplePanel();
   private Widget footerPanel = new Div();
   private Panel timelinePanel = new SimplePanel();
   private Panel timelineFillPanel = new SimplePanel();
   private boolean multiDay = false;
   private Appointment appointment;

   public AppointmentWidget() {
      this.setStylePrimaryName("gwt-appointment");
      headerPanel.setStylePrimaryName("header");
      bodyPanel.setStylePrimaryName("body");
      footerPanel.setStylePrimaryName("footer");
      timelinePanel.setStylePrimaryName("timeline");
      timelineFillPanel.setStylePrimaryName("timeline-fill");

      this.add(headerPanel);
      this.add(bodyPanel);
      this.add(footerPanel);
      this.add(timelinePanel);
      timelinePanel.add(timelineFillPanel);
      DOM.setStyleAttribute(this.getElement(), "position", "absolute");
   }
   
   public Widget getBody() {
	   return this.bodyPanel;
   }
   
   public Widget getHeader() {
	   return this.headerPanel;
   }

   public Date getStart() {
      return start;
   }

   public void setStart(Date start) {
      this.start = start;
   }

   public Date getEnd() {
      return end;
   }

   public void setEnd(Date end) {
      this.end = end;
   }

   public boolean isSelected() {
      return selected;
   }

   public float getTop() {
      return top;
   }

   public void setTop(float top) {
      this.top = top;
      DOM.setStyleAttribute(this.getElement(), "top", top + "px");
   }

   public float getLeft() {
      return left;
   }

   public void setLeft(float left) {
      this.left = left;
      DOM.setStyleAttribute(this.getElement(), "left", left + "%");
   }

   public float getWidth() {
      return width;
   }

   public void setWidth(float width) {
      this.width = width;
      DOM.setStyleAttribute(this.getElement(), "width", width + "%");
   }

   public float getHeight() {
      return height;
   }

   public void setHeight(float height) {
      this.height = height;
      DOM.setStyleAttribute(this.getElement(), "height", height + "px");
   }

   @Override
public String getTitle() {
      return title;
   }

   @Override
public void setTitle(String title) {
      this.title = title;
      DOM.setInnerHTML(headerPanel.getElement(), title);
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
      DOM.setInnerHTML(bodyPanel.getElement(), description);
   }

   public void formatTimeline(float top, float height) {
      timelineFillPanel.setHeight(height + "%");
      DOM.setStyleAttribute(timelineFillPanel.getElement(), "top", top + "%");
   }

   public int compareTo(AppointmentWidget appt) {
      // -1 0 1
      // less, equal, greater
      int compare = this.getStart().compareTo(appt.getStart());

      if (compare == 0) {
         compare = appt.getEnd().compareTo(this.getEnd());
      }

      return compare;
   }

   public Widget getMoveHandle() {
      return headerPanel;
   }

   public Widget getResizeHandle() {
      return footerPanel;
   }

   public boolean isMultiDay() {
      return multiDay;
   }

   public void setMultiDay(boolean isMultiDay) {
      this.multiDay = isMultiDay;
   }

   public Appointment getAppointment() {
      return appointment;
   }

   public void setAppointment(Appointment appointment) {
      this.appointment = appointment;
      
      if (appointment.isReadOnly()) {
    	  this.remove(footerPanel);
      }
   }
}