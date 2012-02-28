package com.areahomeschoolers.baconbits.client.event;

import com.areahomeschoolers.baconbits.client.widgets.FormField;

/**
 * Intended to be an anonymous inner type; the {@link #onFormSubmit(FormField)} method takes a {@link FormField}. This allows the submission handlers for any
 * {@link FormField} (such as {@link FormField}) to have access to the {@link FormField} to toggle it's input visibility or perform other actions.
 */
public interface FormSubmitHandler extends CustomHandler {
	/**
	 * @param formField
	 *            The form widget that will fire this {@link FormSubmitHandler} upon submission
	 */
	public void onFormSubmit(FormField formField);
}
