package edu.benjones.getUp2D.Controllers.SupportPatterns;

import edu.benjones.getUp2D.Controllers.SupportPattern;
import edu.benjones.getUp2D.Utils.Trajectory1D.entry;

public class testPatternGenerator implements SupportPatternGenerator {

	public SupportPattern getPattern(){
		SupportPattern sp = new SupportPattern();
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
	
		sp.addHipHeightKnot(new entry(0,0));
		sp.addHipHeightKnot(new entry(.5f,.5f));
		
		sp.addShoulderHeightKnot(new entry(.3f, .3f));
		sp.addShoulderHeightKnot(new entry(.8f, 2.0f));
		
		return sp;
	}
}
