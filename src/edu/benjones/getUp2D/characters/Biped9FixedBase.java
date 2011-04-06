package edu.benjones.getUp2D.characters;

import org.jbox2d.collision.MassData;
import org.jbox2d.dynamics.World;
import edu.benjones.getUp2D.Character;

public class Biped9FixedBase extends Biped9 {
	public Biped9FixedBase(World w) {
		super(w);

		root.setMass(new MassData());
	
	}

	public static Character makeCharacter(World w){
		System.out.println("used the fixedBase makeCharacter");
		return new Biped9FixedBase(w);
	}
}
