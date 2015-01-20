package com.areahomeschoolers.baconbits.client.content.minimodules;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.content.user.CreateUserDialog;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.ClickLabel;
import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.Constants;
import com.areahomeschoolers.baconbits.shared.dto.User;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LinksMiniModule extends Composite {

	public LinksMiniModule() {
		VerticalPanel sp = new VerticalPanel();
		sp.setSpacing(8);
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		sp.setStyleName("module");
		sp.add(vp);
		Label linkLabel = new Label("LINKS");
		linkLabel.addStyleName("moduleTitle");
		linkLabel.getElement().getStyle().setMarginBottom(4, Unit.PX);

		if (!Common.isNullOrBlank(Application.getCurrentOrg().getFacebookUrl())) {
			HorizontalPanel hp = new HorizontalPanel();
			hp.setWidth("100%");
			hp.add(linkLabel);

			Image icon = new Image(MainImageBundle.INSTANCE.faceBook());
			String fbText = "<a href=\"" + Application.getCurrentOrg().getFacebookUrl() + "\" target=_blank>" + icon + "</a>";
			HTML fbLink = new HTML(fbText);
			hp.add(fbLink);
			hp.setCellHorizontalAlignment(fbLink, HasHorizontalAlignment.ALIGN_RIGHT);

			vp.add(hp);
		} else {
			vp.add(linkLabel);
		}

		if (Application.isAuthenticated()) {
			vp.add(new DefaultHyperlink("Member Directory", PageUrl.userList()));
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
		vp.add(new DefaultHyperlink("Event Listing", PageUrl.eventList()));
		vp.add(new DefaultHyperlink("Event Calendar", PageUrl.eventCalendar()));
		vp.add(new DefaultHyperlink("News", PageUrl.blog(0)));
		vp.add(new DefaultHyperlink("Book Store", PageUrl.bookList()));

		if (Application.isAuthenticated()) {
			vp.add(new DefaultHyperlink("My Events", PageUrl.user(Application.getCurrentUserId()) + "&tab=1"));
			if (Application.getCurrentUser().memberOf(Constants.ONLINE_BOOK_SELLERS_GROUP_ID)) {
				vp.add(new DefaultHyperlink("My Books", PageUrl.user(Application.getCurrentUserId()) + "&tab=4"));
			}
			vp.add(new DefaultHyperlink("My Profile", PageUrl.user(Application.getCurrentUserId())));
			if (!Application.getCurrentUser().isChild()) {
				vp.add(new DefaultHyperlink("My Shopping Cart", PageUrl.payment()));
			}
		}

		initWidget(sp);
	}

}
