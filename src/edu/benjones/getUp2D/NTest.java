package edu.benjones.getUp2D;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.benjones.getUp2D.Utils.FileUtils;
import edu.benjones.getUp2D.Utils.MathUtils;
import edu.benjones.getUp2D.optimization.NTestThread;

public class NTest {
	protected final static int numJobs = 128;

	static protected int first = 30;
	static protected int last = 50;

	public static void main(String[] args) {

		float[] startParams = FileUtils
				.readParameters("./SPParameters/warpedImprovement" + first
						+ ".par");
		float[] endParams = FileUtils
				.readParameters("./SPParameters/warpedImprovement" + last
						+ ".par");

		float[] diff = new float[startParams.length];
		for (int i = 0; i < diff.length; ++i) {
			diff[i] = endParams[i] - startParams[i];
		}

		NTestThread[] evalThreads = new NTestThread[numJobs];

		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 0; i < numJobs; ++i) {
			evalThreads[i] = new NTestThread(null);
			evalThreads[i].setInitialParameters(MathUtils.arrayMultAdd(
					startParams, diff, 1.5f * (float) (i)
							/ (float) (numJobs - 1)));

			es.execute(evalThreads[i]);
		}

		es.shutdown();
		System.out.println("waiting");
		try {
			es.awaitTermination(1, TimeUnit.DAYS);
			System.out.println("done waiting");
		} catch (InterruptedException e) {
			System.out.println("interrupted");
			e.printStackTrace();
		}

		for (int i = 0; i < numJobs; ++i) {
			if (evalThreads[i].getCost() == Float.POSITIVE_INFINITY)
				System.out.println("Inf");
			else
				System.out.println(evalThreads[i].getCost());
		}

	}
}
