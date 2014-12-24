package com.areahomeschoolers.baconbits.client.util;

public class AnimationUtils {

	/*
	 * The two ease-out curves are basically curves that start at a regular speed and slow towards the end. Cubic basically is a bit more aggressive in its
	 * curve. Starts steep and ends more smoothly. Sin starts out linear and ends more abruptly than cubic.
	 * 
	 * We can add more from this site: http://www.gizma.com/easing/
	 */

	public enum AnimationCurveType {
		LINEAR, EASEOUT_CUBIC, EASEOUT_SIN
	}

	public static long getAnimatedPixelPosition(AnimationCurveType easeType, int startPx, int endPx, double currentTime, double totalTimeDuration) {
		return Math.round(getAnimatedPosition(easeType, startPx, endPx, currentTime, totalTimeDuration));
	}

	public static double getAnimatedPosition(AnimationCurveType easeType, double startPos, double endPos, double currentTime, double totalTimeDuration) {
		double t = currentTime / totalTimeDuration;
		double totalDistance = endPos - startPos;

		double curveFactor;

		switch (easeType) {
		case EASEOUT_CUBIC:
			curveFactor = Math.pow(t - 1, 3) + 1;
			break;
		case EASEOUT_SIN:
			curveFactor = Math.sin(t * (Math.PI / 2));
			break;
		case LINEAR:
			curveFactor = t;
			break;
		default:
			curveFactor = -1;
			break;
		}

		return totalDistance * curveFactor + startPos;
	}

}
