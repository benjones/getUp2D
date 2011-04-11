package edu.benjones.getUp2D.Controllers;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Utils.PhysicsUtils;

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
				curr = PhysicsUtils.getChildJoint(curr);
			}
		}
	}
	
	static final Vec2 [] triangle = {new Vec2(0f,-.03f),
									new Vec2(0f,0.03f), 
									new Vec2(.08f,0f)};
	
	static final float scale = .01f;
	static final Color3f forceColor = Color3f.RED;
	
	public void draw(DebugDraw g){
		//XForm xform= new XForm();
		//xform.position = target.getWorldLocation(pLocal);
		Vec2 start = target.getWorldLocation(pLocal);
		Vec2 end = start.add(fGlobal.mul(scale));
		g.drawSegment(start, end, forceColor);
		
		float angle = (float) Math.atan2(fGlobal.y, fGlobal.x);	
		XForm rot = new XForm();
		rot.R = Mat22.createRotationalTransform(angle);
		Vec2[] triRot = new Vec2[3];
		triRot[0] = XForm.mul(rot, triangle[0]);
		triRot[1] = XForm.mul(rot, triangle[1]);
		triRot[2] = XForm.mul(rot, triangle[2]);
		
		
		triRot[0].addLocal(end);
		triRot[1].addLocal(end);
		triRot[2].addLocal(end);
		
		g.drawSolidPolygon(triRot, 3, forceColor);
		}
	
}
