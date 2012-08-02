package com.areahomeschoolers.baconbits.client.widgets.cellview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.event.FormSubmitHandler;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.client.widgets.HasDisableCriteria;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;
import com.areahomeschoolers.baconbits.client.widgets.cellview.EntityCellTable.SelectionPolicy;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Arg;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;
import com.areahomeschoolers.baconbits.shared.dto.HasId;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public abstract class EntityCellSelector<T extends EntityDto<T>, U extends Arg, C extends Enum<C> & EntityCellTableColumn<C>> extends DefaultDialog {
	private boolean isSelfHiding = true;
	private EntityCellTable<T, U, C> entityCellTable;
	private List<Command> submitCommands = new ArrayList<Command>();
	private Button submitButton;
	private VerticalPanel abovePanel = new VerticalPanel();
	private VerticalPanel belowPanel = new VerticalPanel();
	private ButtonPanel buttonPanel = new ButtonPanel(this);
	private SimplePanel bodyPanel = new SimplePanel();
	private SimplePanel body = new MaxHeightScrollPanel();
	protected MaxHeightScrollPanel scrollPanel = (MaxHeightScrollPanel) body;
	private int minSelect = 0;
	private int maxSelect;
	private Label errorLabel = new Label();
	private int width = 750;
	private boolean autoPopulated = true;
	private Button clearButton;
	private List<FormField> requiredFields = new ArrayList<FormField>();
	private FieldTable fieldTable;
	private DataReturnHandler showHandler = null;

	protected EntityCellSelector() {
		errorLabel.setStyleName("errorText");
		VerticalPanel contents = new VerticalPanel();
		contents.setWidth(width + "px");
		setWidget(contents);
		body.setWidth(width + "px");
		abovePanel.setWidth("100%");
		belowPanel.setWidth("100%");
		contents.add(abovePanel);
		bodyPanel.setWidget(WidgetFactory.newSection(" ", body));
		contents.add(bodyPanel);
		contents.add(belowPanel);
		contents.add(errorLabel);

		if (submitButton == null) {
			submitButton = new Button("Submit");
			submitButton.addMouseDownHandler(new MouseDownHandler() {
				@Override
				public void onMouseDown(MouseDownEvent event) {
					submit();
				}
			});
		}

		buttonPanel.addRightButton(submitButton);
		buttonPanel.getCloseButton().setText("Cancel");
		buttonPanel.getCloseButton().addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
					entityCellTable.revertSelectedItems();
				}
			}
		});

		buttonPanel.getCloseButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				entityCellTable.revertSelectedItems();
			}
		});

		clearButton = new Button("Clear");
		clearButton.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				entityCellTable.clearSelection();
			}
		});
		buttonPanel.addLeftButton(clearButton);
		contents.add(buttonPanel);
		clearButton.setVisible(minSelect == 0);
	}

	/**
	 * Adds an item of type T to the underlying DataTable.
	 * 
	 * @param item
	 */
	public void addItem(T item) {
		entityCellTable.addItem(item);
	}

	public void addRequiredSubmitField(final FormField field) {
		requiredFields.add(field);

		if (fieldTable == null) {
			fieldTable = new FieldTable();
			getBelowPanel().add(fieldTable);
		}

		fieldTable.addField(field);
	}

	/**
	 * Adds a {@link FormSubmitHandler}, to be executed upon submission of this selector. There is no default for this; if it is not set, nothing will happen
	 * upon submission except that the selector will be hidden.
	 * 
	 * @param submitCommand
	 */
	public void addSubmitCommand(Command submitCommand) {
		submitCommands.add(submitCommand);
	}

	public void adjustSize() {
		scrollPanel.adjustSize();
	}

	public void clearInitialized() {
		entityCellTable.clearInitialized();
	}

	public void clearSelection() {
		entityCellTable.clearSelection();
	}

	/**
	 * Removes all custom code that was to be executed upon submission of this selector.
	 */
	public void clearSubmitCommands() {
		submitCommands.clear();
	}

	/**
	 * Programmatically execute all commands currently in the submit command list.
	 */
	public void executeSubmitCommands() {
		for (Command command : submitCommands) {
			command.execute();
		}
	}

	public VerticalPanel getAbovePanel() {
		return abovePanel;
	}

	public VerticalPanel getBelowPanel() {
		return belowPanel;
	}

	/**
	 * @return The {@link SimplePanel} that contains the {@link EntityDataTable}.
	 */
	public SimplePanel getBody() {
		return body;
	}

	/**
	 * @return The {@link ButtonPanel} containing the default submit and cancel {@link Button Buttons}.
	 */
	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	/**
	 * @return This method needs to be defined in subclasses because it is a type-specific {@link EntityDataTable}, which requires a type-specific constructor.
	 */
	public abstract EntityCellTable<T, U, ?> getCellTable();

	/**
	 * @return The default close {@link Button}.
	 */
	public Button getCloseButton() {
		return buttonPanel.getCloseButton();
	}

	public FieldTable getFieldTable() {
		return fieldTable;
	}

	/**
	 * @return All items currently in the selector.
	 */
	public List<T> getList() {
		return entityCellTable.getList();
	}

	/**
	 * @return The height of the scrolling content panel.
	 */
	public int getMaxHeight() {
		return scrollPanel.getMaxHeight();
	}

	public int getMaxSelect() {
		return maxSelect;
	}

	public int getMinSelect() {
		return minSelect;
	}

	/**
	 * This method is not {@link SelectionPolicy}-aware, so if multiple items are selected, it will return the first selected item.
	 * 
	 * @return The currently selected item or null if no item is selected.
	 */
	public T getSelectedItem() {
		List<T> items = entityCellTable.getSelectedItems();
		if (items.isEmpty()) {
			return null;
		}
		return items.iterator().next();
	}

	public int getSelectedItemId() {
		List<Integer> ids = entityCellTable.getSelectedItemIds();
		if (ids.isEmpty()) {
			return 0;
		}
		return ids.iterator().next();
	}

	/**
	 * This will return the selected item's IDs. If the item IDs are set with setSelectedItemsById(), this will return the corrected IDs weather or not the data
	 * has been retrieved yet.
	 * 
	 * @return
	 */
	public List<Integer> getSelectedItemIds() {
		return entityCellTable.getSelectedItemIds();
	}

	/**
	 * This method is not {@link SelectionPolicy}-aware, so if only one item can be selected, it will return that single item wrapped in a {@link List}.
	 * 
	 * @return The {@link List} of selected items. Empty {@link List} if no items are selected.
	 */
	public List<T> getSelectedItems() {
		return entityCellTable.getSelectedItems();
	}

	/**
	 * @return The default submit {@link Button}
	 */
	public Button getSubmitButton() {
		return submitButton;
	}

	/**
	 * @return The {@link Command} to be executed upon submission of this selector
	 */
	public List<Command> getSubmitCommands() {
		return submitCommands;
	}

	@Override
	public void hide() {
		super.hide();
		entityCellTable.getTitleBar().clearFilter();
		submitButton.setEnabled(true);
		errorLabel.setText("");
	}

	public boolean isAutoPopulated() {
		return autoPopulated;
	}

	/**
	 * @return Whether this selector's {@link EntityDataTable} has received its data.
	 */
	public boolean isFinishedLoading() {
		return entityCellTable.isFinishedLoading();
	}

	/**
	 * @return Whether this selector allows multiple selections.
	 */
	public boolean isMultiSelect() {
		return entityCellTable.isMultiSelect();
	}

	/**
	 * @return Whether this selector will enforce scrolling of content with a height greater than the default or that returned by {@link #getMaxHeight()}.
	 */
	public boolean isScrolling() {
		return scrollPanel.isScrolling();
	}

	/**
	 * @return Whether this selector will hide itself automatically upon submission.
	 */
	public boolean isSelfHiding() {
		return isSelfHiding;
	}

	/**
	 * Populate the underlying {@link EntityDataTable}.
	 */
	public void populate() {
		entityCellTable.populate();
	}

	/**
	 * Removes the specified item and its row from the underlying DataTable.
	 * 
	 * @param item
	 */
	public void removeItem(T item) {
		entityCellTable.removeItem(item);
	}

	public void setAutoPopulated(boolean autoPopulated) {
		this.autoPopulated = autoPopulated;
	}

	/**
	 * Sets the container for the {@link EntityDataTable}.
	 * 
	 * @param body
	 */
	public void setBody(SimplePanel body) {
		this.body = body;
	}

	/**
	 * Replaces the default button panel.
	 * 
	 * @param buttonPanel
	 */
	public void setButtonPanel(ButtonPanel buttonPanel) {
		this.buttonPanel = buttonPanel;
	}

	/**
	 * Sets the height of the scrolling content panel.
	 * 
	 * @param maxHeight
	 */
	public void setMaxHeight(int maxHeight) {
		scrollPanel.setMaxHeight(maxHeight);
	}

	public void setMaxSelect(int maxSelect) {
		this.maxSelect = maxSelect;
		if (maxSelect > 1) {
			setMultiSelect(true);
		}
	}

	public void setMinSelect(int minSelect) {
		this.minSelect = minSelect;
		clearButton.setVisible(minSelect == 0);
	}

	/**
	 * Sets whether this selector allows multiple selections by applying a {@link SelectionPolicy} to the {@link EntityDataTable}.
	 * 
	 * @param isMultiSelect
	 */
	public void setMultiSelect(boolean isMultiSelect) {
		entityCellTable.setSelectionPolicy(isMultiSelect ? SelectionPolicy.MULTI_ROW : SelectionPolicy.ONE_ROW);
	}

	/**
	 * Sets whether this selector will force scrolling if its content's height is over the maximum. To override the default maximum, use
	 * {@link #setMaxHeight(int)}.
	 * 
	 * @param isScrolling
	 */
	public void setScrolling(boolean isScrolling) {
		scrollPanel.setScrolling(isScrolling);
	}

	/**
	 * Sets the selected item of type T. Generally invoked by widget initializers in forms.
	 * 
	 * @param item
	 */
	public void setSelectedItem(T item) {
		entityCellTable.setSelectedItem(item);
	}

	/**
	 * Sets the value (of type T) of this selector by providing the item's numeric id. Items in the selector must implement {@link HasId} in order for this to
	 * work.
	 * 
	 * @param id
	 */
	public void setSelectedItemById(Integer id) {
		entityCellTable.setSelectedItemById(id);
	}

	/**
	 * Calls {@link #setSelectedItem(Object)} on each item in the provided set. Items in the {@link Set} that are not contained in the selector are ignored.
	 * 
	 * @param items
	 */
	public void setSelectedItems(Collection<T> items) {
		entityCellTable.setSelectedItems(items);
	}

	/**
	 * Calls {@link #setSelectedItemById(Integer)} on each Integer in the set.
	 * 
	 * @param ids
	 */
	public void setSelectedItemsById(Collection<Integer> ids) {
		entityCellTable.setSelectedItemsById(ids);
	}

	/**
	 * Sets whether this selector will hide itself upon submission.
	 * 
	 * @param isSelfHiding
	 */
	public void setSelfHiding(boolean isSelfHiding) {
		this.isSelfHiding = isSelfHiding;
	}

	/**
	 * Replaces the default submit {@link Button}.
	 * 
	 * @param button
	 */
	public void setSubmitButton(Button button) {
		submitButton.removeFromParent();
		this.submitButton = button;

		buttonPanel.insertLeftButton(button, 0);
	}

	@Override
	public void show() {
		if (showHandler == null) {
			showHandler = new DataReturnHandler() {
				@Override
				public void onDataReturn() {
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							EntityCellSelector.super.show();
							entityCellTable.setWidth(width + "px");
							submitButton.setEnabled(true);
							scrollPanel.adjustSizeNow();
							int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
							int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
							setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), Math.max(Window.getScrollTop() + top, 0));
						}
					});
				}
			};
			entityCellTable.addDataReturnHandler(showHandler);
		}

		if (autoPopulated) {
			if (!entityCellTable.hasBeenPopulated()) {
				entityCellTable.populate();
				return;
			}

			if (!entityCellTable.isAttached() && entityCellTable.isFinishedLoading()) {
				body.setWidget(entityCellTable);
				bodyPanel.setWidget(WidgetFactory.newSection(entityCellTable.getTitleBar(), body));
				entityCellTable.setWidth(width + "px");
			}
		}

		super.show();
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				scrollPanel.adjustSizeNow();
				int left = (Window.getClientWidth() - getOffsetWidth()) >> 1;
				int top = (Window.getClientHeight() - getOffsetHeight()) >> 1;
				setPopupPosition(Math.max(Window.getScrollLeft() + left, 0), Math.max(Window.getScrollTop() + top, 0));
				buttonPanel.getCloseButton().setFocus(true);
			}
		});
	}

	/**
	 * Whether this selector currently has enough items selected, according to min and max select parameters.
	 * 
	 * @return
	 */
	private boolean hasValidSelectCount() {
		if (entityCellTable.getSelectionPolicy() == SelectionPolicy.ONE_ROW) {
			maxSelect = 1;
		}
		String message = "Please select a valid number of items (min: " + minSelect + ", max: " + maxSelect + ").";

		int selectCount = getSelectedItems().size();
		if (selectCount < minSelect) {
			errorLabel.setText(message);
			return false;
		}

		if (selectCount > maxSelect && maxSelect > 0) {
			errorLabel.setText(message);
			return false;
		}

		return true;
	}

	protected boolean canSubmit() {
		if (!hasValidSelectCount()) {
			return false;
		}

		boolean canSubmit = true;

		for (FormField field : requiredFields) {
			if (!field.validate()) {
				canSubmit = false;
			}
		}

		return canSubmit;
	}

	/**
	 * Sets or replaces the {@link EntityDataTable} paired with this selector.
	 * 
	 * @param entityCellTable
	 */
	protected void setEntityCellTable(EntityCellTable<T, U, C> edt) {
		this.entityCellTable = edt;
		// links inside selectors should open a new tab
		edt.setLinksOpenNewTab(true);
		// need to configure data table to allow selection (default is one row)
		if (entityCellTable.getSelectionPolicy() == SelectionPolicy.NONE) {
			entityCellTable.setSelectionPolicy(SelectionPolicy.ONE_ROW);
		}
		// we generally don't want a loading message when a selector is being populated
		// entityDataTable.setWaitingIndicator(false);

		entityCellTable.registerScrollPanel(scrollPanel);

		if (Common.isNullOrBlank(entityCellTable.getTitle())) {
			entityCellTable.setDefaultSizePrefName(getText());
		}
		// things that need to run when the data comes in
		entityCellTable.addDataReturnHandler(new DataReturnHandler() {
			@Override
			public void onDataReturn() {
				if (!entityCellTable.isAttached()) {
					body.setWidget(entityCellTable);
					bodyPanel.setWidget(WidgetFactory.newSection(entityCellTable.getTitleBar(), body));
				}
				if (EntityCellSelector.this instanceof HasDisableCriteria<?>) {
					((HasDisableCriteria<?>) EntityCellSelector.this).applyDisableCriteria();
				}
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						entityCellTable.setWidth(width + "px");
						scrollPanel.adjustSizeNow();

						if (isShowing()) {
							center();
						}
					}
				});
			}
		});
	}

	protected void setErrorMessage(String message) {
		errorLabel.setText(message);
	}

	protected void submit() {
		if (!canSubmit()) {
			return;
		}

		errorLabel.setText("");

		submitButton.setEnabled(false);
		if (!submitCommands.isEmpty()) {
			executeSubmitCommands();
		}

		entityCellTable.saveSelectedItems();

		if (isSelfHiding) {
			hide();
		}
	}
}
