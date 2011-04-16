package edu.benjones.getUp2D.test;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.benjones.getUp2D.Controllers.SupportPattern;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;
import edu.benjones.getUp2D.Utils.MathUtils;
import edu.benjones.getUp2D.Utils.Trajectory1D;

public class SupportPatternTest {

	private SupportPattern sp;
	@Before
	public void setupSP(){
		sp = new SupportPattern();
		sp.addLimbStatus(SupportPattern.supportLabel.leftArm, .5f, 
				new SupportPattern.supportInfo(SupportPattern.limbStatus.swing,false, 0f));
		sp.addLimbStatus(SupportPattern.supportLabel.leftArm, .7f, 
				new SupportPattern.supportInfo(SupportPattern.limbStatus.stance,false, 0f));
		sp.addLimbStatus(SupportPattern.supportLabel.leftArm, .8f, 
				new SupportPattern.supportInfo(SupportPattern.limbStatus.swing,false, 0f));
		sp.addLimbStatus(SupportPattern.supportLabel.leftArm, .9f, 
				new SupportPattern.supportInfo(SupportPattern.limbStatus.swing,false, 0f));
		sp.addLimbStatus(SupportPattern.supportLabel.leftArm, 1.0f, 
				new SupportPattern.supportInfo(SupportPattern.limbStatus.stance,false, 0f));
	}
	
	@Test
	public void statusTests(){
		
		
		
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, 0).ls, limbStatus.idle);
		
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, .4f).ls, limbStatus.idle);
		
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, .5f).ls, limbStatus.swing);
		
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, .5f).ls, limbStatus.swing);
		
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, 0.6f).ls, limbStatus.swing);
		
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, 0.65f).ls, limbStatus.swing);
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, 0.7f).ls, limbStatus.stance);
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, 0.75f).ls, limbStatus.stance);
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, .95f).ls, limbStatus.swing);
		
		assertEquals(sp.getInfoAtTime(supportLabel.leftArm, 2.0f).ls, limbStatus.stance);
		
	
		//swingPhaseTests
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), 0f),true);
		assertEquals(MathUtils.floatEquals(sp.getTimeToLift(supportLabel.leftArm), 0f),true);
		sp.advancePhase(.5f);
		
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), 0f),true);
		sp.advancePhase(.1f);
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), .5f),true);
		sp.advancePhase(.15f);//at .75
		assertEquals(MathUtils.floatEquals(sp.getTimeToLift(supportLabel.leftArm), 0.05f),true);
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), 0f),true);
		sp.advancePhase(.05f);//at .8
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), 0f),true);
		sp.advancePhase(.10f);//at .9
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), 0.5f),true);
		sp.advancePhase(.3f);//at 1.2
		assertEquals(sp.getTimeToLift(supportLabel.leftArm)== Float.POSITIVE_INFINITY, true);
	
	
	}
	
	@Test
	public void minMaxTimeTests(){
		SupportPattern s = new SupportPattern();
		s.addLimbStatus(SupportPattern.supportLabel.leftArm, .5f, 
				new SupportPattern.supportInfo(SupportPattern.limbStatus.swing,false, 0f));
		
		assertEquals(MathUtils.floatEquals(s.getMinFiniteTime(), 0.5f),true);
		assertEquals(MathUtils.floatEquals(s.getMaxFiniteTime(), 0.5f),true);
		
		s.addHipHeightKnot(new Trajectory1D.entry(-1f,1f));
		assertEquals(MathUtils.floatEquals(s.getMinFiniteTime(), -1f),true);
		assertEquals(MathUtils.floatEquals(s.getMaxFiniteTime(), 0.5f),true);
	
		s.addShoulderHeightKnot(new Trajectory1D.entry(2f, 1f));
		assertEquals(MathUtils.floatEquals(s.getMinFiniteTime(), -1f),true);
		assertEquals(MathUtils.floatEquals(s.getMaxFiniteTime(), 2f),true);
		
		s.addLimbStatus(SupportPattern.supportLabel.leftArm, 3.5f, 
				new SupportPattern.supportInfo(SupportPattern.limbStatus.swing,false, 0f));
		assertEquals(MathUtils.floatEquals(s.getMinFiniteTime(), -1f),true);
		assertEquals(MathUtils.floatEquals(s.getMaxFiniteTime(), 3.5f),true);
	}
	
}
