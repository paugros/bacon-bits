package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A {@link HorizontalPanel} that adds a separator cell between each user-added cell. Useful for easy grouping of action hyperlinks inside title bars.
 */
public final class LinkPanel extends HorizontalPanel {
	public LinkPanel() {
		setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	}

	@Override
	public void add(Widget w) {
		add(w, true);
	}

	public void add(Widget w, boolean addDivider) {
		if (getWidgetCount() > 0 && addDivider && !getChildren().contains(w)) {
			HTML h = new HTML("&nbsp;|&nbsp;");
			super.add(h);
		}
		super.add(w);
	}

	@Override
	public void insert(Widget w, int beforeIndex) {
		insert(w, beforeIndex, true);
	}

	public void insert(Widget w, int beforeIndex, boolean addDivider) {
		if (getWidgetCount() > 0 && addDivider) {
			super.insert(new HTML("&nbsp;|&nbsp;"), beforeIndex);
		}
		super.insert(w, beforeIndex);
	}

	public boolean removeWidget(Widget w) {
		int widgetCount = getWidgetCount();

		if (widgetCount > 1) {
			int pos = getWidgetIndex(w);
			if (pos == 0) {
				remove(1);
			} else if (pos > 1) {
				remove(pos - 1);
			}
		}
		return super.remove(w);
	}
}
