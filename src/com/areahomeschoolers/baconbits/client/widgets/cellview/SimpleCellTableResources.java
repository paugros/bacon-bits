package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.cellview.client.CellTable;

public interface SimpleCellTableResources extends CellTable.Resources {

	public interface SimpleCellTableStyle extends CellTable.Style {
	}

	public static final SimpleCellTableResources INSTANCE = GWT.create(SimpleCellTableResources.class);

	@Override
	@Source({ "SimpleCellTable.css" })
	public SimpleCellTableStyle cellTableStyle();

	/**
	 * Icon used when a column is sorted in ascending order.
	 */
	@Override
	@Source("sortAscending.png")
	@ImageOptions(flipRtl = true)
	ImageResource cellTableSortAscending();

	/**
	 * Icon used when a column is sorted in descending order.
	 */
	@Override
	@Source("sortDescending.png")
	@ImageOptions(flipRtl = true)
	ImageResource cellTableSortDescending();
}
