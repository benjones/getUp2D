package edu.benjones.getUp2D.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.GetUpScenario;
import edu.benjones.getUp2D.Controllers.SPController;
import edu.benjones.getUp2D.Controllers.SupportPatterns.ParameterizedLyingGenerator;
import edu.benjones.getUp2D.Utils.BufferedImageDebugDraw;
import edu.benjones.getUp2D.Utils.FileUtils;

public class OptimizationThread extends GetUpScenario implements Runnable {

	protected float cost;

	protected Random rand;

	protected final float torqueWeight = .001f;
	protected final float timeWeight = 40f;
	protected final float heightWeight = 1300f;

	// these seem low
	protected final float heightThreshold = .83f;
	protected final float maxHeight = 1.1f;

	protected float[] initialParameters;
	protected float[] parameterMaxDelta;
	protected float[] updatedParameters;

	private int stepNumber;

	public OptimizationThread(DebugDraw g) {
		super(g);
		stepNumber = 0;
		// TODO Auto-generated constructor stub
		if (g == null) {
			drawDesiredPose = false;
			drawControllerExtras = false;
		} else {
			drawDesiredPose = true;
			drawControllerExtras = true;
		}
		rand = new Random();

	}

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

	protected void updateParameters() {
		assert (initialParameters.length == parameterMaxDelta.length);
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
					* parameterMaxDelta[indeces.get(i)] * .5;

			// 2 * (rand.nextFloat() - .5f)
			// * parameterMaxDelta[indeces.get(i)];

		}

	}

	@Override
	public void setupController() {
	}

	private float time;
	private float shoulderHeight;
	private float successTime;

	@Override
	public void run() {

		// perform an evaluation
		time = 0;
		float timestep = (float) (1.0 / physicsFramerate);
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

		// controller.reset();

		float totalTorque;
		float successTime = Float.POSITIVE_INFINITY;

		successTime = Float.POSITIVE_INFINITY;
		shoulderHeight = 0;

		while (time < controller.getEndTime()) {
			stepNumber++;
			if ((stepNumber % 5 == 0)
					&& debugDraw instanceof BufferedImageDebugDraw) {
				((BufferedImageDebugDraw) (debugDraw)).clear();
			}

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
				totalTorque += Math.abs(f);
			}
			cost += totalTorque * torqueWeight;
			time += timestep;

			shoulderHeight = character.getArms().get(0).getBase().getAnchor2().y;

			if (successTime == Float.POSITIVE_INFINITY
					&& shoulderHeight > heightThreshold) {
				successTime = time;
				System.out.println("Time: " + time);
			}
			if ((stepNumber % 5 == 0)
					&& debugDraw instanceof BufferedImageDebugDraw) {
				((BufferedImageDebugDraw) debugDraw)
						.saveImage("debugFrames/frame"
								+ String.format("%06d", stepNumber / 5)
								+ ".png");
			}

		}
		torqueCost = cost;
		timeCost = timeWeight * successTime;
		heightCost = Math.abs(maxHeight - shoulderHeight) * heightWeight;

		if (shoulderHeight < heightThreshold) {
			System.out.println("Failure, height: " + shoulderHeight
					+ " at time: " + time);
			cost = Float.POSITIVE_INFINITY;
			FileUtils.writeParameters("FAIL.Par", updatedParameters);
			if (debugDraw instanceof BufferedImageDebugDraw) {
				((BufferedImageDebugDraw) debugDraw)
						.saveImage("debugFrames/frameFINAL.png");
			}
			System.exit(1);
		} else {
			cost = torqueCost + timeCost + heightCost;

			System.out.println("heightCost: " + heightCost + " torqueCost: "
					+ torqueCost + " timeCost: " + timeCost);
		}
	}

	private void physicsStep(float timestep) {
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

		System.out.println("t: " + time + " Shoulder height " + shoulderHeight);
	}

	public float getCost() {
		return cost;
	}
}
