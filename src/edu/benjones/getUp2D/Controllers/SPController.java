package edu.benjones.getUp2D.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;
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

	private ArrayList<VirtualForce> virtualForces;

	private ArrayList<SupportLimb> supportArms, supportLegs;

	public SPController(Character ch, SupportPatternGenerator g) {
		super(ch);
		sp = g.getPattern();
		List<Limb> arms = character.getArms();
		List<Limb> legs = character.getLegs();
		limbs = new SupportLimb[SupportPattern.supportLabel.values().length];
		limbs[supportLabel.leftArm.ordinal()] = new SupportLimb(arms.get(0),
				sp, supportLabel.leftArm);
		limbs[supportLabel.rightArm.ordinal()] = new SupportLimb(arms.get(1),
				sp, supportLabel.rightArm);
		limbs[supportLabel.leftLeg.ordinal()] = new SupportLimb(legs.get(0),
				sp, supportLabel.leftLeg);
		limbs[supportLabel.rightLeg.ordinal()] = new SupportLimb(legs.get(1),
				sp, supportLabel.rightLeg);

		supportArms = new ArrayList<SupportLimb>(2);
		supportLegs = new ArrayList<SupportLimb>(2);
		supportArms.add(limbs[supportLabel.leftArm.ordinal()]);
		supportArms.add(limbs[supportLabel.rightArm.ordinal()]);
		supportLegs.add(limbs[supportLabel.leftLeg.ordinal()]);
		supportLegs.add(limbs[supportLabel.rightLeg.ordinal()]);

		// copy control params to modify them per step
		originalControlParams = new ArrayList<ControlParam>(
				controlParams.size());
		for (ControlParam cp : controlParams) {
			originalControlParams.add(new ControlParam(cp));

		}
		virtualForces = new ArrayList<VirtualForce>();
	}

	@Override
	public void computeTorques(World w, float dt) {
		virtualForces.clear();
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
				limbs[limb.ordinal()].scaleGains(sp.getIdleModifier(),
						controlParams);
			}
		}

		super.computeTorquesOnly(w, dt);

		// simbicon style feedback
		if (supportLegs.get(0).canAndShouldSupport()
				|| supportLegs.get(1).canAndShouldSupport()) {

			float dx = character.getTorsoLength();
			float dy = sp.getShoulderHeightNow() - sp.getHipHeightNow();

			float desAngle = (float) (Math.atan2(dy, dx) - Math.PI / 2);

			// torque the root WANTS to see
			float desRootTorque = sp.getRootKP()
					* (desAngle - character.getRoot().getAngle())
					- sp.getRootKD() * character.getRoot().getAngularVelocity();

			// torso feels -torques from arms
			float armTorque = 0;
			for (Limb arm : character.getArms()) {
				armTorque -= torques[arm.getJointMap().get(arm.getBase())];
			}

			float hipTorque = armTorque - desRootTorque;

			List<Limb> legs = character.getLegs();
			if (supportLegs.get(0).canAndShouldSupport()) {
				if (supportLegs.get(1).canAndShouldSupport()) {
					torques[legs.get(0).getJointMap()
							.get(legs.get(0).getBase())] = hipTorque * .5f;
					torques[legs.get(1).getJointMap()
							.get(legs.get(1).getBase())] = hipTorque * .5f;
				} else {
					torques[legs.get(0).getJointMap()
							.get(legs.get(0).getBase())] = hipTorque;
				}
			} else {
				torques[legs.get(1).getJointMap().get(legs.get(1).getBase())] = hipTorque;
			}

		}

		super.applyTorques();

		// now VF feedback stuff
		for (supportLabel limb : supportLabel.values()) {
			if (sp.getInfoNow(limb).ls == limbStatus.swing) {
				limbs[limb.ordinal()]
						.addGravityCompensationTorques(virtualForces);
			}
		}

		legFrameHeightCorrection(supportArms, false);
		legFrameHeightCorrection(supportLegs, true);

		// same for hips

		for (VirtualForce v : virtualForces) {
			v.apply();
		}
	}

	/**
	 * 
	 * @param legFrame
	 *            a list of 2 supportLimbs that are part of the leg frame
	 * @param hips
	 *            true if it's the hips, false if it's the shoulders
	 */

	private void legFrameHeightCorrection(List<SupportLimb> legFrame,
			boolean hips) {
		// control shoulder height
		if (legFrame.get(0).canAndShouldSupport()
				|| legFrame.get(1).canAndShouldSupport()) {

			float error;
			float vError;
			float yForce;
			if (hips) {
				error = sp.getHipHeightNow()
						- legFrame.get(0).getShoulderPosition().y;
				vError = character
						.getRoot()
						.getLinearVelocityFromWorldPoint(
								legFrame.get(0).getShoulderPosition()).length();
				yForce = Math.max(
						error * sp.getHipsVerticalKP() - vError
								* sp.getHipsVerticalKD(), 0f);
			} else {
				error = sp.getShoulderHeightNow()
						- legFrame.get(0).getShoulderPosition().y;
				vError = character
						.getRoot()
						.getLinearVelocityFromWorldPoint(
								legFrame.get(0).getShoulderPosition()).length();
				yForce = Math.max(error * sp.getShouldersVerticalKP() - vError
						* sp.getShouldersVerticalKD(), 0f);
			}

			if (legFrame.get(0).canAndShouldSupport()) {
				if (legFrame.get(1).canAndShouldSupport()) {
					// scale based on which one is supposed to lift first.
					// only look 1s into the future
					float lLift = Math.min(
							sp.getTimeToLift(legFrame.get(0).limbLabel), 1f);
					float rLift = Math.min(
							sp.getTimeToLift(legFrame.get(1).limbLabel), 1f);
					Vec2 f = new Vec2(0f, -yForce);

					float lScale;// what fraction of force should the left arm
									// do
					// right arm = 1-lScale.
					if (lLift > 0) {
						if (rLift > 0) {
							lScale = lLift / (rLift + lLift);
						} else {
							lScale = 1.0f;
						}
					} else {
						lScale = 0f;
					}

					legFrame.get(1).addForceOnFoot(f.mul(1 - lScale),
							virtualForces);
					legFrame.get(0)
							.addForceOnFoot(f.mul(lScale), virtualForces);
				} else {
					// left arm only
					legFrame.get(0).addForceOnFoot(new Vec2(0f, -yForce),
							virtualForces);
				}
			} else {
				// right arm only. Must be, since at least one is can/should
				// support
				legFrame.get(1).addForceOnFoot(new Vec2(0f, -yForce),
						virtualForces);
			}

		}
	}

	@Override
	public void drawControllerExtras(DebugDraw g) {
		super.drawControllerExtras(g);

		for (SupportLimb limb : limbs)
			limb.draw(g);

		sp.draw(g);
		for (VirtualForce v : virtualForces) {
			v.draw(g);
		}

	}

	@Override
	public void reset() {
		sp.reset();
	}
}
