package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasText;

public abstract class TextHeaderCreator<W extends HasText> {

	/**
	 * Override if W has a click event
	 * 
	 * @return
	 */
	protected Command getClickCommand() {
		return null;
	}

	protected abstract W getWidget();

}
