package edu.benjones.getUp2D.Controllers;

import java.util.HashMap;
import java.util.List;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Character.Limb;
import edu.benjones.getUp2D.GetUpScenario;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbPattern;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;
import edu.benjones.getUp2D.Utils.PhysicsUtils;
import edu.benjones.getUp2D.Utils.Trajectory1D;
import edu.benjones.getUp2D.Utils.Trajectory1D.entry;

public class SupportLimb {

	protected Limb limb;
	protected limbStatus lastStatus;

	protected float plantedLevel;
	private Trajectory1D heightTraj;

	protected Vec2 ikTarg;

	public SupportLimb(Limb limb) {
		this.limb = limb;
		heightTraj = new Trajectory1D();
		heightTraj.addKnot(new entry(0f, 0f));
		heightTraj.addKnot(new entry(.1f, .15f));
		heightTraj.addKnot(new entry(.8f, .15f));
		heightTraj.addKnot(new entry(1.0f, 0f));
		reset();
	}

	public void reset() {
		this.lastStatus = limbStatus.idle;
		this.plantedLevel = 0;
		swingBegin = new Vec2();
		swingEnd = new Vec2();
	}

	public void draw(DebugDraw g) {
		if (ikTarg != null) {

			g.drawCircle(ikTarg, .05f, Color3f.WHITE);
		}
	}

	public boolean canSupport() {
		return plantedLevel > 0f;
	}

	public boolean canRemoveSupport(float dt) {
		float f = (limb.getNormalForceOnLeg(dt) + limb
				.getTangentialForceOnLeg(dt));
		System.out.println("total force on leg: " + f);
		return f < 20;
	}

	protected Vec2 swingBegin, swingEnd;
	protected boolean halfwayDone;// we'll re-evaluate where the swing EE is
									// halfway through

	public void setPose(limbPattern pattern, float phase, float[] desiredPose) {

		supportInfo info = pattern.getInfoAtTime(phase);
		if (info.ls != lastStatus) {
			if (info.ls == limbStatus.swing) {
				System.out.println("switched to swing from " + lastStatus);
				swingBegin = limb.getEndEffectorPosition();

				swingEnd.x = limb.getBase().getAnchor2().x + info.xOffset;
				swingEnd.y = GetUpScenario.getGroundHeightAt(swingEnd.x);
				halfwayDone = false;
			} else if (info.ls == limbStatus.stance) {
				swingEnd.x = limb.getEndEffectorPosition().x;
				swingEnd.y = GetUpScenario.getGroundHeightAt(swingEnd.x);
			}
			lastStatus = info.ls;
		}

		// now do the work

		if (info.ls == limbStatus.idle) {
			ikTarg = limb.getEndEffectorPosition();
		} else if (info.ls == limbStatus.swing) {
			float swingPhase = pattern.getSwingPhaseAtTime(phase);
			if (swingPhase > .5 && !halfwayDone) {
				halfwayDone = true;
				// reset the ik target to handle movement by the base
				swingEnd.x = limb.getBase().getAnchor2().x + info.xOffset;
				swingEnd.y = GetUpScenario.getGroundHeightAt(swingEnd.x);
			}

			// now set up the iktarget
			ikTarg = swingBegin.mul(1 - swingPhase).add(
					swingEnd.mul(swingPhase));
			ikTarg.y = GetUpScenario.getGroundHeightAt(ikTarg.x)
					+ heightTraj.evaluateLinear(swingPhase);
		} else if (info.ls == limbStatus.stance) {
			ikTarg = swingEnd;
			ikTarg.y = GetUpScenario.getGroundHeightAt(ikTarg.x);
			// might do quadruped style hipHeight - hipHeightDes style thing
			// here if VF's aren't good enough
		}

		if (info.kneel) {
			limb.setDesiredPoseKneel(ikTarg, desiredPose);
		} else {
			limb.setDesiredPose(ikTarg, desiredPose);
		}
	}

	public void updateContactInfo(float dt) {
		// check for contacts
		boolean contact = false;
		for (Body b : limb.getBodies()) {
			if (b.m_contactList != null)
				contact = true;
			break;
		}
		if (contact)
			plantedLevel = 1.0f;
		else
			plantedLevel = Math.max(0f, plantedLevel - .05f);
	}

	public void scaleGains(float alpha, List<ControlParam> controlParams) {
		HashMap<RevoluteJoint, Integer> jmap = limb.getJointMap();
		for (RevoluteJoint j = limb.getBase(); j != null; j = PhysicsUtils
				.getChildJoint(j)) {
			controlParams.get(jmap.get(j)).scale(alpha);
		}
	}

}
