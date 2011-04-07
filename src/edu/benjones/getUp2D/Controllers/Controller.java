package edu.benjones.getUp2D.Controllers;

import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

public interface Controller {
	
	public void computeTorques(World w, float dt);
	
	public void applyTorques();
	
	public void drawControllerExtras(DebugDraw g);
}
