package edu.benjones.getUp2D.optimization;

import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.Controllers.SPController;
import edu.benjones.getUp2D.Controllers.SupportPatterns.ParameterizedLyingGenerator;

public class LineSearchThread extends AbstractOptimizationThread {

	protected final int maxIterations = 10;
	protected final int maxDoublingIterations = 10;
	protected int index;

	protected float minValue, maxValue;// parameters to return

	public LineSearchThread(DebugDraw g) {
		super(g);

	}

	@Override
	public void run() {
		if (updatedParameters == null) {
			updatedParameters = new float[initialParameters.length];
			for (int i = 0; i < initialParameters.length; ++i)
				updatedParameters[i] = initialParameters[i];
		}
		// do min first, since we know where it starts
		float minLow = 0f;
		float minHigh = initialParameters[index];
		float minGuess = 0;
		for (int iter = 0; iter < maxIterations; ++iter) {
			minGuess = (minLow + minHigh) / 2.0f;

			updatedParameters[index] = minGuess;

			if (evaluate(false)) {
				minHigh = minGuess;
			} else {
				minLow = minGuess;
			}

		}
		minValue = minGuess;

		// now do the max. Keep doubling the maxParameterDelta until it breaks
		// or we hit a limit
		float maxLow = initialParameters[index];
		float maxHigh;
		int doublingIteration = 0;
		boolean success = true;
		updatedParameters[index] = initialParameters[index];// reset this
		float stepLength = parameterMaxDelta[index];
		while (doublingIteration < maxDoublingIterations && success) {
			doublingIteration++;
			updatedParameters[index] += stepLength;
			success = evaluate(false);
			stepLength *= 2;
		}
		System.out.println("stepLength: " + stepLength + " after "
				+ doublingIteration + " iterations");

	}

	@Override
	public void setupController() {

	}

	private boolean evaluate(boolean record) {
		// setup
		if (character != null)
			character.destroy();
		setupCharacter();
		controller = new SPController(character,
				new ParameterizedLyingGenerator(), updatedParameters);

		time = 0;
		shoulderHeight = 0;
		successTime = Float.POSITIVE_INFINITY;
		while (time < controller.getEndTime()) {

			physicsStep();

			shoulderHeight = character.getArms().get(0).getBase().getAnchor2().y;
			if (successTime == Float.POSITIVE_INFINITY
					&& shoulderHeight > heightThreshold) {
				successTime = time;
				System.out.println("Time: " + time);
			}

			time += timestep;
		}

		return shoulderHeight > heightThreshold;

	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public float getMinValue() {
		return minValue;
	}

	public float getMaxValue() {
		return maxValue;
	}

}
