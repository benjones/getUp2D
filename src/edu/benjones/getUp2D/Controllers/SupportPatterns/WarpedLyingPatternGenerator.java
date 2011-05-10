package edu.benjones.getUp2D.Controllers.SupportPatterns;

import edu.benjones.getUp2D.Controllers.SupportPattern;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;
import edu.benjones.getUp2D.Controllers.SupportPatterns.ParameterizedLyingGenerator.lyingParamOffsets;
import edu.benjones.getUp2D.Utils.Trajectory1D.entry;

public class WarpedLyingPatternGenerator implements
		ParameterizedSupportPatternGenerator {
	public enum warpedLyingParamOffsets {
		idleModifier,
		hipsVerticalKP,
		hipsVerticalKD,
		shouldersVerticalKP,
		shouldersVerticalKD,
		sagittalKP,
		sagittalKD,
		rootKP,
		rootKD,
		maxRootCorrection,
		laTime1,
		laOffset1,
		laTime2,
		raTime1,
		raOffset1,
		raTime2,
		rlTime1,
		llTime1,
		raTime3,
		raOffset2,
		raTime4,
		laTime3,
		laOffset2,
		laTime4,
		llTime2,
		llOffset1,
		llTime3,
		rlTime2,
		laTime5,
		raTime5,
		shoulderT1,
		shoulderT2,
		shoulderT3,
		shoulderT4,
		shoulderT5,
		shoulderT6,
		shoulderT7,
		shoulderV1,
		shoulderV2,
		shoulderV3,
		shoulderV4,
		shoulderV5,
		shoulderV6,
		shoulderV7,
		hipT1,
		hipT2,
		hipT3,
		hipT4,
		hipT5,
		hipT6,
		hipV1,
		hipV2,
		hipV3,
		hipV4,
		hipV5,
		hipV6;
	}

	@Override
	public SupportPattern getPattern(float[] parameters) {
		SupportPattern sp = new SupportPattern();

		// System.out.println(parameters.length);
		assert (parameters.length == lyingParamOffsets.values().length);

		sp.setIdleModifier(parameters[lyingParamOffsets.idleModifier.ordinal()]);
		sp.setHipsVerticalKP(parameters[lyingParamOffsets.hipsVerticalKP
				.ordinal()]);
		sp.setHipsVerticalKD(parameters[lyingParamOffsets.hipsVerticalKD
				.ordinal()]);

		sp.setShouldersVerticalKP(parameters[lyingParamOffsets.shouldersVerticalKP
				.ordinal()]);
		sp.setShouldersVerticalKD(parameters[lyingParamOffsets.shouldersVerticalKD
				.ordinal()]);

		sp.setSagittalKP(parameters[lyingParamOffsets.sagittalKP.ordinal()]);
		sp.setSagittalKD(parameters[lyingParamOffsets.sagittalKD.ordinal()]);

		sp.setRootKP(parameters[lyingParamOffsets.rootKP.ordinal()]);
		sp.setRootKD(parameters[lyingParamOffsets.rootKD.ordinal()]);

		sp.setMaxRootCorrectionTorque(parameters[lyingParamOffsets.maxRootCorrection
				.ordinal()]);

		sp.addLimbStatus(supportLabel.leftArm,
				parameters[lyingParamOffsets.laTime1.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[lyingParamOffsets.laOffset1.ordinal()]));
		sp.addLimbStatus(supportLabel.leftArm,
				parameters[lyingParamOffsets.laTime2.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightArm,
				parameters[lyingParamOffsets.raTime1.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[lyingParamOffsets.raOffset1.ordinal()]));
		sp.addLimbStatus(supportLabel.rightArm,
				parameters[lyingParamOffsets.raTime2.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightLeg,
				parameters[lyingParamOffsets.rlTime1.ordinal()],
				new supportInfo(limbStatus.stance, true, 0f));

		sp.addLimbStatus(supportLabel.leftLeg,
				parameters[lyingParamOffsets.llTime1.ordinal()],
				new supportInfo(limbStatus.stance, true, 0f));

		sp.addLimbStatus(supportLabel.rightArm,
				parameters[lyingParamOffsets.raTime3.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[lyingParamOffsets.raOffset2.ordinal()]));
		sp.addLimbStatus(supportLabel.rightArm,
				parameters[lyingParamOffsets.raTime4.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.leftArm,
				parameters[lyingParamOffsets.laTime3.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[lyingParamOffsets.laOffset2.ordinal()]));
		sp.addLimbStatus(supportLabel.leftArm,
				parameters[lyingParamOffsets.laTime4.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.leftLeg,
				parameters[lyingParamOffsets.llTime2.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[lyingParamOffsets.llOffset1.ordinal()]));
		sp.addLimbStatus(supportLabel.leftLeg,
				parameters[lyingParamOffsets.llTime3.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightLeg,
				parameters[lyingParamOffsets.rlTime2.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.leftArm,
				parameters[lyingParamOffsets.laTime5.ordinal()],
				new supportInfo(limbStatus.idle, false, 0f));

		sp.addLimbStatus(supportLabel.rightArm,
				parameters[lyingParamOffsets.raTime5.ordinal()],
				new supportInfo(limbStatus.idle, false, 0f));

		sp.addShoulderHeightKnot(new entry(
				parameters[lyingParamOffsets.shoulderT1.ordinal()],
				parameters[lyingParamOffsets.shoulderV1.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[lyingParamOffsets.shoulderT2.ordinal()],
				parameters[lyingParamOffsets.shoulderV2.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[lyingParamOffsets.shoulderT3.ordinal()],
				parameters[lyingParamOffsets.shoulderV3.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[lyingParamOffsets.shoulderT4.ordinal()],
				parameters[lyingParamOffsets.shoulderV4.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[lyingParamOffsets.shoulderT5.ordinal()],
				parameters[lyingParamOffsets.shoulderV5.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[lyingParamOffsets.shoulderT6.ordinal()],
				parameters[lyingParamOffsets.shoulderV6.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[lyingParamOffsets.shoulderT7.ordinal()],
				parameters[lyingParamOffsets.shoulderV7.ordinal()]));

		sp.addHipHeightKnot(new entry(parameters[lyingParamOffsets.hipT1
				.ordinal()], parameters[lyingParamOffsets.hipV1.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[lyingParamOffsets.hipT2
				.ordinal()], parameters[lyingParamOffsets.hipV2.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[lyingParamOffsets.hipT3
				.ordinal()], parameters[lyingParamOffsets.hipV3.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[lyingParamOffsets.hipT4
				.ordinal()], parameters[lyingParamOffsets.hipV4.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[lyingParamOffsets.hipT5
				.ordinal()], parameters[lyingParamOffsets.hipV5.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[lyingParamOffsets.hipT6
				.ordinal()], parameters[lyingParamOffsets.hipV6.ordinal()]));

		return sp;
	}

}
