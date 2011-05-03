package edu.benjones.getUp2D;

import edu.benjones.getUp2D.Utils.FileUtils;
import edu.benjones.getUp2D.optimization.LineSearchThread;

public class CCDOptimize {

	public static void main(String[] args) {
		LineSearchThread lineThread = new LineSearchThread(null);
		lineThread.setInitialParameters(FileUtils
				.readParameters("./SPParameters/dart.par"));
		lineThread.setParameterMaxDelta(FileUtils
				.readParameters("./SPParameters/limits.par"));
		lineThread.setIndex(0);

		lineThread.run();

		System.out.println("Min value: " + lineThread.getMinValue());
		System.out.println("Max value: " + lineThread.getMaxValue());
	}
}
