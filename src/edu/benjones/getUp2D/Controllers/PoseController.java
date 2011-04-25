package edu.benjones.getUp2D.Controllers;

import java.util.List;

import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.Utils.MathUtils;

public class PoseController extends AbstractController {

	protected float[] desiredPose;
	protected List<ControlParam> controlParams;
	protected float[] torques;

	public PoseController(Character ch) {
		super(ch);
		desiredPose = new float[ch.getStateSize()];
		/*
		 * controlParams = new ControlParam[ch.getStateSize()]; for (int i = 0;
		 * i < controlParams.length; ++i) controlParams[i] = new ControlParam();
		 */
		controlParams = character.getDefaultControlParams();
		torques = new float[ch.getStateSize()];
	}

	@Override
	public void computeTorques(World w, float dt) {
		computeTorquesOnly(w, dt);
		applyTorques();
	}

	public void computeTorquesOnly(World w, float dt) {
		List<RevoluteJoint> joints = character.getJoints();
		for (int i = 0; i < joints.size(); ++i) {
			this.computePDTorque(joints.get(i), i);
		}
	}

	@Override
	public void applyTorques() {
		// TODO Auto-generated method stub
		List<RevoluteJoint> joints = character.getJoints();
		RevoluteJoint j;
		for (int i = 0; i < torques.length; ++i) {
			j = joints.get(i);
			// apply + torque to child, and -torque to parent
			j.getBody1().applyTorque(-torques[i]);
			j.getBody2().applyTorque(torques[i]);

		}
	}

	/**
	 * Set the pd torque for joint J, indexed by i
	 * 
	 * @param j
	 *            the joint
	 * @param i
	 *            index of joint/torque/controlParam
	 */
	protected void computePDTorque(RevoluteJoint j, int i) {
		float angleNow = (j.getBody2().getAngle() - j.getBody1().getAngle());
		float angleError = MathUtils.fixAngle(desiredPose[i] - angleNow);

		// lowBroke?
		// boolean lowBroke = angleNow + angleError < j.getLowerLimit();
		boolean highBroke = angleNow + angleError > j.getUpperLimit();
		if (highBroke) {
			angleError = MathUtils.fixAngle((float) (2 * Math.PI - angleError));
		}
		float velError = j.getJointSpeed();
		torques[i] = controlParams.get(i).kp * angleError - velError
				* controlParams.get(i).kd;
	}

	public void clearTorques() {
		for (int i = 0; i < torques.length; ++i)
			torques[i] = 0;
	}

	public float[] getDesiredPose() {
		return desiredPose;
	}

	@Override
	public void drawControllerExtras(DebugDraw g) {

	}

	@Override
	public void reset() {
		// don't think there's anything to do here

	}
}
