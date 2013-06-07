/*
 * This file is part of gwt-cal
 * Copyright (C) 2011  Scottsdale Software LLC
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

package com.areahomeschoolers.baconbits.client.content.calendar.theme.google.client;

import com.areahomeschoolers.baconbits.client.content.calendar.ThemeAppointmentStyle;

public class GoogleAppointmentStyle implements ThemeAppointmentStyle {

	protected String selectedBorder;

	protected String selectedBackground;
	protected String selectedBackgroundImage;
	protected String selectedBackgroundHeader;
	protected String selectedBackgroundFooter;
	protected String selectedText;
	protected String selectedHeaderText;
	protected String border;
	protected String background;
	protected String backgroundImage;
	protected String backgroundHeader;
	protected String backgroundFooter;
	protected String text;
	protected String headerText;

	public GoogleAppointmentStyle(String border, String background) {
		super();

		// set the border
		this.border = background;
		this.selectedBorder = border;

		// set the body text
		this.text = "#FFFFFF";
		this.selectedText = text;

		// set the header text
		this.headerText = text;
		this.selectedHeaderText = text;

		// set the background colors
		this.background = background;
		this.selectedBackground = background;

		// set the header colors to the same color as the border
		this.backgroundHeader = border;
		this.selectedBackgroundHeader = border;

	}

	@Override
	public String getBackground() {
		return background;
	}

	public String getBackgroundFooter() {
		return backgroundFooter;
	}

	@Override
	public String getBackgroundHeader() {
		return backgroundHeader;
	}

	public String getBackgroundImage() {
		return backgroundImage;
	}

	@Override
	public String getBorder() {
		return border;
	}

	@Override
	public String getHeaderText() {
		return headerText;
	}

	public String getSelectedBackground() {
		return selectedBackground;
	}

	public String getSelectedBackgroundFooter() {
		return selectedBackgroundFooter;
	}

	public String getSelectedBackgroundHeader() {
		return selectedBackgroundHeader;
	}

	@Override
	public String getSelectedBackgroundImage() {
		return selectedBackgroundImage;
	}

	@Override
	public String getSelectedBorder() {
		return selectedBorder;
	}

	public String getSelectedHeaderText() {
		return selectedHeaderText;
	}

	public String getSelectedText() {
		return selectedText;
	}

	public String getText() {
		return text;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public void setBackgroundFooter(String backgroundFooter) {
		this.backgroundFooter = backgroundFooter;
	}

	public void setBackgroundHeader(String backgroundHeader) {
		this.backgroundHeader = backgroundHeader;
	}

	public void setBackgroundImage(String backgroundImage) {
		this.backgroundImage = backgroundImage;
	}

	public void setBorder(String border) {
		this.border = border;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public void setSelectedBackground(String selectedBackground) {
		this.selectedBackground = selectedBackground;
	}

	public void setSelectedBackgroundFooter(String selectedBackgroundFooter) {
		this.selectedBackgroundFooter = selectedBackgroundFooter;
	}

	public void setSelectedBackgroundHeader(String selectedBackgroundHeader) {
		this.selectedBackgroundHeader = selectedBackgroundHeader;
	}

	public void setSelectedBackgroundImage(String selectedBackgroundImage) {
		this.selectedBackgroundImage = selectedBackgroundImage;
	}

	public void setSelectedBorder(String selectedBorder) {
		this.selectedBorder = selectedBorder;
	}

	public void setSelectedHeaderText(String selectedHeaderText) {
		this.selectedHeaderText = selectedHeaderText;
	}

	public void setSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}

	public void setText(String text) {
		this.text = text;
	}
}
