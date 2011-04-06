package edu.benjones.getUp2D.Controllers;

import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;

public abstract class AbstractController implements Controller{

	protected Character character;
	public AbstractController(Character ch){
		character = ch;
	}
	
	public abstract void computeTorques(World w, float dt);
	
	public abstract void applyTorques();
}
