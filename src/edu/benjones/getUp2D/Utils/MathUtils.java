package edu.benjones.getUp2D.Utils;

public class MathUtils {

	/**
	 * Returns an angle on the range [-PI, PI]
	 * @param angle
	 * @return
	 */
	public static float fixAngle(float angle){
		while(angle < -Math.PI)
			angle += 2*Math.PI;
		while(angle > Math.PI)
			angle -= 2*Math.PI;
		return angle;
	}
	public static boolean floatEquals(float f1, float f2){
		return Math.abs(f1 - f2) < .000001;
	}
}
