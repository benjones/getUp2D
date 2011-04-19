package edu.benjones.getUp2D.Controllers;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.Character.Limb;

public class IKTestController extends PoseController {

	protected Vec2 ikPosition, handPosition;
	private float elapsed;

	public IKTestController(Character ch) {
		super(ch);
		ikPosition = new Vec2(0, 0);
		handPosition = new Vec2(0, 0);
		elapsed = 0;
	}

	public void computeTorques(World w, float dt) {
		elapsed += dt;
		// do Ik control
		Limb leg = character.getLegs().get(0);
		Vec2 hipPos = leg.getBase().getAnchor2();
		ikPosition.set((float) (hipPos.x - .5 * Math.abs(Math.sin(elapsed))),
				(float) (hipPos.y - .5 * Math.abs(Math.sin(.3 * elapsed))));
		leg.setDesiredPose(ikPosition, desiredPose);

		Limb arm = character.getArms().get(0);
		Vec2 armPos = arm.getBase().getAnchor2();
		handPosition.set(
				(float) (armPos.x + .35 * Math.abs(Math.sin(elapsed))),
				(float) (armPos.y + .35 * Math.abs(Math.sin(.3 * elapsed))));
		arm.setDesiredPose(handPosition, desiredPose);

		super.computeTorques(w, dt);
	}

	@Override
	public void drawControllerExtras(DebugDraw g) {
		super.drawControllerExtras(g);
		g.drawCircle(ikPosition, .02f, Color3f.BLUE);
		g.drawCircle(ikPosition.add(new Vec2(-1f, 1f)), .02f, Color3f.BLUE);

		g.drawCircle(handPosition, .02f, Color3f.BLUE);
		g.drawCircle(handPosition.add(new Vec2(-1f, 1f)), .02f, Color3f.BLUE);

		// draw desired IK positions
	}

}
