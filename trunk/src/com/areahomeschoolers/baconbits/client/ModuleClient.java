package com.areahomeschoolers.baconbits.client;

public interface ModuleClient {
	void onSuccess(Object instance);

	void onUnavailable(Throwable err);
}
