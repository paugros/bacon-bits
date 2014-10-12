package com.areahomeschoolers.baconbits.client.content;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.minimodules.ActiveUsersMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.AdsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.CitrusMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.CommunityEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.LinksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.MyEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.NewBooksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.NewEventsMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.NewUsersMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.SellBooksMiniModule;
import com.areahomeschoolers.baconbits.client.content.minimodules.UpcomingEventsMiniModule;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Sidebar extends Composite {
	public enum MiniModule {
		COMMUNITY_EVENTS, UPCOMING_EVENTS, LINKS, SELL_BOOKS, NEW_EVENTS, MY_EVENTS, CITRUS, NEW_USERS, NEW_BOOKS, ACTIVE_USERS, ADS;
	}

	private static Map<MiniModule, Widget> cache = new HashMap<MiniModule, Widget>();

	private static EnumSet<MiniModule> expireModules = EnumSet.of(MiniModule.COMMUNITY_EVENTS, MiniModule.UPCOMING_EVENTS, MiniModule.NEW_EVENTS,
			MiniModule.MY_EVENTS, MiniModule.ACTIVE_USERS, MiniModule.NEW_USERS);
	private static EnumSet<MiniModule> noCacheModules = EnumSet.of(MiniModule.ACTIVE_USERS);

	public static Sidebar create(MiniModule... modules) {
		return new Sidebar(modules);
	}

	private VerticalPanel vp = new VerticalPanel();

	private SimplePanel container = new SimplePanel();

	public Sidebar() {
		Timer timer = new Timer() {
			@Override
			public void run() {
				for (MiniModule m : expireModules) {
					cache.remove(m);
				}
			}
		};
		Application.scheduleRepeatingPageTimer(timer, 10 * 60 * 1000);

		container.setWidget(vp);
		container.getElement().getStyle().setPaddingLeft(10, Unit.PX);
		container.getElement().getStyle().setPaddingTop(10, Unit.PX);
		initWidget(container);
	}

	public Sidebar(MiniModule... modules) {
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
			add(module, cache.get(module));
			return;
		}

		switch (module) {
		case ADS:
			add(module, new AdsMiniModule());
			break;
		case COMMUNITY_EVENTS:
			add(module, new CommunityEventsMiniModule());
			break;
		case LINKS:
			add(module, new LinksMiniModule());
			break;
		case MY_EVENTS:
			if (Application.isAuthenticated()) {
				add(module, new MyEventsMiniModule());
			}
			break;
		case NEW_BOOKS:
			add(module, new NewBooksMiniModule());
			break;
		case NEW_EVENTS:
			add(module, new NewEventsMiniModule());
			break;
		case SELL_BOOKS:
			if (!Application.memberOf(Constants.ONLINE_BOOK_SELLERS_GROUP_ID)) {
				add(module, new SellBooksMiniModule());
			}
			break;
		case UPCOMING_EVENTS:
			add(module, new UpcomingEventsMiniModule());
			break;
		case CITRUS:
			add(module, new CitrusMiniModule());
			break;
		case ACTIVE_USERS:
			add(module, new ActiveUsersMiniModule());
			break;
		case NEW_USERS:
			add(module, new NewUsersMiniModule());
			break;
		default:
			break;
		}
	}

	private void add(MiniModule module, Widget widget) {
		widget.getElement().getStyle().setMarginBottom(10, Unit.PX);
		if (!noCacheModules.contains(module)) {
			cache.put(module, widget);
		}
		vp.add(widget);
	}

}
