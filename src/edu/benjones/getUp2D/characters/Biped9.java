package edu.benjones.getUp2D.characters;

import java.util.ArrayList;

import org.jbox2d.collision.FilterData;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJoint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

public class Biped9 implements edu.benjones.getUp2D.Character {

	private Body root, leftUpperLeg, leftLowerLeg, rightUpperLeg,
			rightLowerLeg, leftUpperArm, leftLowerArm, rightUpperArm,
			rightLowerArm;

	private Joint leftHip, rightHip, leftKnee, rightKnee, leftShoulder,
			rightShoulder, leftElbow, rightElbow;

	private final Vec2 shoulderTorsoOffset, hipTorsoOffset,
			shoulderUpperArmOffset, hipUpperLegOffset, kneeUpperLegOffset,
			kneeLowerLegOffset, elbowUpperArmOffset, elbowLowerArmOffset;

	private final float torsoDensity = 173;
	private final float limbDensity = 73;

	private ArrayList<Body> bodies;
	private ArrayList<Joint> joints;

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

		leftLowerArm = w.createBody(lowerArmDef);
		leftLowerArm.createShape(lowerArmPolyDef);
		leftLowerArm.setMassFromShapes();

		elbowDef = new RevoluteJointDef();
		elbowDef.initialize(leftUpperArm, leftLowerArm, leftUpperArm
				.getPosition().add(elbowUpperArmOffset));
		elbowDef.lowerAngle = (float) (-5 * Math.PI / 180.0);
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

		joints = new ArrayList<Joint>();
		joints.add(leftShoulder);
		joints.add(rightShoulder);
		joints.add(leftElbow);
		joints.add(rightElbow);
		joints.add(leftHip);
		joints.add(rightHip);
		joints.add(leftKnee);
		joints.add(rightKnee);

		setGroupIndex(-1);
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
		
		//set up the root stuff, then handle all the other bodies
		root.setXForm(position, orientation);
		Vec2 pos;
		float angle;
		Body parent;
		for(int i = 0; i < joints.size(); ++i){
			parent = joints.get(i).getBody1(); 
			angle = parent.getAngle() + relativeOrientations[i];
			
			// position = parent.getPosition().add(joints.get(i).)
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

}
