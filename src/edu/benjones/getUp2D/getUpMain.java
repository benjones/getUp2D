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

	private boolean paused;

	private boolean doReset;

	public getUpMain() {
		super();
	}

	@SuppressWarnings("unused")
	public void setup() {
		paused = false;
		doReset = false;
		size(800, 800, P3D);
		frameRate(GetUpScenario.framerate);
		g = new ProcessingDebugDraw(this);

		if (true) {
			getUpScenario = new GetUpScenario(g);
		} else {
			getUpScenario = new FixedBaseScenario(g);
		}

		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == 'r') {
					doReset = true;
				} else if (e.getKeyChar() == 'd') {
					getUpScenario.setDrawDesiredPose(!getUpScenario
							.getDrawDesiredPose());
				} else if (e.getKeyChar() == 'p') {
					paused = !paused;
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
		PApplet.main(new String[] { "Get up 2D" });

	}

	public void draw() {
		if (!paused) {
			background(0);
			if (doReset) {
				doReset = false;
				getUpScenario.reset();
			}
			getUpScenario.step();

			// this.saveFrame();
			// I could also maybe look at using movie maker
		}
	}

}
