package edu.benjones.getUp2D;

import java.util.ArrayList;
import java.util.Collections;

import edu.benjones.getUp2D.Utils.FileUtils;
import edu.benjones.getUp2D.optimization.LineSearchThread;

public class CCDOptimize {

	public static void main(String[] args) {
		LineSearchThread minThread = new LineSearchThread(null);
		LineSearchThread maxThread = new LineSearchThread(null);
		float[] initialParameters = FileUtils
				.readParameters("./SPParameters/dart.par");
		float[] deltas = FileUtils
				.readParameters("./SPParameters/lineSearchStep.par");
		minThread.setInitialParameters(initialParameters);
		minThread.setParameterMaxDelta(deltas);
		maxThread.setInitialParameters(initialParameters);
		maxThread.setParameterMaxDelta(deltas);

		minThread.setMax(false);
		maxThread.setMax(true);

		ArrayList<Integer> indeces = new ArrayList<Integer>(
				initialParameters.length);
		for (int i = 0; i < initialParameters.length; ++i) {
			indeces.add(i);
		}
		Collections.shuffle(indeces);

		Thread thread1, thread2;

		for (int i = 0; i < indeces.size(); ++i) {
			minThread.setIndex(indeces.get(i));
			maxThread.setIndex(indeces.get(i));

			thread1 = new Thread(minThread);
			thread2 = new Thread(maxThread);

			thread1.start();
			thread2.start();

			try {
				thread1.join();
				thread2.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println();
			System.out.println("iteration: " + i + " index: " + indeces.get(i)
					+ " original value: " + initialParameters[indeces.get(i)]);
			System.out.println("Min value: " + minThread.getMinValue());
			System.out.println("Max value: " + maxThread.getMaxValue());
		}
	}
}
