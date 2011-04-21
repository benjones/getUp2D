package edu.benjones.getUp2D;

import java.util.HashMap;
import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.RevoluteJoint;

import edu.benjones.getUp2D.Controllers.ControlParam;
import edu.benjones.getUp2D.Controllers.VirtualForce;

public interface Character {

	/**
	 * Create a character in the given world w/ the root at the given position
	 * 
	 * @param w
	 *            world to create it in
	 * @param position
	 *            position of the CM of the root
	 * @param orientation
	 *            orientation of the root
	 */

	public Body getRoot();

	/**
	 * Set the character state (velocities are all 0
	 * 
	 * @param position
	 *            position of CM of root
	 * @param orientation
	 *            angle of the root
	 * @param relativeOrientations
	 *            to be interpreted by the concrete characters
	 */
	public void setState(Vec2 position, float orientation,
			float[] relativeOrientations);

	public List<ControlParam> getDefaultControlParams();

	public int getStateSize();

	public List<Body> getBodies();

	public List<RevoluteJoint> getJoints();

	public List<Limb> getArms();

	public List<Limb> getLegs();

	public interface Limb {
		public RevoluteJoint getBase();

		public void addGravityCompenstaionTorques(
				List<VirtualForce> virtualForces);

		public void addForceOnFoot(Vec2 force, List<VirtualForce> virtualForces);

		public HashMap<RevoluteJoint, Integer> getJointMap();

		public void setDesiredPose(Vec2 eepos, float[] desiredPose);

		public void setDesiredPoseKneel(Vec2 kneePos, float[] desiredPose);

		public Vec2 getEndEffectorPosition();

		public Vec2 getKneePosition();

		public List<Body> getBodies();

		public float getNormalForceOnLeg(float dt);

		public float getTangentialForceOnLeg(float dt);
	}
}
