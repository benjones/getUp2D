package edu.benjones.getUp2D.Controllers.SupportPatterns;

import edu.benjones.getUp2D.Controllers.SupportPattern;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;
import edu.benjones.getUp2D.Utils.Trajectory1D.entry;

public class LyingPatternGenerator implements SupportPatternGenerator {

	@Override
	public SupportPattern getPattern() {
		SupportPattern sp = new SupportPattern();

		sp.setIdleModifier(.1f);

		sp.setHipsVerticalKP(1400f);
		sp.setHipsVerticalKD(100f);

		sp.setShouldersVerticalKP(700f);
		sp.setShouldersVerticalKD(50f);

		sp.setSagittalKP(100);
		sp.setSagittalKD(50);

		sp.setRootKP(500f);
		sp.setRootKD(50f);

		sp.setMaxRootCorrectionTorque(30f);

		sp.addLimbStatus(supportLabel.leftArm, 1.0f, new supportInfo(
				limbStatus.swing, false, 0.15f));
		sp.addLimbStatus(supportLabel.leftArm, 2.0f, new supportInfo(
				limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightArm, 2.2f, new supportInfo(
				limbStatus.swing, false, 0.1f));
		sp.addLimbStatus(supportLabel.rightArm, 3.2f, new supportInfo(
				limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightLeg, 1.5f, new supportInfo(
				limbStatus.swing, true, -.1f));
		sp.addLimbStatus(supportLabel.rightLeg, 1.7f, new supportInfo(
				limbStatus.stance, true, 0f));

		sp.addLimbStatus(supportLabel.leftLeg, 1.8f, new supportInfo(
				limbStatus.swing, true, -.1f));
		sp.addLimbStatus(supportLabel.leftLeg, 2.0f, new supportInfo(
				limbStatus.stance, true, 0f));

		sp.addLimbStatus(supportLabel.rightArm, 6.2f, new supportInfo(
				limbStatus.swing, false, 0f));
		sp.addLimbStatus(supportLabel.rightArm, 7.2f, new supportInfo(
				limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.leftArm, 7.5f, new supportInfo(
				limbStatus.swing, false, 0f));
		sp.addLimbStatus(supportLabel.leftArm, 8.5f, new supportInfo(
				limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.leftLeg, 9.5f, new supportInfo(
				limbStatus.swing, false, 0.3f));
		sp.addLimbStatus(supportLabel.leftLeg, 11.0f, new supportInfo(
				limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightLeg, 11.1f, new supportInfo(
				limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.leftArm, 11.2f, new supportInfo(
				limbStatus.idle, false, 0f));

		sp.addLimbStatus(supportLabel.rightArm, 11.2f, new supportInfo(
				limbStatus.idle, false, 0f));

		sp.addShoulderHeightKnot(new entry(3.5f, 0.05f));
		sp.addShoulderHeightKnot(new entry(5.0f, .25f));
		sp.addShoulderHeightKnot(new entry(7.0f, .5f));
		sp.addShoulderHeightKnot(new entry(9.0f, .55f));
		sp.addShoulderHeightKnot(new entry(10.8f, .60f));
		sp.addShoulderHeightKnot(new entry(11.7f, 1.5f));
		sp.addShoulderHeightKnot(new entry(16.5f, 1.5f));

		sp.addHipHeightKnot(new entry(3.5f, 0.05f));
		sp.addHipHeightKnot(new entry(7.0f, .35f));
		sp.addHipHeightKnot(new entry(9.0f, .35f));
		sp.addHipHeightKnot(new entry(10.5f, .45f));
		sp.addHipHeightKnot(new entry(14.0f, .65f));
		sp.addHipHeightKnot(new entry(16.0f, .55f));

		return sp;
	}
}
