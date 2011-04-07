package edu.benjones.getUp2D.Utils;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.JointEdge;
import org.jbox2d.dynamics.joints.RevoluteJoint;

public class PhysicsUtils {
	
	/**
	 * return the next joint on an articulated chain (eg getChild(hip) = knee)
	 * @param parent parent joint
	 * @return child joint.
	 */
	static public RevoluteJoint getChildJoint(RevoluteJoint parent){
		Body b = parent.getBody2();
		JointEdge j = b.getJointList();
		while(j != null && j.joint == parent)
			j = j.next;
		if(j == null)
			return null;
		return (RevoluteJoint)j.joint;
	}
	
	
}
