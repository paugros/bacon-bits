package com.areahomeschoolers.baconbits.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;

public class InactivityManager {

	private static Timer timer;
	private List<Command> wakeUpCommands = new ArrayList<Command>();
	private List<Command> onSleepCommands = new ArrayList<Command>();
	private boolean isIdle;
	private int interval;

	public InactivityManager(int interval) {
		this.interval = interval;

		timer = new Timer() {
			@Override
			public void run() {
				// The full interval has completed without any activity
				isIdle = true;

				for (Command onSleepCommand : onSleepCommands) {
					onSleepCommand.execute();
				}
			}
		};

		Event.addNativePreviewHandler(new NativePreviewHandler() {
			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				switch (event.getTypeInt()) {
				case Event.ONMOUSEMOVE:
					activityPerformed();
					break;
				case Event.ONKEYDOWN:
					activityPerformed();
					break;
				}
			}
		});

		activityPerformed();
	}

	public void addOnSleepCommand(Command command) {
		onSleepCommands.add(command);
	}

	public void addWakeUpCommand(Command command) {
		wakeUpCommands.add(command);
	}

	public boolean isIdle() {
		return isIdle;
	}

	private void activityPerformed() {
		timer.schedule(interval);

		if (isIdle) {
			// Waking up
			isIdle = false;

			for (Command wakeUpCommand : wakeUpCommands) {
				wakeUpCommand.execute();
			}
		}
	}
}
