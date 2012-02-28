package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class MaxHeightScrollPanel extends ScrollPanel {
	private int maxHeight = 450;
	private boolean scrolling = true;
	private HandlerRegistration attachHandler;
	private boolean useChildWidth = true;

	public MaxHeightScrollPanel() {
		super();

		attachHandler = addAttachHandler(new Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				adjustSize();
				attachHandler.removeHandler();
			}
		});
	}

	public MaxHeightScrollPanel(int maxHeight) {
		this();
		this.maxHeight = maxHeight;
	}

	public MaxHeightScrollPanel(int maxHeight, Widget child) {
		this(child);
		this.maxHeight = maxHeight;
	}

	public MaxHeightScrollPanel(Widget child) {
		this();
		setWidget(child);
	}

	public void adjustHeight() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				adjustHeightNow();
			}
		});
	}

	public void adjustHeightNow() {
		Widget w = getWidget();
		if (w == null) {
			setHeight("0px");
			return;
		}
		if (scrolling && w.getOffsetHeight() > maxHeight) {
			setHeight(maxHeight + "px");
		} else {
			if (getMaximumHorizontalScrollPosition() <= 22) {
				getScrollableElement().getStyle().setOverflowX(Overflow.HIDDEN);
			} else {
				getScrollableElement().getStyle().setOverflowX(Overflow.AUTO);
			}
			setHeight((w.getOffsetHeight()) + "px");
		}
	}

	public void adjustSize() {
		adjustHeight();
		adjustWidth();
	}

	public void adjustSizeNow() {
		adjustHeightNow();
		adjustWidthNow();
	}

	public void adjustWidth() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				adjustWidthNow();
			}
		});
	}

	public void adjustWidthNow() {
		if (!useChildWidth) {
			return;
		}
		Widget w = getWidget();
		if (w == null) {
			setWidth("0px");
			return;
		}

		int width = w.getOffsetWidth();
		if (verticalScrollBarIsShowing()) {
			width += 22;
		}
		setWidth(width + "px");
	}

	@Override
	public void clear() {
		super.clear();
		adjustSize();
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public boolean isScrolling() {
		return scrolling;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public void setScrolling(boolean scrolling) {
		this.scrolling = scrolling;
		adjustSize();
	}

	public void setUseChildWidth(boolean useChildWidth) {
		this.useChildWidth = useChildWidth;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		adjustSize();
	}

	@Override
	public void setWidget(Widget w) {
		super.setWidget(w);
		adjustSize();
	}

	public boolean verticalScrollBarIsShowing() {
		return getWidget().getOffsetHeight() > maxHeight;
	}

}
