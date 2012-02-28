package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RichTextArea;

public class ControlledRichTextArea extends Composite {
	private Grid grid = new Grid(2, 1);
	private RichTextArea textArea = new RichTextArea();
	private RichTextToolbar toolbar = new RichTextToolbar(textArea);

	public ControlledRichTextArea() {
		initWidget(grid);
		grid.setWidget(0, 0, toolbar);
		grid.setWidget(1, 0, textArea);
		textArea.setWidth("800px");
		textArea.setHeight("600px");
		textArea.setStyleName("body");
		addStyleName("ControlledRichTextArea");
	}

	public RichTextArea getTextArea() {
		return textArea;
	}

	public RichTextToolbar getToolbar() {
		return toolbar;
	}

	public void setTextArea(RichTextArea textArea) {
		this.textArea = textArea;
	}

	public void setToolbar(RichTextToolbar toolbar) {
		this.toolbar = toolbar;
	}
}
