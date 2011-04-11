package edu.benjones.getUp2D.characters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbox2d.collision.FilterData;
import org.jbox2d.collision.MassData;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;
import edu.benjones.getUp2D.Character;
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
	protected ArrayList<Limb> arms;
	protected ArrayList<Limb> legs;

	protected World world;

	public Biped9(World w) {

		world = w;

		shoulderTorsoOffset = new Vec2(.1f, .35f);
		hipTorsoOffset = new Vec2(0f, -.35f);
		shoulderUpperArmOffset = new Vec2(0f, -.12f);
		hipUpperLegOffset = new Vec2(0f, .15f);
		kneeUpperLegOffset = new Vec2(0f, -.15f);
		kneeLowerLegOffset = new Vec2(0f, .13f);
		elbowUpperArmOffset = new Vec2(0f, .12f);
		elbowLowerArmOffset = new Vec2(0f, -.09f);
		legEEOffset = new Vec2(0f, -.16f);
		armEEOffset = new Vec2(0f, -.11f);

		BodyDef rootDef = new BodyDef();
		rootDef.position = new Vec2(0f, 0f);
		rootDef.angle = 0;

		PolygonDef rootPolyDef = new PolygonDef();
		rootPolyDef.setAsBox(.16f, .39f);
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
		upperArmPolyDef.setAsBox(.05f, .15f);
		upperArmPolyDef.density = limbDensity;
		upperArmPolyDef.filter.groupIndex = filterGroup;

		leftUpperArm = w.createBody(upperArmDef);
		leftUpperArm.createShape(upperArmPolyDef);
		leftUpperArm.setMassFromShapes();

		shoulderDef = new RevoluteJointDef();
		shoulderDef.initialize(root, leftUpperArm, shoulderTorsoOffset);
		shoulderDef.lowerAngle = (float) (-3 * Math.PI / 4.0);
		shoulderDef.upperAngle = (float) (3 * Math.PI / 4.0);
		shoulderDef.enableLimit = true;

		leftShoulder = (RevoluteJoint) w.createJoint(shoulderDef);

		lowerArmDef = new BodyDef();
		lowerArmDef.position = leftUpperArm.getPosition()
				.add(elbowUpperArmOffset).sub(elbowLowerArmOffset);

		lowerArmPolyDef = new PolygonDef();
		lowerArmPolyDef.setAsBox(.05f, .11f);
		lowerArmPolyDef.density = limbDensity;
		lowerArmPolyDef.filter.groupIndex = filterGroup;

		leftLowerArm = w.createBody(lowerArmDef);
		leftLowerArm.createShape(lowerArmPolyDef);
		leftLowerArm.setMassFromShapes();

		elbowDef = new RevoluteJointDef();
		elbowDef.initialize(leftUpperArm, leftLowerArm, leftUpperArm
				.getPosition().add(elbowUpperArmOffset));
		elbowDef.lowerAngle = (float) (-Math.PI);
		elbowDef.upperAngle = (float) (Math.PI);
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
		upperLegPolyDef.setAsBox(.05f, .19f);
		upperLegPolyDef.density = limbDensity;
		upperLegPolyDef.filter.groupIndex = filterGroup;

		leftUpperLeg = w.createBody(upperLegDef);
		leftUpperLeg.createShape(upperLegPolyDef);
		leftUpperLeg.setMassFromShapes();

		hipDef = new RevoluteJointDef();
		hipDef.initialize(root, leftUpperLeg, hipTorsoOffset);
		hipDef.lowerAngle = (float) (-3 * Math.PI / 4.0);
		hipDef.upperAngle = (float) (3 * Math.PI / 4.0);
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
		lowerLegPolyDef.setAsBox(.05f, .16f);
		lowerLegPolyDef.density = limbDensity;
		lowerLegPolyDef.filter.groupIndex = filterGroup;

		leftLowerLeg = w.createBody(lowerLegDef);
		leftLowerLeg.createShape(lowerLegPolyDef);
		leftLowerLeg.setMassFromShapes();

		kneeDef = new RevoluteJointDef();
		kneeDef.initialize(leftUpperLeg, leftLowerLeg, leftUpperLeg
				.getPosition().add(kneeUpperLegOffset));
		kneeDef.lowerAngle = (float)(-.75f*Math.PI);
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
				leftArmMap));
		arms.add(new Bip9Limb(rightShoulder, rightLowerArm, armEEOffset, false,
				rightArmMap));

		legs.add(new Bip9Limb(leftHip, leftLowerLeg, legEEOffset, true,
				leftLegMap));
		legs.add(new Bip9Limb(rightHip, rightLowerLeg, legEEOffset, true,
				rightLegMap));

		// setGroupIndex(-1);
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

		Vec2 pos;
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

	public static Character makeCharacter(World w) {
		return new Biped9(w);
	}

	public static Character getStaticCharacter(World w) {
		Character ret = makeCharacter(w);
		for (Body b : ret.getBodies())
			b.setMass(new MassData());
		return ret;
	}

	public class Bip9Limb implements Limb {
		protected RevoluteJoint hip;
		protected Body endEffector;
		protected Vec2 eeOffset;
		protected boolean posAngle;

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
				boolean posAngle, HashMap<RevoluteJoint, Integer> jointMap) {
			this.hip = hip;
			this.endEffector = endEffector;
			this.eeOffset = eeOffset;
			this.posAngle = posAngle;
			this.jointMap = jointMap;

			l1 = hip.getAnchor2()
					.sub(PhysicsUtils.getChildJoint(hip).getAnchor1()).length();

			l2 = PhysicsUtils.getChildJoint(hip).m_localAnchor2.sub(eeOffset)
					.length();
		}

		@Override
		public RevoluteJoint getBase() {
			return hip;
		}

		@Override
		public void addGravityCompenstaionTorques(
				List<VirtualForce> virtualForces) {

			RevoluteJoint curr = hip;
			Body b;
			Vec2 zero = new Vec2(0f, 0f);
			while (curr != null) {
				b = curr.getBody2();
				virtualForces.add(new VirtualForce(hip, b, zero, world
						.getGravity().mul(-b.getMass())));
				curr = PhysicsUtils.getChildJoint(curr);
			}
		}

		private float maxLength = .999f;

		@Override
		public void setDesiredPose(Vec2 eepos, float[] desiredPose) {
			// time to get my IK on
			Vec2 relVec = eepos.sub(hip.getAnchor2());
			if (relVec.length() > (l1 + l2) * maxLength) {
				relVec.normalize();
				relVec.mulLocal((l1 + l2) * maxLength);
			}

			System.out.println("relvec" + relVec);
			
			float l3 = relVec.length();
			float det = Math.min(
					(l1 * l1 + l2 * l2 - l3 * l3) / (2 * l1 * l2), 1.0f);
			System.out.println("l1: " + l1 + " l2: " + l2 + " l3: " + l3 + " det: " + det);
			float kneeAngle = (float)(Math.acos(det) - Math.PI);
			System.out.println("knee angle: " + kneeAngle);
			
			float hipAngle = (float) Math.asin(l2 * Math.sin(kneeAngle) / l1);
			if (posAngle && kneeAngle < 0) {
				//kneeAngle *= -1;
				hipAngle *= -1;
			}
			System.out.println("hip angle: " + hipAngle);
			// now set the values:
			//relative angle of the hip global angle - parent angle
			float hipAngleParent = hipAngle;//(float) (Math.atan2(relVec.y, relVec.x)
					//- hip.getBody1().getAngle() + hipAngle);
			System.out.println("hipAngleParent:" + hipAngleParent + " kneeAngle: " + kneeAngle);
			desiredPose[jointMap.get(hip)] = hipAngleParent;
			desiredPose[jointMap.get(PhysicsUtils.getChildJoint(hip))] = kneeAngle;
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

}
