package com.areahomeschoolers.baconbits.client;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.exceptions.ClientClassNotFoundException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.impl.RemoteServiceProxy;

/**
 * Client-side GWT RPC service accessor and cache.
 */
public class ServiceCache {

	public static Map<Class<? extends RemoteService>, RemoteServiceProxy> services = new HashMap<Class<? extends RemoteService>, RemoteServiceProxy>();

	/**
	 * Returns a reference to the GWT RPC service requested. Services are instantiated on-demand and cached for later use.
	 * 
	 * @param serviceClass
	 *            class literal of the GWT RPC service interface to be retrieved.
	 * @return the service requested.
	 * @throws ClientClassNotFoundException
	 *             thrown if the service class requested cannot be found at run-time.
	 */
	public static RemoteServiceProxy getService(Class<? extends RemoteService> serviceClass) throws ClientClassNotFoundException {
		RemoteServiceProxy proxy = services.get(serviceClass);

		if (proxy == null) {
			proxy = (RemoteServiceProxy) GWT.create(serviceClass);

			services.put(serviceClass, proxy);
		}

		return proxy;
	}
}
