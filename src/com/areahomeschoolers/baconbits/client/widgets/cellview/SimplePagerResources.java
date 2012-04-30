package com.areahomeschoolers.baconbits.client.widgets.cellview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.cellview.client.SimplePager;

public interface SimplePagerResources extends SimplePager.Resources {

	public interface SimplePagerStyle extends SimplePager.Style {
	}

	public static final SimplePagerResources INSTANCE = GWT.create(SimplePagerResources.class);

	@Override
	@Source({ "SimplePager.css" })
	public SimplePagerStyle simplePagerStyle();

	/**
	 * The image used to skip ahead multiple pages.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerFastForward();

	/**
	 * The disabled "fast forward" image.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerFastForwardDisabled();

	/**
	 * The image used to go to the first page.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerFirstPage();

	/**
	 * The disabled first page image.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerFirstPageDisabled();

	/**
	 * The image used to go to the last page.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerLastPage();

	/**
	 * The disabled last page image.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerLastPageDisabled();

	/**
	 * The image used to go to the next page.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerNextPage();

	/**
	 * The disabled next page image.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerNextPageDisabled();

	/**
	 * The image used to go to the previous page.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerPreviousPage();

	/**
	 * The disabled previous page image.
	 */
	@Override
	@ImageOptions(flipRtl = true)
	ImageResource simplePagerPreviousPageDisabled();

}
