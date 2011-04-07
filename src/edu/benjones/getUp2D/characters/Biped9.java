package edu.benjones.getUp2D.characters;

import java.util.ArrayList;
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

public class Biped9 implements edu.benjones.getUp2D.Character {

	protected Body root, leftUpperLeg, leftLowerLeg, rightUpperLeg,
			rightLowerLeg, leftUpperArm, leftLowerArm, rightUpperArm,
			rightLowerArm;

	protected RevoluteJoint leftHip, rightHip, leftKnee, rightKnee, leftShoulder,
			rightShoulder, leftElbow, rightElbow;

	protected final Vec2 shoulderTorsoOffset, hipTorsoOffset,
			shoulderUpperArmOffset, hipUpperLegOffset, kneeUpperLegOffset,
			kneeLowerLegOffset, elbowUpperArmOffset, elbowLowerArmOffset;

	private final float torsoDensity = 173;
	private final float limbDensity = 73;

	private final int filterGroup = -1;
	
	protected ArrayList<Body> bodies;
	protected ArrayList<RevoluteJoint> joints;

	public Biped9(World w) {

		shoulderTorsoOffset = new Vec2(.1f, .35f);
		hipTorsoOffset = new Vec2(0f, -.35f);
		shoulderUpperArmOffset = new Vec2(0f, -.12f);
		hipUpperLegOffset = new Vec2(0f, .15f);
		kneeUpperLegOffset = new Vec2(0f, -.15f);
		kneeLowerLegOffset = new Vec2(0f, .13f);
		elbowUpperArmOffset = new Vec2(0f, .12f);
		elbowLowerArmOffset = new Vec2(0f, -.09f);

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
		kneeDef.lowerAngle = 0f;
		kneeDef.upperAngle = (float) Math.PI;
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

		//setGroupIndex(-1);
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
		
		Vec2 zero = new Vec2(0,0);
		//set up the root stuff, then handle all the other bodies
		root.setXForm(position, orientation);
		root.setAngularVelocity(0f);
		root.setLinearVelocity(zero);
		
		Vec2 pos;
		float angle;
		Body parent, child;
		RevoluteJoint j;
		XForm xf = new XForm();
		for(int i = 0; i < joints.size(); ++i){
			j = joints.get(i);
			parent = j.getBody1(); 
			child = j.getBody2();
			angle = parent.getAngle() + relativeOrientations[i];
			xf.R = Mat22.createRotationalTransform(angle);
			position = j.getAnchor1().sub(
					XForm.mul(xf, j.m_localAnchor2));
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
	public int getStateSize(){
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

	public static Character makeCharacter(World w){
		return new Biped9(w);
	}
	
	public static Character getStaticCharacter(World w){
		Character ret = makeCharacter(w);
		for(Body b : ret.getBodies())
			b.setMass(new MassData());
		return ret;
	}
	
}
