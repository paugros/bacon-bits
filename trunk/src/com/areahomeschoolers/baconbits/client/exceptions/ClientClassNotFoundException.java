package com.areahomeschoolers.baconbits.client.exceptions;

/**
 * Exception indicating that a page was requested that does not exist.
 */
public final class ClientClassNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClientClassNotFoundException() {
		super("\"Dynamic\" class not found by generated factory.  Did you forget to implement the Page or Instantiable interface?");
	}
}
