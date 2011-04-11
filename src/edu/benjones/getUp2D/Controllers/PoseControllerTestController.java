package edu.benjones.getUp2D.Controllers;

import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;

public class PoseControllerTestController extends PoseController {

	private float elapsed;
	
	public PoseControllerTestController(Character ch){
		super(ch);
	
		for(int i = 0; i < desiredPose.length; ++i)
			desiredPose[i] = 0;
	}
	
	@Override
	public void computeTorques(World w, float dt){
		elapsed += dt;
		desiredPose[0] = (float) Math.sin(elapsed);
		desiredPose[2] = (float) Math.sin(elapsed);
		super.computeTorques(w, dt);
	}
	
}
