package edu.benjones.getUp2D.Utils;

import org.jbox2d.common.Vec2;

public class MathUtils {

	/**
	 * Returns an angle on the range [-PI, PI]
	 * 
	 * @param angle
	 * @return
	 */
	public static float fixAngle(float angle) {
		while (angle < -Math.PI)
			angle += 2 * Math.PI;
		while (angle > Math.PI)
			angle -= 2 * Math.PI;
		return angle;
	}

	public static boolean floatEquals(float f1, float f2) {
		return Math.abs(f1 - f2) < .000001;
	}

	public Vec2 interpolate(Vec2 a, Vec2 b, float alpha) {
		return new Vec2(a.x + alpha * (b.x - a.x), a.y + alpha * (b.y - a.y));
	}

}
