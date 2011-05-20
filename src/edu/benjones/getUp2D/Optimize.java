package edu.benjones.getUp2D;

import java.util.ArrayList;
import java.util.Scanner;

import edu.benjones.getUp2D.Utils.BufferedImageDebugDraw;
import edu.benjones.getUp2D.Utils.FileUtils;
import edu.benjones.getUp2D.optimization.OptimizationThread;
import edu.benjones.getUp2D.optimization.OptimizationThread.costType;

public class Optimize {

	protected final static int numThreads = 16;

	private static boolean stop;

	public static void main(String[] args) {

		stop = false;

		Thread keyWatch = new Thread() {

			public void run() {
				Scanner in = new Scanner(System.in);
				in.nextLine();
				stop = true;
				System.out.println("keywatch exiting");
			}

		};

		keyWatch.start();

		OptimizationThread[] threads = new OptimizationThread[numThreads];
		Thread[] threadContainers = new Thread[numThreads];
		for (int i = 0; i < numThreads; ++i) {

			threads[i] = new OptimizationThread(new BufferedImageDebugDraw());

		}

		float[] initialParameters = FileUtils
				.readParameters("./SPParameters/warpedDart.par");
		// .readParameters("./SPParameters/dart.par");
		// float[] limits =
		// FileUtils.readParameters("./SPParameters/limits.par");
		float[] limits = FileUtils
				.readParameters("./SPParameters/warpedLyingLimits.par");
		for (int i = 0; i < numThreads; ++i)
			threads[i].setParameterMaxDelta(limits);

		int iteration = 0;
		int improvements = 0;
		int iterationsSinceImprovement = 0;
		float gaussianScale = 1.0f;
		float bestTimeSoFar = Float.POSITIVE_INFINITY;
		float bestWidthSoFar = Float.POSITIVE_INFINITY;
		costType optimizationType = costType.endTime;
		ArrayList<Float> costOverTime = new ArrayList<Float>();
		while (!stop) {
			iteration++;
			for (int i = 0; i < numThreads; ++i) {
				threads[i].setInitialParameters(initialParameters);
				threads[i].setGaussianScale(gaussianScale);
				threads[i].setCostType(optimizationType);
				threads[i].reset();
				threadContainers[i] = new Thread(threads[i]);
				threadContainers[i].start();
			}

			try {
				for (Thread thread : threadContainers) {
					thread.join();
				}
			} catch (InterruptedException e) {
				System.out
						.println("Interruption exception..." + e.getMessage());
			}

			int minIndex = -1;
			float minCost = Float.POSITIVE_INFINITY;
			for (int i = 0; i < numThreads; ++i) {
				if (threads[i].getCost() < minCost) {
					minCost = threads[i].getCost();
					minIndex = i;
				}
			}
			float bestSoFar = Float.POSITIVE_INFINITY;
			if (optimizationType == costType.endTime) {
				bestSoFar = bestTimeSoFar;
			} else if (optimizationType == costType.footWidth) {
				bestSoFar = bestWidthSoFar;
			}

			if (minCost < bestSoFar) {
				improvements++;
				initialParameters = threads[minIndex].getUpdatedParameters();
				bestSoFar = minCost;
				System.out.println("bestSoFar improved " + improvements
						+ " times");
				costOverTime.add(minCost);
				FileUtils.writeParameters("./SPParameters/warpedImprovement"
						+ improvements + ".par", initialParameters);
				iterationsSinceImprovement = 0;
				gaussianScale = 1.0f;

				if (optimizationType == costType.endTime) {
					bestTimeSoFar = bestSoFar;
				} else if (optimizationType == costType.footWidth) {
					bestWidthSoFar = bestSoFar;
				}

			} else {
				System.out.println("Nothing better than bestSoFar");
				iterationsSinceImprovement++;
				if (iterationsSinceImprovement > 5) {
					gaussianScale += .05;
					/*
					 * if (optimizationType == costType.endTime) {
					 * optimizationType = costType.footWidth;
					 * System.out.println("switching to foot optimization"); }
					 * else { optimizationType = costType.endTime;
					 * System.out.println("switching to time optimization"); }
					 * iterationsSinceImprovement = 0;
					 */
				}
				if (minCost == Float.POSITIVE_INFINITY) {
					System.out.println("all failures: " + iteration);
					FileUtils.writeParameters("./SPParameters/WarpedFAIL"
							+ iteration, threads[0].getUpdatedParameters());
				}
				/*
				 * if (iterationsSinceImprovement > 20) {
				 * System.out.println("Probably found a minimum."); stop = true;
				 * }
				 */
			}

			System.out.println("minCost is: " + minCost);
			if (iteration % 100 == 0) {
				FileUtils.writeParameters("./SPParameters/0510Warpediteration"
						+ iteration + ".par", initialParameters);
			}

		}

		FileUtils
				.writeParameters("./SPParameters/bestSoFar", initialParameters);

		System.out.println("stopping after iteration: " + iteration);
		System.out.println("Costs: ");
		for (float c : costOverTime)
			System.out.println(c);

		System.exit(0);
	}
}
