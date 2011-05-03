package edu.benjones.getUp2D.optimization;

import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.GetUpScenario;

public abstract class AbstractOptimizationThread extends GetUpScenario
		implements Runnable {

	protected float[] initialParameters;
	protected float[] parameterMaxDelta;
	protected float[] updatedParameters;
	protected int stepNumber;
	protected float time;
	protected float cost;
	protected float shoulderHeight;
	protected float successTime;

	// these seem low
	protected final float heightThreshold = .83f;
	protected final float maxHeight = 1.1f;

	public void setInitialParameters(float[] params) {
		initialParameters = new float[params.length];
		for (int i = 0; i < initialParameters.length; ++i) {
			initialParameters[i] = params[i];
		}
	}

	public void setParameterMaxDelta(float[] delta) {
		parameterMaxDelta = new float[delta.length];
		for (int i = 0; i < parameterMaxDelta.length; ++i) {
			parameterMaxDelta[i] = delta[i];
		}

	}

	public float[] getUpdatedParameters() {
		return updatedParameters;
	}

	public AbstractOptimizationThread(DebugDraw g) {
		super(g);
		stepNumber = 0;
		if (g == null) {
			drawDesiredPose = false;
			drawControllerExtras = false;
		} else {
			drawDesiredPose = true;
			drawControllerExtras = true;
		}
	}

	@Override
	public abstract void run();

	public float getCost() {
		return cost;
	}

}