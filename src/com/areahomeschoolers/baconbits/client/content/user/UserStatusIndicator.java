package com.areahomeschoolers.baconbits.client.content.user;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.areahomeschoolers.baconbits.client.Application;
import com.areahomeschoolers.baconbits.client.images.MainImageBundle;
import com.areahomeschoolers.baconbits.client.util.ClientDateUtils;
import com.areahomeschoolers.baconbits.client.util.Formatter;
import com.areahomeschoolers.baconbits.client.widgets.PaddedPanel;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class UserStatusIndicator extends Composite {
	private static Set<UserStatusIndicator> statusIndicators = new HashSet<UserStatusIndicator>();

	public static void updateAllStatusIndicators() {
		Set<UserStatusIndicator> remove = new HashSet<UserStatusIndicator>();
		for (UserStatusIndicator indicator : statusIndicators) {
			if (!indicator.isAttached()) {
				remove.add(indicator);
				continue;
			}

			indicator.updateStatus();
		}

		statusIndicators.removeAll(remove);
	}

	private boolean showWeeksAndMonths;

	private PaddedPanel statusPanel = new PaddedPanel();
	private Image icon = new Image();
	private Label idleTime = new Label();
	private int userId;
	private long idleMinutes;

	private Date lastActivity;

	public UserStatusIndicator() {
		this(0);
	}

	public UserStatusIndicator(int userId) {
		this.userId = userId;

		initWidget(statusPanel);

		idleTime.addStyleName("smallText");
		statusPanel.add(idleTime);
		statusPanel.setCellVerticalAlignment(idleTime, HasVerticalAlignment.ALIGN_MIDDLE);
		updateStatus();

		statusIndicators.add(this);
	}

	public void clear() {
		statusPanel.clear();
	}

	public long getIdleMinutes() {
		if (lastActivity == null) {
			return 999999999;
		}
		return idleMinutes;
	}

	public void setShowWeeksAndMonths(boolean showWeeksAndMonths) {
		this.showWeeksAndMonths = showWeeksAndMonths;
		updateStatus();
	}

	public void setTextVisible(boolean visible) {
		if (visible) {
			statusPanel.add(idleTime);
		} else {
			idleTime.removeFromParent();
		}
	}

	public void setUserId(int userId) {
		this.userId = userId;
		updateStatus();
	}

	public void updateStatus() {
		if (userId == 0) {
			icon.removeFromParent();
			return;
		}
		statusPanel.insert(icon, 0);

		lastActivity = Application.getUserActivity().get(userId);

		if (lastActivity == null) {
			icon.removeFromParent();
			idleTime.setText("");
			setTitle("");
			return;
		}

		idleMinutes = ClientDateUtils.minutesBetween(lastActivity, new Date());

		String titlePrefix = "";
		if (idleMinutes < 5) {
			icon.setResource(MainImageBundle.INSTANCE.circleGreen());
		} else if (idleMinutes < (60 * 48)) {
			titlePrefix = "";
			icon.setResource(MainImageBundle.INSTANCE.circleOrange());
		} else if (idleMinutes < (60 * 24 * 14)) {
			icon.setResource(MainImageBundle.INSTANCE.circleGray());
		} else {
			titlePrefix = "";
			if (showWeeksAndMonths) {
				icon.setResource(MainImageBundle.INSTANCE.circleGray());
			} else {
				icon.setResource(MainImageBundle.INSTANCE.pixel());
				icon.setSize("9px", "9px");
			}
		}

		updateIdleTime(idleMinutes);

		if (!idleTime.isAttached()) {
			setTitle(titlePrefix + idleTime.getText());
		} else {
			setTitle("");
		}
	}

	private void updateIdleTime(long minutes) {
		String text = "";
		if (minutes < 5) {
			text = "Active";
		} else if (minutes < 60) {
			text = minutes + " minutes";
		} else if (minutes < (60 * 48)) {
			double hours = Math.round(minutes / 60.0);
			text = Formatter.formatNumber(hours, "0") + " hour";
			if (hours > 1) {
				text += "s";
			}
		} else if (minutes < (60 * 24 * 14)) {
			double days = Math.round((minutes / 60.0) / 24);
			text = Formatter.formatNumber(days, "0") + " day";
			if (days > 1) {
				text += "s";
			}
		} else if (minutes < (60 * 24 * 60)) {
			if (showWeeksAndMonths) {
				double weeks = Math.round(((minutes / 60.0) / 24) / 7);
				text = Formatter.formatNumber(weeks, "0") + " week";
				if (weeks > 1) {
					text += "s";
				}
			}
		} else {
			if (showWeeksAndMonths) {
				double months = Math.round(((minutes / 60.0) / 24) / 30.5);
				text = Formatter.formatNumber(months, "0") + " month";
				if (months > 1) {
					text += "s";
				}
			}
		}

		idleTime.setText(text);
	}
}
