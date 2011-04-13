import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.benjones.getUp2D.Controllers.SupportPattern;
import edu.benjones.getUp2D.Controllers.SupportPattern.limbStatus;
import edu.benjones.getUp2D.Controllers.SupportPattern.supportLabel;
import edu.benjones.getUp2D.Utils.MathUtils;

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
		
	
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), 0f),true);
		sp.advancePhase(.5f);
		System.out.println(sp.getSwingPhase(supportLabel.leftArm));
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), 0f),true);
		sp.advancePhase(.1f);
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), .5f),true);
		sp.advancePhase(.15f);//at .75
		assertEquals(MathUtils.floatEquals(sp.getSwingPhase(supportLabel.leftArm), 0f),true);
		
	
	
	}
}
