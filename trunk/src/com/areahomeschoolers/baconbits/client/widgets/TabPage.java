package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.WidgetFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TabPage extends LazyDecoratedTabPanel {
	public interface TabPageCommand {
		void execute(VerticalPanel tabBody);
	}

	private int spacing = WidgetFactory.PAGE_SPACING;

	public TabPage() {
		DeckPanel dp = getDeckPanel();
		dp.removeStyleName("gwt-TabPanelBottom");
		dp.addStyleName("borderlessTabBottom");
	}

	public VerticalPanel add(String tabText, final boolean enableCaching, final TabPageCommand command) {
		final VerticalPanel vp = new VerticalPanel();
		vp.setSpacing(spacing);

		Command cmd = new Command() {
			@Override
			public void execute() {
				if (!enableCaching) {
					vp.clear();
				}
				command.execute(vp);
			}
		};

		super.add(vp, tabText, enableCaching, cmd);
		return vp;
	}

	public VerticalPanel add(String tabText, TabPageCommand command) {
		return this.add(tabText, true, command);
	}

	public int getSpacing() {
		return spacing;
	}

	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}
}
