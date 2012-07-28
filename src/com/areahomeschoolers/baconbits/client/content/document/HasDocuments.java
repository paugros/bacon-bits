package com.areahomeschoolers.baconbits.client.content.document;

import com.areahomeschoolers.baconbits.shared.dto.EntityType;

public interface HasDocuments {
	public int getDocumentCount();

	public EntityType getEntityType();

	public int getId();

	public boolean hasDocuments();
}
