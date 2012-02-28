package com.areahomeschoolers.baconbits.client.event;

public interface UploadCompleteHandler extends CustomHandler {
	/**
	 * @param documetnId
	 *            The ID of the document just uploaded to the database
	 */
	public void onUploadComplete(int documentId);
}