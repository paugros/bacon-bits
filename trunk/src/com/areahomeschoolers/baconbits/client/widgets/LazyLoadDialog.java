package com.areahomeschoolers.baconbits.client.widgets;

// This is meant to be implemented only on classes that extend EntityEditDialog.
// It's meant as a place to make rpc calls that the dialog contents can be created.

public interface LazyLoadDialog {

	// EntityEditDialog.showContent() must be called from this function
	public abstract void loadData();

}
