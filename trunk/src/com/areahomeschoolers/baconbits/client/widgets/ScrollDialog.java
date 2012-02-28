package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ScrollDialog extends DefaultDialog {

	private final VerticalPanel abovePanel = new VerticalPanel();
	private final SimplePanel outerScrollPanel = new SimplePanel();
	private final SimplePanel contentPanel = new SimplePanel();
	private final VerticalPanel belowPanel = new VerticalPanel();
	private final ButtonPanel buttonPanel = new ButtonPanel(this);
	private final MaxHeightScrollPanel scrollPanel = new MaxHeightScrollPanel();

	public ScrollDialog() {
		scrollPanel.setWidget(contentPanel);
		outerScrollPanel.setWidget(scrollPanel);

		VerticalPanel mainPanel = new VerticalPanel();
		mainPanel.add(abovePanel);
		mainPanel.add(outerScrollPanel);
		mainPanel.add(belowPanel);
		mainPanel.add(buttonPanel);

		super.setWidget(mainPanel);
	}

	// public ScrollDialog(EntityCellTable<?, ?, ?> table) {
	// this();
	// setWidget(table);
	// }

	public ScrollDialog(Widget widget) {
		this();
		setWidget(widget);
	}

	@Override
	public void center() {
		show();
		scrollPanel.adjustSize();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				ScrollDialog.super.center();
			}
		});
	}

	public VerticalPanel getAbovePanel() {
		return abovePanel;
	}

	public VerticalPanel getBelowPanel() {
		return belowPanel;
	}

	public ButtonPanel getButtonPanel() {
		return buttonPanel;
	}

	// public void setWidget(final EntityCellTable<?, ?, ?> table) {
	// table.registerScrollPanel(scrollPanel);
	//
	// table.addDataReturnHandler(new DataReturnHandler() {
	// @Override
	// public void onDataReturn() {
	// center();
	// }
	// });
	//
	// contentPanel.setWidget(table);
	// outerScrollPanel.setWidget(WidgetFactory.newSection(table.getTitleBar(), scrollPanel));
	// }

	@Override
	public void setWidget(Widget widget) {
		contentPanel.setWidget(widget);
	}
}
