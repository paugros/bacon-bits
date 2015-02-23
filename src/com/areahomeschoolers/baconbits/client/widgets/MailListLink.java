package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class MailListLink extends Composite {

	public MailListLink(String text) {
		String signUpText = "<form action=\"//myhomeschoolgroups.us3.list-manage.com/subscribe/post?u=6dd656b8b7c640dfc5806efe0&amp;id=4041865d95\" method=\"post\" ";
		signUpText += "id=\"mc-embedded-subscribe-form\" name=\"mc-embedded-subscribe-form\" class=\"validate\" target=\"_blank\" novalidate> \n";
		signUpText += "<input type=\"email\" value=\"\" name=\"EMAIL\" class=\"email\" id=\"mce-EMAIL\" placeholder=\"email address\" required> \n";
		signUpText += "<input type=\"submit\" value=\"Sign up for Updates\" name=\"subscribe\" id=\"mc-embedded-subscribe\" class=\"button\"> \n";
		signUpText += "</form> \n";
		HTML signUp = new HTML(signUpText);

		initWidget(signUp);
	}

}
