package edu.benjones.getUp2D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.jbox2d.dynamics.DebugDraw;
import org.jbox2d.testbed.ProcessingDebugDraw;

import processing.core.PApplet;

public class getUpMain extends PApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8391182451118334015L;
	private GetUpScenario getUpScenario;
	public DebugDraw g;

	public getUpMain() {
		super();
	}

	public void setup() {
		size(800, 800, P3D);
		frameRate(GetUpScenario.framerate);
		g = new ProcessingDebugDraw(this);

		getUpScenario = new GetUpScenario(g);// FixedBaseScenario(g);

		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'r') {
					getUpScenario.reset();
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PApplet.main(new String[] { "org.jbox2d.testbed.TestbedMain" });

	}

	public void draw() {
		background(0);
		getUpScenario.step();
		// do step here
	}

}
