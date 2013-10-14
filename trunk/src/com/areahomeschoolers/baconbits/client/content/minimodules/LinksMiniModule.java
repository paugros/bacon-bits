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
import com.google.gwt.user.client.ui.VerticalPanel;

public class LinksMiniModule extends Composite {

	public LinksMiniModule() {
		VerticalPanel sp = new VerticalPanel();
		sp.setSpacing(8);
		VerticalPanel vp = new VerticalPanel();
		sp.setStyleName("module");
		sp.add(vp);
		Label linkLabel = new Label("LINKS");
		linkLabel.addStyleName("moduleTitle");
		linkLabel.getElement().getStyle().setMarginBottom(4, Unit.PX);
		vp.add(linkLabel);

		if (Application.isAuthenticated()) {
			vp.add(new Hyperlink("Find People", PageUrl.userList()));
		} else {
			ClickLabel cl = new ClickLabel("Create an Account", new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					new CreateUserDialog().center(new User());
				}
			});
			cl.addStyleName("bold");
			vp.add(cl);
		}
		vp.add(new Hyperlink("Event Listing", PageUrl.eventList()));
		vp.add(new Hyperlink("Event Calendar", PageUrl.eventCalendar()));
		vp.add(new Hyperlink("Book Store", PageUrl.bookSearch()));

		if (Application.isAuthenticated()) {
			vp.add(new Hyperlink("My Events", PageUrl.user(Application.getCurrentUserId()) + "&tab=1"));
			if (Application.getCurrentUser().memberOf(Constants.BOOK_SELLERS_GROUP_ID)) {
				vp.add(new Hyperlink("My Books", PageUrl.user(Application.getCurrentUserId()) + "&tab=4"));
			}
			vp.add(new Hyperlink("My Profile", PageUrl.user(Application.getCurrentUserId())));
			if (!Application.getCurrentUser().isChild()) {
				vp.add(new Hyperlink("My Shopping Cart", PageUrl.payment()));
			}
		}

		initWidget(sp);
	}

}
