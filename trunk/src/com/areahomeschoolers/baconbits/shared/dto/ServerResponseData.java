package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ServerResponseData<T> implements IsSerializable {
	private T data;
	private ArrayList<String> errors = new ArrayList<String>();
	private ArrayList<String> warnings = new ArrayList<String>();

	public ServerResponseData() {

	}

	public void addError(String error) {
		errors.add(error);
	}

	public void addWarning(String warning) {
		warnings.add(warning);
	}

	public void copyErrorDataInto(ServerResponseData<?> intoResponse) {
		intoResponse.errors = this.errors;
		intoResponse.warnings = this.warnings;
	}

	public T getData() {
		return data;
	}

	public ArrayList<String> getErrors() {
		return errors;
	}

	public ArrayList<String> getWarnings() {
		return warnings;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public boolean hasErrorsOrWarnings() {
		return hasErrors() || hasWarnings();
	}

	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setErrors(ArrayList<String> errors) {
		if (errors != null) {
			this.errors = errors;
		}
	}

	public void setWarnings(ArrayList<String> warnings) {
		if (warnings != null) {
			this.warnings = warnings;
		}
	}
}
