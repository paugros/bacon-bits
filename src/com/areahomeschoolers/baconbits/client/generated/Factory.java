package com.areahomeschoolers.baconbits.client.generated;

import com.areahomeschoolers.baconbits.client.ModuleClient;
import com.areahomeschoolers.baconbits.client.exceptions.ClientClassNotFoundException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Factory interface to create instance of any {@link Page}, {@link Page} or {@link RemoteService} type at run-time.
 */
public interface Factory {
	/**
	 * Allocates a new instance of type className
	 * 
	 * @param className
	 *            Simple class name of type to be allocated (e.g. "OrderPage")
	 * @return Newly allocated instance of className
	 * @throws ClientClassNotFoundException
	 *             Thrown when generated factory does not recognize className: usually either a typo or the className neglects to implement {@link Page}
	 */
	Object newInstance(String className) throws ClientClassNotFoundException;

	/**
	 * Allocates a new {@link Page} to appear in the content portion of {@link Layout}
	 * 
	 * @param className
	 *            Simple class name of type to be allocated (e.g. "OrderPage")
	 * @param sp
	 *            SimplePanel to which the page adds content intended to appear in the main content area of {@link Layout}
	 * @throws ClientClassNotFoundException
	 *             Thrown when generated factory does not recognize className: usually either a typo or the className neglects to implement {@link Page}
	 */
	void newPageInstance(String className, VerticalPanel sp, ModuleClient client) throws ClientClassNotFoundException;

}
