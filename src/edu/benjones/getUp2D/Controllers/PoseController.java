package edu.benjones.getUp2D.Controllers;

import java.util.List;

import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.Utils.MathUtils;

public class PoseController extends AbstractController {

	public class ControlParam {
		public float kp, kd;

		private static final float defaultKP = 20, defaultKD = 2;

		public ControlParam(float kp, float kd) {
			this.kp = kp;
			this.kd = kd;
		}

		public ControlParam() {
			this.kp = defaultKP;
			this.kd = defaultKD;
		}

	}

	protected float[] desiredPose;
	protected ControlParam[] controlParams;
	protected float[] torques;

	public PoseController(Character ch) {
		super(ch);
		desiredPose = new float[ch.getStateSize()];
		controlParams = new ControlParam[ch.getStateSize()];
		for (int i = 0; i < controlParams.length; ++i)
			controlParams[i] = new ControlParam();
		torques = new float[ch.getStateSize()];
	}

	@Override
	public void computeTorques(World w, float dt) {
		List<RevoluteJoint> joints = character.getJoints();
		for (int i = 0; i < joints.size(); ++i) {
			this.computePDTorque(joints.get(i), i);
		}
		applyTorques();
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
		float angleError = MathUtils.fixAngle(desiredPose[i] - (j.getBody2().getAngle() - 
				j.getBody1().getAngle()));
		
		float velError = j.getJointSpeed();
		torques[i] = controlParams[i].kp * angleError - velError
				* controlParams[i].kd;
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
}
