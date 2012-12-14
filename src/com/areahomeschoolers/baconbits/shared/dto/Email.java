package com.areahomeschoolers.baconbits.shared.dto;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Email implements IsSerializable {
	private ArrayList<String> tos = new ArrayList<String>();
	private ArrayList<String> ccs = new ArrayList<String>();
	private ArrayList<String> bccs = new ArrayList<String>();
	private String body;
	private String subject;

	public void addBcc(Collection<String> addresses) {
		bccs.addAll(addresses);
	}

	public void addBcc(String... addresses) {
		for (String address : addresses) {
			bccs.add(address);
		}
	}

	public void addCc(Collection<String> addresses) {
		ccs.addAll(addresses);
	}

	public void addCc(String... addresses) {
		for (String address : addresses) {
			ccs.add(address);
		}
	}

	public void addTo(Collection<String> addresses) {
		tos.addAll(addresses);
	}

	public void addTo(String recip) {
		tos.add(recip);
	}

	public void addTo(String... addresses) {
		for (String address : addresses) {
			tos.add(address);
		}
	}

	public ArrayList<String> getBccs() {
		return bccs;
	}

	public String getBody() {
		return body;
	}

	public ArrayList<String> getCcs() {
		return ccs;
	}

	public String getSubject() {
		return subject;
	}

	public ArrayList<String> getTos() {
		return tos;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
