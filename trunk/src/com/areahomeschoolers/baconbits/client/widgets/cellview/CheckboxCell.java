package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.HashSet;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class CheckboxCell extends AbstractEditableCell<Boolean, Boolean> {
	/**
	 * An html string representation of a checked input box.
	 */
	private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\" checked/>");

	/**
	 * An html string representation of an unchecked input box.
	 */
	private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"checkbox\" tabindex=\"-1\"/>");

	private static final SafeHtml INPUT_DISABLED = SafeHtmlUtils.fromSafeConstant("<input disabled=\"disabled\" type=\"checkbox\" tabindex=\"-1\"/>");
	private static final SafeHtml INPUT_HIDDEN = SafeHtmlUtils.fromSafeConstant("<div></div>");

	private final boolean dependsOnSelection;
	private final boolean handlesSelection;

	private HashSet<Object> disabledItems = new HashSet<Object>();
	private HashSet<Object> hiddenItems = new HashSet<Object>();

	/**
	 * Construct a new {@link CheckboxCell} that optionally controls selection.
	 * 
	 * @param dependsOnSelection
	 *            true if the cell depends on the selection state
	 * @param handlesSelection
	 *            true if the cell modifies the selection state
	 */
	public CheckboxCell(boolean dependsOnSelection, boolean handlesSelection) {
		super("change", "keydown");
		this.dependsOnSelection = dependsOnSelection;
		this.handlesSelection = handlesSelection;
	}

	@Override
	public boolean dependsOnSelection() {
		return dependsOnSelection;
	}

	public void disable(Object key) {
		disabledItems.add(key);
	}

	public void enable(Object key) {
		if (disabledItems.contains(key)) {
			disabledItems.remove(key);
		}
	}

	@Override
	public boolean handlesSelection() {
		return handlesSelection;
	}

	public void hide(Object key) {
		hiddenItems.add(key);
	}

	@Override
	public boolean isEditing(Context context, Element parent, Boolean value) {
		return false;
	}

	public boolean isEnabled(Object key) {
		return !disabledItems.contains(key);
	}

	@Override
	public void onBrowserEvent(Context context, Element parent, Boolean value, NativeEvent event, ValueUpdater<Boolean> valueUpdater) {
		if (disabledItems.contains(context.getKey())) {
			event.stopPropagation();
			return;
		}
		String type = event.getType();

		boolean enterPressed = "keydown".equals(type) && event.getKeyCode() == KeyCodes.KEY_ENTER;
		if ("change".equals(type) || enterPressed) {
			InputElement input = parent.getFirstChild().cast();
			Boolean isChecked = input.isChecked();

			/*
			 * Toggle the value if the enter key was pressed and the cell handles selection or doesn't depend on selection. If the cell depends on selection but
			 * doesn't handle selection, then ignore the enter key and let the SelectionEventManager determine which keys will trigger a change.
			 */
			if (enterPressed && (handlesSelection() || !dependsOnSelection())) {
				isChecked = !isChecked;
				input.setChecked(isChecked);
			}

			/*
			 * Save the new value. However, if the cell depends on the selection, then do not save the value because we can get into an inconsistent state.
			 */
			if (value != isChecked && !dependsOnSelection()) {
				setViewData(context.getKey(), isChecked);
			} else {
				clearViewData(context.getKey());
			}

			if (valueUpdater != null) {
				valueUpdater.update(isChecked);
			}
		}
	}

	@Override
	public void render(Context context, Boolean value, SafeHtmlBuilder sb) {
		// Get the view data.
		Object key = context.getKey();
		Boolean viewData = getViewData(key);
		if (viewData != null && viewData.equals(value)) {
			clearViewData(key);
			viewData = null;
		}

		if (disabledItems.contains(key)) {
			sb.append(INPUT_DISABLED);
		} else if (hiddenItems.contains(key)) {
			sb.append(INPUT_HIDDEN);
		} else {
			if (value != null && ((viewData != null) ? viewData : value)) {
				sb.append(INPUT_CHECKED);
			} else {
				sb.append(INPUT_UNCHECKED);
			}
		}
	}

	public void show(Object key) {
		if (hiddenItems.contains(key)) {
			hiddenItems.remove(key);
		}
	}
}
