package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * A composite that contains three HorizontalPanels to which buttons may be added. The panels are left, center, and right aligned inside an outer panel.
 * Intended for use with dialogs and forms. A DialogBox may be provided, in which case a default cancel button is present. The text or behavior of the cancel
 * button may be specified in the constructor. Providing null will not indicates the default should be used. The default cancel button will <i>always</i> close
 * the dialog; if an additional ClickHandler is provided, it will be executed <i>after</i> the dialog is closed.
 */
public class ButtonPanel extends Composite {
	private HorizontalPanel outerPanel = new HorizontalPanel();
	private HorizontalPanel leftPanel = new HorizontalPanel();
	private HorizontalPanel centerPanel = new HorizontalPanel();
	private HorizontalPanel rightPanel = new HorizontalPanel();
	private DialogBox dialog;
	private Button closeButton;

	public ButtonPanel() {
		outerPanel.setWidth("100%");
		outerPanel.add(leftPanel);
		outerPanel.add(centerPanel);
		outerPanel.add(rightPanel);

		int spacing = 10;
		leftPanel.setSpacing(spacing);
		centerPanel.setSpacing(spacing);
		rightPanel.setSpacing(spacing);

		outerPanel.setCellHorizontalAlignment(leftPanel, HasHorizontalAlignment.ALIGN_LEFT);
		outerPanel.setCellHorizontalAlignment(centerPanel, HasHorizontalAlignment.ALIGN_CENTER);
		outerPanel.setCellHorizontalAlignment(rightPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		initWidget(outerPanel);
	}

	public ButtonPanel(DialogBox dialogBox) {
		this();
		this.dialog = dialogBox;

		closeButton = new Button("Close");
		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent e) {
				dialog.hide();
			}
		});
		leftPanel.add(closeButton);
	}

	public void addCenterButton(Button button) {
		centerPanel.add(button);
	}

	public void addLeftButton(Button button) {
		leftPanel.add(button);
	}

	public void addRightButton(Button button) {
		rightPanel.add(button);
	}

	public Button getCloseButton() {
		return closeButton;
	}

	public void insertCenterButton(Button button, int beforeIndex) {
		centerPanel.insert(button, beforeIndex);
	}

	public void insertLeftButton(Button button, int beforeIndex) {
		leftPanel.insert(button, beforeIndex);
	}

	public void insertRightButton(Button button, int beforeIndex) {
		rightPanel.insert(button, beforeIndex);
	}

	public void setCenterButtonsEnabled(boolean enabled) {
		setPanelButtonsEnabled(centerPanel, enabled);
	}

	public void setCenterButtonsVisible(boolean visible) {
		centerPanel.setVisible(visible);
	}

	public void setEnabled(boolean enabled) {
		setLeftButtonsEnabled(enabled);
		setRightButtonsEnabled(enabled);
		setCenterButtonsEnabled(enabled);
	}

	public void setLeftButtonsEnabled(boolean enabled) {
		setPanelButtonsEnabled(leftPanel, enabled);
	}

	public void setLeftButtonsVisible(boolean visible) {
		leftPanel.setVisible(visible);
	}

	public void setRightButtonsEnabled(boolean enabled) {
		setPanelButtonsEnabled(rightPanel, enabled);
	}

	public void setRightButtonsVisible(boolean visible) {
		rightPanel.setVisible(visible);
	}

	private void setPanelButtonsEnabled(HorizontalPanel panel, boolean enabled) {
		for (int i = 0; i < panel.getWidgetCount(); i++) {
			Button button = (Button) panel.getWidget(i);
			button.setEnabled(enabled);
		}
	}
}
