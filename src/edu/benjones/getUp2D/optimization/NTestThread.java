package edu.benjones.getUp2D.optimization;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.Controllers.SPController;
import edu.benjones.getUp2D.Controllers.SupportPatterns.WarpedLyingPatternGenerator;
import edu.benjones.getUp2D.characters.ParameterizedBiped9;

public class NTestThread extends AbstractOptimizationThread {

	protected float rootScale;

	public float getRootScale() {
		return rootScale;
	}

	public void setRootScale(float rootScale) {
		this.rootScale = rootScale;
	}

	public NTestThread(DebugDraw g) {
		super(g);
		rootScale = 1.0f;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		float time = 0;
		float successTime = Float.POSITIVE_INFINITY;
		if (character != null)
			character.destroy();

		setupCharacter();
		controller = new SPController(character,
				new WarpedLyingPatternGenerator(), initialParameters);

		while (time < controller.getEndTime()) {
			physicsStep();
			time += timestep;
			shoulderHeight = character.getArms().get(0).getBase().getAnchor2().y;
			if (successTime == Float.POSITIVE_INFINITY
					&& shoulderHeight > heightThreshold) {
				successTime = time;
			}
		}

		if (shoulderHeight < heightThreshold) {
			successTime = Float.POSITIVE_INFINITY;
		}
		cost = successTime;
		// System.out.println("I'm done");
	}

	@Override
	public void setupController() {
	}

	@Override
	public void setupCharacter() {
		character = new ParameterizedBiped9(world, this, rootScale);
		float[] zeros = new float[character.getStateSize()];
		character.setState(new Vec2(originalPosition.x, originalPosition.y),
				originalPosition.angle, zeros);
	}

}
