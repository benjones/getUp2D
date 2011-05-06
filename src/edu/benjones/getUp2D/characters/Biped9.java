package edu.benjones.getUp2D.characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbox2d.collision.ContactID;
import org.jbox2d.collision.FilterData;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactResult;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.GetUpScenario;
import edu.benjones.getUp2D.Controllers.ControlParam;
import edu.benjones.getUp2D.Controllers.VirtualForce;
import edu.benjones.getUp2D.Utils.PhysicsUtils;

public class Biped9 implements edu.benjones.getUp2D.Character {

	protected Body root, leftUpperLeg, leftLowerLeg, rightUpperLeg,
			rightLowerLeg, leftUpperArm, leftLowerArm, rightUpperArm,
			rightLowerArm;

	protected RevoluteJoint leftHip, rightHip, leftKnee, rightKnee,
			leftShoulder, rightShoulder, leftElbow, rightElbow;

	protected final Vec2 shoulderTorsoOffset, hipTorsoOffset,
			shoulderUpperArmOffset, hipUpperLegOffset, kneeUpperLegOffset,
			kneeLowerLegOffset, elbowUpperArmOffset, elbowLowerArmOffset,
			legEEOffset, armEEOffset;

	private final float torsoDensity = 173;
	private final float limbDensity = 73;

	private final int filterGroup = -1;

	protected ArrayList<Body> bodies;
	protected ArrayList<RevoluteJoint> joints;
	protected HashMap<RevoluteJoint, Integer> jointMap;
	protected ArrayList<Limb> arms;
	protected ArrayList<Limb> legs;

	protected ArrayList<ControlParam> defaultControlParams;

	protected World world;
	protected GetUpScenario scenario;

	public Biped9(World w, GetUpScenario scenario) {
		this.scenario = scenario;
		world = w;

		shoulderTorsoOffset = new Vec2(.0f, .18f);
		hipTorsoOffset = new Vec2(0f, -.18f);
		shoulderUpperArmOffset = new Vec2(0f, -.12f);
		hipUpperLegOffset = new Vec2(0f, .15f);
		kneeUpperLegOffset = new Vec2(0f, -.15f);
		kneeLowerLegOffset = new Vec2(0f, .13f);
		elbowUpperArmOffset = new Vec2(0f, .12f);
		elbowLowerArmOffset = new Vec2(0f, -.11f);
		legEEOffset = new Vec2(0f, -.16f);
		armEEOffset = new Vec2(0f, .13f);

		BodyDef rootDef = new BodyDef();
		rootDef.position = new Vec2(0f, 0f);
		rootDef.angle = 0;

		PolygonDef rootPolyDef = new PolygonDef();
		rootPolyDef.setAsBox(.08f, .20f);
		rootPolyDef.density = torsoDensity;// mass/area
		rootPolyDef.filter.groupIndex = filterGroup;

		root = w.createBody(rootDef);
		root.createShape(rootPolyDef);
		root.setMassFromShapes();

		BodyDef upperArmDef, lowerArmDef, upperLegDef, lowerLegDef;

		PolygonDef upperArmPolyDef, lowerArmPolyDef, upperLegPolyDef, lowerLegPolyDef;

		RevoluteJointDef hipDef, kneeDef, shoulderDef, elbowDef;

		// setup arms
		upperArmDef = new BodyDef();
		upperArmDef.position = shoulderTorsoOffset.sub(shoulderUpperArmOffset);
		leftUpperArm = w.createBody(upperArmDef);

		upperArmPolyDef = new PolygonDef();
		upperArmPolyDef.setAsBox(.041f, .15f);
		upperArmPolyDef.density = limbDensity;
		upperArmPolyDef.filter.groupIndex = filterGroup;

		leftUpperArm = w.createBody(upperArmDef);
		leftUpperArm.createShape(upperArmPolyDef);
		leftUpperArm.setMassFromShapes();

		shoulderDef = new RevoluteJointDef();
		shoulderDef.initialize(root, leftUpperArm, shoulderTorsoOffset);
		shoulderDef.lowerAngle = (float) (-2 * Math.PI);
		shoulderDef.upperAngle = (float) (2 * Math.PI);
		shoulderDef.enableLimit = false;

		leftShoulder = (RevoluteJoint) w.createJoint(shoulderDef);

		lowerArmDef = new BodyDef();
		lowerArmDef.position = leftUpperArm.getPosition()
				.add(elbowUpperArmOffset).sub(elbowLowerArmOffset);

		lowerArmPolyDef = new PolygonDef();
		lowerArmPolyDef.setAsBox(.041f, .13f);
		lowerArmPolyDef.density = limbDensity;
		lowerArmPolyDef.filter.groupIndex = filterGroup;
		lowerArmPolyDef.friction = .3f;

		leftLowerArm = w.createBody(lowerArmDef);
		leftLowerArm.createShape(lowerArmPolyDef);
		leftLowerArm.setMassFromShapes();

		elbowDef = new RevoluteJointDef();
		elbowDef.initialize(leftUpperArm, leftLowerArm, leftUpperArm
				.getPosition().add(elbowUpperArmOffset));
		elbowDef.lowerAngle = (float) (-.95 * Math.PI);
		elbowDef.upperAngle = (float) (.95 * Math.PI);
		elbowDef.enableLimit = true;

		leftElbow = (RevoluteJoint) w.createJoint(elbowDef);

		rightUpperArm = w.createBody(upperArmDef);
		rightUpperArm.createShape(upperArmPolyDef);
		rightUpperArm.setMassFromShapes();

		shoulderDef.initialize(root, rightUpperArm, shoulderTorsoOffset);

		rightShoulder = (RevoluteJoint) w.createJoint(shoulderDef);

		lowerArmDef.position = rightUpperArm.getPosition()
				.add(elbowUpperArmOffset).sub(elbowLowerArmOffset);

		rightLowerArm = w.createBody(lowerArmDef);
		rightLowerArm.createShape(lowerArmPolyDef);
		rightLowerArm.setMassFromShapes();

		elbowDef.initialize(rightUpperArm, rightLowerArm, rightUpperArm
				.getPosition().add(elbowUpperArmOffset));

		rightElbow = (RevoluteJoint) w.createJoint(elbowDef);

		// setup legs
		upperLegDef = new BodyDef();
		upperLegDef.position = hipTorsoOffset.sub(hipUpperLegOffset);
		leftUpperLeg = w.createBody(upperLegDef);

		upperLegPolyDef = new PolygonDef();
		upperLegPolyDef.setAsBox(.041f, .19f);
		upperLegPolyDef.density = limbDensity;
		upperLegPolyDef.filter.groupIndex = filterGroup;

		leftUpperLeg = w.createBody(upperLegDef);
		leftUpperLeg.createShape(upperLegPolyDef);
		leftUpperLeg.setMassFromShapes();

		hipDef = new RevoluteJointDef();
		hipDef.initialize(root, leftUpperLeg, hipTorsoOffset);
		hipDef.lowerAngle = (float) (-3 * Math.PI / 4.0);
		hipDef.upperAngle = (float) (6.0 * Math.PI / 4.0);
		hipDef.enableLimit = true;

		leftHip = (RevoluteJoint) w.createJoint(hipDef);

		rightUpperLeg = w.createBody(upperLegDef);
		rightUpperLeg.createShape(upperLegPolyDef);
		rightUpperLeg.setMassFromShapes();
		hipDef.initialize(root, rightUpperLeg, hipTorsoOffset);
		rightHip = (RevoluteJoint) w.createJoint(hipDef);

		lowerLegDef = new BodyDef();
		lowerLegDef.position = leftUpperLeg.getPosition()
				.add(kneeUpperLegOffset).sub(kneeLowerLegOffset);

		lowerLegPolyDef = new PolygonDef();
		lowerLegPolyDef.setAsBox(.041f, .16f);
		lowerLegPolyDef.density = limbDensity;
		lowerLegPolyDef.filter.groupIndex = filterGroup;
		lowerLegPolyDef.friction = .8f;

		leftLowerLeg = w.createBody(lowerLegDef);
		leftLowerLeg.createShape(lowerLegPolyDef);
		leftLowerLeg.setMassFromShapes();

		kneeDef = new RevoluteJointDef();
		kneeDef.initialize(leftUpperLeg, leftLowerLeg, leftUpperLeg
				.getPosition().add(kneeUpperLegOffset));
		kneeDef.lowerAngle = (float) (-.75f * Math.PI);
		kneeDef.upperAngle = 0f;
		kneeDef.enableLimit = true;

		leftKnee = (RevoluteJoint) w.createJoint(kneeDef);

		lowerLegDef.position = rightUpperLeg.getPosition()
				.add(kneeUpperLegOffset).sub(kneeLowerLegOffset);

		rightLowerLeg = w.createBody(lowerLegDef);
		rightLowerLeg.createShape(lowerLegPolyDef);
		rightLowerLeg.setMassFromShapes();

		kneeDef.initialize(rightUpperLeg, rightLowerLeg, rightUpperLeg
				.getPosition().add(kneeUpperLegOffset));

		rightKnee = (RevoluteJoint) w.createJoint(kneeDef);

		bodies = new ArrayList<Body>();
		bodies.add(root);
		bodies.add(leftUpperArm);
		bodies.add(leftLowerArm);
		bodies.add(rightUpperArm);
		bodies.add(rightLowerArm);
		bodies.add(leftUpperLeg);
		bodies.add(leftLowerLeg);
		bodies.add(rightUpperLeg);
		bodies.add(rightLowerLeg);

		joints = new ArrayList<RevoluteJoint>();
		joints.add(leftShoulder);
		joints.add(rightShoulder);
		joints.add(leftElbow);
		joints.add(rightElbow);
		joints.add(leftHip);
		joints.add(rightHip);
		joints.add(leftKnee);
		joints.add(rightKnee);

		jointMap = new HashMap<RevoluteJoint, Integer>();
		for (RevoluteJoint j : joints) {
			jointMap.put(j, joints.indexOf(j));
		}

		HashMap<RevoluteJoint, Integer> leftLegMap, rightLegMap, leftArmMap, rightArmMap;
		leftLegMap = new HashMap<RevoluteJoint, Integer>();
		leftLegMap.put(leftHip, joints.indexOf(leftHip));
		leftLegMap.put(leftKnee, joints.indexOf(leftKnee));
		rightLegMap = new HashMap<RevoluteJoint, Integer>();
		rightLegMap.put(rightHip, joints.indexOf(rightHip));
		rightLegMap.put(rightKnee, joints.indexOf(rightKnee));

		leftArmMap = new HashMap<RevoluteJoint, Integer>();
		leftArmMap.put(leftShoulder, joints.indexOf(leftShoulder));
		leftArmMap.put(leftElbow, joints.indexOf(leftElbow));

		rightArmMap = new HashMap<RevoluteJoint, Integer>();
		rightArmMap.put(rightShoulder, joints.indexOf(rightShoulder));
		rightArmMap.put(rightElbow, joints.indexOf(rightElbow));

		arms = new ArrayList<Limb>();
		legs = new ArrayList<Limb>();
		arms.add(new Bip9Limb(leftShoulder, leftLowerArm, armEEOffset, false,
				leftArmMap, this));
		arms.add(new Bip9Limb(rightShoulder, rightLowerArm, armEEOffset, false,
				rightArmMap, this));

		legs.add(new Bip9Limb(leftHip, leftLowerLeg, legEEOffset, true,
				leftLegMap, this));
		legs.add(new Bip9Limb(rightHip, rightLowerLeg, legEEOffset, true,
				rightLegMap, this));

		defaultControlParams = new ArrayList<ControlParam>();
		ControlParam hip, shoulder, knee, elbow;
		hip = new ControlParam(35, 8);
		shoulder = new ControlParam(35, 5);
		knee = new ControlParam(35, 8);
		elbow = new ControlParam(35, 3);
		defaultControlParams.add(shoulder);
		defaultControlParams.add(shoulder);
		defaultControlParams.add(elbow);
		defaultControlParams.add(elbow);
		defaultControlParams.add(hip);
		defaultControlParams.add(hip);
		defaultControlParams.add(knee);
		defaultControlParams.add(knee);

	}

	public Body getRoot() {
		return root;
	}

	/**
	 * @param position
	 *            position of COM of the root
	 * @param orientation
	 *            orientation of the root
	 * @param relativeOrientations
	 *            joint angles in the order of the joints arrayList
	 */
	public void setState(Vec2 position, float orientation,
			float[] relativeOrientations) {

		Vec2 zero = new Vec2(0, 0);
		// set up the root stuff, then handle all the other bodies
		root.setXForm(position, orientation);
		root.setAngularVelocity(0f);
		root.setLinearVelocity(zero);

		float angle;
		Body parent, child;
		RevoluteJoint j;
		XForm xf = new XForm();
		for (int i = 0; i < joints.size(); ++i) {
			j = joints.get(i);
			parent = j.getBody1();
			child = j.getBody2();

			angle = parent.getAngle() + relativeOrientations[i];
			xf.R = Mat22.createRotationalTransform(angle);
			position = j.getAnchor1().sub(XForm.mul(xf, j.m_localAnchor2));
			child.setXForm(position, angle);
			child.setAngularVelocity(0f);
			child.setLinearVelocity(zero);

		}

	}

	public void setGroupIndex(int i) {
		FilterData fd = new FilterData();
		fd.categoryBits = 1;
		fd.maskBits = 0xffffffff;
		fd.groupIndex = i;

		for (Body b : bodies) {
			for (Shape s = b.getShapeList(); s != null; s = s.getNext()) {
				s.setFilterData(fd);
			}
		}
	}

	public int getStateSize() {
		return this.joints.size();
	}

	@Override
	public List<Body> getBodies() {
		return bodies;
	}

	@Override
	public List<RevoluteJoint> getJoints() {
		return joints;
	}

	public static Character makeCharacter(World w, GetUpScenario scenario) {
		return new Biped9(w, scenario);
	}

	public static Character getStaticCharacter(World w, GetUpScenario scenario) {
		Character ret = makeCharacter(w, scenario);
		// for (Body b : ret.getBodies())
		// b.setMass(new MassData());
		return ret;
	}

	public class Bip9Limb implements Limb {
		protected RevoluteJoint hip;
		protected Body endEffector;
		protected Vec2 eeOffset;
		protected boolean posAngle;
		protected Character character;

		protected HashMap<RevoluteJoint, Integer> jointMap;

		protected float l1, l2;// lengths of link1 and link2

		/**
		 * Construct a new leg
		 * 
		 * @param hip
		 *            hip joint
		 * @param endEffector
		 *            link to be used as the end effector
		 * @param eeOffset
		 *            where on the link we are controlling
		 * @param posAngle
		 *            true when the desired angle must be positive, does the
		 *            knee bend only one way?
		 * @param jointMap
		 *            map of joint to indeces in the joint array
		 */
		private Bip9Limb(RevoluteJoint hip, Body endEffector, Vec2 eeOffset,
				boolean posAngle, HashMap<RevoluteJoint, Integer> jointMap,
				Character character) {
			this.hip = hip;
			this.endEffector = endEffector;
			this.eeOffset = eeOffset;
			this.posAngle = posAngle;
			this.jointMap = jointMap;
			this.character = character;

			l1 = hip.getAnchor2()
					.sub(PhysicsUtils.getChildJoint(hip).getAnchor1()).length();

			l2 = PhysicsUtils.getChildJoint(hip).m_localAnchor2.sub(eeOffset)
					.length();
		}

		@Override
		public RevoluteJoint getBase() {
			return hip;
		}

		private final Vec2 zero = new Vec2(0f, 0f);

		@Override
		public void addGravityCompenstaionTorques(
				List<VirtualForce> virtualForces) {

			RevoluteJoint curr = hip;
			Body b;

			while (curr != null) {
				b = curr.getBody2();
				virtualForces.add(new VirtualForce(hip, b, zero, world
						.getGravity().mul(-b.getMass() * .75f)));
				curr = PhysicsUtils.getChildJoint(curr);
			}
		}

		@Override
		public void addForceOnFoot(Vec2 force, List<VirtualForce> virtualForces) {
			virtualForces.add(new VirtualForce(hip, this.endEffector, zero,
					force));
		}

		private float maxLength = .9999f;

		@Override
		public void setDesiredPose(Vec2 eepos, float[] desiredPose) {
			// time to get my IK on
			Vec2 relVec = eepos.sub(hip.getAnchor2());
			if (relVec.length() > (l1 + l2) * maxLength) {
				relVec.normalize();
				relVec.mulLocal((l1 + l2) * maxLength);
			}

			float l3 = relVec.length();
			float det = Math.min((l1 * l1 + l2 * l2 - l3 * l3) / (2 * l1 * l2),
					1.0f);

			float kneePre = (float) Math.acos(det);

			float kneeAngle = (float) (kneePre - Math.PI);

			// angle between relVec and the desired link1
			float theta1 = (float) Math.asin(Math.min(l2 * Math.sin(kneePre)
					/ l3, 1.0f));

			// global angle of relVec
			float thetav = (float) Math.atan2(relVec.y, relVec.x);
			// angle of parent
			float thetap = hip.getBody1().getAngle();

			float hipAngle = -(float) (thetap - (thetav - .5 * Math.PI) - theta1);

			if (posAngle) {
				hipAngle = (float) (thetav - .5 * Math.PI + theta1 - thetap - Math.PI);
			}

			desiredPose[jointMap.get(hip)] = hipAngle;
			desiredPose[jointMap.get(PhysicsUtils.getChildJoint(hip))] = kneeAngle;
		}

		private ArrayList<Body> bodies;

		public List<Body> getBodies() {
			if (bodies == null) {
				bodies = new ArrayList<Body>();

				RevoluteJoint j = hip;
				while (j != null) {
					bodies.add(j.getBody2());
					j = PhysicsUtils.getChildJoint(j);
				}
			}
			return bodies;
		}

		// this looks TERRIBLE. I THINK, that all the loops will be 1-2 elements
		// max
		@Override
		public float getNormalForceOnLeg(float dt) {
			float force = 0f;
			HashMap<ContactID, ContactResult> contactMap = scenario
					.getContactMap();

			for (Body b : getBodies()) {
				for (ContactResult cr : contactMap.values()) {
					if (cr.shape1.m_body == b || cr.shape2.m_body == b) {

						force += Math.abs(cr.normalImpulse / dt);
					}
				}
			}
			return force;
		}

		@Override
		public float getTangentialForceOnLeg(float dt) {
			float force = 0f;
			HashMap<ContactID, ContactResult> contactMap = scenario
					.getContactMap();
			for (Body b : getBodies()) {
				for (ContactResult cr : contactMap.values()) {
					if (cr.shape1.m_body == b || cr.shape2.m_body == b) {

						force += Math.abs(cr.tangentImpulse / dt);
					}
				}
			}

			return force;
		}

		@Override
		public Vec2 getEndEffectorPosition() {
			return endEffector.getWorldLocation(eeOffset);
		}

		@Override
		public void setDesiredPoseKneel(Vec2 kneePos, float[] desiredPose) {
			Vec2 relVec = kneePos.sub(hip.getAnchor2());
			float upperAngle = (float) Math.atan2(relVec.y, relVec.x);

			desiredPose[jointMap.get(hip)] = (float) (upperAngle
					- hip.getBody1().getAngle() + Math.PI * .5);
			desiredPose[jointMap.get(PhysicsUtils.getChildJoint(hip))] = (float) (-.5
					* Math.PI - hip.getBody2().getAngle());
		}

		@Override
		public HashMap<RevoluteJoint, Integer> getJointMap() {
			return jointMap;
		}

		@Override
		public Vec2 getKneePosition() {
			return PhysicsUtils.getChildJoint(hip).getAnchor1();
		}

		@Override
		public Character getCharacter() {
			return character;
		}
	}

	@Override
	public List<Limb> getArms() {
		return arms;
	}

	@Override
	public List<Limb> getLegs() {
		return legs;
	}

	@Override
	public List<ControlParam> getDefaultControlParams() {
		return defaultControlParams;
	}

	@Override
	public float getTorsoLength() {

		return hipTorsoOffset.sub(shoulderTorsoOffset).length();
	}

	@Override
	public GetUpScenario getScenario() {
		return scenario;
	}

	@Override
	public HashMap<RevoluteJoint, Integer> getJointMap() {
		return jointMap;
	}

	@Override
	public void destroy() {
		for (RevoluteJoint j : joints) {
			world.destroyJoint(j);
		}
		for (Body b : bodies) {
			world.destroyBody(b);
		}

	}

	@Override
	public Vec2 getCOMPosition() {
		Vec2 cm = new Vec2(0f, 0f);
		float mass = 0f;
		for (Body b : bodies) {
			float bMass = b.getMass();
			mass += bMass;
			cm.addLocal(b.getWorldCenter().mul(bMass));
		}
		cm.mul(1.0f / mass);
		return cm;
	}

	@Override
	public Vec2 getCOMVelocity() {
		Vec2 cmv = new Vec2(0f, 0f);
		float mass = 0f;
		for (Body b : bodies) {
			float bMass = b.getMass();
			mass += bMass;
			cmv.addLocal(b.getLinearVelocity().mul(bMass));
		}
		cmv.mul(1.0f / mass);
		return cmv;
	}
}
