package edu.benjones.getUp2D.Controllers;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.Character.Limb;

public class GravityCompensationTestController extends
		PoseControllerTestController {

	protected ArrayList<VirtualForce> virtualForces;

	public GravityCompensationTestController(Character ch) {
		super(ch);
		virtualForces = new ArrayList<VirtualForce>();
	}

	@Override
	public void computeTorques(World w, float dt) {
		super.computeTorques(w, dt);// compute the PC stuff
		virtualForces.clear();

		/*for (Limb l : character.getLegs()) {
			l.addGravityCompenstaionTorques(virtualForces);
		}*/
		character.getLegs().get(0).addGravityCompenstaionTorques(virtualForces);
	
		for(Limb arm : character.getArms()){
			arm.addGravityCompenstaionTorques(virtualForces);
		}
		
		for (VirtualForce v : virtualForces){
			v.apply();
		}
	}

	public void drawControllerExtras(DebugDraw g){
		for (VirtualForce v : virtualForces)
			v.draw(g);
	}
}
