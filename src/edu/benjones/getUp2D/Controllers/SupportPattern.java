package edu.benjones.getUp2D.Controllers;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import edu.benjones.getUp2D.Utils.Trajectory1D;

public class SupportPattern {

	public enum supportLabel {
		leftArm, rightArm, leftLeg, rightLeg, butt
	}

	public enum limbStatus {
		idle, swing, stance
	}

	public static class supportInfo {
		public limbStatus ls;
		public boolean kneel;
		public float xOffset;// where should the support be?

		public supportInfo() {
			ls = limbStatus.idle;
			kneel = false;
			xOffset = 0;
		}

		public supportInfo(limbStatus ls, boolean kneel, float xOffset) {
			this.ls = ls;
			this.kneel = kneel;
			this.xOffset = xOffset;
		}

	}

	private ArrayList<TreeMap<Float, supportInfo>> limbPatterns;
	private Trajectory1D hipHeight, shoulderHeight;
	private float phase;

	public SupportPattern() {
		phase = 0;
		hipHeight = new Trajectory1D();
		shoulderHeight = new Trajectory1D();

		limbPatterns = new ArrayList<TreeMap<Float, supportInfo>>(
				supportLabel.values().length);

		for (int i = 0; i < supportLabel.values().length; ++i) {
			supportInfo empty = new supportInfo(limbStatus.idle, false, 0);
			limbPatterns.add(new TreeMap<Float, supportInfo>());
			limbPatterns.get(i).put(Float.NEGATIVE_INFINITY, empty);
			limbPatterns.get(i).put(Float.POSITIVE_INFINITY, empty);
		}

	}

	public void addLimbStatus(supportLabel limb, float time, supportInfo info) {
		limbPatterns.get(limb.ordinal()).put(time, info);
	}

	public void advancePhase(float phase) {
		this.phase += phase;
	}

	public void addHipHeightKnot(Trajectory1D.entry entry) {
		hipHeight.addKnot(entry);
	}

	public void addShoulderHeightKnot(Trajectory1D.entry entry) {
		shoulderHeight.addKnot(entry);
	}

	public float getHipHeightAtTime(float t) {
		return hipHeight.evaluateLinear(t);
	}

	public float getShoulderHeightAtTime(float t) {
		return shoulderHeight.evaluateLinear(t);
	}

	public float getHipHeightNow(float t) {
		return getHipHeightAtTime(phase);
	}

	public float getShoulderHeightNow(float t) {
		return getShoulderHeightAtTime(phase);
	}

	public supportInfo getInfoAtTime(supportLabel limb, float t) {
		return limbPatterns.get(limb.ordinal()).floorEntry(t).getValue();
	}

	public supportInfo getInfoNow(supportLabel limb) {
		return getInfoAtTime(limb, phase);
	}

	public float getSwingPhase(supportLabel limb) {
		supportInfo info = getInfoNow(limb);
		if (info.ls != limbStatus.swing) {
			return 0;
		}

		TreeMap<Float, supportInfo> map = limbPatterns.get(limb.ordinal());
		Entry<Float, supportInfo> ent = map.floorEntry(phase);
		// figure out when the swing started
		float start = ent.getKey();
		while (ent != null && ent.getValue().ls == limbStatus.swing) {
			start = ent.getKey();
			ent = map.lowerEntry(ent.getKey());
		}

		ent = map.ceilingEntry(phase);
		float end = ent.getKey();
		while (ent != null && ent.getValue().ls == limbStatus.swing) {

			ent = map.higherEntry(ent.getKey());
			end = ent.getKey();
		}

		if (end == start)
			return 0f;
		return (phase - start) / (end - start);

	}

	public float getTimeToLift(supportLabel limb) {
		supportInfo info = getInfoNow(limb);
		if (info.ls != limbStatus.stance)
			return 0;

		TreeMap<Float, supportInfo> map = limbPatterns.get(limb.ordinal());
		Entry<Float, supportInfo> ent = map.ceilingEntry(phase);
		float end = ent.getKey();
		while (ent != null && ent.getValue().ls == limbStatus.stance) {
			ent = map.higherEntry(ent.getKey());
			end = ent.getKey();
		}
		return end - phase;
	}

	public float getMinFiniteTime() {
		float ret = Float.POSITIVE_INFINITY;

		Entry<Float, supportInfo> ent;
		for (TreeMap<Float, supportInfo> map : limbPatterns) {
			ent = map.firstEntry();
			while (ent != null && ent.getKey() == Float.NEGATIVE_INFINITY)
				ent = map.higherEntry(ent.getKey());
			if (ent != null && ent.getKey() < ret)
				ret = ent.getKey();
		}
		if (hipHeight.getMinT() < ret
				&& hipHeight.getMinT() != Float.NEGATIVE_INFINITY)
			ret = hipHeight.getMinT();
		if (shoulderHeight.getMinT() < ret
				&& shoulderHeight.getMinT() != Float.NEGATIVE_INFINITY)
			ret = shoulderHeight.getMinT();

		return ret;
	}

	public float getMaxFiniteTime() {
		float ret = Float.NEGATIVE_INFINITY;

		Entry<Float, supportInfo> ent;
		for (TreeMap<Float, supportInfo> map : limbPatterns) {
			ent = map.lastEntry();
			while (ent != null && ent.getKey() == Float.POSITIVE_INFINITY)
				ent = map.lowerEntry(ent.getKey());
			if (ent != null && ent.getKey() > ret)
				ret = ent.getKey();
		}
		if (hipHeight.getMaxT() > ret
				&& hipHeight.getMaxT() != Float.POSITIVE_INFINITY)
			ret = hipHeight.getMaxT();
		if (shoulderHeight.getMaxT() > ret
				&& shoulderHeight.getMaxT() != Float.POSITIVE_INFINITY)
			ret = shoulderHeight.getMaxT();

		return ret;
	}

}
