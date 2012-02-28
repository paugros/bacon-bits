package com.areahomeschoolers.baconbits.client.event;

import com.areahomeschoolers.baconbits.client.widgets.FormField;

public interface FormCancelHandler extends CustomHandler {
	public void onFormCancel(FormField formWidget);
}
