package com.areahomeschoolers.baconbits.server.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.areahomeschoolers.baconbits.shared.Common;
import com.areahomeschoolers.baconbits.shared.dto.Email;
import com.areahomeschoolers.baconbits.shared.dto.User;

public class Mailer {
	private final List<String> tos = new ArrayList<String>();
	private final List<String> ccs = new ArrayList<String>();
	private final List<String> bccs = new ArrayList<String>();
	private String subject;
	private String body;
	private final Properties properties = new Properties();
	private boolean isHtmlMail = false;
	private final Message message;
	private boolean includeDoNotReply = true;
	private static final String DO_NOT_REPLY = "NOTE: This is a post-only mailing.  Replies to this message are not monitored or answered.";

	public Mailer() {
		Session session = Session.getDefaultInstance(properties, null);

		message = new MimeMessage(session);
	}

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

	public void addTo(List<User> users) {
		for (User u : users) {
			addTo(u);
		}
	}

	public void addTo(String... addresses) {
		for (String address : addresses) {
			tos.add(address);
		}
	}

	public void addTo(User u) {
		addTo(u.getFullName() + " <" + u.getEmail() + ">");
	}

	public void clearCc() {
		ccs.clear();
	}

	public String getBody() {
		String b = body;
		if (includeDoNotReply) {
			b += isHtmlMail ? "<br><br>" : "\n\n";
			b += DO_NOT_REPLY;
		}
		return b;
	}

	public String getFrom() {
		return "info@citrusgroups.com";
	}

	public Message getMessage() {
		return message;
	}

	public String getSubject() {
		return subject;
	}

	public List<String> getTos() {
		return tos;
	}

	public boolean isHtmlMail() {
		return isHtmlMail;
	}

	public void send() {
		if (tos.isEmpty() && ccs.isEmpty() && bccs.isEmpty()) {
			return;
		}

		try {
			message.setFrom(new InternetAddress(getFrom()));

			String bodyText, subjectText;

			if (ServerContext.isLive()) {
				InternetAddress[] recipientTo = getAddresses(tos);
				message.setRecipients(Message.RecipientType.TO, recipientTo);
				if (!ccs.isEmpty()) {
					InternetAddress[] recipientCc = getAddresses(ccs);
					message.setRecipients(Message.RecipientType.CC, recipientCc);
				}
				if (!bccs.isEmpty()) {
					InternetAddress[] recipientBcc = getAddresses(bccs);
					message.setRecipients(Message.RecipientType.BCC, recipientBcc);
				}

				subjectText = subject;
				bodyText = getBody();

				message.setSubject(subjectText);
				message.setSentDate(new Date());

				if (isHtmlMail) {
					message.setContent(bodyText, "text/html; charset=UTF-8");
				} else {
					message.setContent(bodyText, "text/plain; charset=UTF-8");
				}
				Transport.send(message);
			} else {
				subjectText = "BACONBITS EMAIL: " + subject;

				String newLine = isHtmlMail ? "<br>" : "\n";
				bodyText = "/*** HEADERS ***/" + newLine;
				if (!tos.isEmpty()) {
					bodyText += "To: " + Common.join(tos, ", ") + newLine;
				}
				if (!ccs.isEmpty()) {
					bodyText += "Cc: " + Common.join(ccs, ", ") + newLine;
				}
				if (!bccs.isEmpty()) {
					bodyText += "Bcc: " + Common.join(bccs, ", ") + newLine;
				}
				bodyText += "/*** HEADERS ***/" + newLine + newLine;

				bodyText += getBody();

				System.out.println(subjectText + "\n\n" + bodyText);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setEmail(Email email) {
		setHtmlMail(email.isHtmlMail());
		addTo(email.getTos());
		addCc(email.getCcs());
		addBcc(email.getBccs());
		setSubject(email.getSubject());
		setBody(email.getBody());
	}

	public void setHtmlMail(boolean isHtmlMail) {
		this.isHtmlMail = isHtmlMail;
	}

	public void setIncludeDoNotReply(boolean includeDoNotReply) {
		this.includeDoNotReply = includeDoNotReply;
	}

	public void setSubject(String subject) {
		if (subject != null) {
			// strip non-ascii chars from subject, because they will break the email
			subject.replaceAll("[^\\p{ASCII}]", "");
		}
		this.subject = subject;
	}

	@Override
	public String toString() {
		String ret = "to: " + tos;
		ret += "\ncc: " + ccs;
		ret += "\nfrom: " + getFrom();
		ret += "\nsubject: " + subject;
		ret += "\nbody: " + getBody();

		return ret;
	}

	private InternetAddress[] getAddresses(List<String> addresses) throws AddressException {
		List<InternetAddress> validList = new ArrayList<InternetAddress>();

		for (String address : addresses) {
			if (!Common.isNullOrBlank(address)) {
				validList.add(new InternetAddress(address));
			}
		}

		InternetAddress[] ret = new InternetAddress[validList.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = validList.get(i);
		}

		return ret;
	}
}
