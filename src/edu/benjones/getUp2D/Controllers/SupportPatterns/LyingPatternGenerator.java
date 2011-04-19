package edu.benjones.getUp2D.Controllers.SupportPatterns;

import edu.benjones.getUp2D.Controllers.SupportPattern;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;

public class LyingPatternGenerator implements SupportPatternGenerator {

	@Override
	public SupportPattern getPattern() {
		SupportPattern sp = new SupportPattern();

		sp.addLimbStatus(supportLabel.leftArm, 1.0f, new supportInfo(
				limbStatus.swing, false, 0f));
		sp.addLimbStatus(supportLabel.leftArm, 2.0f, new supportInfo(
				limbStatus.stance, false, 0f));

		return sp;
	}

}
