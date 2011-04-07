package edu.benjones.getUp2D.Controllers;

import java.util.List;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;

public class GravityCompensationTest extends PoseControllerTest {

	protected VirtualForce [] virtualForces;
	
	public GravityCompensationTest(Character ch) {
		super(ch);
	}

	@Override
	public void computeTorques(World w, float dt){
		super.computeTorques(w, dt);//compute the PC stuff
		
		List<Body> bodies = character.getBodies();
		for(int i = 1; i < bodies.size(); ++i){
			
		}
	}
	
}
