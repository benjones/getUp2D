package edu.benjones.getUp2D.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.jbox2d.dynamics.DebugDraw;

import edu.benjones.getUp2D.GetUpScenario;
import edu.benjones.getUp2D.Controllers.SPController;
import edu.benjones.getUp2D.Controllers.SupportPatterns.ParameterizedLyingGenerator;

public class OptimizationThread extends GetUpScenario implements Runnable {

	protected float cost;

	protected Random rand;

	protected final float torqueWeight = .05f;
	protected final float timeWeight = 40f;
	protected final float heightWeight = 1300f;

	// these seem low
	protected final float heightThreshold = .9f;
	protected final float maxHeight = 1.1f;

	protected float[] initialParameters;
	protected float[] parameterMaxDelta;
	protected float[] updatedParameters;

	public OptimizationThread(DebugDraw g) {
		super(g);
		// TODO Auto-generated constructor stub
		drawDesiredPose = false;
		drawControllerExtras = false;
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
		int tweaks = (int) (initialParameters.length * .2);
		ArrayList<Integer> indeces = new ArrayList<Integer>(
				initialParameters.length);
		for (int i = 0; i < initialParameters.length; ++i) {
			indeces.add(i);
			updatedParameters[i] = initialParameters[i];
		}
		Collections.shuffle(indeces);
		// modify tweak number of parameters
		for (int i = 0; i < tweaks; ++i) {
			updatedParameters[indeces.get(i)] += 2 * (rand.nextFloat() - .5f)
					* parameterMaxDelta[indeces.get(i)];
		}

	}

	@Override
	public void setupController() {
	}

	@Override
	public void run() {

		// perform an evaluation
		float time = 0;
		float timestep = (float) (1.0 / physicsFramerate);
		cost = 0;

		float torqueCost = 0, timeCost = 0, heightCost = 0;

		// setup the controller
		updateParameters();
		controller = new SPController(character,
				new ParameterizedLyingGenerator(), updatedParameters);

		// controller.reset();

		float totalTorque;
		float successTime = Float.POSITIVE_INFINITY;
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
			// compute cost
			totalTorque = 0;
			for (float f : controller.getTorques()) {
				totalTorque += Math.abs(f);
			}
			cost += totalTorque * torqueWeight;
			time += timestep;

			if (successTime == Float.POSITIVE_INFINITY
					&& character.getArms().get(0).getBase().getAnchor2().y > heightThreshold) {
				successTime = time;
				System.out.println("Time: " + time);
			}

		}
		torqueCost = cost;
		timeCost = timeWeight * successTime;
		heightCost = Math.abs(maxHeight
				- character.getArms().get(0).getBase().getAnchor2().y)
				* heightWeight;

		if (character.getArms().get(0).getBase().getAnchor2().y < heightThreshold) {
			System.out.println("Failure");
			cost = Float.POSITIVE_INFINITY;
		} else {
			cost = torqueCost + timeCost + heightCost;

			System.out.println("heightCost: " + heightCost + " torqueCost: "
					+ torqueCost + " timeCost: " + timeCost);
		}
	}

	@Override
	protected void setupCamera() {

	}

	public float getCost() {
		return cost;
	}
}
