package edu.benjones.getUp2D.optimization;

import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.Controllers.SPController;
import edu.benjones.getUp2D.Controllers.SupportPatterns.WarpedLyingPatternGenerator;

public class NTestThread extends AbstractOptimizationThread {

	public NTestThread(DebugDraw g) {
		super(g);
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

}
