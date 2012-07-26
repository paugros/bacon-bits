package com.areahomeschoolers.baconbits.client.rpc;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.widgets.AlertDialog;
import com.areahomeschoolers.baconbits.client.widgets.StatusPanel;
import com.areahomeschoolers.baconbits.shared.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Label;

/**
 * Basic {@link AsyncCallback} that optionally displays a "Loading..." message and handles common failure exceptions
 * 
 * @param <T>
 *            return type of GWT RPC method invoked.
 */
public abstract class Callback<T> implements AsyncCallback<T> {
	private static StatusPanel sp;
	private static int callCount = 0;

	public static void handleRpcExeption(Throwable caught) {
		String errorMessage = caught.toString();

		if (Application.getRpcFailureCommand() != null) {
			Application.getRpcFailureCommand().execute();
		}

		if (caught instanceof StatusCodeException) {
			StatusCodeException st = (StatusCodeException) caught;
			switch (st.getStatusCode()) {
			case 403:
				// this is commented out because it takes over the whole page, even when most of the page would load normally
				// maybe we can make it into a small popup panel at the bottom of the screen that fades away
				// new ErrorPage(PageError.NOT_AUTHORIZED);
			case 0:
			}
		} else if (caught instanceof InvocationException) {
			if (errorMessage.indexOf(Constants.NOT_AUTHENTICATED_TOKEN) != -1) {

			} else {
				GWT.log("RPC call failed", caught);
			}
		} else if (caught instanceof IncompatibleRemoteServiceException) {
			String message = "This version of the application is out of date. Click below to update.";
			Label label = new Label(message);
			label.setWidth("300px");
			AlertDialog dialog = new AlertDialog("Application Out of Date", label);
			dialog.getButton().setText("Update");
			dialog.getButton().addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.Location.reload();
				}
			});
			dialog.center();
		} else {
			String message = "Sorry, Dash could not reach the server. ";
			message += "If the problem persists, please stop working until you are able to re-establish a network connection, as your unsaved changes may be lost.";
			Label label = new Label(message);
			label.setWidth("300px");
			AlertDialog dialog = new AlertDialog("Network Error", label);
			dialog.center();
		}
	}

	public static void hideStatusPanel() {
		callCount = 0;
		sp.hide();
	}

	/**
	 * Force the "Loading..." status panel to display: for use when not newing up a Callback
	 */
	public static void incrementCallCount() {
		if (callCount == 0) {
			sp.show();
		}

		callCount++;
	}

	public static void setStatusPanel(StatusPanel statusPanel) {
		sp = statusPanel;
	}

	private boolean showStatus;

	public Callback() {
		this(true);
	}

	public Callback(boolean showStatus) {
		this.showStatus = showStatus;
		increment();
	}

	@Override
	public void onFailure(Throwable caught) {
		handleRpcExeption(caught);

		try {
			doOnFailure(caught);
		} finally {
			decrement();
		}
	}

	@Override
	public void onSuccess(T result) {
		try {
			doOnSuccess(result);
		} finally {
			decrement();
		}
	}

	public void setShowStatus(boolean showStatus) {
		this.showStatus = showStatus;
	}

	private void decrement() {
		Application.setRpcFailureCommand(null);

		if (showStatus) {
			if (callCount > 0) {
				callCount--;
			}

			if (callCount == 0) {
				sp.hide();
			}

		}
	}

	private void increment() {
		if (showStatus) {
			if (callCount == 0) {
				sp.show();
			}

			callCount++;
		}
	}

	protected void doOnFailure(Throwable caught) {
	}

	protected abstract void doOnSuccess(T result);
}
