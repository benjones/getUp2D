package edu.benjones.getUp2D;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.PolygonDef;
import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Controllers.Controller;
import edu.benjones.getUp2D.Controllers.PoseController;
import edu.benjones.getUp2D.characters.Biped9;

public class GetUpScenario {
	public DebugDraw debugDraw;

	protected World world;
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

	public GetUpScenario(DebugDraw g) {
		drawDesiredPose = true;//false;
		desiredPoseSetup = false;
		drawControllerExtras = true;
		debugDraw = g;
		createWorld();
		setupCharacter();
		setupController();
	}

	public void createWorld() {
		worldAABB = new AABB();
		worldAABB.lowerBound.set(-100, -100f);
		worldAABB.upperBound.set(100, 100f);
		Vec2 gravity = new Vec2(0f, -10f);
		// skip sleeping, since there shouldn't be anything sleeping
		world = new World(worldAABB, gravity, false);
		world.setDebugDraw(debugDraw);
		setDrawContactPoints(false);

		debugDraw.setCamera(0f, 0f, 150f);

		// setup ground
		PolygonDef sd = new PolygonDef();
		sd.setAsBox(50f, 3f);

		BodyDef bd = new BodyDef();
		bd.position.set(0f, -5f);
		Body ground = world.createBody(bd);
		ground.createShape(sd);

	}

	public void setupCharacter() {
		character = new Biped9(world);
		float[] zeros = new float[character.getStateSize()];
		character.setState(new Vec2(0f, 0f), (float) (-Math.PI / 4), zeros);
	}

	public void setupController() {
		controller = new PoseController(character);
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

		//rolling my own clear forces:
		for(Body b = world.getBodyList(); b != null; b = b.getNext()){
			b.m_force.setZero();
			b.m_torque = 0f;
		}
		float timestep = (float) (1.0 / framerate)*.25f;
		//drawing gets done here I guess?
		controller.computeTorques(world,timestep);
		world.step(timestep, iterationCount);

		if (drawDesiredPose) {
			if (!desiredPoseSetup)
				setupDesiredPoseCharacter();
			float [] desiredPose = ((PoseController)controller).getDesiredPose();
			desiredPoseCharacter.setState(character.getRoot().getPosition()
					.add(new Vec2(-.5f, .5f)), character.getRoot().getAngle(),
					desiredPose);
		}

		if(drawControllerExtras){
			controller.drawControllerExtras(debugDraw);
		}
		// debugDraw.drawString(5, 12, "test", new Color3f(255f, 255f, 255f));

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

}
