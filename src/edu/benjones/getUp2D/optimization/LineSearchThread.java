package edu.benjones.getUp2D.optimization;

import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.Controllers.SPController;
import edu.benjones.getUp2D.Controllers.SupportPatterns.ParameterizedLyingGenerator;

public class LineSearchThread extends AbstractOptimizationThread {

	protected final int maxIterations = 10;
	protected final int maxDoublingIterations = 10;
	protected int index;

	protected boolean max;// true if we want max, false if we want min

	protected float minValue, maxValue;// parameters to return

	public LineSearchThread(DebugDraw g) {
		super(g);

	}

	@Override
	public void run() {
		if (updatedParameters == null) {
			updatedParameters = new float[initialParameters.length];
		}
		// reset all parameters
		for (int i = 0; i < initialParameters.length; ++i)
			updatedParameters[i] = initialParameters[i];
		// do min first, since we know where it starts
		if (max)
			computeMax();
		else
			computeMin();
	}

	private void computeMax() {
		// now do the max. Keep doubling the maxParameterDelta until it breaks
		// or we hit a limit
		float maxLow = initialParameters[index];
		float maxHigh;
		float maxGuess = 0;
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

		if (success) {
			maxValue = updatedParameters[index];
			System.out.println("MAXED OUT!!");
		} else {
			maxLow = updatedParameters[index] - stepLength / 2;
			maxHigh = updatedParameters[index];

			updatedParameters[index] = maxLow;
			if (!evaluate(false)) {
				System.out.println("maxLow failed");
				System.exit(1);
			}

			for (int iter = 0; iter < maxIterations; ++iter) {
				maxGuess = (maxLow + maxHigh) / 2.0f;
				updatedParameters[index] = maxGuess;

				if (evaluate(false)) {
					maxLow = maxGuess;
				} else {
					maxHigh = maxGuess;
				}
			}
			maxValue = maxGuess;
		}
	}

	private void computeMin() {
		float minLow = 0f;// NOT NECESSARILY (x offsets can be negative)
		float stepLength = parameterMaxDelta[index];
		int doublingIteration = 0;
		boolean success = true;
		while (doublingIteration < maxDoublingIterations && success) {
			doublingIteration++;
			updatedParameters[index] -= stepLength;
			success = evaluate(false);
			stepLength *= 2;
		}
		if (success) {
			minValue = updatedParameters[index];
			System.out.println("MIN'd OUT!!!");
		} else {
			minLow = updatedParameters[index];
			float minHigh = updatedParameters[index] + stepLength / 2;
			updatedParameters[index] = minHigh;
			if (!evaluate(false)) {
				System.out.println("minHigh failed");
				System.exit(1);
			}

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
		}
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

			try {
				physicsStep();
			} catch (Exception e) {
				System.out.println("step failed: " + e.getMessage());
				System.out.println("controlParams: ");
				for (float p : updatedParameters)
					System.out.println(p);
				System.out.println("Torques: ");
				for (float t : controller.getTorques()) {
					System.out.println(t);
				}
				System.exit(1);
			}

			shoulderHeight = character.getArms().get(0).getBase().getAnchor2().y;
			if (successTime == Float.POSITIVE_INFINITY
					&& shoulderHeight > heightThreshold) {
				successTime = time;
				System.out.print("Time: " + time + " ");
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

	public void setMax(boolean max) {
		this.max = max;
	}

}
