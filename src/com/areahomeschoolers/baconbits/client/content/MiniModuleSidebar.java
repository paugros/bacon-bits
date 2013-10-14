package com.areahomeschoolers.baconbits.client.content;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.minimodules.CitrusMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.CommunityEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.FindPeopleMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.LinksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.MyEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.NewEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.SellBooksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.UpcomingEventsMiniModule;
import com.areahomeschoolers.baconbits.client.widgets.SidebarPanel;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

public class MiniModuleSidebar extends SidebarPanel {
	public enum MiniModule {
		COMMUNITY_EVENTS, UPCOMING_EVENTS, FIND_PEOPLE, LINKS, SELL_BOOKS, NEW_EVENTS, MY_EVENTS, CITRUS;
	}

	public MiniModuleSidebar() {
	}

	public void add(MiniModule... modules) {
		for (MiniModule module : modules) {
			add(module);
		}
	}

	public void add(MiniModule module) {
		switch (module) {
		case COMMUNITY_EVENTS:
			add(new CommunityEventsMiniModule());
			break;
		case FIND_PEOPLE:
			if (Application.isAuthenticated()) {
				add(new FindPeopleMiniModule());
			}
			break;
		case LINKS:
			add(new LinksMiniModule());
			break;
		case MY_EVENTS:
			if (Application.isAuthenticated()) {
				add(new MyEventsMiniModule());
			}
			break;
		case NEW_EVENTS:
			add(new NewEventsMiniModule());
			break;
		case SELL_BOOKS:
			if (!Application.memberOf(Constants.BOOK_SELLERS_GROUP_ID)) {
				add(new SellBooksMiniModule());
			}
			break;
		case UPCOMING_EVENTS:
			add(new UpcomingEventsMiniModule());
			break;
		case CITRUS:
			add(new CitrusMiniModule());
			break;
		default:
			break;
		}
	}

	@Override
	public void add(Widget w) {
		w.getElement().getStyle().setMarginLeft(10, Unit.PX);
		if (getWidgetCount() == 0) {
			w.getElement().getStyle().setMarginTop(10, Unit.PX);
		}
		super.add(w);
	}

}
