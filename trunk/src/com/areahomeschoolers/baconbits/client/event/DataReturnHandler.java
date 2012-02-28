package com.areahomeschoolers.baconbits.client.event;


/**
 * For use with code that is intended to run upon the return of data from RPC.
 */
public interface DataReturnHandler extends CustomHandler {
	/**
	 * The code to run upon receipt of the data.
	 */
	public void onDataReturn();
}
