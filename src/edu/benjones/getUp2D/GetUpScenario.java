package edu.benjones.getUp2D;

import java.util.HashMap;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.ContactID;
import org.jbox2d.collision.Segment;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.RaycastResult;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;

import edu.benjones.getUp2D.Controllers.Controller;
import edu.benjones.getUp2D.Controllers.PoseController;
import edu.benjones.getUp2D.Controllers.SPController;
import edu.benjones.getUp2D.Controllers.SupportPatterns.LyingPatternGenerator;
import edu.benjones.getUp2D.characters.Biped9;

public class GetUpScenario {

	protected static HashMap<ContactID, ContactResult> contactMap;

	public static final class defaultCameraParams {
		public final static float x = 0f;
		public final static float y = 1f;
		public final static float scale = 150f;
	}

	protected static class OriginalPosition {
		public final float x;
		public final float y;
		public final float angle;

		public OriginalPosition(float x, float y, float angle) {
			this.x = x;
			this.y = y;
			this.angle = angle;
		}
	}

	protected OriginalPosition originalPosition;

	public DebugDraw debugDraw;

	protected static World world;
	protected Character character, desiredPoseCharacter;
	protected Controller controller;
	private AABB worldAABB;
	// create()

	public static final float framerate = 60;
	public static final int iterationCount = 10;

	private boolean drawContactPoints;

	private boolean drawDesiredPose;
	private boolean drawControllerExtras;
	private boolean desiredPoseSetup;

	private static Body ground;

	public GetUpScenario(DebugDraw g) {
		setupInitialPosition();
		if (contactMap == null)
			contactMap = new HashMap<ContactID, ContactResult>();
		else {
			System.out
					.println("WARNING: contactMap already exists.  Hope you know what you're doing");
		}

		drawDesiredPose = true;// false;
		desiredPoseSetup = false;
		drawControllerExtras = true;
		debugDraw = g;
		createWorld();
		setupCharacter();
		setupController();
	}

	public void setupInitialPosition() {
		originalPosition = new OriginalPosition(0f, 1f, (float) -(Math.PI / 2));
	}

	public void createWorld() {
		worldAABB = new AABB();
		worldAABB.lowerBound.set(-100, -100f);
		worldAABB.upperBound.set(100, 100f);
		Vec2 gravity = new Vec2(0f, -10f);
		// skip sleeping, since there shouldn't be anything sleeping
		if (world != null)
			System.out.println("WARNING: World not null in createWorld");
		world = new World(worldAABB, gravity, false);
		world.setDebugDraw(debugDraw);
		setDrawContactPoints(false);

		debugDraw.setCamera(defaultCameraParams.x, defaultCameraParams.y,
				defaultCameraParams.scale);

		// setup ground
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(50f, 5f);

		BodyDef bd = new BodyDef();
		bd.position.set(0f, -5f);
		if (ground != null) {
			System.out.println("WARNING: Ground is not null in createWorld");
		}
		ground = world.createBody(bd);
		ground.createShape(sd);

		// setup contactListener
		world.setContactListener(new ContactListener() {
			public void add(ContactPoint point) {
			}

			public void persist(ContactPoint point) {
			}

			public void remove(ContactPoint point) {
			}

			public void result(ContactResult point) {
				contactMap.put(point.id, point);
			}
		});

	}

	public void setupCharacter() {
		character = new Biped9(world);
		float[] zeros = new float[character.getStateSize()];
		character.setState(new Vec2(originalPosition.x, originalPosition.y),
				originalPosition.angle, zeros);
	}

	public void setupController() {
		// controller = new IKTestController(character);
		controller = new SPController(character, new LyingPatternGenerator());
		// controller = new PoseController(character);
	}

	public void setupDesiredPoseCharacter() {
		desiredPoseCharacter = Biped9.getStaticCharacter(world);
		desiredPoseSetup = true;
	}

	public void step() {

		debugDraw.setFlags(0);
		// debugDraw.appendFlags(DebugDraw.e_aabbBit);
		// debugDraw.appendFlags(DebugDraw.e_coreShapeBit);
		debugDraw.appendFlags(DebugDraw.e_shapeBit);
		debugDraw.appendFlags(DebugDraw.e_jointBit);

		// rolling my own clear forces:
		for (Body b = world.getBodyList(); b != null; b = b.getNext()) {
			b.m_force.setZero();
			b.m_torque = 0f;
		}
		float timestep = (float) (1.0 / framerate) * .25f;
		// drawing gets done here I guess?
		controller.computeTorques(world, timestep);

		// empty it, the refill it next frame
		contactMap.clear();

		if (drawDesiredPose) {
			if (!desiredPoseSetup)
				setupDesiredPoseCharacter();
			float[] desiredPose =
			/*
			 * new float[desiredPoseCharacter.getStateSize()]; for (int i = 0; i
			 * < desiredPose.length; ++i) { desiredPose[i] = 0f; }
			 */
			((PoseController) controller).getDesiredPose();

			desiredPoseCharacter.setState(character.getRoot().getPosition()
					.add(new Vec2(-1f, 1f)), character.getRoot().getAngle(),
					desiredPose);
		}

		if (drawControllerExtras) {
			controller.drawControllerExtras(debugDraw);
		}
		// debugDraw.drawString(5, 12, "test", new Color3f(255f, 255f, 255f));
		world.step(timestep, iterationCount);
	}

	public void setDrawContactPoints(boolean drawContactPoints) {
		this.drawContactPoints = drawContactPoints;
	}

	public boolean getDrawContactPoints() {
		return drawContactPoints;
	}

	public void setDrawDesiredPose(boolean drawDesiredPose) {
		this.drawDesiredPose = drawDesiredPose;
	}

	public boolean getDrawDesiredPose() {
		return drawDesiredPose;
	}

	public void setDrawControllerExtras(boolean drawControllerExtras) {
		this.drawControllerExtras = drawControllerExtras;
	}

	public boolean isDrawControllerExtras() {
		return drawControllerExtras;
	}

	public static HashMap<ContactID, ContactResult> getContactMap() {
		return contactMap;
	}

	/**
	 * returns the height of the ground body at the specified x coordinate
	 * 
	 * @param x
	 * @return height at x=x
	 */
	private static Segment testRay;
	private static RaycastResult testRayResult;

	// TODO TEST THIS METHOD!!!!
	public static float getGroundHeightAt(float x) {
		if (testRay == null) {
			testRay = new Segment();
			testRay.p1.y = 1000f;
			testRay.p2.y = -1000f;
			testRayResult = new RaycastResult();
		}
		testRay.p1.x = x;
		testRay.p2.x = x;

		Float ret = null;
		for (Shape s = ground.getShapeList(); s != null; s = s.getNext()) {
			s.testSegment(ground.getXForm(), testRayResult, testRay, 1f);
			float y = (1 - testRayResult.lambda) * testRay.p1.y
					+ testRayResult.lambda * testRay.p2.y;
			if (ret == null || y > ret) {
				ret = y;
			}
		}
		return ret;

	}

	public void reset() {
		float[] zeros = new float[character.getStateSize()];
		character.setState(new Vec2(originalPosition.x, originalPosition.y),
				originalPosition.angle, zeros);
		controller.reset();
	}

}
