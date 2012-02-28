package com.areahomeschoolers.baconbits.client.event;

public interface ParameterHandler<T> extends CustomHandler {
	public void execute(T item);
}
