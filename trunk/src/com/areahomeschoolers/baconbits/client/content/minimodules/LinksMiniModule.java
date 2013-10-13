package com.areahomeschoolers.baconbits.client.content.minimodules;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.user.CreateUserDialog;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LinksMiniModule extends Composite {

	public LinksMiniModule() {
		SimplePanel sp = new SimplePanel();
		sp.getElement().getStyle().setPadding(8, Unit.PX);
		VerticalPanel lp = new VerticalPanel();
		sp.setStyleName("module");
		sp.addStyleDependentName("light");
		sp.setWidget(lp);
		Label linkLabel = new Label("LINKS");
		linkLabel.addStyleName("moduleTitle");
		linkLabel.getElement().getStyle().setMarginBottom(4, Unit.PX);
		lp.add(linkLabel);

		if (Application.isAuthenticated()) {
			lp.add(new Hyperlink("Find People", PageUrl.userList()));
		} else {
			ClickLabel cl = new ClickLabel("Create an Account", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					new CreateUserDialog().center(new User());
				}
			});
			cl.addStyleName("bold");
			lp.add(cl);
		}
		lp.add(new Hyperlink("Event Listing", PageUrl.eventList()));
		lp.add(new Hyperlink("Event Calendar", PageUrl.eventCalendar()));
		lp.add(new Hyperlink("Book Store", PageUrl.bookSearch()));

		if (Application.isAuthenticated()) {
			lp.add(new Hyperlink("My Events", PageUrl.user(Application.getCurrentUserId()) + "&tab=1"));
			if (Application.getCurrentUser().memberOf(Constants.BOOK_SELLERS_GROUP_ID)) {
				lp.add(new Hyperlink("My Books", PageUrl.user(Application.getCurrentUserId()) + "&tab=4"));
			}
			lp.add(new Hyperlink("My Profile", PageUrl.user(Application.getCurrentUserId())));
			if (!Application.getCurrentUser().isChild()) {
				lp.add(new Hyperlink("My Shopping Cart", PageUrl.payment()));
			}
		}

		initWidget(sp);
	}

}
