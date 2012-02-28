package com.areahomeschoolers.baconbits.shared.dto;

/**
 * Indicates the presence of a numeric id, usually corresponding to a DTO's database table id.
 */
public interface HasId {
	/**
	 * @return The id of the implementing type
	 */
	public int getId();

	/**
	 * Sets the id of the entity
	 * 
	 * @param id
	 */
	public void setId(int id);
}
