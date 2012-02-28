package com.areahomeschoolers.baconbits.client.validation;

/**
 * Intended for use as an anonymous inner type in conjunction with a {@link Validator}. Implementations of {@link #validate(Validator)} must use the provided
 * Validator's methods to report error state and information.
 */
public interface ValidatorCommand {
	/**
	 * Performs validation of user input and reports error state, message and level.
	 * 
	 * @param validator
	 */
	public void validate(Validator validator);
}
