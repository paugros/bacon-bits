package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class MailListLink extends Composite {

	public MailListLink(String text) {
		// String signUpText = "<a href=\"" + Constants.CONSTANT_CONTACT_URL + "\" target=_blank ";
		// signUpText += "style=\"background-color:#e8e8e8;border:solid 1px #5b5b5b;color:#5b5b5b;padding: 5px 10px;text-shadow:none;\">" + text + "</a>";

		String signUpText = "<link href=\"//cdn-images.mailchimp.com/embedcode/slim-081711.css\" rel=\"stylesheet\" type=\"text/css\"> \n";
		signUpText += "<style type=\"text/css\"> \n";
		signUpText += "#mc_embed_signup{background:#fff; clear:left; font:14px Helvetica,Arial,sans-serif; } \n";
		signUpText += "/* Add your own MailChimp form style overrides in your site stylesheet or in this style block. \n";
		signUpText += "We recommend moving this block and the preceding CSS link to the HEAD of your HTML file. */ \n";
		signUpText += "</style> \n";
		signUpText += "<div id=\"mc_embed_signup\"> \n";
		signUpText += "<form action=\"//myhomeschoolgroups.us3.list-manage.com/subscribe/post?u=6dd656b8b7c640dfc5806efe0&amp;id=4041865d95\" method=\"post\" ";
		signUpText += "id=\"mc-embedded-subscribe-form\" name=\"mc-embedded-subscribe-form\" class=\"validate\" target=\"_blank\" novalidate> \n";
		signUpText += "<div id=\"mc_embed_signup_scroll\"> \n";
		signUpText += "<label for=\"mce-EMAIL\">Subscribe to our mailing list</label> \n";
		signUpText += "<input type=\"email\" value=\"\" name=\"EMAIL\" class=\"email\" id=\"mce-EMAIL\" placeholder=\"email address\" required> \n";
		signUpText += "<!-- real people should not fill this in and expect good things - do not remove this or risk form bot signups--> \n";
		signUpText += "<div style=\"position: absolute; left: -5000px;\"><input type=\"text\" name=\"b_6dd656b8b7c640dfc5806efe0_4041865d95\" tabindex=\"-1\" value=\"\"></div> \n";
		signUpText += "<div class=\"clear\"><input type=\"submit\" value=\"Subscribe\" name=\"subscribe\" id=\"mc-embedded-subscribe\" class=\"button\"></div> \n";
		signUpText += "</div> \n";
		signUpText += "</form> \n";
		signUpText += "</div>";
		HTML signUp = new HTML(signUpText);

		initWidget(signUp);
	}

}
