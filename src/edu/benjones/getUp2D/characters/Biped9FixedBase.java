package edu.benjones.getUp2D.characters;

import org.jbox2d.collision.MassData;
import org.jbox2d.dynamics.World;

import edu.benjones.getUp2D.Character;
import edu.benjones.getUp2D.GetUpScenario;

public class Biped9FixedBase extends Biped9 {
	public Biped9FixedBase(World w, GetUpScenario scenario) {
		super(w, scenario);

		root.setMass(new MassData());

	}

	public static Character makeCharacter(World w, GetUpScenario scenario) {
		System.out.println("used the fixedBase makeCharacter");
		return new Biped9FixedBase(w, scenario);
	}
}
