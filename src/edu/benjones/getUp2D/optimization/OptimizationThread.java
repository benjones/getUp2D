package edu.benjones.getUp2D.optimization;

import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.GetUpScenario;

public class OptimizationThread extends GetUpScenario implements Runnable {

	public OptimizationThread(DebugDraw g) {
		super(g);
		// TODO Auto-generated constructor stub
		drawDesiredPose = false;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void setupCamera() {

	}
}
