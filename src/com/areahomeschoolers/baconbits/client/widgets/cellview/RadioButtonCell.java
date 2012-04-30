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

public class RadioButtonCell extends AbstractEditableCell<Boolean, Boolean> {

	private static final SafeHtml INPUT_CHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"radio\" tabindex=\"-1\" checked=\"checked\"/>");

	private static final SafeHtml INPUT_UNCHECKED = SafeHtmlUtils.fromSafeConstant("<input type=\"radio\" tabindex=\"-1\"/>");

	private static final SafeHtml INPUT_DISABLED = SafeHtmlUtils.fromSafeConstant("<input disabled=\"disabled\" type=\"radio\" tabindex=\"-1\"/>");

	private boolean dependsOnSelection, handlesSelection;

	private HashSet<Object> disabledItems = new HashSet<Object>();

	public RadioButtonCell() {
		super("change", "keydown");
	}

	/**
	 * Constructs a new {@link RadioButtonCell} that can be configured to depend and/or handle selection
	 * 
	 * @param groupName
	 *            HTML name attribute of the radiobutton
	 * @param dependsOnSelection
	 *            true if the cell depends on the selection state
	 * @param handlesSelection
	 *            true if the cell modifies the selection state
	 */
	public RadioButtonCell(boolean dependsOnSelection, boolean handlesSelection) {
		this();
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

	@Override
	public boolean isEditing(Context context, Element parent, Boolean value) {
		return false;
	}

	public boolean isEnabled(Object key) {
		return !disabledItems.contains(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.cell.client.AbstractCell#onBrowserEvent(com.google.gwt.cell.client.Cell.Context, com.google.gwt.dom.client.Element, java.lang.Object,
	 * com.google.gwt.dom.client.NativeEvent, com.google.gwt.cell.client.ValueUpdater)
	 */
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

			/**
			 * Check the radio button if the enter key was pressed and the cell handles selection or doesn't depend on selection. If the cell depends on
			 * selection but doesn't handle selection, then ignore the enter key and let the SelectionEventManager determine which keys will trigger a change.
			 */
			if (enterPressed && (handlesSelection() || !dependsOnSelection())) {
				isChecked = true;
				input.setChecked(isChecked);
			}

			/**
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
		Boolean viewData = getViewData(context.getKey());
		if (viewData != null && viewData.equals(value)) {
			clearViewData(context.getKey());
			viewData = null;
		}

		if (!disabledItems.contains(context.getKey())) {
			if (value != null && ((viewData != null) ? viewData : value)) {
				sb.append(INPUT_CHECKED);
			} else {
				sb.append(INPUT_UNCHECKED);
			}
		} else {
			sb.append(INPUT_DISABLED);
		}
	}

}
