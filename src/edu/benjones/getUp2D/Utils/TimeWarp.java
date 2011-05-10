package edu.benjones.getUp2D.Utils;

import java.util.ArrayList;

/**
 * Implements a "time warp". Given a "real time," wall clock time, maps into a
 * "phase time", which is the phase of the supportPattern
 * 
 * @author jonesben
 * 
 */
public class TimeWarp {

	private int numSegments;
	ArrayList<Float> phaseVelocities;
	ArrayList<Float> trapAreas;

	private float beginTime;// real time begin/end times
	private float dt;

	public TimeWarp(float beginTime, float endTime) {
		this(beginTime, endTime, 10);
	}

	public TimeWarp(float beginTime, float endTime, int numSegments) {
		this.numSegments = numSegments;
		this.beginTime = beginTime;
		phaseVelocities = new ArrayList<Float>(numSegments + 1);
		trapAreas = new ArrayList<Float>(numSegments);

		dt = (endTime - beginTime) / numSegments;

		for (int i = 0; i < numSegments; ++i) {
			phaseVelocities.add(1f);
			trapAreas.add(dt);// start off with no warping
		}
		phaseVelocities.add(1f);
	}

	public float getPhaseAtTime(float t) {
		if (t <= beginTime) {
			return beginTime;
		}

		float delta = t - beginTime;
		float ret = beginTime;
		int i = 0;
		while (delta > dt && i < trapAreas.size()) {
			delta -= dt;
			ret += trapAreas.get(i);
			++i;
		}
		if (i + 1 < phaseVelocities.size()) {
			float a = phaseVelocities.get(i);
			float b = phaseVelocities.get(i + 1);
			float midpoint = a + (b - a) * delta / dt;
			float partArea = (a + midpoint) * delta * .5f;
			ret += partArea;
			return ret;
		} else {
			// use the last available slope info
			float a = phaseVelocities.get(i);
			ret += a * delta;
			return ret;
		}
	}

	public float unWarp(float warpedTime) {
		if (warpedTime <= beginTime) {
			return beginTime;
		}
		float delta = warpedTime - beginTime;
		float ret = beginTime;
		int i = 0;
		while (i < trapAreas.size() && delta > trapAreas.get(i)) {
			delta -= trapAreas.get(i);
			ret += dt;
			++i;
		}
		// now handle the last piece
		if (i + 1 < phaseVelocities.size()) {
			// solve a quadratic:
			float start = phaseVelocities.get(i);
			float stop = phaseVelocities.get(i + 1);
			float a = (stop - start) / (2 * dt);
			float b = start;
			float c = -delta;
			if (MathUtils.floatEquals(a, 0)) {
				ret += delta / phaseVelocities.get(i);
			} else {
				float sol = (float) (-b / (2 * a) + Math
						.sqrt(b * b - 4 * a * c) / (2 * a));

				ret += sol;
			}

		} else {
			ret += delta / phaseVelocities.get(i);
		}

		return ret;
	}

	public float getWarp(int index) {
		assert (index >= 0 && index <= numSegments);
		return phaseVelocities.get(index);
	}

	public void setWarp(int index, float f) {
		assert (index >= 0 && index <= numSegments);
		phaseVelocities.set(index, f);

		// update the modified areas
		if (index > 0) {
			trapAreas.set(index - 1, (phaseVelocities.get(index - 1) + f) * dt
					* .5f);
		}
		if (index < numSegments) {
			trapAreas.set(index, (phaseVelocities.get(index + 1) + f) * dt
					* .5f);
		}

	}
}
