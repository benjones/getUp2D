package edu.benjones.getUp2D.optimization;

import java.util.concurrent.Callable;

import org.jbox2d.dynamics.DebugDraw;

public class ContinuationThread extends NTestThread implements Callable<Object> {

	public ContinuationThread(DebugDraw g) {
		super(g);
	}

	@Override
	public Object call() throws Exception {
		run();
		return null;
	}

}
