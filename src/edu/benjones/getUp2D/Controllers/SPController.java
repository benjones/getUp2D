package edu.benjones.getUp2D.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.Character.Limb;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;
import edu.benjones.getUp2D.Controllers.SupportPatterns.SupportPatternGenerator;

public class SPController extends PoseController {

	private SupportPattern sp;
	private SupportLimb[] limbs;

	private ArrayList<ControlParam> originalControlParams;

	// scale "idle" limb torques by this much
	private float idleModifier = .1f;

	public SPController(Character ch, SupportPatternGenerator g) {
		super(ch);
		sp = g.getPattern();
		List<Limb> arms = character.getArms();
		List<Limb> legs = character.getLegs();
		limbs = new SupportLimb[SupportPattern.supportLabel.values().length];
		limbs[supportLabel.leftArm.ordinal()] = new SupportLimb(arms.get(0));
		limbs[supportLabel.rightArm.ordinal()] = new SupportLimb(arms.get(1));
		limbs[supportLabel.leftLeg.ordinal()] = new SupportLimb(legs.get(0));
		limbs[supportLabel.rightLeg.ordinal()] = new SupportLimb(legs.get(1));

		// copy control params to modify them per step
		originalControlParams = new ArrayList<ControlParam>(
				controlParams.size());
		for (ControlParam cp : controlParams) {
			originalControlParams.add(new ControlParam(cp));
		}
	}

	@Override
	public void computeTorques(World w, float dt) {
		boolean advance = true;
		supportInfo now, later;
		for (supportLabel limb : supportLabel.values()) {
			limbs[limb.ordinal()].updateContactInfo(dt);
			now = sp.getInfoNow(limb);
			later = sp.getInfoAtTime(limb, sp.getPhase() + dt);
			if (now.ls != later.ls) {
				if (later.ls == limbStatus.stance
						&& !limbs[limb.ordinal()].canSupport()) {
					advance = false;
				}
				if (now.ls == limbStatus.stance
						&& !limbs[limb.ordinal()].canRemoveSupport(dt)) {
					advance = false;
				}
			}
		}
		if (advance)
			sp.advancePhase(dt);

		for (supportLabel limb : supportLabel.values()) {
			limbs[limb.ordinal()].setPose(sp.getPattern(limb), sp.getPhase(),
					desiredPose);
		}
		// modify torques
		for (int i = 0; i < controlParams.size(); ++i) {
			controlParams.get(i).setFrom(originalControlParams.get(i));
		}
		for (supportLabel limb : supportLabel.values()) {
			if (sp.getInfoNow(limb).ls == limbStatus.idle) {
				limbs[limb.ordinal()].scaleGains(idleModifier, controlParams);
			}
		}

		super.computeTorques(w, dt);

		// now VF feedback stuff
	}

	@Override
	public void drawControllerExtras(DebugDraw g) {
		super.drawControllerExtras(g);

		for (SupportLimb limb : limbs)
			limb.draw(g);

		sp.draw(g);

	}

	@Override
	public void reset() {
		sp.reset();
	}
}
