package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.i18n.shared.DirectionEstimator;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.areahomeschoolers.baconbits.client.widgets.DefaultHyperlink;

public class DefaultInlineHyperlink extends DefaultHyperlink {

	/**
	 * Creates an empty hyperlink.
	 */
	public DefaultInlineHyperlink() {
		super(null);

		setStyleName("gwt-InlineHyperlink");
	}

	/**
	 * Creates a hyperlink with its html and target history token specified.
	 * 
	 * @param html
	 *            the hyperlink's html
	 * @param dir
	 *            the html's direction
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 * @see #setTargetHistoryToken
	 */
	public DefaultInlineHyperlink(SafeHtml html, Direction dir, String targetHistoryToken) {
		this(html.asString(), true, dir, targetHistoryToken);
	}

	/**
	 * Creates a hyperlink with its html and target history token specified.
	 * 
	 * @param html
	 *            the hyperlink's html
	 * @param directionEstimator
	 *            A DirectionEstimator object used for automatic direction adjustment. For convenience, {@link Hyperlink#DEFAULT_DIRECTION_ESTIMATOR} can be
	 *            used.
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 * @see #setTargetHistoryToken
	 */
	public DefaultInlineHyperlink(SafeHtml html, DirectionEstimator directionEstimator, String targetHistoryToken) {
		this(html.asString(), true, directionEstimator, targetHistoryToken);
	}

	/**
	 * Creates a hyperlink with its html and target history token specified.
	 * 
	 * @param html
	 *            the hyperlink's html
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 * @see #setTargetHistoryToken
	 */
	public DefaultInlineHyperlink(SafeHtml html, String targetHistoryToken) {
		this(html.asString(), true, targetHistoryToken);
	}

	/**
	 * Creates a hyperlink with its text and target history token specified.
	 * 
	 * @param text
	 *            the hyperlink's text
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as html
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 * @see #setTargetHistoryToken
	 */
	public DefaultInlineHyperlink(String text, boolean asHTML, String targetHistoryToken) {
		this();
		directionalTextHelper.setTextOrHtml(text, asHTML);
		setTargetHistoryToken(targetHistoryToken);
	}

	/**
	 * Creates a hyperlink with its text and target history token specified.
	 * 
	 * @param text
	 *            the hyperlink's text
	 * @param dir
	 *            the text's direction
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 */
	public DefaultInlineHyperlink(String text, Direction dir, String targetHistoryToken) {
		this(text, false, dir, targetHistoryToken);
	}

	/**
	 * Creates a hyperlink with its text and target history token specified.
	 * 
	 * @param text
	 *            the hyperlink's text
	 * @param directionEstimator
	 *            A DirectionEstimator object used for automatic direction adjustment. For convenience, {@link Hyperlink#DEFAULT_DIRECTION_ESTIMATOR} can be
	 *            used.
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 */
	public DefaultInlineHyperlink(String text, DirectionEstimator directionEstimator, String targetHistoryToken) {
		this(text, false, directionEstimator, targetHistoryToken);
	}

	/**
	 * Creates a hyperlink with its text and target history token specified.
	 * 
	 * @param text
	 *            the hyperlink's text
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 */
	public DefaultInlineHyperlink(String text, String targetHistoryToken) {
		this(text, false, targetHistoryToken);
	}

	/**
	 * Creates a hyperlink with its text and target history token specified.
	 * 
	 * @param text
	 *            the hyperlink's text
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as html
	 * @param dir
	 *            the text's direction
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 * @see #setTargetHistoryToken
	 */
	private DefaultInlineHyperlink(String text, boolean asHTML, Direction dir, String targetHistoryToken) {
		this();
		directionalTextHelper.setTextOrHtml(text, dir, asHTML);
		setTargetHistoryToken(targetHistoryToken);
	}

	/**
	 * Creates a hyperlink with its text and target history token specified.
	 * 
	 * @param text
	 *            the hyperlink's text
	 * @param asHTML
	 *            <code>true</code> to treat the specified text as html
	 * @param directionEstimator
	 *            A DirectionEstimator object used for automatic direction adjustment. For convenience, {@link Hyperlink#DEFAULT_DIRECTION_ESTIMATOR} can be
	 *            used.
	 * @param targetHistoryToken
	 *            the history token to which it will link
	 * @see #setTargetHistoryToken
	 */
	private DefaultInlineHyperlink(String text, boolean asHTML, DirectionEstimator directionEstimator, String targetHistoryToken) {
		this();
		directionalTextHelper.setDirectionEstimator(directionEstimator);
		directionalTextHelper.setTextOrHtml(text, asHTML);
		setTargetHistoryToken(targetHistoryToken);
	}
}
