package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Event;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MarkupTextBox extends Composite {
	private NumericTextBox preInput = new NumericTextBox(2);
	private NumericTextBox postInput = new NumericTextBox(2);
	private NumericTextBox feeInput = new NumericTextBox(2);
	private Event event;
	private VerticalPanel vp = new VerticalPanel();
	private Command changeCommand;

	public MarkupTextBox(Event event) {
		vp.setSpacing(4);
		this.event = event;
		preInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						updateFromPre();
					}
				});
			}
		});

		preInput.setVisibleLength(8);
		preInput.setMaxLength(10);
		preInput.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		PaddedPanel pre = new PaddedPanel();
		Label preView = new Label("Price before fee (amount you'll receive)");
		preView.setWordWrap(false);
		pre.add(preInput);
		pre.add(preView);
		pre.setCellVerticalAlignment(preView, HasVerticalAlignment.ALIGN_MIDDLE);

		postInput.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						updateFromPost();
					}
				});
			}
		});

		postInput.setVisibleLength(8);
		postInput.setMaxLength(10);
		postInput.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		Label postView = new Label("Price after fee (amount they'll pay to register)");
		postView.setWordWrap(false);
		PaddedPanel post = new PaddedPanel();
		post.add(postInput);
		post.add(postView);
		post.setCellVerticalAlignment(postView, HasVerticalAlignment.ALIGN_MIDDLE);

		feeInput.setEnabled(false);
		feeInput.setVisibleLength(8);
		feeInput.setMaxLength(10);
		feeInput.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
		double percent = Common.getMarkupPercent(event);
		double dollars = Common.getMarkupDollars(event);
		Label feeView = new Label("Fee (" + percent + "% plus " + Formatter.formatCurrency(dollars) + ")");
		PaddedPanel fee = new PaddedPanel();
		fee.add(feeInput);
		fee.add(feeView);
		pre.setCellVerticalAlignment(feeView, HasVerticalAlignment.ALIGN_MIDDLE);

		vp.add(pre);
		vp.add(fee);
		vp.add(post);

		initWidget(vp);
	}

	public Command getChangeCommand() {
		return changeCommand;
	}

	public Double getDouble() {
		return preInput.getDouble();
	}

	public void setChangeCommand(Command changeCommand) {
		this.changeCommand = changeCommand;
	}

	public void setValue(double value) {
		preInput.setValue(value);
		updateFromPre();
	}

	private void updateFee() {
		double pre = preInput.getDouble();
		double post = postInput.getDouble();
		double fee = post - pre;

		if (changeCommand != null) {
			changeCommand.execute();
		}

		if (pre == 0) {
			feeInput.setValue(0.00);
			return;
		}

		feeInput.setValue(fee);
	}

	private void updateFromPost() {
		double postDollars = postInput.getDouble();

		double markup = postDollars == 0 ? 0 : Common.getEventPreMarkup(postDollars, event);

		preInput.setText(Formatter.formatCurrency(postDollars - markup));

		updateFee();
	}

	private void updateFromPre() {
		double preDollars = preInput.getDouble();

		double markup = preDollars == 0 ? 0 : Common.getEventMarkup(preDollars, event);

		postInput.setText(Formatter.formatCurrency(preDollars + markup));

		updateFee();
	}
}
