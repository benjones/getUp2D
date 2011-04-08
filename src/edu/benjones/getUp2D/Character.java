package edu.benjones.getUp2D;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Controllers.VirtualForce;

public interface Character {

	/**
	 * Create a character in the given world w/ the root at the given position
	 * @param w world to create it in
	 * @param position position of the CM of the root
	 * @param orientation orientation of the root
	 */

	public Body getRoot();
	
	/**
	 * Set the character state (velocities are all 0
	 * @param position position of CM of root
	 * @param orientation angle of the root 
	 * @param relativeOrientations to be interpreted by the concrete characters
	 */
	public void setState(Vec2 position, float orientation, float[] relativeOrientations);

	public int getStateSize();
	public List<Body> getBodies();
	public List<RevoluteJoint> getJoints();
	
	public List<Limb> getArms();
	public List<Limb> getLegs();
	
	public interface Limb {
		public RevoluteJoint getBase();
		public void addGravityCompenstaionTorques(List<VirtualForce> virtualForces);
		public void setDesiredPose(Vec2 eepos, float[] desiredPose);
	}
}
