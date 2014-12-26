package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class ConstantContactLink extends Composite {

	public ConstantContactLink(String text) {
		String signUpText = "<a href=\"" + Constants.CONSTANT_CONTACT_URL + "\" target=_blank ";
		signUpText += "style=\"background-color:#e8e8e8;border:solid 1px #5b5b5b;color:#5b5b5b;padding: 5px 10px;text-shadow:none;\">" + text + "</a>";
		HTML signUp = new HTML(signUpText);

		initWidget(signUp);
	}

}
