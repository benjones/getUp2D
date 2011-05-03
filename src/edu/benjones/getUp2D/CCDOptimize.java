package edu.benjones.getUp2D;

import java.util.ArrayList;
import java.util.Collections;

import edu.benjones.getUp2D.Utils.FileUtils;
import edu.benjones.getUp2D.optimization.LineSearchThread;

public class CCDOptimize {

	public static void main(String[] args) {
		LineSearchThread lineThread = new LineSearchThread(null);
		float[] initialParameters = FileUtils
				.readParameters("./SPParameters/dart.par");
		lineThread.setInitialParameters(initialParameters);
		lineThread.setParameterMaxDelta(FileUtils
				.readParameters("./SPParameters/limits.par"));

		ArrayList<Integer> indeces = new ArrayList<Integer>(
				initialParameters.length);
		for (int i = 0; i < initialParameters.length; ++i) {
			indeces.add(i);
		}
		Collections.shuffle(indeces);

		for (int i = 0; i < indeces.size(); ++i) {
			lineThread.setIndex(indeces.get(i));

			lineThread.run();

			System.out.println();
			System.out.println("iteration: " + i + " index: " + indeces.get(i)
					+ " original value: " + initialParameters[indeces.get(i)]);
			System.out.println("Min value: " + lineThread.getMinValue());
			System.out.println("Max value: " + lineThread.getMaxValue());
		}
	}
}
