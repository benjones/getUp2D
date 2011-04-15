package edu.benjones.getUp2D.Controllers;

import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.Controllers.SupportPatterns.SupportPatternGenerator;

public class SPController extends PoseController {

	private SupportPattern sp;

	public SPController(Character ch, SupportPatternGenerator g) {
		super(ch);

		sp = g.getPattern();
	}

	@Override
	public void computeTorques(World w, float dt) {
		sp.advancePhase(dt);
		super.computeTorques(w, dt);
	}

	@Override
	public void drawControllerExtras(DebugDraw g) {
		super.drawControllerExtras(g);
		sp.draw(g);
	}
}
