package com.areahomeschoolers.baconbits.client.content.minimodules;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.util.PageUrl;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FindPeopleMiniModule extends Composite {

	public FindPeopleMiniModule() {
		VerticalPanel vp = new VerticalPanel();
		vp.setWidth("100%");
		vp.setSpacing(8);
		Hyperlink h = new Hyperlink("Find People", PageUrl.userList());
		h.addStyleName("moduleTitle");

		PaddedPanel pp = new PaddedPanel();
		pp.add(h);

		vp.add(pp);
		Hyperlink priv = new Hyperlink("privacy preferences", PageUrl.user(Application.getCurrentUserId()) + "&tab=7");
		Hyperlink prof = new Hyperlink("profile page", PageUrl.user(Application.getCurrentUserId()));
		String t = "Click above to search our directory of homeschoolers. Find people in your area, with kids the same age as your own, who share your interests.<br><br>";
		t += "Visit your " + prof.toString() + " to add your interests.<br><br>";
		t += "Visit your " + priv.toString() + " to adjust what information you share with other people.";
		vp.add(new HTML(t));

		initWidget(vp);
	}

}
