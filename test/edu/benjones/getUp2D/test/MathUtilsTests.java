package edu.benjones.getUp2D.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.benjones.getUp2D.Utils.MathUtils;

public class MathUtilsTests {

	@Test
	public void testFloatEquals() {
		assertEquals(MathUtils.floatEquals(0f, 0f), true);
		assertEquals(MathUtils.floatEquals(1f, 1f), true);
		assertEquals(MathUtils.floatEquals(1f, 0f), false);
		assertEquals(MathUtils.floatEquals(1, 2.0f / 2.0f), true);
		assertEquals(MathUtils.floatEquals(99f / 3.0f, 66f / 2.0f), true);
	}

	@Test
	public void testFixAngle() {
		assertEquals(MathUtils.floatEquals(MathUtils.fixAngle(0f), 0f), true);
		assertEquals(
				MathUtils.floatEquals(
						MathUtils.fixAngle((float) (-2 * Math.PI)), 0f), true);
		assertEquals(MathUtils.floatEquals(
				MathUtils.fixAngle((float) (2 * Math.PI)), 0f), true);
		assertEquals(MathUtils.floatEquals(
				MathUtils.fixAngle((float) (2.5 * Math.PI)),
				(float) (Math.PI * .5 )), true);
		assertEquals(MathUtils.floatEquals(
				MathUtils.fixAngle((float) (-2.5 * Math.PI)),
				(float) (-Math.PI * .5 )), true);	
	}
}
