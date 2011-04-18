package edu.benjones.getUp2D.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.benjones.getUp2D.Utils.MathUtils;
import edu.benjones.getUp2D.Utils.Trajectory1D;

public class Trajectory1DTest {

	private Trajectory1D traj;

	@Before
	public void setupTrajectory() {
		traj = new Trajectory1D();
		traj.addKnot(new Trajectory1D.entry(0, 1));
		traj.addKnot(new Trajectory1D.entry(1, 2));
		traj.addKnot(new Trajectory1D.entry(2, 3));
	}

	@Test
	public void testTrajectory() {
		assertEquals(MathUtils.floatEquals(traj.getMinT(), 0), true);
		assertEquals(MathUtils.floatEquals(traj.getMaxT(), 2), true);
		assertEquals(MathUtils.floatEquals(traj.evaluateLinear(-1), 1), true);
		assertEquals(MathUtils.floatEquals(traj.evaluateLinear(4), 3), true);
		assertEquals(MathUtils.floatEquals(traj.evaluateLinear(1), 2), true);
		assertEquals(MathUtils.floatEquals(traj.evaluateLinear(2), 3), true);
		assertEquals(MathUtils.floatEquals(traj.evaluateLinear(0), 1), true);
		assertEquals(MathUtils.floatEquals(traj.evaluateLinear(.5f), 1.5f),
				true);
		assertEquals(MathUtils.floatEquals(traj.evaluateLinear(1.5f), 2.5f),
				true);

	}
}
