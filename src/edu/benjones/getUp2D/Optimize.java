package edu.benjones.getUp2D;

import java.util.ArrayList;
import java.util.Scanner;

import org.jbox2d.testbed.ProcessingDebugDraw;

import processing.core.PApplet;
import edu.benjones.getUp2D.Utils.FileUtils;
import edu.benjones.getUp2D.optimization.OptimizationThread;

public class Optimize {

	protected final static int numThreads = 1;

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
			threads[i] = new OptimizationThread(new ProcessingDebugDraw(
					new PApplet()));
		}

		float[] initialParameters = FileUtils
				.readParameters("./SPParameters/dart.par");
		float[] limits = FileUtils.readParameters("./SPParameters/limits.par");
		assert (initialParameters.length == limits.length);
		for (int i = 0; i < numThreads; ++i)
			threads[i].setParameterMaxDelta(limits);

		int iteration = 0;
		int improvements = 0;
		float bestSoFar = Float.POSITIVE_INFINITY;
		ArrayList<Float> costOverTime = new ArrayList<Float>();
		while (!stop) {
			iteration++;
			for (int i = 0; i < numThreads; ++i) {
				threads[i].setInitialParameters(initialParameters);

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
			if (minCost < bestSoFar) {
				improvements++;
				initialParameters = threads[minIndex].getUpdatedParameters();
				bestSoFar = minCost;
				System.out.println("bestSoFar improved " + improvements
						+ " times");
				costOverTime.add(minCost);
				FileUtils.writeParameters("./SPParameters/bestSoFar",
						initialParameters);
			} else {
				System.out.println("Nothing better than bestSoFar");
				if (minCost == Float.POSITIVE_INFINITY) {
					System.out.println("all failures: " + iteration);
					FileUtils.writeParameters(
							"./SPParameters/FAIL" + iteration,
							threads[0].getUpdatedParameters());
				}
			}

			System.out.println("minCost is: " + minCost);
			if (iteration % 100 == 0) {
				FileUtils.writeParameters("./SPParameters/0429iteration"
						+ iteration + ".par", initialParameters);
			}

		}

		System.out.println("stopping after iteration: " + iteration);
		System.out.println("Costs: ");
		for (float c : costOverTime)
			System.out.println(c);

	}
}
