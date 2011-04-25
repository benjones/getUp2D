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

		sp.setHipsVerticalKP(900f);
		sp.setHipsVerticalKD(80f);

		sp.setShouldersVerticalKP(500f);
		sp.setShouldersVerticalKD(50f);

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
				limbStatus.swing, false, 0.2f));
		sp.addLimbStatus(supportLabel.leftLeg, 12.0f, new supportInfo(
				limbStatus.stance, false, 0f));

		sp.addShoulderHeightKnot(new entry(3.5f, 0.05f));
		sp.addShoulderHeightKnot(new entry(6.0f, .2f));
		sp.addShoulderHeightKnot(new entry(8.0f, .4f));
		sp.addShoulderHeightKnot(new entry(9.0f, .55f));

		sp.addHipHeightKnot(new entry(3.5f, 0.05f));
		sp.addHipHeightKnot(new entry(7.0f, .35f));
		sp.addHipHeightKnot(new entry(9.0f, .45f));

		return sp;
	}

}
