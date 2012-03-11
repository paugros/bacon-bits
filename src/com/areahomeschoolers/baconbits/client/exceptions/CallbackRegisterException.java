package com.areahomeschoolers.baconbits.client.exceptions;


/**
 * A {@link RuntimeException} thrown when a {@link CallbackManager} receives more than the promised number of callback registrations.
 */
public final class CallbackRegisterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CallbackRegisterException() {
		super("Cannot register additional callbacks after the register has been finalized.");
	}
}
