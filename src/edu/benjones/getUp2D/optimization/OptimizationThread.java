package edu.benjones.getUp2D.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.Controllers.SPController;
import edu.benjones.getUp2D.Controllers.SupportPatterns.ParameterizedLyingGenerator;
import edu.benjones.getUp2D.Utils.BufferedImageDebugDraw;
import edu.benjones.getUp2D.Utils.FileUtils;

public class OptimizationThread extends AbstractOptimizationThread {

	protected Random rand;

	protected final float torqueWeight = .001f;
	protected final float timeWeight = 40f;
	protected final float heightWeight = 1300f;

	public OptimizationThread(DebugDraw g) {
		super(g);

		rand = new Random();
	}

	float evaluate(boolean record) {
		float totalTorque;
		float successTime = Float.POSITIVE_INFINITY;
		while (time < controller.getEndTime()) {
			if (record)
				stepNumber++;
			if (record && (stepNumber % 5 == 0)
					&& debugDraw instanceof BufferedImageDebugDraw) {
				((BufferedImageDebugDraw) (debugDraw)).clear();
			}

			try {
				evaluationStep();
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
			// compute cost
			totalTorque = 0;
			for (float f : controller.getTorques()) {
				totalTorque += Math.pow(f, 2);
			}
			cost += totalTorque * torqueWeight;

			shoulderHeight = character.getArms().get(0).getBase().getAnchor2().y;

			if (successTime == Float.POSITIVE_INFINITY
					&& shoulderHeight > heightThreshold) {
				successTime = time;
				System.out.println("Time: " + time);
			}
			if ((record && stepNumber % 5 == 0)
					&& debugDraw instanceof BufferedImageDebugDraw) {
				((BufferedImageDebugDraw) debugDraw)
						.saveImage("debugFrames/frame"
								+ String.format("%06d", stepNumber / 5)
								+ ".png");
			}

		}
		return successTime;
	}

	private void evaluationStep() {
		float totalTorque;
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
		// compute cost
		totalTorque = 0;
		for (float f : controller.getTorques()) {
			totalTorque += Math.pow(f, 2);
		}
		cost += totalTorque * torqueWeight;
		time += timestep;
		shoulderHeight = character.getArms().get(0).getBase().getAnchor2().y;
		if (successTime == Float.POSITIVE_INFINITY
				&& shoulderHeight > heightThreshold) {
			successTime = time;
			System.out.println("Time: " + time);
		}
	}

	protected void updateParameters() {
		updatedParameters = new float[initialParameters.length];
		int tweaks = (int) (initialParameters.length * .1);

		ArrayList<Integer> indeces = new ArrayList<Integer>(
				initialParameters.length);
		for (int i = 0; i < initialParameters.length; ++i) {
			indeces.add(i);
			updatedParameters[i] = initialParameters[i];
		}
		Collections.shuffle(indeces);
		// modify tweak number of parameters
		for (int i = 0; i < tweaks; ++i) {

			updatedParameters[indeces.get(i)] += rand.nextGaussian()
					* parameterMaxDelta[indeces.get(i)];

			// 2 * (rand.nextFloat() - .5f)
			// * parameterMaxDelta[indeces.get(i)];

		}

	}

	public void run() {

		// perform an evaluation
		time = 0;
		cost = 0;

		float torqueCost = 0, timeCost = 0, heightCost = 0;

		// setup the controller
		if (character != null) {
			character.destroy();
		}
		setupCharacter();
		updateParameters();
		controller = new SPController(character,
				new ParameterizedLyingGenerator(), updatedParameters);

		float successTime = evaluate(false);
		torqueCost = cost;
		timeCost = timeWeight * successTime;
		heightCost = Math.abs(maxHeight - shoulderHeight) * heightWeight;

		if (shoulderHeight < heightThreshold) {
			System.out.println("Failure, height: " + shoulderHeight
					+ " at time: " + time);
			cost = Float.POSITIVE_INFINITY;
			FileUtils.writeParameters("FAIL.Par", updatedParameters);

			// record the error:
			/*
			 * time = 0; character.destroy(); setupCharacter(); controller = new
			 * SPController(character, new ParameterizedLyingGenerator(),
			 * updatedParameters); evaluate(timestep, true);
			 */
			if (debugDraw instanceof BufferedImageDebugDraw) {
				((BufferedImageDebugDraw) debugDraw)
						.saveImage("debugFrames/frameFINAL.png");
			}
			// System.exit(1);
		} else {
			cost = torqueCost + timeCost + heightCost;

			System.out.println("heightCost: " + heightCost + " torqueCost: "
					+ torqueCost + " timeCost: " + timeCost);
		}
	}

}
