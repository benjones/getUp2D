package edu.benjones.getUp2D;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.Controllers.GravityCompensationTestController;
import edu.benjones.getUp2D.Controllers.IKTestController;
import edu.benjones.getUp2D.Controllers.PoseControllerTestController;
import edu.benjones.getUp2D.characters.Biped9FixedBase;

public class FixedBaseScenario extends GetUpScenario {
	public FixedBaseScenario(DebugDraw g){
		super(g);
	}
	
	public void setupCharacter(){
		character = new Biped9FixedBase(world);
		float[] zeros = new float[character.getStateSize()];
		character.setState(new Vec2(0f,0f), (float)(-Math.PI/4), zeros);
	}
	
	public void setupController(){
		//controller = new IKTestController(character);
		controller = new GravityCompensationTestController(character);
		//controller = new PoseControllerTestController(character);
	}
}
