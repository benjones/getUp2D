package edu.benjones.getUp2D.Controllers;

public class ControlParam {
	public float kp, kd;

	private static final float defaultKP = 20, defaultKD = 2;

	public ControlParam(float kp, float kd) {
		this.kp = kp;
		this.kd = kd;
	}

	public ControlParam() {
		this.kp = defaultKP;
		this.kd = defaultKD;
	}

}