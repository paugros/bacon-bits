package com.areahomeschoolers.baconbits.client.validation;

/**
 * Indicates that an object stores its own validator
 */
public interface HasValidator {
	public Validator getValidator();

	public boolean isRequired();

	public void setRequired(boolean required);
}
