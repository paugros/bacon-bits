package com.areahomeschoolers.baconbits.client.content;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

public class MiniModuleSidebar extends SidebarPanel {
	public enum MiniModule {
		COMMUNITY_EVENTS, UPCOMING_EVENTS, FIND_PEOPLE, LINKS, SELL_BOOKS, NEW_EVENTS, MY_EVENTS, CITRUS;
	}

	private static Map<MiniModule, Widget> cache = new HashMap<MiniModule, Widget>();

	private static EnumSet<MiniModule> expireModules = EnumSet.of(MiniModule.COMMUNITY_EVENTS, MiniModule.UPCOMING_EVENTS, MiniModule.NEW_EVENTS,
			MiniModule.MY_EVENTS);

	private static Timer timer = new Timer() {
		@Override
		public void run() {
			for (MiniModule m : expireModules) {
				cache.remove(m);
			}
		}
	};

	static {
		timer.scheduleRepeating(10 * 60 * 1000);
	}

	public static MiniModuleSidebar create(MiniModule... modules) {
		return new MiniModuleSidebar(modules);
	}

	public MiniModuleSidebar() {

	}

	public MiniModuleSidebar(MiniModule... modules) {
		this();
		add(modules);
	}

	public void add(MiniModule... modules) {
		for (MiniModule module : modules) {
			add(module);
		}
	}

	public void add(MiniModule module) {
		if (cache.containsKey(module)) {
			add(cache.get(module));
			return;
		}

		switch (module) {
		case COMMUNITY_EVENTS:
			add(module, new CommunityEventsMiniModule());
			break;
		case FIND_PEOPLE:
			if (Application.isAuthenticated()) {
				add(module, new FindPeopleMiniModule());
			}
			break;
		case LINKS:
			add(module, new LinksMiniModule());
			break;
		case MY_EVENTS:
			if (Application.isAuthenticated()) {
				add(module, new MyEventsMiniModule());
			}
			break;
		case NEW_EVENTS:
			add(module, new NewEventsMiniModule());
			break;
		case SELL_BOOKS:
			if (!Application.memberOf(Constants.BOOK_SELLERS_GROUP_ID)) {
				add(module, new SellBooksMiniModule());
			}
			break;
		case UPCOMING_EVENTS:
			add(module, new UpcomingEventsMiniModule());
			break;
		case CITRUS:
			add(module, new CitrusMiniModule());
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

	private void add(MiniModule module, Widget widget) {
		cache.put(module, widget);
		add(widget);
	}

}
