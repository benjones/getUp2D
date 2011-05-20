package edu.benjones.getUp2D;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.benjones.getUp2D.Utils.FileUtils;
import edu.benjones.getUp2D.Utils.MathUtils;
import edu.benjones.getUp2D.optimization.NTestThread;

public class NTest {
	protected final static int numJobs = 128;

	static protected float first = -.1f;
	static protected float last = -.19f;

	public static void main(String[] args) {

		float[] startParams = FileUtils
				.readParameters("./SPParameters/continuationDownhillResult-0.09999998.par");
		float[] endParams = FileUtils
				.readParameters("./SPParameters/continuationDownhillResult-0.19000003.par");

		float[] diff = new float[startParams.length];
		for (int i = 0; i < diff.length; ++i) {
			diff[i] = endParams[i] - startParams[i];
		}

		NTestThread[] evalThreads = new NTestThread[numJobs];

		ExecutorService es = Executors.newCachedThreadPool();
		for (int i = 0; i < numJobs; ++i) {
			float ratio = (float) (i) / (float) (numJobs - 1);
			evalThreads[i] = new NTestThread(null);
			evalThreads[i].setInitialParameters(MathUtils.arrayMultAdd(
					startParams, diff, ratio));
			evalThreads[i].setGroundAngle(first + ratio * (last - first));
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
