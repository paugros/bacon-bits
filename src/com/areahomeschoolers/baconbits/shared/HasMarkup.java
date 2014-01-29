package com.areahomeschoolers.baconbits.shared;

public interface HasMarkup {
	public double getMarkupDollars();

	public boolean getMarkupOverride();

	public double getMarkupPercent();

	public void setMarkupDollars(double markup);

	public void setMarkupOverride(boolean override);

	public void setMarkupPercent(double markup);
}
