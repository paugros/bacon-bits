package com.areahomeschoolers.baconbits.client;

import java.util.HashMap;
import java.util.Map;

import com.areahomeschoolers.baconbits.client.exceptions.ClientClassNotFoundException;
import com.areahomeschoolers.baconbits.client.rpc.service.ArticleService;
import com.areahomeschoolers.baconbits.client.rpc.service.BookService;
import com.areahomeschoolers.baconbits.client.rpc.service.DocumentService;
import com.areahomeschoolers.baconbits.client.rpc.service.EventService;
import com.areahomeschoolers.baconbits.client.rpc.service.LoginService;
import com.areahomeschoolers.baconbits.client.rpc.service.SuggestService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserPreferenceService;
import com.areahomeschoolers.baconbits.client.rpc.service.UserService;
import com.areahomeschoolers.baconbits.shared.Common;

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
			String className = Common.getSimpleClassName(serviceClass);
			if ("ArticleService".equals(className)) {
				proxy = (RemoteServiceProxy) GWT.create(ArticleService.class);
			} else if ("LoginService".equals(className)) {
				proxy = (RemoteServiceProxy) GWT.create(LoginService.class);
			} else if ("UserService".equals(className)) {
				proxy = (RemoteServiceProxy) GWT.create(UserService.class);
			} else if ("EventService".equals(className)) {
				proxy = (RemoteServiceProxy) GWT.create(EventService.class);
			} else if ("DocumentService".equals(className)) {
				proxy = (RemoteServiceProxy) GWT.create(DocumentService.class);
			} else if ("UserPreferenceService".equals(className)) {
				proxy = (RemoteServiceProxy) GWT.create(UserPreferenceService.class);
			} else if ("BookService".equals(className)) {
				proxy = (RemoteServiceProxy) GWT.create(BookService.class);
			} else if ("SuggestService".equals(className)) {
				proxy = (RemoteServiceProxy) GWT.create(SuggestService.class);
			}

			services.put(serviceClass, proxy);
		}

		return proxy;
	}
}
