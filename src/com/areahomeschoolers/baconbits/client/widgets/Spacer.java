package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.images.MainImageBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

public class Spacer extends Composite {
	private Image pixel = new Image(MainImageBundle.INSTANCE.pixel());
	private SimplePanel sp = new SimplePanel();

	public Spacer(int width) {
		this(width, -1);
	}

	public Spacer(int width, int height) {
		pixel.setWidth(width + "px");
		if (height != -1) {
			pixel.setHeight(height + "px");
		}
		sp.setWidget(pixel);
		initWidget(sp);
	}
}
