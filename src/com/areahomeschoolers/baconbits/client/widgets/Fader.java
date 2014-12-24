package com.areahomeschoolers.baconbits.client.widgets;

import com.areahomeschoolers.baconbits.client.util.AnimationUtils;
import com.areahomeschoolers.baconbits.client.util.AnimationUtils.AnimationCurveType;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.UIObject;

public class Fader {
	public static void fadeObjectIn(UIObject target) {
		fadeObjectIn(target, null);
	}

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

	private UIObject object;
	private Timer fadeOutTimer;
	private Timer fadeInTimer;
	private double fadeStep = 0.05;
	private int fadeDelay = 10;
	private double currentOpacity = 1.0;
	private double maxOpacity = 1.0;
	private int commandDelay;
	private Command onCompleteCommand;
	private boolean toggleVisibility = true;

	public Fader(UIObject target) {
		object = target;

		fadeOutTimer = new Timer() {
			@Override
			public void run() {
				currentOpacity -= fadeStep;
				if (currentOpacity < 0) {
					currentOpacity = 0;
				}

				double curvedOpacity = AnimationUtils.getAnimatedPosition(AnimationCurveType.EASEOUT_SIN, 0, maxOpacity, currentOpacity, 1);
				object.getElement().getStyle().setOpacity(curvedOpacity);

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
				if (currentOpacity > maxOpacity) {
					currentOpacity = maxOpacity;
				}

				double curvedOpacity = AnimationUtils.getAnimatedPosition(AnimationCurveType.EASEOUT_CUBIC, 0, maxOpacity, currentOpacity, 1);
				object.getElement().getStyle().setOpacity(curvedOpacity);

				if (currentOpacity == maxOpacity) {
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

		if (toggleVisibility) {
			object.setVisible(true);
		}

		fadeInTimer.scheduleRepeating(fadeDelay);
	}

	public void fadeOut() {
		fadeOut(null);
	}

	public void fadeOut(Command onCompleteCommand) {
		this.onCompleteCommand = onCompleteCommand;
		currentOpacity = maxOpacity;
		fadeOutTimer.scheduleRepeating(fadeDelay);
	}

	public void setCommandDelay(int commandDelay) {
		this.commandDelay = commandDelay;
	}

	public void setFadeDelay(int delay) {
		fadeDelay = delay;
	}

	public void setFadeStep(double fadeStep) {
		this.fadeStep = fadeStep;
	}

	public void setMaxOpacity(double maxOpacity) {
		this.maxOpacity = maxOpacity;
	}

	public void setToggleVisibility(boolean set) {
		toggleVisibility = set;
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
			if (toggleVisibility) {
				object.setVisible(false);
				object.getElement().getStyle().setOpacity(maxOpacity);
			}
		}
	}
}
