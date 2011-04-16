package edu.benjones.getUp2D.Controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.GetUpScenario;
import edu.benjones.getUp2D.Utils.Trajectory1D;

public class SupportPattern {

	public static class limbPattern {

		protected TreeMap<Float, supportInfo> pattern;

		public limbPattern() {
			supportInfo empty = new supportInfo(limbStatus.idle, false, 0);
			pattern = new TreeMap<Float, supportInfo>();
			pattern.put(Float.NEGATIVE_INFINITY, empty);
			pattern.put(Float.POSITIVE_INFINITY, empty);
		}

		public void put(float time, supportInfo info) {
			pattern.put(time, info);
		}

		public supportInfo getInfoAtTime(float t) {
			return pattern.floorEntry(t).getValue();
		}

		public float getSwingPhaseAtTime(float t) {
			supportInfo info = getInfoAtTime(t);
			if (info.ls != limbStatus.swing) {
				return 0f;
			}

			Entry<Float, supportInfo> ent = pattern.floorEntry(t);
			// figure out when the swing started
			float start = ent.getKey();
			while (ent != null && ent.getValue().ls == limbStatus.swing) {
				start = ent.getKey();
				ent = pattern.lowerEntry(ent.getKey());
			}

			ent = pattern.ceilingEntry(t);
			float end = ent.getKey();
			while (ent != null && ent.getValue().ls == limbStatus.swing) {

				ent = pattern.higherEntry(ent.getKey());
				end = ent.getKey();
			}

			if (end == start)
				return 0f;
			return (t - start) / (end - start);
		}

		public float getTimeToLiftAtTime(float t) {
			supportInfo info = getInfoAtTime(t);
			if (info.ls != limbStatus.stance)
				return 0;

			Entry<Float, supportInfo> ent = pattern.ceilingEntry(t);
			float end = ent.getKey();
			while (ent != null && ent.getValue().ls == limbStatus.stance) {
				ent = pattern.higherEntry(ent.getKey());
				end = ent.getKey();
			}
			return end - t;
		}

	}

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

	private ArrayList<limbPattern> limbPatterns;
	private Trajectory1D hipHeight, shoulderHeight;
	private float phase;

	public SupportPattern() {
		phase = 0;
		hipHeight = new Trajectory1D();
		shoulderHeight = new Trajectory1D();

		limbPatterns = new ArrayList<limbPattern>(supportLabel.values().length);

		for (int i = 0; i < supportLabel.values().length; ++i) {

			limbPatterns.add(new limbPattern());

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
		return limbPatterns.get(limb.ordinal()).getInfoAtTime(t);
	}

	public supportInfo getInfoNow(supportLabel limb) {
		return getInfoAtTime(limb, phase);
	}

	public float getSwingPhase(supportLabel limb) {
		return limbPatterns.get(limb.ordinal()).getSwingPhaseAtTime(phase);
	}

	public float getTimeToLift(supportLabel limb) {
		return limbPatterns.get(limb.ordinal()).getTimeToLiftAtTime(phase);
	}

	public float getPhase() {
		return phase;
	}

	public float getMinFiniteTime() {
		float ret = Float.POSITIVE_INFINITY;

		Entry<Float, supportInfo> ent;
		for (limbPattern pattern : limbPatterns) {
			TreeMap<Float, supportInfo> map = pattern.pattern;
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
		for (limbPattern pattern : limbPatterns) {
			TreeMap<Float, supportInfo> map = pattern.pattern;
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

	private static final Color3f[] stripeColors = {
			new Color3f(130f, 0f, 130f), new Color3f(0f, 130f, 0f),
			new Color3f(0f, 0f, 130f) };

	private static final Color3f hipHeightColor = Color3f.RED;
	private static final Color3f shoulderHeightColor = new Color3f(130, 130,
			255);

	public void draw(DebugDraw g) {
		g.setCamera(0, 0, 400);

		float minFinite, maxFinite;
		minFinite = Math.min(0, getMinFiniteTime());
		maxFinite = Math.max(1, getMaxFiniteTime() + 1);

		float boxWidth = 1.0f, boxHeight = .4f, boxLeftX = -.95f, boxBotY = .5f;

		// reuse this for drawing all boxes
		Vec2[] box = { new Vec2(boxLeftX, boxBotY),
				new Vec2(boxLeftX, boxBotY + boxHeight),
				new Vec2(boxLeftX + boxWidth, boxBotY + boxHeight),
				new Vec2(boxLeftX + boxWidth, boxBotY) };

		g.drawSolidPolygon(box, 4, Color3f.WHITE);

		float eachBox = boxHeight / supportLabel.values().length;
		float boxBegin;
		Float boxEnd;
		TreeMap<Float, supportInfo> map;
		for (supportLabel i : supportLabel.values()) {
			// setup heights
			box[0].y = (float) (boxBotY + (i.ordinal() + .05) * eachBox);
			box[1].y = (float) (boxBotY + (i.ordinal() + .95) * eachBox);
			box[2].y = (float) (boxBotY + (i.ordinal() + .95) * eachBox);
			box[3].y = (float) (boxBotY + (i.ordinal() + .05) * eachBox);
			map = limbPatterns.get(i.ordinal()).pattern;
			boxBegin = minFinite;
			boxEnd = map.higherKey(minFinite);
			while (boxEnd != null && boxEnd < Float.POSITIVE_INFINITY) {
				box[0].x = (float) (boxLeftX + (boxBegin - minFinite)
						* boxWidth / (maxFinite - minFinite));
				box[1].x = (float) (boxLeftX + (boxBegin - minFinite)
						* boxWidth / (maxFinite - minFinite));
				box[2].x = (float) (boxLeftX + (boxEnd - minFinite) * boxWidth
						/ (maxFinite - minFinite));
				box[3].x = (float) (boxLeftX + (boxEnd - minFinite) * boxWidth
						/ (maxFinite - minFinite));

				g.drawSolidPolygon(box, 4,
						stripeColors[getInfoAtTime(i, boxBegin).ls.ordinal()]);

				boxBegin = boxEnd;
				boxEnd = map.higherKey(boxEnd);
			}
			// draw the last box
			box[0].x = (float) (boxLeftX + (boxBegin - minFinite) * boxWidth
					/ (maxFinite - minFinite));
			box[1].x = (float) (boxLeftX + (boxBegin - minFinite) * boxWidth
					/ (maxFinite - minFinite));
			box[2].x = (float) (boxLeftX + boxWidth);
			box[3].x = (float) (boxLeftX + boxWidth);

			g.drawSolidPolygon(box, 4,
					stripeColors[getInfoAtTime(i, boxBegin).ls.ordinal()]);

		}

		// draw the hip/shoulder height trajectories
		float minHeight = 0, maxHeight = Math.max(hipHeight.getMaxVal(),
				shoulderHeight.getMaxVal());

		if (hipHeight.size() != 0) {
			Vec2 lineStart = new Vec2(0, 0);
			Vec2 lineEnd = new Vec2(0, 0);
			if (hipHeight.size() == 1) {
				lineStart.x = boxLeftX;
				lineStart.y = boxBotY
						+ (hipHeight.evaluateLinear(minFinite) - minHeight)
						* boxHeight / (maxHeight - minHeight);
				lineEnd.x = boxLeftX + boxWidth;
				lineEnd.y = lineStart.y;
				g.drawSegment(lineStart, lineEnd, hipHeightColor);
			} else {
				Iterator<Trajectory1D.entry> it = hipHeight.getIterator();
				Trajectory1D.entry e;
				lineStart.x = boxLeftX;
				lineStart.y = boxBotY + boxHeight
						* (hipHeight.evaluateLinear(minFinite) - minHeight)
						/ (maxHeight - minHeight);
				while (it.hasNext()) {
					e = it.next();
					if (e.t < minFinite)
						continue;
					if (e.t > maxFinite)
						break;
					lineEnd.x = boxLeftX + boxWidth * (e.t - minFinite)
							/ (maxFinite - minFinite);
					lineEnd.y = boxBotY + boxHeight * (e.val - minHeight)
							/ (maxHeight - minHeight);
					g.drawSegment(lineStart, lineEnd, hipHeightColor);
					lineStart.x = lineEnd.x;
					lineStart.y = lineEnd.y;
				}
				// draw the end part
				lineEnd.x = boxLeftX + boxWidth;
				lineEnd.y = boxBotY + boxHeight
						* (hipHeight.evaluateLinear(maxFinite) - minHeight)
						/ (maxHeight - minHeight);
				g.drawSegment(lineStart, lineEnd, hipHeightColor);
			}
		}

		if (shoulderHeight.size() != 0) {
			Vec2 lineStart = new Vec2(0, 0);
			Vec2 lineEnd = new Vec2(0, 0);
			if (shoulderHeight.size() == 1) {
				lineStart.x = boxLeftX;
				lineStart.y = boxBotY
						+ (shoulderHeight.evaluateLinear(minFinite) - minHeight)
						* boxHeight / (maxHeight - minHeight);
				lineEnd.x = boxLeftX + boxWidth;
				lineEnd.y = lineStart.y;
				g.drawSegment(lineStart, lineEnd, shoulderHeightColor);
			} else {
				Iterator<Trajectory1D.entry> it = shoulderHeight.getIterator();
				Trajectory1D.entry e;
				lineStart.x = boxLeftX;
				lineStart.y = boxBotY
						+ boxHeight
						* (shoulderHeight.evaluateLinear(minFinite) - minHeight)
						/ (maxHeight - minHeight);
				while (it.hasNext()) {
					e = it.next();
					if (e.t < minFinite)
						continue;
					if (e.t > maxFinite)
						break;
					lineEnd.x = boxLeftX + boxWidth * (e.t - minFinite)
							/ (maxFinite - minFinite);
					lineEnd.y = boxBotY + boxHeight * (e.val - minHeight)
							/ (maxHeight - minHeight);
					g.drawSegment(lineStart, lineEnd, shoulderHeightColor);
					lineStart.x = lineEnd.x;
					lineStart.y = lineEnd.y;
				}
				// draw the end part
				lineEnd.x = boxLeftX + boxWidth;
				lineEnd.y = boxBotY
						+ boxHeight
						* (shoulderHeight.evaluateLinear(maxFinite) - minHeight)
						/ (maxHeight - minHeight);
				g.drawSegment(lineStart, lineEnd, shoulderHeightColor);
			}
		}

		// finally draw phase

		g.drawSegment(new Vec2(boxLeftX + (phase - minFinite)
				/ (maxFinite - minFinite), boxBotY), new Vec2(boxLeftX
				+ (phase - minFinite) / (maxFinite - minFinite), boxBotY
				+ boxHeight), Color3f.WHITE);

		// return the camera to where it was
		g.setCamera(GetUpScenario.defaultCameraParams.x,
				GetUpScenario.defaultCameraParams.y,
				GetUpScenario.defaultCameraParams.scale);

	}
}
