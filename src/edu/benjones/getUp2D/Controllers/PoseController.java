package edu.benjones.getUp2D.Controllers;

import java.util.List;

import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Character;

public class PoseController extends AbstractController {

	public class ControlParam{
		public float kp, kd;
		public ControlParam(float kp, float kd){
			this.kp = kp;
			this.kd = kd;
		}
	}
	
	private float[] desiredPose;
	private ControlParam[] controlParams;
	private float[] torques;
	
	public PoseController(Character ch){
		super(ch);
		desiredPose = new float[ch.getStateSize()];
		controlParams = new ControlParam[ch.getStateSize()];
		torques = new float[ch.getStateSize()];
	}
	
	@Override
	public void computeTorques(World w, float dt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void applyTorques() {
		// TODO Auto-generated method stub
		List<RevoluteJoint> joints = character.getJoints();
		RevoluteJoint j;
		for(int i = 0; i < torques.length; ++i){
			j = joints.get(i);
			//apply + torque to child, and -torque to parent
			j.getBody1().applyTorque(-torques[i]);
			j.getBody2().applyTorque(torques[i]);
			
		}
	}

	private void computePDTorque(RevoluteJoint j){
		
	}
}
