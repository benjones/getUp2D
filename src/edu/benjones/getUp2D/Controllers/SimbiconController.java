package edu.benjones.getUp2D.Controllers;

import java.util.ArrayList;
import java.util.HashMap;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.Character.Limb;
import edu.benjones.getUp2D.Utils.PhysicsUtils;

public class SimbiconController extends PoseController {

	private float phase;
	private ControlParam rootCP;
	private Limb leftLeg, rightLeg;
	private ArrayList<SimbiconState> states;
	private int currentState;

	private float cd, cv;// swing foot placement feedback terms

	public ControlParam getRootCP() {
		return rootCP;
	}

	public void setRootCP(ControlParam rootCP) {
		this.rootCP = rootCP;
	}

	public float getCd() {
		return cd;
	}

	public void setCd(float cd) {
		this.cd = cd;
	}

	public float getCv() {
		return cv;
	}

	public void setCv(float cv) {
		this.cv = cv;
	}

	public float getPhase() {
		return phase;
	}

	public SimbiconController(Character ch) {
		super(ch);

		// make arms limp, but still damped
		for (Limb l : character.getArms()) {
			for (RevoluteJoint j = l.getBase(); j != null; j = PhysicsUtils
					.getChildJoint(j)) {
				controlParams.get(l.getJointMap().get(j)).kp = 0;
			}
		}

	}

	public void addStaet(SimbiconState s) {
		states.add(s);
	}

	@Override
	public void computeTorques(World w, float dt) {
		phase += dt;

		clearTorques();
		SimbiconState s = states.get(currentState);
		HashMap<RevoluteJoint, Integer> jointMap = character.getJointMap();
		leftLeg = character.getLegs().get(0);
		rightLeg = character.getLegs().get(0);

		Vec2 COM = character.getCOMPosition();
		Vec2 COMVelocity = character.getCOMVelocity();

		// set knee desired poses
		if (s.leftStance) {
			desiredPose[jointMap.get(PhysicsUtils.getChildJoint(leftLeg
					.getBase()))] = s.stanceKneeAngle;
		} else {
			desiredPose[jointMap.get(PhysicsUtils.getChildJoint(leftLeg
					.getBase()))] = s.swingKneeAngle;

			// assumes the left leg is in stance mode
			desiredPose[jointMap.get(leftLeg.getBase())] = 0;
		}
		if (s.rightStance) {
			desiredPose[jointMap.get(PhysicsUtils.getChildJoint(rightLeg
					.getBase()))] = s.stanceKneeAngle;
		} else {
			desiredPose[jointMap.get(PhysicsUtils.getChildJoint(rightLeg
					.getBase()))] = s.swingKneeAngle;
		}

		// swing hip

		computeTorquesOnly(w, dt);

		// torso

		// stance hip

		// should we advance the state

		if (s.endOnContact) {
			// check legs
			if (!s.leftStance
					&& (Math.abs(leftLeg.getNormalForceOnLeg(dt))
							+ Math.abs(leftLeg.getTangentialForceOnLeg(dt)) > 0)) {
				advanceState();

			} else if (!s.rightStance
					&& (Math.abs(rightLeg.getNormalForceOnLeg(dt))
							+ Math.abs(rightLeg.getTangentialForceOnLeg(dt)) > 0)) {
				advanceState();

			}
		} else if (phase > s.duration) {
			advanceState();
		}

	}

	private void advanceState() {
		currentState = (currentState + 1) % states.size();
		phase = 0;
	}
}
