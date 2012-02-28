package com.areahomeschoolers.baconbits.client.validation;

import java.util.List;

import com.areahomeschoolers.baconbits.client.widgets.FormField;

public interface ValidationErrorHandler {
	public void onError(List<FormField> errorFields);
}
