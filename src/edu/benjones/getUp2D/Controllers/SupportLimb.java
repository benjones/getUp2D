package edu.benjones.getUp2D.Controllers;

import java.util.HashMap;
import java.util.List;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Character.Limb;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbPattern;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;
import edu.benjones.getUp2D.Utils.PhysicsUtils;
import edu.benjones.getUp2D.Utils.Trajectory1D;
import edu.benjones.getUp2D.Utils.Trajectory1D.entry;

public class SupportLimb {

	protected Limb limb;
	protected supportLabel limbLabel;
	protected supportInfo lastInfo;

	protected float plantedLevel;
	private Trajectory1D heightTraj;

	protected Vec2 ikTarg;

	protected SupportPattern parent;

	public SupportLimb(Limb limb, SupportPattern parent, supportLabel limbLabel) {
		this.limb = limb;
		heightTraj = new Trajectory1D();
		heightTraj.addKnot(new entry(0f, 0f));
		heightTraj.addKnot(new entry(.1f, .10f));
		heightTraj.addKnot(new entry(.9f, .15f));
		heightTraj.addKnot(new entry(1.0f, 0f));

		this.parent = parent;
		this.limbLabel = limbLabel;

		reset();
	}

	public void reset() {
		this.lastInfo = new supportInfo(limbStatus.idle, false, 0f);
		this.plantedLevel = 0;
		swingBegin = new Vec2();
		swingEnd = new Vec2();
	}

	public void draw(DebugDraw g) {
		if (ikTarg != null) {

			g.drawCircle(ikTarg, .05f, Color3f.WHITE);
			g.drawCircle(ikTarg.add(new Vec2(-1f, 1f)), .05f, Color3f.WHITE);

		}
	}

	public boolean canSupport() {
		return plantedLevel > 0f;
	}

	public boolean canAndShouldSupport() {
		return plantedLevel > 0f && this.lastInfo.ls == limbStatus.stance;
	}

	public boolean canRemoveSupport(float dt) {
		float tangential = limb.getTangentialForceOnLeg(dt);
		float normal = limb.getNormalForceOnLeg(dt);
		float f = (tangential + normal);

		return f < 30;
	}

	protected Vec2 swingBegin, swingEnd;
	protected boolean halfwayDone;// we'll re-evaluate where the swing EE is
									// halfway through

	public void setPose(limbPattern pattern, float phase, float[] desiredPose) {

		supportInfo info = pattern.getInfoAtTime(phase);
		if (info.ls != lastInfo.ls || info.kneel != lastInfo.kneel) {
			if (info.ls == limbStatus.swing) {
				if (info.kneel) {

					swingBegin = limb.getKneePosition();
				} else {

					swingBegin = limb.getEndEffectorPosition();
				}
				swingEnd.x = limb.getBase().getAnchor2().x + info.xOffset;
				swingEnd.y = limb.getCharacter().getScenario()
						.getGroundHeightAt(swingEnd.x);
				halfwayDone = false;
			} else if (info.ls == limbStatus.stance) {
				if (info.kneel) {
					swingEnd.x = limb.getKneePosition().x;
				} else {
					swingEnd.x = limb.getEndEffectorPosition().x;
				}
				swingEnd.y = limb.getCharacter().getScenario()
						.getGroundHeightAt(swingEnd.x);

			}
			lastInfo = info;
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
				swingEnd.y = limb.getCharacter().getScenario()
						.getGroundHeightAt(swingEnd.x);
			}

			// now set up the iktarget
			ikTarg = swingBegin.mul(1 - swingPhase).add(
					swingEnd.mul(swingPhase));
			ikTarg.y = limb.getCharacter().getScenario()
					.getGroundHeightAt(ikTarg.x)
					+ heightTraj.evaluateLinear(swingPhase);
		} else if (info.ls == limbStatus.stance) {
			ikTarg = swingEnd;
			ikTarg.y = limb.getCharacter().getScenario()
					.getGroundHeightAt(ikTarg.x);
			float desHeight;
			if (limbLabel == supportLabel.leftLeg
					|| limbLabel == supportLabel.rightLeg) {
				// use hip traj
				desHeight = parent.getHipHeightNow();
			} else {
				desHeight = parent.getShoulderHeightNow();
			}
			ikTarg.y = Math.min(limb.getBase().getAnchor2().y - desHeight, 0f);

			// if its lifting soon, shift weight to other limb
			float ttl = parent.getTimeToLift(limbLabel);
			if (ttl < .5) {
				swingEnd.y += .04 * (.5 - ttl);
			}

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
			if (b.m_contactList != null) {
				contact = true;
				break;
			}
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

	public void addGravityCompensationTorques(List<VirtualForce> virtualForces) {
		limb.addGravityCompenstaionTorques(virtualForces);
	}

	public Vec2 getShoulderPosition() {
		return limb.getBase().getAnchor2();
	}

	public void addForceOnFoot(Vec2 force, List<VirtualForce> virtualForces) {
		limb.addForceOnFoot(force, virtualForces);
	}

}
