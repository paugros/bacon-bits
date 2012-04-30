package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable;
import com.areahomeschoolers.baconbits.client.widgets.FieldTable.LabelColumnWidth;
import com.areahomeschoolers.baconbits.client.widgets.FormField;
import com.areahomeschoolers.baconbits.shared.dto.EntityDto;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EntityTableSearchDialog<T extends EntityDto<T>> extends DefaultDialog {
	private VerticalPanel vp = new VerticalPanel();
	private FieldTable fieldTable = new FieldTable();
	private EntityCellTable<T, ?, ?> cellTable;
	private TextBox textInput = new TextBox();
	private String lastFilter = "";
	private Timer searchTimer = new Timer() {
		@Override
		public void run() {
			searchTable();
		}
	};

	public EntityTableSearchDialog(EntityCellTable<T, ?, ?> cellTable) {
		super(true, false);
		setText("Search");
		this.cellTable = cellTable;
		initialize();
	}

	public void clearFilter() {
		textInput.setText("");
		searchTable();
	}

	public int getVisibleRowCount() {
		return cellTable.getVisibleItemCount();
	}

	public boolean hasFilter() {
		return !textInput.getText().isEmpty();
	}

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				textInput.setFocus(true);
			}
		});
	}

	private void initialize() {
		textInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				int keyCode = event.getNativeKeyCode();
				switch (keyCode) {
				case KeyCodes.KEY_ESCAPE:
					hide();
					break;
				case KeyCodes.KEY_ENTER:
					searchTimer.cancel();
					searchTable();
					break;
				}
			}
		});

		textInput.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				searchTimer.cancel();
				searchTimer.schedule(150);
			}
		});
		FormField textField = new FormField("Search:", textInput, null);
		fieldTable.addField(textField);

		vp.add(fieldTable);
		fieldTable.setLabelColumnWidth(LabelColumnWidth.NARROW);
		setWidget(vp);
	}

	private void searchTable() {
		String searchText = textInput.getText().toLowerCase();
		if (searchText.equals(lastFilter)) {
			return;
		}
		boolean searchVisibleOnly = searchText.contains(lastFilter);
		boolean searchHiddenOnly = lastFilter.contains(searchText);
		boolean searchIsEmpty = searchText.isEmpty();
		lastFilter = searchText;

		for (T entity : cellTable.getFullList()) {
			if (searchIsEmpty) {
				cellTable.showAllItems();
				return;
			}
			boolean rowIsVisible = cellTable.isItemVisible(entity);
			if ((searchVisibleOnly && !rowIsVisible) || (searchHiddenOnly && rowIsVisible)) {
				continue;
			}
			boolean visible = cellTable.itemContainsString(searchText, entity);
			cellTable.setItemVisible(entity, visible);
		}
	}
}
