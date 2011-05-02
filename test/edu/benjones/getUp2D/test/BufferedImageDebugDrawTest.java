package edu.benjones.getUp2D.test;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.Vec2;

import edu.benjones.getUp2D.Utils.BufferedImageDebugDraw;

public class BufferedImageDebugDrawTest {

	public static void main(String [] args){
		BufferedImageDebugDraw bidd = new BufferedImageDebugDraw();
		
		bidd.clear();
		bidd.saveImage("testImages/blackImage.png");
		bidd.clear(Color3f.RED);
		bidd.saveImage("testImages/redImage.png");
		
		bidd.clear();
		bidd.drawCircle(new Vec2(400, 400), 25.0f, Color3f.RED);
		bidd.drawSolidCircle(new Vec2(400,500), 40f, new Vec2(0,1f), Color3f.BLUE);
		
		Vec2[] tri = {
			new Vec2(400,400),
			new Vec2(100,200),
			new Vec2(200,200)
			
		};
		
		bidd.drawPolygon(tri, 3, Color3f.GREEN);
		
		Vec2[] pent = {
				new Vec2(300,300),
				new Vec2(400,400),
				new Vec2(500,300),
				new Vec2(300,250),
				new Vec2(200,225)			
			};
		
		bidd.drawSolidPolygon(pent, 5, Color3f.GREEN);
		
		bidd.drawPoint(new Vec2(10,10), 3, Color3f.WHITE);
		bidd.drawPoint(new Vec2(10,20), 3, Color3f.RED);
		
		bidd.drawSegment(new Vec2(30,40), new Vec2(40, 80), Color3f.RED);
		
		bidd.drawString(30, 50, "this is a string", Color3f.WHITE);
		
		bidd.saveImage("testImages/redCircle.png");
	}
}
