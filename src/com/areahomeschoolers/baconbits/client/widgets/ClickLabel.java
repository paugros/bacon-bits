package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;

public class ClickLabel extends Label {
	private List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();
	private Set<MouseDownHandler> handlers = new LinkedHashSet<MouseDownHandler>();

	private boolean enabled = true;
	private String titleText = "";
	private static final String style = "ClickLabel";

	public ClickLabel() {
		this("");
	}

	public ClickLabel(MouseDownHandler mouseDownHandler) {
		this("", mouseDownHandler);
	}

	public ClickLabel(String label) {
		super(label);
		addStyleName(style);
	}

	public ClickLabel(String label, MouseDownHandler mouseDownHandler) {
		this(label);
		addMouseDownHandler(mouseDownHandler);
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		HandlerRegistration reg = null;
		if (enabled) {
			reg = super.addMouseDownHandler(handler);
			registrations.add(reg);
		}

		handlers.add(handler);

		return reg;
	}

	public void click() {
		fireEvent(new MouseDownEvent() {
		});
	}

	public Set<MouseDownHandler> getMouseDownHandlers() {
		return handlers;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled == enabled) {
			return;
		}
		this.enabled = enabled;

		if (!enabled) {
			for (HandlerRegistration reg : registrations) {
				reg.removeHandler();
			}
			removeStyleName(style);
			super.setTitle("");
		} else {
			registrations.clear();
			for (MouseDownHandler handler : handlers) {
				addMouseDownHandler(handler);
			}
			addStyleName(style);
			if (getTitle().isEmpty() && !titleText.isEmpty()) {
				setTitle(titleText);
			}
		}
	}

	@Override
	public void setTitle(String title) {
		titleText = title;
		super.setTitle(title);
	}
}
