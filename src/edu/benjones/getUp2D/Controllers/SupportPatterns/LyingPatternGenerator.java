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

		sp.setHipsVerticalKP(5f);
		sp.setHipsVerticalKD(2f);

		sp.setShouldersVerticalKP(5000f);
		sp.setShouldersVerticalKD(100f);

		sp.addLimbStatus(supportLabel.leftArm, 1.0f, new supportInfo(
				limbStatus.swing, false, 0.04f));
		sp.addLimbStatus(supportLabel.leftArm, 2.0f, new supportInfo(
				limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightArm, 2.2f, new supportInfo(
				limbStatus.swing, false, 0.04f));
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

		sp.addShoulderHeightKnot(new entry(3.5f, 0.05f));
		sp.addShoulderHeightKnot(new entry(6.0f, .4f));

		sp.addHipHeightKnot(new entry(0f, 0f));

		return sp;
	}

}
