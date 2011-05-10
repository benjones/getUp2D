package edu.benjones.getUp2D.Controllers.SupportPatterns;

import edu.benjones.getUp2D.Controllers.SupportPattern;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportInfo;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;
import edu.benjones.getUp2D.Utils.TimeWarp;
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
		hipV6,
		warpVel0,
		warpVel1,
		warpVel2,
		warpVel3,
		warpVel4,
		warpVel5,
		warpVel6,
		warpVel7,
		warpVel8,
		warpVel9,
		warpVel10;
	}

	@Override
	public SupportPattern getPattern(float[] parameters) {
		SupportPattern sp = new SupportPattern();

		// System.out.println(parameters.length);
		assert (parameters.length == warpedLyingParamOffsets.values().length);

		sp.setIdleModifier(parameters[warpedLyingParamOffsets.idleModifier
				.ordinal()]);
		sp.setHipsVerticalKP(parameters[warpedLyingParamOffsets.hipsVerticalKP
				.ordinal()]);
		sp.setHipsVerticalKD(parameters[warpedLyingParamOffsets.hipsVerticalKD
				.ordinal()]);

		sp.setShouldersVerticalKP(parameters[warpedLyingParamOffsets.shouldersVerticalKP
				.ordinal()]);
		sp.setShouldersVerticalKD(parameters[warpedLyingParamOffsets.shouldersVerticalKD
				.ordinal()]);

		sp.setSagittalKP(parameters[warpedLyingParamOffsets.sagittalKP
				.ordinal()]);
		sp.setSagittalKD(parameters[warpedLyingParamOffsets.sagittalKD
				.ordinal()]);

		sp.setRootKP(parameters[warpedLyingParamOffsets.rootKP.ordinal()]);
		sp.setRootKD(parameters[warpedLyingParamOffsets.rootKD.ordinal()]);

		sp.setMaxRootCorrectionTorque(parameters[warpedLyingParamOffsets.maxRootCorrection
				.ordinal()]);

		sp.addLimbStatus(
				supportLabel.leftArm,
				parameters[warpedLyingParamOffsets.laTime1.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[warpedLyingParamOffsets.laOffset1.ordinal()]));
		sp.addLimbStatus(supportLabel.leftArm,
				parameters[warpedLyingParamOffsets.laTime2.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(
				supportLabel.rightArm,
				parameters[warpedLyingParamOffsets.raTime1.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[warpedLyingParamOffsets.raOffset1.ordinal()]));
		sp.addLimbStatus(supportLabel.rightArm,
				parameters[warpedLyingParamOffsets.raTime2.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightLeg,
				parameters[warpedLyingParamOffsets.rlTime1.ordinal()],
				new supportInfo(limbStatus.stance, true, 0f));

		sp.addLimbStatus(supportLabel.leftLeg,
				parameters[warpedLyingParamOffsets.llTime1.ordinal()],
				new supportInfo(limbStatus.stance, true, 0f));

		sp.addLimbStatus(
				supportLabel.rightArm,
				parameters[warpedLyingParamOffsets.raTime3.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[warpedLyingParamOffsets.raOffset2.ordinal()]));
		sp.addLimbStatus(supportLabel.rightArm,
				parameters[warpedLyingParamOffsets.raTime4.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(
				supportLabel.leftArm,
				parameters[warpedLyingParamOffsets.laTime3.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[warpedLyingParamOffsets.laOffset2.ordinal()]));
		sp.addLimbStatus(supportLabel.leftArm,
				parameters[warpedLyingParamOffsets.laTime4.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(
				supportLabel.leftLeg,
				parameters[warpedLyingParamOffsets.llTime2.ordinal()],
				new supportInfo(limbStatus.swing, false,
						parameters[warpedLyingParamOffsets.llOffset1.ordinal()]));
		sp.addLimbStatus(supportLabel.leftLeg,
				parameters[warpedLyingParamOffsets.llTime3.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.rightLeg,
				parameters[warpedLyingParamOffsets.rlTime2.ordinal()],
				new supportInfo(limbStatus.stance, false, 0f));

		sp.addLimbStatus(supportLabel.leftArm,
				parameters[warpedLyingParamOffsets.laTime5.ordinal()],
				new supportInfo(limbStatus.idle, false, 0f));

		sp.addLimbStatus(supportLabel.rightArm,
				parameters[warpedLyingParamOffsets.raTime5.ordinal()],
				new supportInfo(limbStatus.idle, false, 0f));

		sp.addShoulderHeightKnot(new entry(
				parameters[warpedLyingParamOffsets.shoulderT1.ordinal()],
				parameters[warpedLyingParamOffsets.shoulderV1.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[warpedLyingParamOffsets.shoulderT2.ordinal()],
				parameters[warpedLyingParamOffsets.shoulderV2.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[warpedLyingParamOffsets.shoulderT3.ordinal()],
				parameters[warpedLyingParamOffsets.shoulderV3.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[warpedLyingParamOffsets.shoulderT4.ordinal()],
				parameters[warpedLyingParamOffsets.shoulderV4.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[warpedLyingParamOffsets.shoulderT5.ordinal()],
				parameters[warpedLyingParamOffsets.shoulderV5.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[warpedLyingParamOffsets.shoulderT6.ordinal()],
				parameters[warpedLyingParamOffsets.shoulderV6.ordinal()]));
		sp.addShoulderHeightKnot(new entry(
				parameters[warpedLyingParamOffsets.shoulderT7.ordinal()],
				parameters[warpedLyingParamOffsets.shoulderV7.ordinal()]));

		sp.addHipHeightKnot(new entry(parameters[warpedLyingParamOffsets.hipT1
				.ordinal()],
				parameters[warpedLyingParamOffsets.hipV1.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[warpedLyingParamOffsets.hipT2
				.ordinal()],
				parameters[warpedLyingParamOffsets.hipV2.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[warpedLyingParamOffsets.hipT3
				.ordinal()],
				parameters[warpedLyingParamOffsets.hipV3.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[warpedLyingParamOffsets.hipT4
				.ordinal()],
				parameters[warpedLyingParamOffsets.hipV4.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[warpedLyingParamOffsets.hipT5
				.ordinal()],
				parameters[warpedLyingParamOffsets.hipV5.ordinal()]));
		sp.addHipHeightKnot(new entry(parameters[warpedLyingParamOffsets.hipT6
				.ordinal()],
				parameters[warpedLyingParamOffsets.hipV6.ordinal()]));

		TimeWarp tw = new TimeWarp(0, 15, 10);
		tw.setWarp(0, parameters[warpedLyingParamOffsets.warpVel0.ordinal()]);
		tw.setWarp(1, parameters[warpedLyingParamOffsets.warpVel1.ordinal()]);
		tw.setWarp(2, parameters[warpedLyingParamOffsets.warpVel2.ordinal()]);
		tw.setWarp(3, parameters[warpedLyingParamOffsets.warpVel3.ordinal()]);
		tw.setWarp(4, parameters[warpedLyingParamOffsets.warpVel4.ordinal()]);
		tw.setWarp(5, parameters[warpedLyingParamOffsets.warpVel5.ordinal()]);
		tw.setWarp(6, parameters[warpedLyingParamOffsets.warpVel6.ordinal()]);
		tw.setWarp(7, parameters[warpedLyingParamOffsets.warpVel7.ordinal()]);
		tw.setWarp(8, parameters[warpedLyingParamOffsets.warpVel8.ordinal()]);
		tw.setWarp(9, parameters[warpedLyingParamOffsets.warpVel9.ordinal()]);
		tw.setWarp(10, parameters[warpedLyingParamOffsets.warpVel10.ordinal()]);

		sp.setTimeWarp(tw);
		return sp;
	}
}
