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

public class Mailer {
	private final List<String> tos = new ArrayList<String>();
	private final List<String> ccs = new ArrayList<String>();
	private final List<String> bccs = new ArrayList<String>();
	private String subject;
	private String body;
	private final Properties properties = new Properties();
	private boolean isHtmlMail = false;
	private final Message message;

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

	public void addTo(String... addresses) {
		for (String address : addresses) {
			tos.add(address);
		}
	}

	public void clearCc() {
		ccs.clear();
	}

	public String getBody() {
		return body;
	}

	public String getFrom() {
		return "admin@wearehomeeducators.com";
		// String from = this.from;
		//
		// if (from == null) {
		// User curUser = getUser();
		// if (Common.isValidEmail(curUser.getEmail())) {
		// from = curUser.getFullName() + " <" + curUser.getEmail() + ">";
		// } else {
		// from = sysFrom;
		// }
		// }
		//
		// return from;
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

		if (tos.isEmpty()) {
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
				bodyText = body;

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

				bodyText += body;

				System.out.println(subjectText + "\n\n" + bodyText);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setHtmlMail(boolean isHtmlMail) {
		this.isHtmlMail = isHtmlMail;
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
		ret += "\nbody: " + body;

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
