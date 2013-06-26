package com.areahomeschoolers.baconbits.shared.dto;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GroupData implements IsSerializable, Serializable {

	private static final long serialVersionUID = 1L;

	private int organizationId;
	private boolean isAdministrator;
	private boolean isOrganization;

	public GroupData() {

	}

	public int getOrganizationId() {
		return organizationId;
	}

	public boolean isAdministrator() {
		return isAdministrator;
	}

	public boolean isOrganization() {
		return isOrganization;
	}

	public void setAdministrator(boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}

	public void setOrganization(boolean isOrganization) {
		this.isOrganization = isOrganization;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

}
