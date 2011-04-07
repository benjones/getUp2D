package edu.benjones.getUp2D;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;

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
	
	public abstract class Limb {
		public abstract RevoluteJoint getBase();
		
	}
}
