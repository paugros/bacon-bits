package com.areahomeschoolers.baconbits.client.widgets;

import java.util.EnumSet;
import java.util.Set;

/**
 * Indicates the ability to track and apply a set of criteria used for enabling and disabling sub elements.
 */
public interface HasDisableCriteria<T> {

	/**
	 * Adds a single disable criterion to the set. The criterion is usually a member of an enum. The criterion is not applied at this time. A call to
	 * {@link #applyDisableCriteria()} is required for application.
	 * 
	 * @param criterion
	 */
	public void addDisableCriterion(T criterion);

	/**
	 * Applies the current set of disable criteria. Sub elements matching any of the criteria will be disabled. Those that do not match any criteria will be
	 * enabled.
	 */
	public void applyDisableCriteria();

	/**
	 * Removes a specific disable criterion from the current set.
	 * 
	 * @param criterion
	 */
	public void removeDisableCriterion(T criterion);

	/**
	 * Overwrites the current set of disable criteria (generally an {@link EnumSet}).
	 * 
	 * @param disableCriteria
	 */
	public void setDisableCriteria(Set<T> disableCriteria);
}
