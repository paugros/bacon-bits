package com.areahomeschoolers.baconbits.shared.dto;

import java.util.Collection;
import java.util.HashSet;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Email implements IsSerializable {
	private HashSet<String> tos = new HashSet<String>();
	private HashSet<String> ccs = new HashSet<String>();
	private HashSet<String> bccs = new HashSet<String>();
	private String body;
	private String subject;
	private boolean isHtmlMail = false;

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

	public HashSet<String> getBccs() {
		return bccs;
	}

	public String getBody() {
		return body;
	}

	public HashSet<String> getCcs() {
		return ccs;
	}

	public String getSubject() {
		return subject;
	}

	public HashSet<String> getTos() {
		return tos;
	}

	public boolean isHtmlMail() {
		return isHtmlMail;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setHtmlMail(boolean isHtmlMail) {
		this.isHtmlMail = isHtmlMail;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
