package edu.benjones.getUp2D.Utils;

import org.jbox2d.common.Vec2;

import edu.benjones.getUp2D.Utils.Trajectory1D.entry;

public class Trajectory2D {

	private Trajectory1D xTraj;
	private Trajectory1D yTraj;
	
	public Trajectory2D(){
		xTraj = new Trajectory1D();
		yTraj = new Trajectory1D();
	}

	public void addKnot(float t, Vec2 v){
		xTraj.addKnot(new entry(t, v.x));
		yTraj.addKnot(new entry(t, v.y));
	}

}
