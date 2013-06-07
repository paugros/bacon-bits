package com.areahomeschoolers.baconbits.client.content.calendar;

public interface HasLayout {

	/**
	 * Forces the widget to re-calculate and perform
	 * layout operations.
	 */
	public void doLayout();
	
	/**
	 * Suspends the widget from performing layout operations.
	 */
	public void suspendLayout();

	/**
	 * Enables the widget to perform layout operations. Any pending layout
	 * operations will be executed.
	 */
	public void resumeLayout();
}
