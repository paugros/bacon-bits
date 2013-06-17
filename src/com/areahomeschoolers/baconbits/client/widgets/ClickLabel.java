package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Label;

public class ClickLabel extends Label {
	private List<HandlerRegistration> registrations = new ArrayList<HandlerRegistration>();
	private Set<ClickHandler> handlers = new LinkedHashSet<ClickHandler>();

	private boolean enabled = true;
	private String titleText = "";
	private static final String style = "ClickLabel";

	public ClickLabel() {
		this("");
	}

	public ClickLabel(ClickHandler clickHandler) {
		this("", clickHandler);
	}

	public ClickLabel(String label) {
		super(label);
		addStyleName(style);
	}

	public ClickLabel(String label, ClickHandler clickHandler) {
		this(label);
		addClickHandler(clickHandler);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		HandlerRegistration reg = null;
		if (enabled) {
			reg = super.addClickHandler(handler);
			registrations.add(reg);
		}

		handlers.add(handler);

		return reg;
	}

	public void click() {
		fireEvent(new ClickEvent() {
		});
	}

	public Set<ClickHandler> getClickHandlers() {
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
			for (ClickHandler handler : handlers) {
				addClickHandler(handler);
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
