package edu.benjones.getUp2D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.benjones.getUp2D.Utils.FileUtils;
import edu.benjones.getUp2D.optimization.ContinuationThread;

/*
 * while(?){
 * optimize for stability:
 * {
 *    do{
 *    pick n tweaks
 *    if(n works){
 *        pick m neighbors
 *        compute success/statistics about neighbors
 *    }
 *    pick best from n (most successful neighbors, lowest time/variance)
 *    }while(best from n percentage is too low)
 * }
 *modify the word:
 *{
 *  change world
 *  test m neighbors
 *  if( > 50% of neighbors work): keep changing world
 *  
 *} 
 * }
 */

public class ContinuationOptimizer {
	public static final int numTweaks = 16;
	public static final int numNeighbors = 16;

	public static float[] parameterMaxDelta;
	static boolean stop;

	public static void main(String[] args) {
		parameterMaxDelta = FileUtils
				.readParameters("./SPParameters/warpedLyingLimits.par");
		ExecutorService es = Executors.newCachedThreadPool();

		float[] initialParameters = FileUtils
				.readParameters("./SPParameters/warpedImprovement250.par");

		Thread keyWatch = new Thread() {

			public void run() {
				Scanner in = new Scanner(System.in);
				in.nextLine();
				stop = true;
				System.out.println("keywatch exiting");
			}

		};

		keyWatch.start();

		ArrayList<ContinuationThread> neighborThreads = new ArrayList<ContinuationThread>(
				numNeighbors);
		for (int i = 0; i < numNeighbors; ++i) {
			neighborThreads.add(new ContinuationThread(null));
		}
		float[] bestParams = initialParameters;
		while (!stop) {
			int maxNumSuccesses = 0;
			float bestAverageCost;
			float averageCost;

			int numSuccesses;
			do {
				bestAverageCost = Float.POSITIVE_INFINITY;
				// make sure it works, then
				// optimize the current solution
				initialParameters = bestParams;
				for (int tweak = 0; tweak < numTweaks; ++tweak) {
					// evaluate nearby samples
					float[] tweakedParameters = updateParameters(initialParameters);
					for (int neighbor = 0; neighbor < numNeighbors; ++neighbor) {
						neighborThreads.get(neighbor).setInitialParameters(
								updateParameters(tweakedParameters));
					}
					try {
						es.invokeAll(neighborThreads);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					averageCost = 0;
					numSuccesses = 0;
					float c;
					// float[] bestNeighborParams;
					// float bestNeighborCost = Float.POSITIVE_INFINITY;
					for (int i = 0; i < numNeighbors; ++i) {
						c = neighborThreads.get(i).getCost();
						averageCost += c;
						if (c < Float.POSITIVE_INFINITY) {
							numSuccesses++;
							// if (c < bestNeighborCost) {
							// bestNeighborCost = c;
							// bestNeighborParams = neighborThreads.get(i)
							// .getInitialParameters();
							// }

						}
					}
					// compare results for the different tweaks
					averageCost /= numNeighbors;
					if (numSuccesses > maxNumSuccesses) {
						maxNumSuccesses = numSuccesses;
						bestParams = tweakedParameters;
						System.out
								.println("more successes, updated bestParams");
					}
					if (numSuccesses == maxNumSuccesses) {
						if (averageCost < bestAverageCost) {
							bestAverageCost = averageCost;
							bestParams = tweakedParameters;
							System.out.println("lower cost, updatedBestParams");
						}
					}
					System.out.println("numSuccesses at tweak " + tweak + ": "
							+ numSuccesses);
				}
				// update and try again
				System.out.println("Parameters updated");

			} while (maxNumSuccesses < numNeighbors);

			System.out.println("best params: ");
			for (float p : bestParams)
				System.out.println(p);

			// change the environment
			stop = true;
		}

	}

	public static float[] updateParameters(float[] initialParameters) {
		Random rand = new Random();
		float[] updatedParameters = new float[initialParameters.length];

		float tweakPercentage = (float) (.2 + (rand).nextGaussian() * .15f);
		tweakPercentage = Math.min(1.0f, Math.max(.1f, tweakPercentage));
		// clamp from .1f to 1.0f

		int tweaks = (int) (initialParameters.length * tweakPercentage);

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
		}
		return updatedParameters;

	}

}
