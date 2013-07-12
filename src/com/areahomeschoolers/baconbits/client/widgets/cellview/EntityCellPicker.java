package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.validation.HasValidator;
import com.areahomeschoolers.baconbits.client.validation.Validator;
import com.areahomeschoolers.baconbits.client.validation.ValidatorCommand;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.CustomFocusWidget;
import com.areahomeschoolers.baconbits.client.widgets.RequiredTextBox;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;
import com.areahomeschoolers.baconbits.shared.dto.HasId;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public abstract class EntityCellPicker<T extends EntityDto<T>, U extends Arg, C extends Enum<C> & EntityCellTableColumn<C>> extends Composite implements
		HasValidator, CustomFocusWidget {
	private EntityCellSelector<T, U, C> entitySelector;
	private HorizontalPanel pickerPanel = new HorizontalPanel();
	private FocusPanel focusPanel = new FocusPanel();
	private TextBox textBox = new TextBox();
	private Validator validator = new Validator(textBox, new ValidatorCommand() {
		@Override
		public void validate(Validator validator) {
			if (!entitySelector.isShowing()) {
				validator.setError(entitySelector.getCellTable().getSelectedItemIds().isEmpty());
			}
		}
	});
	private Button changeButton = new Button("Change...");

	protected EntityCellPicker() {
		ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				if (entitySelector != null) {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							entitySelector.center();
						}
					});
				} else {
					AlertDialog.alert("Error", new Label("Oh my! This picker has no associated selector!"));
				}
			}
		};

		textBox.setReadOnly(true);
		textBox.addClickHandler(clickHandler);
		pickerPanel.add(textBox);
		pickerPanel.add(changeButton);
		pickerPanel.setCellVerticalAlignment(changeButton, HasVerticalAlignment.ALIGN_MIDDLE);
		changeButton.addClickHandler(clickHandler);

		focusPanel.setWidget(pickerPanel);
		initWidget(focusPanel);
	}

	/**
	 * Adds an item of type T to the underlying DataTable.
	 * 
	 * @param item
	 */
	public void addItem(T item) {
		entitySelector.getCellTable().addItem(item);
	}

	public void clear() {
		entitySelector.clearInitialized();
		textBox.setText("");
	}

	public Button getChangeButton() {
		return changeButton;
	}

	/**
	 * This method needs to be defined in subclasses because it is a type-specific {@link EntitySelector}, which requires a type-specific constructor.
	 * 
	 * @return The selector that is paired with this picker. The selector returned must be of type T.
	 */
	public abstract EntityCellSelector<T, U, C> getSelector();

	public TextBox getTextBox() {
		return textBox;
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	/**
	 * This method is not {@link SelectionPolicy}-aware, so if multiple items are selected, it will return the first selected item.
	 * 
	 * @return The selected item from the paired {@link EntitySelector} or null if no item is selected.
	 */
	public T getValue() {
		return entitySelector.getSelectedItem();
	}

	public int getValueId() {
		return entitySelector.getSelectedItemId();
	}

	/**
	 * This will return the selected item's IDs. If the item IDs are set with setSelectedItemsById(), this will return the corrected IDs weather or not the data
	 * has been retrieved yet.
	 * 
	 * @return
	 */
	public List<Integer> getValueIds() {
		return entitySelector.getSelectedItemIds();
	}

	/**
	 * This method is not {@link SelectionPolicy}-aware, so if only one item can be selected, it will return that single item wrapped in a {@link List}.
	 * 
	 * @return The {@link List} of selected items from the paired {@link EntitySelector}. Empty {@link List} if no items are selected.
	 */
	public List<T> getValues() {
		return entitySelector.getSelectedItems();
	}

	public boolean isEnabled() {
		return changeButton.isEnabled();
	}

	@Override
	public boolean isRequired() {
		return entitySelector.getMinSelect() != 0;
	}

	/**
	 * Pass-through method to {@link EntitySelector#init()}, which populates the selector either after an asynchronous call, or directly from a provided
	 * {@link List}. This method <b>must</b> be called in order for the selector to load its data. It does not run at construction time to allow flexibility in
	 * configuring the picker and selector prior to loading data.
	 */
	public void populate() {
		entitySelector.populate();
	}

	/**
	 * Removes the specified item and its row from the underlying DataTable.
	 * 
	 * @param item
	 */
	public void removeItem(T item) {
		entitySelector.getCellTable().removeItem(item);
	}

	public void setChangeButton(Button changeButton) {
		this.changeButton = changeButton;
	}

	@Override
	public void setEnabled(boolean enabled) {
		changeButton.setEnabled(enabled);
		textBox.setEnabled(enabled);
	}

	@Override
	public void setFocus(boolean focus) {
		focusPanel.setFocus(focus);
	}

	@Override
	public void setRequired(boolean isRequired) {
		getValidator().setRequired(isRequired);
		if (!isRequired) {
			entitySelector.setMinSelect(0);
		} else if (entitySelector.getMinSelect() == 0) {
			entitySelector.setMinSelect(1);
		}
	}

	public void setTextBox(RequiredTextBox textBox) {
		this.textBox = textBox;
	}

	/**
	 * Pass-through method to {@link EntitySelector#setSelectedItem(Object)}, which sets the value (of type T) of the selector (which is used as the picker's
	 * value as well). Generally invoked by widget initializers in forms.
	 * 
	 * @param item
	 */
	public void setValue(T item) {
		if (item == null) {
			entitySelector.getCellTable().clearSelection();
			textBox.setText("");
			validator.setError(false);
			return;
		}
		entitySelector.setSelectedItem(item);
		updateTextBox();
	}

	/**
	 * Pass-through method to {@link EntitySelector#setSelectedItemById(Integer)}, which sets the value (of type T) of this selector by providing the item's
	 * numeric id. Items in the selector must implement {@link HasId} in order for this to work.
	 * 
	 * 
	 * @param id
	 */
	public void setValueById(int id) {
		if (id == 0) {
			setValue(null);
			return;
		}
		entitySelector.setSelectedItemById(id);
		updateTextBox();
	}

	/**
	 * Pass-through method to {@link EntitySelector#setSelectedItems(Set)}, which calls {@link EntitySelector#setSelectedItem(Object)} on each item in the set.
	 * Items in the {@link Set} that are not contained in the selector are ignored.
	 * 
	 * @param items
	 *            Set of items of type T that are to be selected
	 */
	public void setValues(Collection<T> items) {
		entitySelector.setSelectedItems(items);
		updateTextBox();
	}

	/**
	 * Pass-through method to {@link EntitySelector#setSelectedItemsById(Set)}, which calls {@link EntitySelector#setSelectedItemById(Integer)} on each Integer
	 * in the set.
	 * 
	 * @param ids
	 */
	public void setValuesById(Collection<Integer> ids) {
		entitySelector.setSelectedItemsById(ids);
		updateTextBox();
	}

	public void updateTextBox() {
		List<T> values = entitySelector.getSelectedItems();
		if (values.isEmpty()) {
			// don't overwrite the textbox value if we haven't been loaded yet and we've set values by id
			if (!entitySelector.isFinishedLoading() && !entitySelector.getSelectedItemIds().isEmpty()) {
				return;
			}
			textBox.setText("");
			return;
		}
		Collections.sort(values);

		validator.setError(false);
		List<String> descriptors = new ArrayList<String>();
		for (T value : values) {
			descriptors.add(getTextBoxText(value));
		}
		textBox.setText(Common.join(descriptors, ", "));
	}

	protected String getTextBoxText(T value) {
		if (value == null) {
			return "";
		}
		return value.getDescriptor();
	}

	protected void setEntitySelector(final EntityCellSelector<T, U, C> entitySelector) {
		setEntitySelector(entitySelector, true);
	}

	/**
	 * Sets the selector that is to be paired with this picker.
	 * 
	 * @param entitySelector
	 * @param formSubmitHandler
	 * @param addDataReturnHandler
	 */
	protected void setEntitySelector(final EntityCellSelector<T, U, C> entitySelector, boolean addDataReturnHandler) {
		this.entitySelector = entitySelector;

		if (addDataReturnHandler) {
			entitySelector.getCellTable().addDataReturnHandler(new DataReturnHandler() {
				@Override
				public void onDataReturn() {
					updateTextBox();
				}
			});
		}

		entitySelector.addSubmitCommand(new Command() {
			@Override
			public void execute() {
				updateTextBox();
			}
		});
	}
}
