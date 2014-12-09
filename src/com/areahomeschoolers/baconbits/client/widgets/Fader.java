package com.areahomeschoolers.baconbits.client.widgets;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.UIObject;

public class Fader {
	public static void fadeObjectIn(UIObject target, Command onCompleteCommand) {
		Fader f = new Fader(target);
		f.fadeIn(onCompleteCommand);
	}

	public static void fadeObjectOut(UIObject target) {
		fadeObjectOut(target, null);
	}

	public static void fadeObjectOut(UIObject target, Command onCompleteCommand) {
		Fader f = new Fader(target);
		f.fadeOut(onCompleteCommand);
	}

	public static void fadeOjbectIn(UIObject target) {
		fadeObjectIn(target, null);
	}

	private UIObject object;
	private Timer fadeOutTimer;
	private Timer fadeInTimer;
	private double fadeStep = 0.05;
	private int fadeDelay = 15;
	private double currentOpacity = 1.0;
	private int commandDelay;
	private Command onCompleteCommand;

	public Fader(UIObject target) {
		object = target;

		fadeOutTimer = new Timer() {
			@Override
			public void run() {
				if (object == null) {
					throw new RuntimeException("The object provided is null.");
				}
				currentOpacity -= fadeStep;
				if (currentOpacity < 0) {
					currentOpacity = 0;
				}
				object.getElement().getStyle().setOpacity(currentOpacity);

				if (currentOpacity == 0) {
					cancel();

					executeCommand();
				}
			}
		};

		fadeInTimer = new Timer() {
			@Override
			public void run() {
				currentOpacity += fadeStep;
				if (currentOpacity > 1) {
					currentOpacity = 1;
				}

				object.getElement().getStyle().setOpacity(currentOpacity);

				if (currentOpacity == 1) {
					cancel();
					executeCommand();
				}
			}
		};
	}

	public void fadeIn() {
		fadeIn(null);
	}

	public void fadeIn(Command onCompleteCommand) {
		this.onCompleteCommand = onCompleteCommand;
		currentOpacity = 0;
		object.getElement().getStyle().setOpacity(currentOpacity);
		fadeInTimer.scheduleRepeating(fadeDelay);
	}

	public void fadeOut() {
		fadeOut(null);
	}

	public void fadeOut(Command onCompleteCommand) {
		this.onCompleteCommand = onCompleteCommand;
		currentOpacity = 1;
		fadeOutTimer.scheduleRepeating(fadeDelay);
	}

	public void setCommandDelay(int commandDelay) {
		this.commandDelay = commandDelay;
	}

	private void executeCommand() {
		if (onCompleteCommand != null) {
			Timer t = new Timer() {
				@Override
				public void run() {
					onCompleteCommand.execute();
					onCompleteCommand = null;

					// reset the opacity and set visible false afterwards
					resetOpacity();
				}
			};

			t.schedule(commandDelay);
		} else {
			resetOpacity();
		}
	}

	private void resetOpacity() {
		if (currentOpacity == 0) {
			object.setVisible(false);
			object.getElement().getStyle().setOpacity(1.0);
		}
	}
}
