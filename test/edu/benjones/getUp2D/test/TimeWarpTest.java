package edu.benjones.getUp2D.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.benjones.getUp2D.Utils.MathUtils;
import edu.benjones.getUp2D.Utils.TimeWarp;

public class TimeWarpTest {

	@Test
	public void getPhaseUnchangedTest() {
		TimeWarp tw = new TimeWarp(0, 10, 10);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(0), 0), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(10), 10), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(5), 5), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(3.5f), 3.5f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(.5f), .5f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(9.5f), 9.5f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(20f), 20f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(110f), 110f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(11f), 11f), true);
	}

	@Test
	public void getWarpedPhaseTest() {
		TimeWarp tw = new TimeWarp(0, 2, 2);
		tw.setWarp(2, 2f);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(1f), 1f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(2f), 2.5f), true);
		tw.setWarp(1, 2f);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(1f), 1.5f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(2f), 3.5f), true);
		tw.setWarp(0, 2f);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(2f), 4f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(.5f), 1f), true);
		assertEquals(MathUtils.floatEquals(tw.getPhaseAtTime(1.5f), 3f), true);
	}

	@Test
	public void unWarpTest() {
		// unwarped-warped should be the be inverses
		TimeWarp tw = new TimeWarp(0, 10, 10);
		assertEquals(MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(0)), 0),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(1f)), 1f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(1.5f)), 1.5f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(10f)), 10f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(15f)), 15f),
				true);
		assertEquals(MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(15.5f)),
				15.5f), true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(7.5f)), 7.5f),
				true);
		tw.setWarp(2, 2);
		assertEquals(MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(0)), 0),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(1f)), 1f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(1.5f)), 1.5f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(10f)), 10f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(15f)), 15f),
				true);
		assertEquals(MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(15.5f)),
				15.5f), true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(7.5f)), 7.5f),
				true);
		tw.setWarp(5, 5);
		assertEquals(MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(0)), 0),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(1f)), 1f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(1.5f)), 1.5f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(10f)), 10f),
				true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(15f)), 15f),
				true);
		assertEquals(MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(15.5f)),
				15.5f), true);
		assertEquals(
				MathUtils.floatEquals(tw.unWarp(tw.getPhaseAtTime(7.5f)), 7.5f),
				true);
	}

}
