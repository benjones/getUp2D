package edu.benjones.getUp2D.Controllers;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

public class VirtualForce {

	
	
	
	private RevoluteJoint base;
	private Body target;
	private Vec2 pLocal, fGlobal;
	private boolean reversed;
	/**
	 * Create a new virtual force
	 * @param base joint to a "grounded" link where the force starts
	 * @param target body where the force is desired
	 * @param plocal local coordinate on the body where to apply it
	 * @param fGlobal desired force in global coordinates
	 */
	public VirtualForce(RevoluteJoint base, Body target, Vec2 pLocal, Vec2 fGlobal){
		this.base = base;
		this.target = target;
		this.pLocal = pLocal;
		this.fGlobal = fGlobal;
		this.reversed = false;
	}
	/**
	 * Create a new virtual force
	 * @param base joint to a "grounded" link where the force starts
	 * @param target body where the force is desired
	 * @param plocal local coordinate on the body where to apply it
	 * @param fGlobal desired force in global coordinates
	 * @param reversed true if base is a "child" of target
	 */
	public VirtualForce(RevoluteJoint base, Body target, Vec2 pLocal, Vec2 fGlobal,
			boolean reversed){
		this.base = base;
		this.target = target;
		this.pLocal = pLocal;
		this.fGlobal = fGlobal;
		this.reversed = reversed;
	}
	
	public void apply(){
		RevoluteJoint curr;
		if(reversed){
		
		}
		else{
			curr = base;
			float torque;
			Vec2 leverArm;
			Vec2 pGlobal = target.getWorldLocation(pLocal);
			while(curr != null){
				leverArm = pGlobal.sub(curr.getAnchor1());
				torque = Vec2.cross(leverArm, fGlobal);
				curr.getBody2().applyTorque(torque);
				curr.getBody1().applyTorque(-torque);
			}
		}
	}
	
}
