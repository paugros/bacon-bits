package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class MarkupTextBox extends Composite {
	private HorizontalPanel hp = new PaddedPanel();
	private NumericTextBox input = new NumericTextBox(2);
	private HTML preview = new HTML("&nbsp;");
	private Event event;

	public MarkupTextBox(Event event) {
		this.event = event;
		input.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						updatePreview();
					}
				});
			}
		});

		input.setVisibleLength(8);
		input.setMaxLength(10);
		preview.setWordWrap(false);
		hp.add(input);
		hp.add(preview);
		hp.setCellVerticalAlignment(preview, HasVerticalAlignment.ALIGN_MIDDLE);

		initWidget(hp);
	}

	public Double getDouble() {
		return input.getDouble();
	}

	public void setValue(double value) {
		input.setValue(value);
		updatePreview();
	}

	private void updatePreview() {
		double dollars = input.getDouble();

		if (dollars == 0) {
			preview.setText("Free");
		} else {
			double markedUp = Common.getEventMarkup(dollars, event) + dollars;
			if (markedUp == dollars) {
				preview.setText("");
			} else {
				preview.setText(Formatter.formatCurrency(markedUp) + " with fees");
			}
		}
	}
}
