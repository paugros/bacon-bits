package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.DialogBox;

public class DefaultDialog extends DialogBox {

	public DefaultDialog() {
		setAutoHideOnHistoryEventsEnabled(true);
	}

	public DefaultDialog(boolean autoHide) {
		super(autoHide);
		setAutoHideOnHistoryEventsEnabled(true);
	}

	public DefaultDialog(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		setAutoHideOnHistoryEventsEnabled(true);
	}

	public void centerDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				center();
			}
		});
	}

	@Override
	public void setWidth(String width) {
		// prefer setting the width on the child
		if (getWidget() != null) {
			getWidget().setWidth(width);
		} else {
			super.setWidth(width);
		}
	}

}
