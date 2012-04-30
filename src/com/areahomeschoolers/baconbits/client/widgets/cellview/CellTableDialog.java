package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.areahomeschoolers.baconbits.client.event.DataReturnHandler;
import com.areahomeschoolers.baconbits.client.util.WidgetFactory;
import com.areahomeschoolers.baconbits.client.widgets.ButtonPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultDialog;
import com.areahomeschoolers.baconbits.client.widgets.MaxHeightScrollPanel;

import com.google.gwt.user.client.ui.VerticalPanel;

public class CellTableDialog extends DefaultDialog {

	private EntityCellTable<?, ?, ?> table;
	private boolean isInitialized = false;
	private MaxHeightScrollPanel scrollPanel;

	public CellTableDialog(EntityCellTable<?, ?, ?> table) {
		this.table = table;
	}

	public void populateAndCenter() {
		if (!isInitialized) {
			table.populate();
			table.addDataReturnHandler(new DataReturnHandler() {
				@Override
				public void onDataReturn() {
					isInitialized = true;
					scrollPanel = new MaxHeightScrollPanel(table);
					VerticalPanel mainPanel = new VerticalPanel();
					mainPanel.add(WidgetFactory.newSection(table.getTitleBar(), scrollPanel));
					mainPanel.add(new ButtonPanel(CellTableDialog.this));
					setWidget(mainPanel);

					center();
					scrollPanel.adjustSizeNow();
					centerDeferred();
				}
			});
		} else {
			center();
		}
	}
}
