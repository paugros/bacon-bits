package com.areahomeschoolers.baconbits.shared.dto;


/**
 * Primarily for DTOs, indicates the ability of an object to provide a short string descriptor of itself. Useful for type-agnostic code that wishes to output a
 * brief summary of an object, as in EntityPicker text boxes.
 */
public interface HasDescriptor {
	/**
	 * @return The string descriptor of the implementing object.
	 */
	public String getDescriptor();
}
