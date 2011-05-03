package edu.benjones.getUp2D.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.Character.Limb;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;
import edu.benjones.getUp2D.Controllers.SupportPatterns.ParameterizedSupportPatternGenerator;
import edu.benjones.getUp2D.Controllers.SupportPatterns.SupportPatternGenerator;
import edu.benjones.getUp2D.Utils.MathUtils;
import edu.benjones.getUp2D.Utils.PhysicsUtils;

public class SPController extends PoseController {

	private SupportPattern sp;
	private SupportLimb[] limbs;

	private ArrayList<ControlParam> originalControlParams;

	private ArrayList<VirtualForce> virtualForces;

	private ArrayList<SupportLimb> supportArms, supportLegs;

	private float desiredXPosition;

	private final float maxTorque = 80f;

	public SPController(Character ch, SupportPatternGenerator g) {
		super(ch);
		sp = g.getPattern();
		controllerSetup();
	}

	public SPController(Character ch, ParameterizedSupportPatternGenerator g,
			float[] parameters) {
		super(ch);
		sp = g.getPattern(parameters);
		controllerSetup();
	}

	private void controllerSetup() {
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
		// if (supportLegs.get(0).canAndShouldSupport()
		// || supportLegs.get(1).canAndShouldSupport()) {
		// List<Limb> legs = character.getLegs();
		ArrayList<Limb> simbiconLimbs = new ArrayList<Limb>();
		for (SupportLimb sl : limbs) {
			if (sl.canAndShouldSupport()) {
				simbiconLimbs.add(sl.limb);
			}
		}
		if (simbiconLimbs.size() > 0) {
			float tl = character.getTorsoLength();
			float dy = Math.max(Math.min(
					sp.getShoulderHeightNow() - sp.getHipHeightNow(),
					tl * .9999f), -tl * .9999f);

			float desAngle = (float) (Math.asin(dy / tl) - Math.PI / 2);

			// torque the root WANTS to see
			float desRootTorque = sp.getRootKP()
					* (desAngle - character.getRoot().getAngle())
					- sp.getRootKD() * character.getRoot().getAngularVelocity();

			// torso feels -torques from arms
			float armTorque = 0;
			for (Limb arm : character.getArms()) {
				armTorque -= torques[arm.getJointMap().get(arm.getBase())];
			}

			float legTorque = 0;
			for (Limb leg : character.getLegs()) {
				legTorque -= torques[leg.getJointMap().get(leg.getBase())];
			}

			// this is the difference between what torque the root gets vs
			// what it wants to get
			float rootTorqueError = desRootTorque - armTorque - legTorque;

			rootTorqueError = Math.max(
					-simbiconLimbs.size() * sp.getMaxRootCorrectionTorque(),
					Math.min(
							simbiconLimbs.size()
									* sp.getMaxRootCorrectionTorque(),
							rootTorqueError));
			for (Limb l : simbiconLimbs) {
				torques[l.getJointMap().get(l.getBase())] -= rootTorqueError
						/ simbiconLimbs.size();
			}
		}

		// now VF feedback stuff
		for (supportLabel limb : supportLabel.values()) {
			if (sp.getInfoNow(limb).ls == limbStatus.swing) {
				limbs[limb.ordinal()]
						.addGravityCompensationTorques(virtualForces);
			}
		}

		legFrameHeightCorrection(supportArms, false);
		legFrameHeightCorrection(supportLegs, true);

		// now for a sagittal control
		desiredXPosition = 0;
		float weightSum = 0;
		for (supportLabel limb : supportLabel.values()) {
			if (limbs[limb.ordinal()].canAndShouldSupport()) {
				float weight = Math.min(sp.getTimeToLift(limb), .5f);
				weightSum += weight;
				// desiredXPosition += weight * limbs[limb.ordinal()].ikTarg.x;
				// use the COM of the end effector as the indicator
				desiredXPosition += weight
						* PhysicsUtils
								.getChildJoint(
										limbs[limb.ordinal()].limb.getBase())
								.getBody2().getPosition().x;
			}
		}
		if (weightSum > 0) {
			desiredXPosition /= weightSum;

			// we'll multiply the whole desired force by weighted sum, so
			// it'll count for more when more limbs are planted
			float sagErr = desiredXPosition
					- character.getRoot().getPosition().x;
			float sagVErr = character.getRoot().getLinearVelocity().x;

			float totalForce = sp.getSagittalKP() * sagErr - sagVErr
					* sp.getSagittalKD() * weightSum;

			for (supportLabel limb : supportLabel.values()) {
				if (limbs[limb.ordinal()].canAndShouldSupport()) {
					limbs[limb.ordinal()].addForceOnFoot(new Vec2(-totalForce
							* Math.min(sp.getTimeToLift(limb), .5f), 0f),
							virtualForces);
				}
			}
		}

		for (VirtualForce v : virtualForces) {
			v.apply(character.getJointMap(), torques);
		}

		for (int i = 0; i < torques.length; ++i) {

			torques[i] = MathUtils.clamp(torques[i], maxTorque);

		}
		super.applyTorques();
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

		float tl = character.getTorsoLength();
		float dy = sp.getShoulderHeightNow() - sp.getHipHeightNow();
		float dx = (float) Math.sqrt(tl * tl
				- Math.pow(MathUtils.clamp(dy, tl * .9999f), 2));
		Vec2 desAngleStart = character.getRoot().getPosition()
				.add(new Vec2(0f, 1f));
		Vec2 desAngle = new Vec2(dx, dy);
		desAngle.normalize();
		g.drawSegment(desAngleStart, desAngleStart.add(desAngle), Color3f.WHITE);

		g.drawCircle(new Vec2(desiredXPosition, -.04f), .03f, Color3f.WHITE);
	}

	@Override
	public void reset() {
		sp.reset();
	}

	@Override
	public float getEndTime() {
		return sp.getMaxFiniteTime() + 3.0f;
	}
}
