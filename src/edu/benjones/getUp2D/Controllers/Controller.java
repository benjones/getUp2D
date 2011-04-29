package edu.benjones.getUp2D.Controllers;

import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.dynamics.World;

public interface Controller {

	public void computeTorques(World w, float dt);

	public void applyTorques();

	public void drawControllerExtras(DebugDraw g);

	public void reset();

	public float getEndTime();

	/**
	 * get the torques the controller applied last frame
	 * 
	 * @return
	 */
	public float[] getTorques();
}
