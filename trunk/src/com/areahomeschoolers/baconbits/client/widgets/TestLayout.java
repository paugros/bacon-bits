package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TestLayout extends Composite {

	public TestLayout() {

		DockPanel currentPagePanel = new DockPanel();
		initWidget(currentPagePanel);
		currentPagePanel.setSize("873px", "374px");

		HorizontalPanel headerPanel = new HorizontalPanel();
		currentPagePanel.add(headerPanel, DockPanel.NORTH);
		headerPanel.setSize("870px", "41px");

		SuggestBox suggestBox = new SuggestBox();
		suggestBox.setText("Search:");
		headerPanel.add(suggestBox);
		headerPanel.setCellHorizontalAlignment(suggestBox, HasHorizontalAlignment.ALIGN_RIGHT);

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		currentPagePanel.add(horizontalPanel, DockPanel.NORTH);
		horizontalPanel.setSize("821px", "41px");

		MenuBar menuBar = new MenuBar(false);
		horizontalPanel.add(menuBar);
		menuBar.setWidth("873px");
		MenuBar menuBar_1 = new MenuBar(true);

		MenuItem mntmNewMenu_1 = new MenuItem("Home", false, menuBar_1);

		MenuItem mntmThing = new MenuItem("FAQs", false, (Command) null);
		menuBar_1.addItem(mntmThing);

		MenuItem mntmDirections = new MenuItem("Directions", false, new Command() {
			@Override
			public void execute() {
			}
		});
		menuBar_1.addItem(mntmDirections);
		menuBar.addItem(mntmNewMenu_1);
		MenuBar WHE = new MenuBar(true);

		MenuItem mntmCalendars = new MenuItem("Calendars", false, WHE);

		MenuItem mntmWhe = new MenuItem("WHE", false, (Command) null);
		WHE.addItem(mntmWhe);

		MenuItem mntmCommunity = new MenuItem("Community", false, (Command) null);
		WHE.addItem(mntmCommunity);
		menuBar.addItem(mntmCalendars);
		MenuBar menuBar_2 = new MenuBar(true);

		MenuItem mntmEventRegistration = new MenuItem("Event Registration", false, menuBar_2);

		MenuItem mntmBookSaleSeller = new MenuItem("Book Sale Seller", false, (Command) null);
		menuBar_2.addItem(mntmBookSaleSeller);

		MenuItem mntmNewItem = new MenuItem("Co-op Classes", false, new Command() {
			@Override
			public void execute() {
			}
		});
		menuBar_2.addItem(mntmNewItem);

		MenuItem mntmFieldTrips = new MenuItem("Field Trips", false, (Command) null);
		menuBar_2.addItem(mntmFieldTrips);

		MenuItem mntmNewItem_2 = new MenuItem("PE", false, (Command) null);
		menuBar_2.addItem(mntmNewItem_2);

		MenuItem mntmMixedClubDay = new MenuItem("Mixed Club Day", false, (Command) null);
		menuBar_2.addItem(mntmMixedClubDay);

		MenuItem mntmRecreationalDay = new MenuItem("Recreational Day", false, (Command) null);
		menuBar_2.addItem(mntmRecreationalDay);
		menuBar.addItem(mntmEventRegistration);
		MenuBar menuBar_3 = new MenuBar(true);

		MenuItem mntmBlog = new MenuItem("Blog", false, menuBar_3);

		MenuItem mntmHomeschoolStories = new MenuItem("Homeschool Stories", false, (Command) null);
		menuBar_3.addItem(mntmHomeschoolStories);

		MenuItem mntmNewsletter = new MenuItem("Newsletter", false, (Command) null);
		menuBar_3.addItem(mntmNewsletter);
		menuBar.addItem(mntmBlog);
		MenuBar menuBar_4 = new MenuBar(true);

		MenuItem mntmNewMenu = new MenuItem("Additonal Activies", false, menuBar_4);

		MenuItem mntmNewItem_1 = new MenuItem("Local Sports", false, new Command() {
			@Override
			public void execute() {
			}
		});
		menuBar_4.addItem(mntmNewItem_1);

		MenuItem mntmMomsNightOut = new MenuItem("Mom's Night Out", false, (Command) null);
		menuBar_4.addItem(mntmMomsNightOut);

		MenuItem mntmMom = new MenuItem("", false, (Command) null);
		menuBar_4.addItem(mntmMom);

		MenuItem mntmParentsSupportMeeting = new MenuItem("Parent's Support Meeting", false, (Command) null);
		menuBar_4.addItem(mntmParentsSupportMeeting);
		menuBar.addItem(mntmNewMenu);

		VerticalPanel verticalPanel = new VerticalPanel();
		currentPagePanel.add(verticalPanel, DockPanel.WEST);
		verticalPanel.setSize("107px", "147px");

		ScrollPanel bodyPanel = new ScrollPanel();
		currentPagePanel.add(bodyPanel, DockPanel.CENTER);
		bodyPanel.setHeight("173px");
	}

}
