package com.areahomeschoolers.baconbits.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.areahomeschoolers.baconbits.client.event.CancelHandler;
import com.areahomeschoolers.baconbits.client.event.ConfirmHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ConfirmDialog extends DefaultDialog {

	public enum ConfirmDialogType {
		CONTINUE_CANCEL, YES_NO
	}

	public static void confirm(ConfirmDialogType type, String caption, String message, ConfirmHandler handler) {
		ConfirmDialog ad = new ConfirmDialog(type, caption, message, handler);
		ad.center();
	}

	public static void confirm(String message, ConfirmHandler handler) {
		ConfirmDialog ad = new ConfirmDialog(message, handler);
		ad.center();
	}

	public static void confirm(String caption, String message, ConfirmHandler handler) {
		ConfirmDialog ad = new ConfirmDialog(caption, message, handler);
		ad.center();
	}

	private Button continueButton;
	private final List<CancelHandler> cancelHandlers = new ArrayList<CancelHandler>();
	private final ButtonPanel buttons = new ButtonPanel(this);

	public ConfirmDialog(ConfirmDialogType type, String message, final ConfirmHandler handler) {

	}

	public ConfirmDialog(ConfirmDialogType type, String caption, String message, final ConfirmHandler handler) {
		super(false, true);

		setText(caption);
		buttons.getCloseButton().setText(getCancelText(type));
		buttons.getCloseButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for (CancelHandler handler : cancelHandlers) {
					handler.onCancel();
				}
			}
		});
		continueButton = new Button(getConfirmText(type));
		buttons.addRightButton(continueButton);

		continueButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				handler.onConfirm();
				hide();
			}
		});

		VerticalPanel vPanel = new VerticalPanel();
		vPanel.setWidth("300px");
		HTML messageLabel = new HTML(message);
		messageLabel.setStyleName("heavyPadding");
		vPanel.add(messageLabel);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.add(buttons);
		setWidget(vPanel);
	}

	public ConfirmDialog(String message, ConfirmHandler handler) {
		this("Confirm Action", message, handler);
	}

	public ConfirmDialog(String caption, String message, final ConfirmHandler handler) {
		this(ConfirmDialogType.CONTINUE_CANCEL, caption, message, handler);
	}

	public HandlerRegistration addCancelHandler(final CancelHandler handler) {
		cancelHandlers.add(handler);
		return new HandlerRegistration() {
			@Override
			public void removeHandler() {
				cancelHandlers.remove(handler);
			}
		};
	}

	public void setCancelButtonText(String text) {
		buttons.getCloseButton().setText(text);
	}

	public void setConfirmButtonText(String text) {
		continueButton.setText(text);
	}

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
				continueButton.setFocus(true);
			}
		});
	}

	private String getCancelText(ConfirmDialogType type) {
		switch (type) {
		case CONTINUE_CANCEL:
			return "Cancel";
		case YES_NO:
			return "No";
		}

		return null;
	}

	private String getConfirmText(ConfirmDialogType type) {
		switch (type) {
		case CONTINUE_CANCEL:
			return "Confirm";
		case YES_NO:
			return "Yes";
		}

		return null;
	}
}
